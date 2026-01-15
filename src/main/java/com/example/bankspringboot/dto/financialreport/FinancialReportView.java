package com.example.bankspringboot.dto.financialreport;

import java.math.BigDecimal;

public interface FinancialReportView {
  BigDecimal getTotalDeposit();
  BigDecimal getTotalWithdraw();
  BigDecimal getTotalTransfer();
}
