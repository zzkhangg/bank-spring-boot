package com.example.bankspringboot.controller;

import com.example.bankspringboot.dto.financialreport.FinancialReportDTO;
import com.example.bankspringboot.exporters.FinancialReportExcelExporter;
import com.example.bankspringboot.exporters.FinancialReportPdfExporter;
import com.example.bankspringboot.service.FinancialReportService;
import java.io.IOException;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports/financial")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FinancialReportController {

  FinancialReportService financialReportService;
  FinancialReportExcelExporter excelExporter;
  FinancialReportPdfExporter pdfExporter;

  @GetMapping("/pdf")
  public ResponseEntity<byte[]> getPdfReport(@RequestParam LocalDate from, @RequestParam LocalDate to) {
    FinancialReportDTO report = financialReportService.getFinancialReport(from, to);
    byte[] file = pdfExporter.export(report);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=financial-report.pdf")
        .contentType(MediaType.APPLICATION_PDF)
        .contentLength(file.length)
        .body(file);
  }

  @GetMapping("/excel")
  public ResponseEntity<byte[]> getExcelReport(@RequestParam LocalDate from, @RequestParam LocalDate to)
      throws IOException {
    FinancialReportDTO report = financialReportService.getFinancialReport(from, to);
    byte[] file = excelExporter.export(report);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=financial-report.xlsx")
        .contentType(MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        ))
        .contentLength(file.length)
        .body(file);
  }
}
