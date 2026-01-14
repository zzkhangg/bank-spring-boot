package com.example.bankspringboot.dto.statistics;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddressStatisticsDto {

  private String city;
  private BigDecimal customerNum;
}
