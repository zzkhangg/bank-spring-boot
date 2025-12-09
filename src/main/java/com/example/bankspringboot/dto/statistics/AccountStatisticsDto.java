package com.example.bankspringboot.dto.statistics;

import com.example.bankspringboot.common.AccountBalanceType;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class AccountStatisticsDto {

  final private String balanceType;
  final private Long accountNum;
  final private Long transactionNum;

  public AccountStatisticsDto(String balanceType, Long accountNum, Long transactionNum) {
    this.balanceType = balanceType;
    this.accountNum = accountNum;
    this.transactionNum = transactionNum;
  }
}
