package com.example.bankspringboot.service;

import com.example.bankspringboot.common.AccountBalanceType;
import com.example.bankspringboot.dto.statistics.AccountStatisticsDto;
import com.example.bankspringboot.dto.statistics.AddressStatisticsDto;
import com.example.bankspringboot.dto.statistics.TransactionReportView;
import com.example.bankspringboot.repository.AccountRepository;
import com.example.bankspringboot.repository.CustomerRepository;
import com.example.bankspringboot.repository.TransactionRepository;
import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticsService {

  AccountRepository accountRepository;
  CustomerRepository customerRepository;
  private final TransactionRepository transactionRepository;

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional(readOnly = true)
  public List<AccountStatisticsDto> getNumOfAccountsAndTransactionsByBalance() {

    return Arrays.stream(AccountBalanceType.values())
        .map(
            (balanceType) ->
                accountRepository.findAccountTransactionNumByBalanceType(
                    balanceType, balanceType.getMin(), balanceType.getMax()))
        .toList();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
  public List<AddressStatisticsDto> getNumOfCustomersByCity() {
    return customerRepository.countCustomersByCity();
  }
}
