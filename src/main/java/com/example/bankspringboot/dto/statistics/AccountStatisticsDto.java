package com.example.bankspringboot.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountStatisticsDto {

  private final String balanceType;
  private final Long accountNum;
  private final Long transactionNum;
}
