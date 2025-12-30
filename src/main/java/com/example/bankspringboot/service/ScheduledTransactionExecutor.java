package com.example.bankspringboot.service;

import com.example.bankspringboot.common.ScheduledStatus;
import com.example.bankspringboot.domain.scheduledtransaction.ScheduledTransaction;
import com.example.bankspringboot.dto.transaction.DepositRequest;
import com.example.bankspringboot.dto.transaction.TransferRequest;
import com.example.bankspringboot.mapper.ScheduledTransactionMapper;
import com.example.bankspringboot.repository.ScheduledTransactionRepository;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScheduledTransactionExecutor {

  ScheduledTransactionMapper scheduledTransactionMapper;
  ScheduledTransactionRepository scheduledTransactionRepository;
  private final ScheduledTransactionCalculator scheduledTransactionCalculator;
  private final TransactionExecutor transactionExecutor;

  @Transactional
  public void execute(ScheduledTransaction scheduledTransaction) {
    try {
      log.info("{}", scheduledTransaction.getDescription());
      UUID accountId = scheduledTransaction.getFromAccount().getId();

      switch (scheduledTransaction.getTransactionType()) {
        case DEPOSIT:
          DepositRequest depositRequest = scheduledTransactionMapper.toDepositRequest(
              scheduledTransaction);
          transactionExecutor.depositInternal(accountId, depositRequest);
          break;
        case TRANSFER_OUT:
          TransferRequest transferRequest = scheduledTransactionMapper.toTransferRequest(
              scheduledTransaction);
          transferRequest.setDestinationAccountId(scheduledTransaction.getToAccount().getId());
          transactionExecutor.transferInternal(accountId, transferRequest);
          break;
        default:
          break;
      }
      scheduledTransaction.setNextRunAt(
          scheduledTransactionCalculator.calculateNextRunAt(scheduledTransaction));
      scheduledTransactionRepository.save(scheduledTransaction);
    } catch (Exception e) {
      scheduledTransaction.setStatus(ScheduledStatus.FAILED);
      scheduledTransactionRepository.save(scheduledTransaction);
      throw e;
    }
  }
}
