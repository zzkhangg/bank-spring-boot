package com.example.bankspringboot.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountStatisticsDto {

  final private String balanceType;
  final private Long accountNum;
  final private Long transactionNum;
}
