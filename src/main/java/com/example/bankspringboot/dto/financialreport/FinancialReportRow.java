package com.example.bankspringboot.dto.financialreport;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface FinancialReportRow {
  LocalDate getPeriod();

  BigDecimal getTotalDeposit();

  BigDecimal getTotalWithdrawal();

  BigDecimal getTotalTransfer();

  BigDecimal getTransactionFee();

  BigDecimal getNetChange();
}
