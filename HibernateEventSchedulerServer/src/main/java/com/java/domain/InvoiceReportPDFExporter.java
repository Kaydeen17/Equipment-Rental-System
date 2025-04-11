package com.java.domain;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.kernel.geom.PageSize;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class InvoiceReportPDFExporter {

    public static void generateReport(List<Invoice> invoices, Date startDate, Date endDate, String filePath) {
        try {
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument, PageSize.LETTER);

            PdfFont font = PdfFontFactory.createFont("Helvetica-Bold");

            // Title
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Paragraph title = new Paragraph("Invoice Summary Report")
                    .setFont(font)
                    .setFontSize(18)
                    .setTextAlignment(com.itextpdf.layout.property.TextAlignment.CENTER);
            document.add(title);
            document.add(new Paragraph("Period: " + sdf.format(startDate) + " to " + sdf.format(endDate)));
            document.add(new Paragraph("\n"));

            // Summary calculation
            int paidCount = 0;
            float paidTotal = 0;
            int unpaidCount = 0;
            float unpaidTotal = 0;

            for (Invoice invoice : invoices) {
                if ("PAID".equalsIgnoreCase(invoice.getStatus())) {
                    paidCount++;
                    paidTotal += invoice.getTotal();
                } else {
                    unpaidCount++;
                    unpaidTotal += invoice.getTotal();
                }
            }

            // Summary section
            document.add(new Paragraph("Summary:"));
            document.add(new Paragraph("Total Invoices: " + invoices.size()));
            document.add(new Paragraph("Paid Invoices: " + paidCount));
            document.add(new Paragraph(String.format("Total Amount Paid: $%.2f", paidTotal)));
            document.add(new Paragraph("Unpaid/Quote Invoices: " + unpaidCount));
            document.add(new Paragraph(String.format("Total Unpaid Amount: $%.2f", unpaidTotal)));
            document.add(new Paragraph("\n"));

            // Table header
            float[] columnWidths = {60F, 120F, 80F, 60F, 80F};
            Table table = new Table(columnWidths);
            table.addHeaderCell("Invoice ID");
            table.addHeaderCell("Client Name");
            table.addHeaderCell("Created On");
            table.addHeaderCell("Status");
            table.addHeaderCell("Total ($)");

            // Table content
            for (Invoice invoice : invoices) {
                table.addCell(String.valueOf(invoice.getInvoiceId()));
                table.addCell(invoice.getClientName());
                table.addCell(sdf.format(invoice.getCreationDate()));
                table.addCell(invoice.getStatus());
                table.addCell(String.format("%.2f", invoice.getTotal()));
            }

            document.add(table);

            document.close();
            System.out.println("Invoice report PDF generated successfully at " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error generating invoice report PDF: " + e.getMessage());
        }
    }
}
