package com.example.bankspringboot.exporters;

import com.example.bankspringboot.dto.financialreport.FinancialReportDTO;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import org.springframework.stereotype.Component;

@Component
public class FinancialReportPdfExporter {

  public byte[] export(FinancialReportDTO report) {

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Document document = new Document();
    PdfWriter.getInstance(document, out);

    document.open();

    document.add(new Paragraph("FINANCIAL REPORT"));
    document.add(new Paragraph("From: " + report.fromDate()));
    document.add(new Paragraph("To: " + report.toDate()));
    document.add(new Paragraph(" "));

    PdfPTable table = new PdfPTable(2);
    table.addCell("Total Deposit");
    table.addCell(report.totalDeposit().toString());

    table.addCell("Total Withdrawal");
    table.addCell(report.totalWithdrawal().toString());

    table.addCell("Total Transfer");
    table.addCell(report.totalTransfer().toString());

    table.addCell("Net Balance");
    table.addCell(report.netBalance().toString());

    document.add(table);
    document.close();

    return out.toByteArray();
  }
}
