package com.example.bankspringboot.dto.financialreport;

import com.example.bankspringboot.common.GroupBy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record FinancialReportDTO(
    GroupBy groupBy,
    LocalDate fromDate,
    LocalDate toDate,
    BigDecimal totalDeposit,
    BigDecimal totalWithdrawal,
    BigDecimal totalTransfer,
    BigDecimal netBalance,
    BigDecimal totalFee,
    List<FinancialReportRowDto> rows) {

}
