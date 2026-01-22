package com.example.bankspringboot.exporters;

import com.example.bankspringboot.dto.financialreport.FinancialReportDTO;
import com.example.bankspringboot.dto.financialreport.FinancialReportRowDto;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.AxisCrosses;
import org.apache.poi.xddf.usermodel.chart.AxisPosition;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.MarkerStyle;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFLineChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FinancialReportExcelExporter {

  public byte[] export(FinancialReportDTO report) throws IOException {
    log.info("Exporting FinancialReport to Excel " + report.toString());
    try (Workbook workbook = new XSSFWorkbook()) {

      Sheet sheet = workbook.createSheet("Financial Report");
      int rowIdx = 0;

      // ===== Title =====
      Row title = sheet.createRow(rowIdx++);
      title.createCell(0).setCellValue("Financial Report");

      // ===== Metadata =====
      rowIdx = writeMeta(sheet, rowIdx, report);

      // ===== Table Header =====
      Row header = sheet.createRow(rowIdx++);
      String[] headers = {
        "Period", "Deposit", "Withdrawal", "Transfer", "Fee", "Net Change", "Balance"
      };
      for (int i = 0; i < headers.length; i++) {
        header.createCell(i).setCellValue(headers[i]);
      }

      // ===== Rows =====
      for (FinancialReportRowDto r : report.rows()) {
        Row row = sheet.createRow(rowIdx++);
        row.createCell(0).setCellValue(r.getPeriod());
        row.createCell(1).setCellValue(r.getTotalDeposit().doubleValue());
        row.createCell(2).setCellValue(r.getTotalWithdrawal().doubleValue());
        row.createCell(3).setCellValue(r.getTotalTransfer().doubleValue());
        row.createCell(4).setCellValue(r.getTransactionFee().doubleValue());
        row.createCell(5).setCellValue(r.getNetChange().doubleValue());
        row.createCell(6).setCellValue(r.getBalance().doubleValue());
      }

      // ===== Summary =====
      rowIdx++;
      writeSummary(sheet, rowIdx, report);

      // ===== Charts =====
      createTrendChart(sheet, report.rows());

      for (int i = 0; i < headers.length; i++) {
        sheet.autoSizeColumn(i);
      }

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      workbook.write(out);
      return out.toByteArray();
    }
  }

  private int writeMeta(Sheet sheet, int rowIdx, FinancialReportDTO report) {
    Row r1 = sheet.createRow(rowIdx++);
    r1.createCell(0).setCellValue("Group By:");
    r1.createCell(1).setCellValue(report.groupBy().name());

    Row r2 = sheet.createRow(rowIdx++);
    r2.createCell(0).setCellValue("From:");
    r2.createCell(1).setCellValue(report.fromDate().toString());

    Row r3 = sheet.createRow(rowIdx++);
    r3.createCell(0).setCellValue("To:");
    r3.createCell(1).setCellValue(report.toDate().toString());

    return rowIdx + 1;
  }

  private void writeSummary(Sheet sheet, int rowIdx, FinancialReportDTO r) {
    Row row = sheet.createRow(rowIdx);
    row.createCell(0).setCellValue("TOTAL");
    row.createCell(1).setCellValue(r.totalDeposit().doubleValue());
    row.createCell(2).setCellValue(r.totalWithdrawal().doubleValue());
    row.createCell(3).setCellValue(r.totalTransfer().doubleValue());
    row.createCell(4).setCellValue(r.totalFee().doubleValue());
    row.createCell(6).setCellValue(r.netBalance().doubleValue());
  }

  private void createTrendChart(Sheet sheet, List<FinancialReportRowDto> rows) {

    XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();

    XSSFChart chart =
        drawing.createChart(
            drawing.createAnchor(0, 0, 0, 0, 0, rows.size() + 8, 10, rows.size() + 25));

    chart.setTitleText("Balance Trend");
    chart.setTitleOverlay(false);

    XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);

    XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);

    leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

    // Period (column A)
    XDDFDataSource<String> periods =
        XDDFDataSourcesFactory.fromStringCellRange(
            (XSSFSheet) sheet, new CellRangeAddress(4, rows.size() + 3, 0, 0));

    // Balance (column G)
    XDDFNumericalDataSource<Double> balances =
        XDDFDataSourcesFactory.fromNumericCellRange(
            (XSSFSheet) sheet, new CellRangeAddress(4, rows.size() + 3, 6, 6));

    XDDFLineChartData data =
        (XDDFLineChartData) chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);

    XDDFLineChartData.Series series = (XDDFLineChartData.Series) data.addSeries(periods, balances);

    series.setTitle("Balance", null);
    series.setSmooth(false);
    series.setMarkerStyle(MarkerStyle.CIRCLE);

    chart.plot(data);
  }
}
