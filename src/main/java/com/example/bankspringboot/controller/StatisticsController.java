package com.example.bankspringboot.controller;

import com.example.bankspringboot.dto.statistics.AccountStatisticsDto;
import com.example.bankspringboot.dto.statistics.AddressStatisticsDto;
import com.example.bankspringboot.service.StatisticsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticsController {

  StatisticsService statisticsService;

  @GetMapping("/account-transactions-by-balance")
  public List<AccountStatisticsDto> getNumOfAccountsAndTransactionsByBalance() {
    return statisticsService.getNumOfAccountsAndTransactionsByBalance();
  }

  @GetMapping("customers-by-city")
  public List<AddressStatisticsDto> getNumOfCustomersByCity() {
    return statisticsService.getNumOfCustomersByCity();
  }
}
