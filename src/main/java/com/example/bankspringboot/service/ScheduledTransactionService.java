package com.example.bankspringboot.service;

import com.example.bankspringboot.common.ScheduledStatus;
import com.example.bankspringboot.common.ScheduledType;
import com.example.bankspringboot.domain.account.Account;
import com.example.bankspringboot.domain.scheduledtransaction.ScheduledTransaction;
import com.example.bankspringboot.domain.transaction.TransactionType;
import com.example.bankspringboot.dto.transaction.ScheduledTransactionRequest;
import com.example.bankspringboot.dto.transaction.ScheduledTransactionResponse;
import com.example.bankspringboot.exceptions.AppException;
import com.example.bankspringboot.exceptions.ErrorCode;
import com.example.bankspringboot.mapper.ScheduledTransactionMapper;
import com.example.bankspringboot.repository.AccountRepository;
import com.example.bankspringboot.repository.ScheduledTransactionRepository;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ScheduledTransactionService {

  ScheduledTransactionRepository scheduledTransactionRepository;
  AccountRepository accountRepository;
  ScheduledTransactionMapper scheduledTransactionMapper;
  private final ScheduledTransactionCalculator scheduledTransactionCalculator;

  @PreAuthorize("hasRole('USER') &&" + "@accountSecurity.isOwner(#fromId, authentication.name)")
  public ScheduledTransactionResponse scheduleTransaction(
      UUID fromId, ScheduledTransactionRequest request) {
    UUID toId = UUID.fromString(request.getToAccountId());
    Account fromAccount =
        accountRepository
            .findById(fromId)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Account not found"));

    Account toAccount =
        accountRepository
            .findById(toId)
            .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Account not found"));

    ScheduledTransaction scheduledTransaction =
        scheduledTransactionMapper.toScheduledTransaction(request);
    scheduledTransaction.setFromAccount(fromAccount);
    scheduledTransaction.setToAccount(toAccount);
    scheduledTransaction.setStatus(ScheduledStatus.ACTIVE);

    if (request.getTransactionType() == TransactionType.WITHDRAW
        || request.getTransactionType() == TransactionType.TRANSFER_IN) {
      throw new AppException(
          ErrorCode.CREATE_ERROR, "Can not schedule transaction for withdrawal and transfer in");
    }

    if (fromId.equals(toId)) {
      if (request.getTransactionType() == TransactionType.TRANSFER_OUT) {
        throw new AppException(
            ErrorCode.TRANSFER_ERROR,
            "Transfer in/out transactions for the same account not allowed");
      }
    } else {
      if (request.getTransactionType() == TransactionType.DEPOSIT) {
        throw new AppException(
            ErrorCode.TRANSFER_ERROR,
            "Cannot deposit/withdraw transactions for the different account");
      }
    }

    if (request.getScheduledType() == ScheduledType.DAILY) {
      scheduledTransaction.setIntervalDays(
          request.getIntervalDays() != null ? request.getIntervalDays() : 1);
    }

    if (request.getScheduledType() == ScheduledType.WEEKLY) {
      scheduledTransaction.setDayOfWeek(request.getDayOfWeek());
    }

    if (request.getScheduledType() == ScheduledType.MONTHLY) {
      scheduledTransaction.setDayOfMonth(request.getDayOfMonth());
    }
    scheduledTransaction.setNextRunAt(
        scheduledTransactionCalculator.calculateNextRunAt(scheduledTransaction));

    ScheduledTransaction saved = scheduledTransactionRepository.save(scheduledTransaction);

    return scheduledTransactionMapper.toResponse(saved);
  }
}
