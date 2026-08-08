package com.example.bankspringboot.service;

import com.example.bankspringboot.dto.statistics.TransactionReportResponse;
import com.example.bankspringboot.repository.TransactionRepository;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class TransactionReportService {

  private final TransactionRepository transactionRepository;

  public TransactionReportService(TransactionRepository transactionRepository) {
    this.transactionRepository = transactionRepository;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Cacheable("weeklyTransactionReport")
  public List<TransactionReportResponse> weekly() {
    return transactionRepository.weeklyTransactionReport().stream()
        .map(TransactionReportResponse::from)
        .toList();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Cacheable("quarterlyTransactionReport")
  public List<TransactionReportResponse> quarterly() {
    return transactionRepository.quarterlyTransactionReport().stream()
        .map(TransactionReportResponse::from)
        .toList();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Cacheable("yearlyTransactionReport")
  public List<TransactionReportResponse> yearly() {
    return transactionRepository.yearlyTransactionReport().stream()
        .map(TransactionReportResponse::from)
        .toList();
  }
}
