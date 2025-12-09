package com.example.bankspringboot.service;

import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

import com.example.bankspringboot.domain.account.Account;
import com.example.bankspringboot.domain.account.AccountStatus;
import com.example.bankspringboot.domain.customer.Customer;
import com.example.bankspringboot.dto.account.AccountResponse;
import com.example.bankspringboot.dto.account.CreateAccountRequest;
import com.example.bankspringboot.dto.account.UpdateAccountRequest;
import com.example.bankspringboot.repository.AccountRepository;
import com.example.bankspringboot.repository.CustomerRepository;
import com.example.bankspringboot.service.exceptions.IdInvalidException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import java.util.Optional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class AccountService {

  final private BigDecimal TRANSACTION_LIMIT = new BigDecimal("100");
  final private AccountRepository accountRepository;
  final private CustomerRepository customerRepository;

  public AccountService(AccountRepository accountRepository, CustomerRepository customerRepository) {
    this.accountRepository = accountRepository;
    this.customerRepository = customerRepository;
  }

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

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public AccountResponse getAccount(Long id) {
    Account account = accountRepository.findById(id).orElseThrow(
        () -> new IdInvalidException("No account with id " + id));

    return new AccountResponse(account.getId(), account.getAccountNumber(),
        account.getAccountType(),
        account.getBalance(), account.getTransactionLimit(), account.getStatus(),
        account.getCreatedAt());
  }

  @Transactional
  public AccountResponse createAccount(CreateAccountRequest req) {
    Account account = new Account();
    Customer customer = customerRepository.findById(req.getCustomerId()).orElseThrow(
        () -> new IdInvalidException("No Customer found with id " + req.getCustomerId()));
    account.setCustomer(customer);
    String accountNumber = generateUniqueAccountNumber();
    account.setAccountNumber(accountNumber);
    account.setAccountType(req.getAccountType());
    account.setBalance(Optional.ofNullable(req.getInitialDeposit()).orElse(BigDecimal.ZERO));
    account.setStatus(AccountStatus.ACTIVE);
    account.setTransactionLimit(TRANSACTION_LIMIT);
    Account saved = accountRepository.save(account);
    return new AccountResponse(saved.getId(), saved.getAccountNumber(), saved.getAccountType(),
        saved.getBalance(), saved.getTransactionLimit(), saved.getStatus(), saved.getCreatedAt());
  }

  @Transactional
  public void deleteAccount(Long id) {
    Account account = accountRepository.findById(id).orElseThrow(
        () -> new IdInvalidException("No account with id " + id));
    accountRepository.delete(account);
  }

  @Transactional
  public AccountResponse updateAccountStatus(Long id, UpdateAccountRequest req) {
    Account account = accountRepository.findById(id).orElseThrow(
        () -> new IdInvalidException("No account with id " + id));
    account.setStatus(req.getStatus());
    Account saved = accountRepository.save(account);
    return new AccountResponse(saved.getId(),
            saved.getAccountNumber(),
            saved.getAccountType(),
            saved.getBalance(),
            saved.getTransactionLimit(),
            saved.getStatus(),
            saved.getCreatedAt());
  }
}
