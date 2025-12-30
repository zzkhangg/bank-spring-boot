package com.example.bankspringboot.service;

import com.example.bankspringboot.domain.account.Account;
import com.example.bankspringboot.domain.account.AccountStatus;
import com.example.bankspringboot.domain.account.AccountStatusHistory;
import com.example.bankspringboot.domain.customer.Customer;
import com.example.bankspringboot.dto.account.AccountResponse;
import com.example.bankspringboot.dto.account.AccountStatusHistoryResponse;
import com.example.bankspringboot.dto.account.CreateAccountRequest;
import com.example.bankspringboot.dto.account.UpdateAccountRequest;
import com.example.bankspringboot.exceptions.ErrorCode;
import com.example.bankspringboot.mapper.AccountMapper;
import com.example.bankspringboot.mapper.AccountStatusHistoryMapper;
import com.example.bankspringboot.repository.AccountRepository;
import com.example.bankspringboot.repository.AccountStatusHistoryRepository;
import com.example.bankspringboot.repository.CustomerRepository;
import com.example.bankspringboot.exceptions.AppException;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class AccountService {

  BigDecimal TRANSACTION_LIMIT = new BigDecimal("10000");
  AccountRepository accountRepository;
  CustomerRepository customerRepository;
  AccountMapper accountMapper;
  AccountStatusHistoryRepository accountStatusHistoryRepository;
  private final AccountStatusHistoryMapper accountStatusHistoryMapper;

  private static String generateAccountNumber() {
    int LENGTH = 10; // length of the account number
    Random random = new Random();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < LENGTH; i++) {
      sb.append(random.nextInt(10)); // append a digit 0-9
    }
    return sb.toString();
  }

  private String generateUniqueAccountNumber() {
    String number;
    do {
      number = generateAccountNumber();
    } while (accountRepository.existsByAccountNumber(number));
    return number;
  }

  @PreAuthorize(
      "hasRole('ADMIN') || (@accountSecurity.isOwner(#id, authentication.name) && hasAuthority('SEE_PERSONAL_INFO'))"
  )
  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public AccountResponse getAccount(UUID id) {
    return accountMapper.toResponse(accountRepository.findById(id)
        .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND)));
  }

  @Transactional
  public AccountResponse createAccount(CreateAccountRequest req) {
    Customer customer = customerRepository.findByEmail(req.getEmail())
        .orElseThrow(
            () -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
    Account account = accountMapper.toAccount(req);

    String accountNumber = generateUniqueAccountNumber();

    account.setCustomer(customer);
    account.setAccountNumber(accountNumber);
    account.setStatus(AccountStatus.ACTIVE);
    account.setTransactionLimit(TRANSACTION_LIMIT);
    Account saved = accountRepository.save(account);
    return accountMapper.toResponse(saved);
  }

  @Transactional
  public void deleteAccount(UUID id) {
    Account account = accountRepository.findById(id).orElseThrow(
        () -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
    accountRepository.delete(account);
  }

  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public AccountResponse updateAccountStatus(UUID id, UpdateAccountRequest req, String username) {
    Account account = accountRepository.findById(id).orElseThrow(
        () -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

    if (account.getStatus() == req.getStatus()) {
      throw new AppException(ErrorCode.UPDATE_ERROR, "Same account status");
    }
    if (account.getStatus() == AccountStatus.CLOSED) {
      throw new AppException(ErrorCode.UPDATE_ERROR, "Account closed");
    }

    accountMapper.updateAccountFromRequest(req, account);
    AccountStatusHistory history = AccountStatusHistory.builder()
        .account(account)
        .status(req.getStatus())
        .changedBy(username)
        .reason(req.getReason())
        .build();
    accountStatusHistoryRepository.save(history);
    return accountMapper.toResponse(accountRepository.save(account));
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasRole('ADMIN')")
  public List<AccountStatusHistoryResponse> getAccountStatusHistory(UUID id) {
    return accountStatusHistoryRepository.
        findAllByAccountIdOrderByChangedAtDesc(id).stream()
        .map(accountStatusHistoryMapper::toResponse)
        .toList();
  }

  public List<AccountResponse> getMyAccounts() {
    String email = SecurityContextHolder.getContext()
        .getAuthentication()
        .getName();
    return accountRepository.findAllByCustomer_Email(email).stream()
        .map(accountMapper::toResponse)
        .toList();
  }
}
