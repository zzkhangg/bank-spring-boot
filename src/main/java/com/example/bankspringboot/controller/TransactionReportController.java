package com.example.bankspringboot.controller;

import com.example.bankspringboot.dto.statistics.TransactionReportView;
import com.example.bankspringboot.service.TransactionReportService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transaction-report")
public class TransactionReportController {

  private final TransactionReportService transactionReportService;

  public TransactionReportController(TransactionReportService transactionReportService) {
    this.transactionReportService = transactionReportService;
  }

  @GetMapping("/weekly")
  public List<TransactionReportView> weekly() {
    return transactionReportService.weekly();
  }

  @GetMapping("/quarterly")
  public List<TransactionReportView> quarterly() {
    return transactionReportService.quarterly();
  }

  @GetMapping("/yearly")
  public List<TransactionReportView> yearly() {
    return transactionReportService.yearly();
  }
}
