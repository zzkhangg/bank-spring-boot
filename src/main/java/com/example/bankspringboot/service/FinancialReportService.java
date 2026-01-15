package com.example.bankspringboot.service;

import com.example.bankspringboot.dto.financialreport.FinancialReportDTO;
import com.example.bankspringboot.dto.financialreport.FinancialReportView;
import com.example.bankspringboot.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class FinancialReportService {

  private final TransactionRepository transactionRepository;

  @PreAuthorize("hasRole('ADMIN')")
  public FinancialReportDTO getFinancialReport(LocalDate from, LocalDate to) {
    FinancialReportView reportView = transactionRepository.getFinancialReport(from, to);
    BigDecimal totalDeposit =
        reportView.getTotalDeposit() == null ? BigDecimal.ZERO : reportView.getTotalDeposit();
    BigDecimal totalWithdraw =
        reportView.getTotalDeposit() == null ? BigDecimal.ZERO : reportView.getTotalWithdraw();
    BigDecimal totalTransfer =
        reportView.getTotalDeposit() == null ? BigDecimal.ZERO : reportView.getTotalTransfer();

    BigDecimal netBalance = totalDeposit.subtract(totalWithdraw).subtract(totalTransfer);

    return new FinancialReportDTO(from, to, totalDeposit, totalWithdraw, totalTransfer, netBalance);
  }
}
