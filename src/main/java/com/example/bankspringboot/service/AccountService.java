package com.example.bankspringboot.service;

import com.example.bankspringboot.domain.account.Account;
import com.example.bankspringboot.domain.account.AccountStatus;
import com.example.bankspringboot.domain.customer.Customer;
import com.example.bankspringboot.dto.account.AccountResponse;
import com.example.bankspringboot.dto.account.CreateAccountRequest;
import com.example.bankspringboot.dto.account.UpdateAccountRequest;
import com.example.bankspringboot.mapper.AccountMapper;
import com.example.bankspringboot.repository.AccountRepository;
import com.example.bankspringboot.repository.CustomerRepository;
import com.example.bankspringboot.exceptions.IdInvalidException;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AccountService {

  BigDecimal TRANSACTION_LIMIT = new BigDecimal("10000");
  AccountRepository accountRepository;
  CustomerRepository customerRepository;
  AccountMapper accountMapper;

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
      "hasRole('ADMIN') || @accountSecurity.isOwner(#id, authentication)"
  )
  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public AccountResponse getAccount(UUID id) {
    return accountMapper.toResponse(accountRepository.findById(id)
        .orElseThrow(() -> new IdInvalidException("Account not found")));
  }

  @Transactional
  public AccountResponse createAccount(CreateAccountRequest req) {
    Customer customer = customerRepository.findByEmail(req.getEmail())
        .orElseThrow(
            () -> new IdInvalidException("No Customer found with email " + req.getEmail()));
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
        () -> new IdInvalidException("No account with id " + id));
    accountRepository.delete(account);
  }

  @Transactional
  public AccountResponse updateAccountStatus(UUID id, UpdateAccountRequest req) {
    Account account = accountRepository.findById(id).orElseThrow(
        () -> new IdInvalidException("No account with id " + id));
    accountMapper.updateAccountFromRequest(req, account);
    Account saved = accountRepository.save(account);
    return accountMapper.toResponse(saved);
  }

  public List<AccountResponse> getMyAccounts() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UUID userId = UUID.fromString(authentication.getName());
    List<Account> accounts = accountRepository.findAllByCustomerId(userId);
    return accountMapper.toResponseList(accounts);
  }
}
