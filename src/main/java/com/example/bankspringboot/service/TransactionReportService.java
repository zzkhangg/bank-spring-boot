package com.example.bankspringboot.service;

import com.example.bankspringboot.dto.statistics.TransactionReportView;
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
  public List<TransactionReportView> weekly() {
    return transactionRepository.weeklyReport();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Cacheable("quarterlyTransactionReport")
  public List<TransactionReportView> quarterly() {
    return transactionRepository.quarterlyReport();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Cacheable("yearlyTransactionReport")
  public List<TransactionReportView> yearly() {
    return transactionRepository.yearlyReport();
  }
}
