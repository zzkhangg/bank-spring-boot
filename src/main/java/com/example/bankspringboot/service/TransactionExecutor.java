package com.example.bankspringboot.service;

import static java.util.Objects.requireNonNull;

import com.example.bankspringboot.domain.account.Account;
import com.example.bankspringboot.domain.account.AccountStatus;
import com.example.bankspringboot.domain.transaction.Transaction;
import com.example.bankspringboot.domain.transaction.TransactionChannel;
import com.example.bankspringboot.domain.transaction.TransactionStatus;
import com.example.bankspringboot.domain.transaction.TransactionType;
import com.example.bankspringboot.dto.transaction.DepositRequest;
import com.example.bankspringboot.dto.transaction.TransactionResponse;
import com.example.bankspringboot.dto.transaction.TransferRequest;
import com.example.bankspringboot.dto.transaction.WithdrawalRequest;
import com.example.bankspringboot.events.TransactionCreatedEvent;
import com.example.bankspringboot.exceptions.AppException;
import com.example.bankspringboot.exceptions.ErrorCode;
import com.example.bankspringboot.mapper.TransactionMapper;
import com.example.bankspringboot.repository.AccountRepository;
import com.example.bankspringboot.repository.TransactionRepository;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransactionExecutor {

  TransactionRepository transactionRepository;
  AccountRepository accountRepository;
  BigDecimal FEE_PERCENTAGE = BigDecimal.valueOf(0.01);
  TransactionMapper transactionMapper;
  ApplicationEventPublisher eventPublisher;



  @Transactional
  public TransactionResponse transferInternal(UUID fromId, TransferRequest request) {
    UUID toId = request.getDestinationAccountId();
    BigDecimal amount = request.getAmount();

    if (fromId.equals(toId)) {
      throw new AppException(
          ErrorCode.TRANSFER_ERROR, "Cannot transfer funds to the same account.");
    }

    Account fromAccount =
        accountRepository
            .findByIdWithLock(fromId)
            .orElseThrow(
                () ->
                    new AppException(
                        ErrorCode.RESOURCE_NOT_FOUND, "Account with id " + fromId + " not found"));
    Account toAccount =
        accountRepository
            .findByIdWithLock(toId)
            .orElseThrow(
                () ->
                    new AppException(
                        ErrorCode.RESOURCE_NOT_FOUND, "Account with id " + toId + " not found"));

    if (amount.compareTo(fromAccount.getTransactionLimit()) > 0) {
      throw new AppException(
          ErrorCode.TRANSFER_ERROR,
          "Transfer amount must be less than or equal to TRANSACTION_LIMIT");
    }

    if (fromAccount.getStatus() != AccountStatus.ACTIVE) {
      throw new AppException(ErrorCode.TRANSFER_ERROR, "Account is not active");
    }

    BigDecimal fee = FEE_PERCENTAGE.multiply(amount);

    BigDecimal totalDeduction = amount.add(fee);
    if (fromAccount.getBalance().compareTo(totalDeduction) <= 0) {
      throw new AppException(ErrorCode.TRANSFER_ERROR, "Insufficient funds");
    }

    // Update balance for source customer
    fromAccount.setBalance(fromAccount.getBalance().subtract(totalDeduction));

    // Update balance for destination customer
    toAccount.setBalance(toAccount.getBalance().add(amount));

    // Transaction for source
    Transaction txSource = transactionMapper.transferReqToTransaction(request);
    txSource.setFee(fee);
    txSource.setType(TransactionType.TRANSFER_OUT);
    txSource.setAccount(fromAccount);
    txSource.setCustomer(fromAccount.getCustomer());
    txSource.setStatus(TransactionStatus.COMPLETED);

    // Transaction for destination
    // 1. Create a NEW instance instead of using the mapper.
    Transaction txDest = transactionMapper.transferReqToTransaction(request);
    txDest.setAmount(request.getAmount()); // Copies the amount
    txDest.setFee(BigDecimal.ZERO);
    txDest.setType(TransactionType.TRANSFER_IN);
    txDest.setAccount(toAccount); // Receiver's account
    txDest.setCustomer(toAccount.getCustomer());
    txDest.setStatus(TransactionStatus.COMPLETED);

    // Save to database
    accountRepository.save(fromAccount);
    accountRepository.save(toAccount);
    Transaction saved = transactionRepository.save(txSource);
    transactionRepository.save(txDest);

    TransactionResponse response = transactionMapper.toResponse(saved);
    response.setCustomerId(fromAccount.getCustomer().getId().toString());
    response.setAccountId(fromAccount.getId().toString());

    eventPublisher.publishEvent(new TransactionCreatedEvent(saved.getId()));
    eventPublisher.publishEvent(new TransactionCreatedEvent(saved.getId()));
    return response;
  }

  @Transactional
  public TransactionResponse depositInternal(UUID accountId, DepositRequest request) {
    if (request.getChannel() == TransactionChannel.ATM
        || request.getChannel() == TransactionChannel.BRANCH) {
      requireNonNull(request.getAddress());
    }

    Account account =
        accountRepository
            .findByIdWithLock(accountId)
            .orElseThrow(
                () ->
                    new AppException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "Account with id " + accountId + " not found"));

    if (account.getStatus() != AccountStatus.ACTIVE) {
      throw new AppException(ErrorCode.TRANSFER_ERROR, "Account is not active");
    }

    // Update balance
    BigDecimal newBalance = account.getBalance().add(request.getAmount());
    account.setBalance(newBalance);

    // Build transaction
    Transaction transaction = transactionMapper.depositReqToTransaction(request);
    transaction.setFee(BigDecimal.ZERO);
    transaction.setType(TransactionType.DEPOSIT);
    transaction.setAccount(account);
    transaction.setCustomer(account.getCustomer());

    // Save transaction after updating balance
    transaction.setStatus(TransactionStatus.COMPLETED);
    accountRepository.save(account);
    Transaction saved = transactionRepository.save(transaction);
    TransactionResponse response = transactionMapper.toResponse(saved);
    response.setCustomerId(transaction.getCustomer().getId().toString());
    response.setAccountId(accountId.toString());

    eventPublisher.publishEvent(new TransactionCreatedEvent(saved.getId()));
    return response;
  }

  public TransactionResponse withdrawInternal(UUID accountId, WithdrawalRequest request) {
    if (request.getChannel() == TransactionChannel.ATM
        || request.getChannel() == TransactionChannel.BRANCH) {
      requireNonNull(request.getAddress());
    }

    BigDecimal amount = request.getAmount();
    Account account =
        accountRepository
            .findByIdWithLock(accountId)
            .orElseThrow(
                () ->
                    new AppException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "Account with id " + accountId + " not found"));

    if (amount.compareTo(account.getTransactionLimit()) > 0) {
      throw new AppException(
          ErrorCode.TRANSFER_ERROR,
          "Withdrawal amount must be less than or equal to TRANSACTION_LIMIT");
    }

    if (account.getStatus() != AccountStatus.ACTIVE) {
      throw new AppException(ErrorCode.TRANSFER_ERROR, "Account is not active");
    }

    BigDecimal fee = FEE_PERCENTAGE.multiply(amount);
    BigDecimal totalDeduction = amount.add(fee);
    if (account.getBalance().compareTo(totalDeduction) <= 0) {
      throw new AppException(ErrorCode.TRANSFER_ERROR, "Insufficient funds");
    }

    // Update balance
    account.setBalance(account.getBalance().subtract(totalDeduction));

    // Build transaction
    Transaction transaction = transactionMapper.withdrawalReqToTransaction(request);
    transaction.setFee(fee);
    transaction.setType(TransactionType.WITHDRAW);
    transaction.setAccount(account);
    transaction.setCustomer(account.getCustomer());
    transaction.setStatus(TransactionStatus.COMPLETED);

    // Save transaction after updating balance
    accountRepository.save(account);
    Transaction saved = transactionRepository.save(transaction);
    TransactionResponse response = transactionMapper.toResponse(saved);
    response.setCustomerId(transaction.getCustomer().getId().toString());
    response.setAccountId(accountId.toString());

    eventPublisher.publishEvent(new TransactionCreatedEvent(saved.getId()));
    return response;
  }
}
