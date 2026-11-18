package com.example.bankspringboot.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bankspringboot.common.ScheduledStatus;
import com.example.bankspringboot.common.ScheduledType;
import com.example.bankspringboot.domain.idempotency.IdempotencyStatus;
import com.example.bankspringboot.domain.scheduledtransaction.ScheduledTransaction;
import com.example.bankspringboot.domain.scheduledtransaction.ScheduledTransactionExecution;
import com.example.bankspringboot.domain.transaction.TransactionChannel;
import com.example.bankspringboot.dto.transaction.DepositRequest;
import com.example.bankspringboot.dto.transaction.TransferRequest;
import com.example.bankspringboot.mapper.ScheduledTransactionMapper;
import com.example.bankspringboot.repository.ScheduledTransactionExecutionRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScheduledTransactionExecutor {

  ScheduledTransactionMapper scheduledTransactionMapper;
  ScheduledTransactionCalculator scheduledTransactionCalculator;
  TransactionExecutor transactionExecutor;
  ScheduledTransactionExecutionRepository executionRepository;

  @Transactional
  public void execute(UUID executionId) {
      ScheduledTransactionExecution execution = executionRepository.findById(executionId).orElseThrow();
      ScheduledTransaction schedule = execution.getScheduledTransaction();

      validateExecution(execution, schedule);

      executeMoneyMovement(schedule);
      
      advanceSchedule(execution, schedule);

      execution.setStatus(IdempotencyStatus.SUCCESS);
  }

  private void validateExecution(
    ScheduledTransactionExecution execution,
    ScheduledTransaction schedule
) {
    if (execution.getStatus() != IdempotencyStatus.PROCESSING) {
        throw new IllegalStateException(
            "Scheduled execution is not PROCESSING: executionId="
                + execution.getId()
                + ", status="
                + execution.getStatus()
        );
    }

    if (schedule.getStatus() != ScheduledStatus.ACTIVE) {
        throw new IllegalStateException(
            "Scheduled transaction is not ACTIVE: scheduleId="
                + schedule.getId()
                + ", status="
                + schedule.getStatus()
        );
    }

    if (!Objects.equals(
        execution.getIntendedRunAt(),
        schedule.getNextRunAt()
    )) {
        throw new IllegalStateException(
            "Stale scheduled execution: executionId="
                + execution.getId()
                + ", intendedRunAt="
                + execution.getIntendedRunAt()
                + ", currentNextRunAt="
                + schedule.getNextRunAt()
        );
    }
}

  private void executeMoneyMovement(ScheduledTransaction schedule) {
    UUID accountId = schedule.getFromAccount().getId();
    switch (schedule.getTransactionType()) {
        case DEPOSIT:
          DepositRequest depositRequest =
              scheduledTransactionMapper.toDepositRequest(schedule);
          depositRequest.setChannel(TransactionChannel.SCHEDULED);
          transactionExecutor.executeDeposit(accountId, depositRequest);
          break;
        case TRANSFER_OUT:
          TransferRequest transferRequest =
              scheduledTransactionMapper.toTransferRequest(schedule);
          transferRequest.setDestinationAccountId(schedule.getToAccount().getId());
          transferRequest.setChannel(TransactionChannel.SCHEDULED);
          transactionExecutor.executeTransfer(accountId, transferRequest);
          break;
        default:
          throw new IllegalStateException(
            "Unsupported scheduled transaction type: "
                + schedule.getTransactionType()
        );
      }
  }

  private void advanceSchedule(
      ScheduledTransactionExecution execution,
      ScheduledTransaction schedule
  ) {
      LocalDateTime intendedLocal =
          LocalDateTime.ofInstant(
              execution.getIntendedRunAt(),
              schedule.getZoneId()
          );

      schedule.setLastIntendedLocal(intendedLocal);

      if (schedule.getScheduledType() == ScheduledType.ONCE) {
          schedule.setStatus(ScheduledStatus.COMPLETED);
          schedule.setNextRunAt(null);
          return;
      }

      Instant nextRunAt =
          scheduledTransactionCalculator.calculateNextRunAt(schedule);

      schedule.setNextRunAt(nextRunAt);
  }
}
