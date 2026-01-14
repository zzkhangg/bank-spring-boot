package com.example.bankspringboot.service;

import com.example.bankspringboot.common.ScheduledStatus;
import com.example.bankspringboot.domain.idempotency.IdempotencyStatus;
import com.example.bankspringboot.domain.scheduledtransaction.ScheduledTransaction;
import com.example.bankspringboot.domain.scheduledtransaction.ScheduledTransactionExecution;
import com.example.bankspringboot.domain.transaction.TransactionChannel;
import com.example.bankspringboot.dto.transaction.DepositRequest;
import com.example.bankspringboot.dto.transaction.TransferRequest;
import com.example.bankspringboot.mapper.ScheduledTransactionMapper;
import com.example.bankspringboot.repository.ScheduledTransactionRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScheduledTransactionExecutor {

  ScheduledTransactionMapper scheduledTransactionMapper;
  ScheduledTransactionRepository scheduledTransactionRepository;
  ScheduledTransactionCalculator scheduledTransactionCalculator;
  TransactionExecutor transactionExecutor;
  IdempotencyKeyWriter idempotencyKeyWriter;
  ScheduledTransactionWriter scheduledTransactionWriter;

  @Transactional
  public void execute(ScheduledTransaction scheduledTransaction) {
    try {
      ScheduledTransactionExecution execution =
          ScheduledTransactionExecution.builder()
              .intendedRunAt(scheduledTransaction.getNextRunAt())
              .scheduledTransactionId(scheduledTransaction.getId())
              .status(IdempotencyStatus.PROCESSING)
              .build();
      idempotencyKeyWriter.write(execution);

      UUID accountId = scheduledTransaction.getFromAccount().getId();

      switch (scheduledTransaction.getTransactionType()) {
        case DEPOSIT:
          DepositRequest depositRequest =
              scheduledTransactionMapper.toDepositRequest(scheduledTransaction);
          depositRequest.setChannel(TransactionChannel.SCHEDULED);
          transactionExecutor.depositInternal(accountId, depositRequest);
          break;
        case TRANSFER_OUT:
          TransferRequest transferRequest =
              scheduledTransactionMapper.toTransferRequest(scheduledTransaction);
          transferRequest.setDestinationAccountId(scheduledTransaction.getToAccount().getId());
          transferRequest.setChannel(TransactionChannel.SCHEDULED);
          transactionExecutor.transferInternal(accountId, transferRequest);
          break;
        default:
          break;
      }
      Instant nextRunAt = scheduledTransactionCalculator.calculateNextRunAt(scheduledTransaction);
      scheduledTransactionWriter.markNextRunAt(nextRunAt, scheduledTransaction);
      execution.setStatus(IdempotencyStatus.SUCCESS);
      idempotencyKeyWriter.write(execution);
    } catch (DataIntegrityViolationException e) {
      log.info(e.getMessage());
    } catch (Exception e) {
      scheduledTransaction.setStatus(ScheduledStatus.FAILED);
      scheduledTransactionRepository.save(scheduledTransaction);
      throw e;
    }
  }
}
