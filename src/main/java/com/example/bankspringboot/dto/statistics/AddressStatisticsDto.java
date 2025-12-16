package com.example.bankspringboot.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class AddressStatisticsDto {

  private String city;
  private BigDecimal customerNum;
}
