package com.example.bankspringboot.exporters;

import com.example.bankspringboot.dto.financialreport.FinancialReportDTO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

@Component
public class FinancialReportExcelExporter {

  public byte[] export(FinancialReportDTO report) throws IOException {

    try (Workbook workbook = new XSSFWorkbook()) {
      Sheet sheet = workbook.createSheet("Financial Report");

      int rowIdx = 0;

      rowIdx = createRow(sheet, rowIdx, "From Date", report.fromDate().toString());
      rowIdx = createRow(sheet, rowIdx, "To Date", report.toDate().toString());
      rowIdx = createRow(sheet, rowIdx, "Total Deposit", report.totalDeposit());
      rowIdx = createRow(sheet, rowIdx, "Total Withdrawal", report.totalWithdrawal());
      rowIdx = createRow(sheet, rowIdx, "Total Transfer", report.totalTransfer());
      rowIdx = createRow(sheet, rowIdx, "Net Balance", report.netBalance());

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      workbook.write(out);
      return out.toByteArray();
    }
  }

  private int createRow(Sheet sheet, int rowIdx, String label, Object value) {
    Row row = sheet.createRow(rowIdx++);
    row.createCell(0).setCellValue(label);
    row.createCell(1).setCellValue(value.toString());
    return rowIdx;
  }
}
