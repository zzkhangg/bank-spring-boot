package com.example.bankspringboot.service;

import com.example.bankspringboot.domain.account.Account;
import com.example.bankspringboot.domain.account.AccountStatus;
import com.example.bankspringboot.domain.transaction.Transaction;
import com.example.bankspringboot.domain.transaction.TransactionStatus;
import com.example.bankspringboot.domain.transaction.TransactionType;
import com.example.bankspringboot.domain.transaction.Transaction_;
import com.example.bankspringboot.dto.transaction.DepositRequest;
import com.example.bankspringboot.dto.transaction.TransactionResponse;
import com.example.bankspringboot.dto.transaction.TransferRequest;
import com.example.bankspringboot.dto.transaction.WithdrawalRequest;
import com.example.bankspringboot.exceptions.ErrorCode;
import com.example.bankspringboot.mapper.TransactionMapper;
import com.example.bankspringboot.repository.AccountRepository;
import com.example.bankspringboot.repository.TransactionRepository;
import com.example.bankspringboot.exceptions.AppException;
import com.example.bankspringboot.service.specifications.TransactionSpecs;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransactionFacade {

  TransactionRepository transactionRepository;
  AccountRepository accountRepository;
  BigDecimal FEE_PERCENTAGE = BigDecimal.valueOf(0.01);
  BigDecimal TRANSACTION_LIMIT = BigDecimal.valueOf(2000);
  TransactionMapper transactionMapper;
  TransactionExecutor transactionExecutor;

  @PreAuthorize(
      "hasRole('USER') &&"
          + "@accountSecurity.isOwner(#accountId, authentication.name)"
  )
  @Transactional
  public TransactionResponse deposit(UUID accountId, DepositRequest request) {
    return transactionExecutor.depositInternal(accountId, request);
  }

  @PreAuthorize(
      "hasRole('USER') &&"
          + "@accountSecurity.isOwner(#accountId, authentication.name)"
  )
  @Transactional
  public TransactionResponse withdraw(UUID accountId, WithdrawalRequest request) {
    BigDecimal amount = request.getAmount();
    Account account = accountRepository.findByIdWithLock(accountId).orElseThrow(
        () -> new AppException(ErrorCode.RESOURCE_NOT_FOUND,
            "Account with id " + accountId + " not found"));

    if (amount.compareTo(TRANSACTION_LIMIT) > 0) {
      throw new AppException(ErrorCode.TRANSFER_ERROR,
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
    return response;
  }


  @PreAuthorize(
      "hasRole('USER') &&"
          + "@accountSecurity.isOwner(#fromId, authentication.name)"
  )
  public TransactionResponse transfer(UUID fromId, TransferRequest request) {
    return transactionExecutor.transferInternal(fromId, request);
  }

  @PreAuthorize(
      "hasRole('USER') &&"
          + "@accountSecurity.isOwner(#accountId, authentication.name)"
  )
  @Transactional(readOnly = true)
  public List<TransactionResponse> getTransactions(UUID accountId,
      int pageNumber, BigDecimal minPrice, BigDecimal maxPrice, TransactionType type,
      LocalDate createdBefore, LocalDate createdAfter, String sortStr) {
    pageNumber = Math.max(1, pageNumber);
    accountRepository.findById(accountId)
        .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Account not found"));

    Specification<Transaction> spec = Specification.where(
        TransactionSpecs.belongsToAccount(accountId));
    if (minPrice != null) {
      spec = spec.and(TransactionSpecs.priceGreaterThanOrEqualTo(minPrice));
    }
    if (maxPrice != null) {
      spec = spec.and(TransactionSpecs.priceLessThanOrEqualTo(maxPrice));
    }
    if (type != null) {
      spec = spec.and(TransactionSpecs.hasTransactionType(type));
    }
    if (createdBefore != null) {
      spec = spec.and(TransactionSpecs.createdDateBefore(createdBefore));
    }
    if (createdAfter != null) {
      spec = spec.and(TransactionSpecs.createdDateAfter(createdAfter));
    }

    // Create sort object
    Sort sort = Sort.unsorted();
    if (sortStr != null) {
      sort = switch (sortStr.toUpperCase()) {
        case "ASC" -> Sort.by(Sort.Direction.ASC, Transaction_.AMOUNT);
        case "DESC" -> Sort.by(Sort.Direction.DESC, Transaction_.AMOUNT);
        case "DATE-OLDEST" -> Sort.by(Sort.Direction.ASC, Transaction_.CREATED_AT);
        case "DATE-LATEST" -> Sort.by(Sort.Direction.DESC, Transaction_.CREATED_AT);
        default -> Sort.unsorted();
      };
    }
    Page<Transaction> transactionPage = transactionRepository.findAll(spec,
        PageRequest.of(pageNumber - 1, 10, sort));
    List<Transaction> transactions = transactionPage.getContent();

    return transactions.stream()
        .map(t -> {
          TransactionResponse res = transactionMapper.toResponse(t);
          res.setCustomerId(t.getCustomer().getId().toString());
          res.setAccountId(t.getAccount().getId().toString());
          return res;
        })
        .toList();
  }

  @PreAuthorize(
      "hasRole('USER') &&"
          + "@accountSecurity.isOwner(#accountId, authentication.name) &&"
          + "@transactionSecurity.isCustomerOf(#transactionId, authentication.name)"
  )
  @Transactional(readOnly = true)
  public TransactionResponse getTransaction(UUID accountId, UUID transactionId) {
    Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(
        () -> new AppException(ErrorCode.RESOURCE_NOT_FOUND,
            "Transaction with id " + transactionId + " not found"));
    TransactionResponse response = transactionMapper.toResponse(transaction);
    response.setCustomerId(transaction.getCustomer().getId().toString());
    response.setAccountId(transaction.getAccount().getId().toString());
    return response;
  }
}
