package com.example.bankspringboot.exporters;

import com.example.bankspringboot.dto.financialreport.FinancialReportDTO;
import com.example.bankspringboot.dto.financialreport.FinancialReportRowDto;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Component;

@Slf4j
@Component
  public class FinancialReportPdfExporter {

  public byte[] export(FinancialReportDTO report) throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Document doc = new Document(PageSize.A4);
    PdfWriter.getInstance(doc, out);

    doc.open();

    doc.add(new Paragraph("Financial Report"));
    doc.add(new Paragraph("Period: " + report.fromDate() + " → " + report.toDate()));
    doc.add(Chunk.NEWLINE);

    // ===== Table =====
    PdfPTable table = new PdfPTable(7);
    table.setWidthPercentage(100);

    Stream.of("Period", "Deposit", "Withdrawal", "Transfer", "Fee", "Net", "Balance")
        .forEach(h -> table.addCell(new PdfPCell(new Phrase(h))));

    for (FinancialReportRowDto r : report.rows()) {
      table.addCell(r.getPeriod().toString());
      table.addCell(r.getTotalDeposit().toString());
      table.addCell(r.getTotalWithdrawal().toString());
      table.addCell(r.getTotalTransfer().toString());
      table.addCell(r.getTransactionFee().toString());
      table.addCell(r.getNetChange().toString());
      table.addCell(r.getBalance().toString());
    }

    doc.add(table);
    doc.add(Chunk.NEWLINE);

    // ===== Chart =====
    Image chart = Image.getInstance(createBalanceChart(report));
    chart.scaleToFit(500, 300);
    doc.add(chart);

    doc.close();
    return out.toByteArray();
  }

  private byte[] createBalanceChart(FinancialReportDTO report) throws IOException {

    DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    for (FinancialReportRowDto r : report.rows()) {
      dataset.addValue(r.getBalance(), "Balance", r.getPeriod());
    }

    JFreeChart chart = ChartFactory.createLineChart(
        "Balance Trend",
        "Period",
        "Balance",
        dataset
    );

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ChartUtils.writeChartAsPNG(out, chart, 600, 350);
    return out.toByteArray();
  }

}
