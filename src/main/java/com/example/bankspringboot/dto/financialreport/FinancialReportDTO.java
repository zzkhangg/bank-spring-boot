package com.example.bankspringboot.dto.financialreport;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FinancialReportDTO(LocalDate fromDate,
                                 LocalDate toDate, BigDecimal totalDeposit, BigDecimal totalWithdrawal,
                                 BigDecimal totalTransfer, BigDecimal netBalance) {

}
