package com.example.bankspringboot.dto.financialreport;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FinancialReportRowDto {

  private LocalDate period;        // "2024-01" or "2024"
  private BigDecimal totalDeposit;
  private BigDecimal totalWithdrawal;
  private BigDecimal totalTransfer;
  private BigDecimal transactionFee;
  private BigDecimal netChange;
  private BigDecimal balance;    // calculated later

}
