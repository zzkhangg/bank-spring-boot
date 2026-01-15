package com.example.bankspringboot.dto.statistics;

import java.math.BigDecimal;

public interface TransactionReportView {
  Integer getYear();
  Integer getPeriod();
  BigDecimal getMaxAmount();
  BigDecimal getMinAmount();
  BigDecimal getAvgAmount();
}
