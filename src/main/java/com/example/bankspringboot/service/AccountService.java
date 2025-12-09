package com.example.bankspringboot.service;

import static org.springframework.transaction.annotation.Propagation.SUPPORTS;

import com.example.bankspringboot.domain.account.Account;
import com.example.bankspringboot.domain.account.AccountStatus;
import com.example.bankspringboot.domain.customer.Customer;
import com.example.bankspringboot.dto.account.AccountResponse;
import com.example.bankspringboot.dto.account.CreateAccountRequest;
import com.example.bankspringboot.dto.account.UpdateAccountRequest;
import com.example.bankspringboot.mapper.AccountMapper;
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
  private final AccountMapper accountMapper;

  public AccountService(AccountRepository accountRepository, CustomerRepository customerRepository,
      AccountMapper accountMapper) {
    this.accountRepository = accountRepository;
    this.customerRepository = customerRepository;
    this.accountMapper = accountMapper;
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

    AccountResponse accountResponse = accountMapper.toResponse(account);
    accountResponse.setAccountId(id);
    return accountResponse;
  }

  @Transactional
  public AccountResponse createAccount(CreateAccountRequest req) {
    Customer customer = customerRepository.findById(req.getCustomerId()).orElseThrow(
        () -> new IdInvalidException("No Customer found with id " + req.getCustomerId()));
    Account account = accountMapper.toAccount(req);

    String accountNumber = generateUniqueAccountNumber();

    account.setCustomer(customer);
    account.setAccountNumber(accountNumber);
    account.setStatus(AccountStatus.ACTIVE);
    account.setTransactionLimit(TRANSACTION_LIMIT);
    Account saved = accountRepository.save(account);
    AccountResponse accountResponse = accountMapper.toResponse(saved);
    accountResponse.setAccountId(saved.getId());
    return accountResponse;
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
    accountMapper.updateAccountFromRequest(req, account);
    Account saved = accountRepository.save(account);
    AccountResponse accountResponse = accountMapper.toResponse(saved);
    accountResponse.setAccountId(id);
    return accountResponse;
  }
}
