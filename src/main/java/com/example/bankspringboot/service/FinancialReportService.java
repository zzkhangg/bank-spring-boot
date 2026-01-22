package com.example.bankspringboot.service;

import com.example.bankspringboot.common.GroupBy;
import com.example.bankspringboot.dto.financialreport.FinancialReportDTO;
import com.example.bankspringboot.dto.financialreport.FinancialReportRow;
import com.example.bankspringboot.dto.financialreport.FinancialReportRowDto;
import com.example.bankspringboot.exceptions.AppException;
import com.example.bankspringboot.exceptions.ErrorCode;
import com.example.bankspringboot.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class FinancialReportService {

  private final TransactionRepository transactionRepository;

  @PreAuthorize("hasRole('ADMIN')")
  public FinancialReportDTO getFinancialReport(GroupBy groupBy, LocalDate from, LocalDate to,
      Integer year, Integer month) {
    if (year != null && groupBy == GroupBy.YEAR) {
      from = LocalDate.of(year, 1, 1);
      to = LocalDate.of(year, 12, 31);
    }

    if (month != null && groupBy == GroupBy.MONTH) {
      if (year == null) {
        year = Year.now().getValue();
      }
      from = LocalDate.of(year, month, 1);
      to = YearMonth.of(year, month).atEndOfMonth();
    }

    if (from == null || to == null) {
      throw new AppException(ErrorCode.VALIDATION_ERROR, "from and to cannot be null");
    }

    if (from.isAfter(to)) {
      throw new AppException(ErrorCode.VALIDATION_ERROR, "from cannot be after to");
    }

    Instant instantFrom = from.atStartOfDay(ZoneOffset.UTC).toInstant();
    Instant  instantTo = to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
    log.info("from: {}, to: {}", instantFrom, instantTo);

    List<FinancialReportRow> rows = switch (groupBy) {
      case YEAR -> transactionRepository.financialReportByYear(instantFrom, instantTo);
      case MONTH -> transactionRepository.financialReportByMonth(instantFrom, instantTo);
      case DAY -> transactionRepository.financialReportByDay(instantFrom, instantTo);
    };

    List<FinancialReportRowDto> rowDtos = rows.stream().map(row -> {
      FinancialReportRowDto rowDto = new FinancialReportRowDto();
      rowDto.setPeriod(row.getPeriod());
      rowDto.setTotalDeposit(row.getTotalDeposit());
      rowDto.setTotalWithdrawal(row.getTotalWithdrawal());
      rowDto.setTotalTransfer(row.getTotalTransfer());
      rowDto.setTransactionFee(row.getTransactionFee());
      rowDto.setNetChange(row.getNetChange());
      return rowDto;
    }).toList();

    BigDecimal initialBalance = Optional.ofNullable(
            transactionRepository.getNetBalance(instantFrom))
        .map(netBalance -> netBalance.min(BigDecimal.ZERO))
        .orElse(BigDecimal.ZERO);
    BigDecimal runningBalance = initialBalance;
    for (FinancialReportRowDto dto : rowDtos) {
      runningBalance = runningBalance.add(dto.getNetChange());
      dto.setBalance(runningBalance);
    }

    BigDecimal totalDeposit = rows.stream()
        .map(FinancialReportRow::getTotalDeposit)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal totalWithdraw = rows.stream()
        .map(FinancialReportRow::getTotalWithdrawal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal totalTransfer = rows.stream()
        .map(FinancialReportRow::getTotalTransfer)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal netChange = rows.stream()
        .map(FinancialReportRow::getNetChange)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal totalFee = rows.stream()
        .map(FinancialReportRow::getTransactionFee)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal netBalance = initialBalance.add(netChange);

    return new FinancialReportDTO(groupBy, from, to, totalDeposit, totalWithdraw, totalTransfer,
        netBalance,
        totalFee, rowDtos);
  }
}
