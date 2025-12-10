package com.example.bankspringboot.service;

import com.example.bankspringboot.domain.account.Account;
import com.example.bankspringboot.domain.account.AccountStatus;
import com.example.bankspringboot.domain.transaction.Transaction;
import com.example.bankspringboot.domain.transaction.TransactionStatus;
import com.example.bankspringboot.domain.transaction.TransactionType;
import com.example.bankspringboot.dto.transaction.DepositRequest;
import com.example.bankspringboot.dto.transaction.TransactionResponse;
import com.example.bankspringboot.dto.transaction.TransferRequest;
import com.example.bankspringboot.dto.transaction.WithdrawalRequest;
import com.example.bankspringboot.mapper.TransactionMapper;
import com.example.bankspringboot.repository.AccountRepository;
import com.example.bankspringboot.repository.TransactionRepository;
import com.example.bankspringboot.service.exceptions.BusinessException;
import com.example.bankspringboot.service.exceptions.IdInvalidException;
import com.example.bankspringboot.service.specifications.TransactionSpecs;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

  final private TransactionRepository transactionRepository;
  final private AccountRepository accountRepository;
  final private BigDecimal FEE_PERCENTAGE = BigDecimal.valueOf(0.01);
  final private BigDecimal TRANSACTION_LIMIT = BigDecimal.valueOf(2000);
  private final TransactionMapper transactionMapper;

  public TransactionService(TransactionRepository transactionRepository,
      AccountRepository accountRepository, TransactionMapper transactionMapper) {
    this.transactionRepository = transactionRepository;
    this.accountRepository = accountRepository;
    this.transactionMapper = transactionMapper;
  }


  @Transactional
  public TransactionResponse deposit(DepositRequest depositRequest) {
    Long accountId = depositRequest.getAccountId();

    Account account = accountRepository.findByIdWithLock(accountId)
        .orElseThrow(() -> new BusinessException("Account with id " + accountId + " not found"));

    if (account.getStatus() != AccountStatus.ACTIVE) {
      throw new BusinessException("Account is not active");
    }

    // Update balance
    BigDecimal newBalance = account.getBalance().add(depositRequest.getAmount());
    account.setBalance(newBalance);

    // Build transaction
    Transaction transaction = transactionMapper.depositReqToTransaction(depositRequest);
    transaction.setFee(BigDecimal.ZERO);
    transaction.setType(TransactionType.DEPOSIT);
    transaction.setAccount(account);
    transaction.setCustomer(account.getCustomer());

    // Save transaction after updating balance
    transaction.setStatus(TransactionStatus.COMPLETED);
    accountRepository.save(account);
    Transaction saved = transactionRepository.save(transaction);
    TransactionResponse response = transactionMapper.toResponse(saved);
    response.setCustomerId(transaction.getCustomer().getId());
    response.setAccountId(accountId);
    return response;
  }

  @Transactional
  public TransactionResponse withdraw(WithdrawalRequest req) {
    Long accountId = req.getAccountId();
    BigDecimal amount = req.getAmount();
    Account account = accountRepository.findByIdWithLock(accountId).orElseThrow(
        () -> new IdInvalidException("Account with id " + accountId + " not found"));

    if (amount.compareTo(TRANSACTION_LIMIT) > 0) {
      throw new BusinessException(
          "Withdrawal amount must be less than or equal to TRANSACTION_LIMIT");
    }

    if (account.getStatus() != AccountStatus.ACTIVE) {
      throw new BusinessException("Account is not active");
    }

    BigDecimal fee = FEE_PERCENTAGE.multiply(amount);

    BigDecimal totalDeduction = amount.add(fee);
    if (account.getBalance().compareTo(totalDeduction) <= 0) {
      throw new BusinessException("Insufficient funds");
    }

    // Update balance
    account.setBalance(account.getBalance().subtract(totalDeduction));

    // Build transaction
    Transaction transaction = transactionMapper.withdrawalReqToTransaction(req);
    transaction.setFee(fee);
    transaction.setType(TransactionType.WITHDRAW);
    transaction.setAccount(account);
    transaction.setCustomer(account.getCustomer());
    transaction.setStatus(TransactionStatus.COMPLETED);

    // Save transaction after updating balance
    accountRepository.save(account);
    transactionRepository.save(transaction);
    return transactionMapper.toResponse(transaction);
  }

  @Transactional
  public TransactionResponse transfer(TransferRequest req) {
    Long fromId = req.getSourceAccountId();
    Long toId = req.getDestinationAccountId();
    BigDecimal amount = req.getAmount();

    if (fromId.equals(toId)) {
      throw new BusinessException("Cannot transfer funds to the same account.");
    }

    Account fromAccount = accountRepository.findByIdWithLock(fromId).orElseThrow(
        () -> new IdInvalidException("Account with id " + fromId + " not found"));
    Account toAccount = accountRepository.findByIdWithLock(toId).orElseThrow(
        () -> new IdInvalidException("Account with id " + toId + " not found"));

    if (amount.compareTo(TRANSACTION_LIMIT) > 0) {
      throw new BusinessException(
          "Transfer amount must be less than or equal to TRANSACTION_LIMIT");
    }

    if (fromAccount.getStatus() != AccountStatus.ACTIVE) {
      throw new BusinessException("Account is not active");
    }

    BigDecimal fee = FEE_PERCENTAGE.multiply(amount);

    BigDecimal totalDeduction = amount.add(fee);
    if (fromAccount.getBalance().compareTo(totalDeduction) <= 0) {
      throw new BusinessException("Insufficient funds");
    }

    // Update balance for source customer
    fromAccount.setBalance(fromAccount.getBalance().subtract(totalDeduction));

    // Update balance for destination customer
    toAccount.setBalance(toAccount.getBalance().add(amount));

    // Transaction for source
    Transaction txSource = transactionMapper.transferReqToTransaction(req);
    txSource.setFee(fee);
    txSource.setType(TransactionType.TRANSFER_OUT);
    txSource.setAccount(fromAccount);
    txSource.setCustomer(fromAccount.getCustomer());
    txSource.setStatus(TransactionStatus.COMPLETED);

    // Transaction for destination
    // 1. Create a NEW instance instead of using the mapper.
    Transaction txDest = transactionMapper.transferReqToTransaction(req);
    txDest.setAmount(req.getAmount()); // Copies the amount
    txDest.setFee(BigDecimal.ZERO);
    txDest.setType(TransactionType.TRANSFER_IN);
    txDest.setAccount(toAccount); // Receiver's account
    txDest.setCustomer(toAccount.getCustomer());
    txDest.setStatus(TransactionStatus.COMPLETED);

    // Save to database
    accountRepository.save(fromAccount);
    accountRepository.save(toAccount);
    transactionRepository.save(txSource);
    transactionRepository.save(txDest);

    return transactionMapper.toResponse(txSource);
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public List<TransactionResponse> getTransactions(Long accountId, int pageNumber
      , BigDecimal minPrice, BigDecimal maxPrice, TransactionType type, LocalDate createdBefore,
      LocalDate createdAfter) {
    pageNumber = Math.max(1, pageNumber);
    accountRepository.findById(accountId)
        .orElseThrow(() -> new IdInvalidException("Account not found"));

    Specification<Transaction> spec = TransactionSpecs.belongsToAccount(accountId);
    if (minPrice != null) {
      spec = spec.and(TransactionSpecs.priceGreaterThanOrEqualTo(minPrice));
    }
    if (maxPrice != null) {
      spec = spec.and(TransactionSpecs.priceLessThanOrEqualTo(maxPrice));
    }
    if (type != null) {
      spec = spec.and(TransactionSpecs.inTransactionType(type));
    }
    if (createdBefore != null) {
      spec = spec.and(TransactionSpecs.createdDateBefore(createdBefore));
    }
    if (createdAfter != null) {
      spec = spec.and(TransactionSpecs.createdDateAfter(createdAfter));
    }

    Page<Transaction> transactionPage = transactionRepository.findAll(spec,
        PageRequest.of(pageNumber - 1, 10));
    List<Transaction> transactions = transactionPage.getContent();

    List<TransactionResponse> transactionResponses = new ArrayList<>();
    transactions.forEach(transaction -> {
      TransactionResponse transactionResponse = transactionMapper.toResponse(transaction);
      transactionResponse.setCustomerId(transaction.getCustomer().getId());
      transactionResponse.setAccountId(transaction.getAccount().getId());
      transactionResponses.add(transactionResponse);
    });
    return transactionResponses;
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public TransactionResponse getTransaction(Long transactionId) {
    Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(
        () -> new IdInvalidException("Transaction with id " + transactionId + " not found"));
    TransactionResponse transactionResponse = transactionMapper.toResponse(transaction);
    transactionResponse.setCustomerId(transaction.getCustomer().getId());
    transactionResponse.setAccountId(transaction.getAccount().getId());
    return transactionResponse;
  }
}
