package com.example.bankspringboot.service;

import com.example.bankspringboot.common.AccountBalanceType;
import com.example.bankspringboot.dto.statistics.AccountStatisticsDto;
import com.example.bankspringboot.dto.statistics.AdressStatisticsDto;
import com.example.bankspringboot.repository.AccountRepository;
import com.example.bankspringboot.repository.CustomerRepository;
import com.example.bankspringboot.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StatisticsService {

  final private AccountRepository accountRepository;
  final private TransactionRepository transactionRepository;
  private final CustomerRepository customerRepository;

  public StatisticsService(AccountRepository accountRepository,
      TransactionRepository transactionRepository, CustomerRepository customerRepository) {
    this.accountRepository = accountRepository;
    this.transactionRepository = transactionRepository;
    this.customerRepository = customerRepository;
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public List<AccountStatisticsDto> getNumOfAccountsAndTransactionsByBalance() {
    List<AccountStatisticsDto> accountStatisticsDtoList = new ArrayList<>();
    for (AccountBalanceType accountBalanceType : AccountBalanceType.values()) {
      Long accountNum = accountRepository.countAccountsInRange(accountBalanceType.getMin(),
          accountBalanceType.getMax());
      Long transactionNum = transactionRepository.countTransactionsInRange(
          accountBalanceType.getMin(), accountBalanceType.getMax());
      accountStatisticsDtoList.add(
          new AccountStatisticsDto(accountBalanceType.toString(), accountNum, transactionNum));
    }

    return accountStatisticsDtoList;
  }

  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public List<AdressStatisticsDto> getNumOfCustomersByCity() {
    return customerRepository.countCustomersByCity();
  }
}
