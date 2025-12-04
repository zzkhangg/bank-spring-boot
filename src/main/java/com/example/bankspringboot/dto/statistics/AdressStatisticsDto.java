package com.example.bankspringboot.dto.statistics;

import com.example.bankspringboot.domain.common.Address;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class AdressStatisticsDto {
    private String city;
    private BigDecimal customerNum;
}
