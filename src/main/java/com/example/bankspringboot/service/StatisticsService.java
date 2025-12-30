package com.example.bankspringboot.service;

import com.example.bankspringboot.common.AccountBalanceType;
import com.example.bankspringboot.dto.statistics.AccountStatisticsDto;
import com.example.bankspringboot.dto.statistics.AddressStatisticsDto;
import com.example.bankspringboot.repository.AccountRepository;
import com.example.bankspringboot.repository.CustomerRepository;
import com.example.bankspringboot.repository.TransactionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticsService {

  AccountRepository accountRepository;
  TransactionRepository transactionRepository;
  CustomerRepository customerRepository;

  @PreAuthorize("hasRole('ADMIN')")
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

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public List<AddressStatisticsDto> getNumOfCustomersByCity() {
    return customerRepository.countCustomersByCity();
  }
}
