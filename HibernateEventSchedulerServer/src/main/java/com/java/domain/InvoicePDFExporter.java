package com.java.domain;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.kernel.geom.PageSize;


import java.io.IOException;

public class InvoicePDFExporter {

    public static void generateInvoicePDF(Invoice invoice, String filePath) {
        try {
            // Create PdfWriter instance to write to the file
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument, PageSize.LETTER);

            // Load a font for the title (using iText 7 font)
            PdfFont font = PdfFontFactory.createFont("Helvetica-Bold");
            // Add title to the document
            Paragraph title = new Paragraph("Invoice #" + invoice.getInvoiceId())
                    .setFont(font)
                    .setFontSize(18)
                    .setTextAlignment(com.itextpdf.layout.property.TextAlignment.CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            // Add client and invoice details
            document.add(new Paragraph("Client Name: " + invoice.getClientName()));
            document.add(new Paragraph("Booking ID: " + invoice.getBooking().getBookingId())); // Assuming Booking has getBookingId() method
            document.add(new Paragraph("User ID: " + invoice.getUserId()));
            document.add(new Paragraph("Status: " + invoice.getStatus()));
            document.add(new Paragraph("Created On: " + invoice.getCreationDate()));

            // Add pricing information
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Price: $" + invoice.getPrice()));
            document.add(new Paragraph("Tax: $" + invoice.getTax()));
            document.add(new Paragraph("Total: $" + invoice.getTotal()));

            // Add payment information if available
            if (invoice.getPaymentDate() != null) {
                document.add(new Paragraph("Payment Date: " + invoice.getPaymentDate()));
            } else {
                document.add(new Paragraph("Payment Date: Pending"));
            }
         // Add Booking details in a table format
            document.add(new Paragraph("\nBooking Details:"));

            // Create a table with the number of columns equal to the fields in your booking
            float[] pointColumnWidths = {150F, 200F}; 
            Table table = new Table(pointColumnWidths);

            // Adding column headers
            table.addCell(new Cell().add(new Paragraph("Field")));
            table.addCell(new Cell().add(new Paragraph("Value")));

            // Adding booking data to the table
            table.addCell(new Cell().add(new Paragraph("Booking ID")));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(invoice.getBooking().getBookingId()))));

            table.addCell(new Cell().add(new Paragraph("Client Name")));
            table.addCell(new Cell().add(new Paragraph(invoice.getBooking().getClientName())));

            table.addCell(new Cell().add(new Paragraph("Client Contact")));
            table.addCell(new Cell().add(new Paragraph(invoice.getBooking().getClientContact())));

            table.addCell(new Cell().add(new Paragraph("User ID")));
            table.addCell(new Cell().add(new Paragraph(invoice.getBooking().getUserId())));

            table.addCell(new Cell().add(new Paragraph("Booking Date")));
            table.addCell(new Cell().add(new Paragraph(invoice.getBooking().getBookDate().toString())));

            table.addCell(new Cell().add(new Paragraph("Return Date")));
            table.addCell(new Cell().add(new Paragraph(invoice.getBooking().getReturnDate().toString())));

            table.addCell(new Cell().add(new Paragraph("Status")));
            table.addCell(new Cell().add(new Paragraph(invoice.getBooking().getStatus().toString())));

            table.addCell(new Cell().add(new Paragraph("Late Return")));
            table.addCell(new Cell().add(new Paragraph(invoice.getBooking().isLate() ? "Yes" : "No")));

            // If there are assets in the booking, we can list them in a comma-separated format
            StringBuilder assets = new StringBuilder();
            for (Asset asset : invoice.getBooking().getAssetList()) {
                assets.append(asset.getName()).append(", ");
            }
            // Remove last comma and space
            if (assets.length() > 0) {
                assets.setLength(assets.length() - 2);
            }
            table.addCell(new Cell().add(new Paragraph("Assets")));
            table.addCell(new Cell().add(new Paragraph(assets.toString())));

            // Add the table to the document
            document.add(table);

            try {
                document.close();
            } catch (Exception e) {
                e.printStackTrace();
            }


            System.out.println("Invoice PDF generated successfully at " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error generating invoice PDF: " + e.getMessage());
        }
    }
}
