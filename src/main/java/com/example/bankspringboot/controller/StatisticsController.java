package com.example.bankspringboot.controller;

import com.example.bankspringboot.dto.statistics.AccountStatisticsDto;
import com.example.bankspringboot.dto.statistics.AdressStatisticsDto;
import com.example.bankspringboot.service.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

  final private StatisticsService statisticsService;

  public StatisticsController(StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

  @GetMapping("/account-transactions-by-balance")
  public List<AccountStatisticsDto> getNumOfAccountsAndTransactionsByBalance() {
    return statisticsService.getNumOfAccountsAndTransactionsByBalance();
  }

  @GetMapping("customers-by-city")
  public List<AdressStatisticsDto> getNumOfCustomersByCity() {
    return statisticsService.getNumOfCustomersByCity();
  }
}
