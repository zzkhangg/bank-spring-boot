package com.example.bankspringboot.controller;

import com.example.bankspringboot.common.GroupBy;
import com.example.bankspringboot.common.ReportFormat;
import com.example.bankspringboot.dto.financialreport.FinancialReportDTO;
import com.example.bankspringboot.exporters.FinancialReportExcelExporter;
import com.example.bankspringboot.exporters.FinancialReportPdfExporter;
import com.example.bankspringboot.service.FinancialReportService;
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

  @GetMapping()
  public ResponseEntity<byte[]> getFinancialReport(
      @RequestParam GroupBy groupBy,
      @RequestParam(defaultValue = "EXCEL") ReportFormat format,
      @RequestParam(required = false) LocalDate from,
      @RequestParam(required = false) LocalDate to,
      @RequestParam(required = false) Integer year,
      @RequestParam(required = false) Integer month
  ) throws Exception {
    FinancialReportDTO financialReportDTO = financialReportService.getFinancialReport(groupBy, from,
        to, year, month);
    byte[] file = switch (format) {
      case EXCEL -> excelExporter.export(financialReportDTO);
      case PDF -> pdfExporter.export(financialReportDTO);
    };
    if (format == ReportFormat.EXCEL) {
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION,
              "attachment; filename=financial-report.xlsx")
          .contentType(
              MediaType.parseMediaType(
                  "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
              )
          )
          .contentLength(file.length)
          .body(file);
    } else {
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION,
              "attachment; filename=financial-report.pdf")
          .contentType(MediaType.APPLICATION_PDF)
          .contentLength(file.length)
          .body(file);
    }
  }
}
