package com.java.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Date;
import java.util.List;

import com.java.domain.Invoice;
import com.java.hibernate.InvoiceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InvoiceCommandHandler {

    private final ObjectInputStream objIs;
    private final ObjectOutputStream objOs;
    private final InvoiceManager invoiceManager;
    private static final Logger logger = LogManager.getLogger(InvoiceCommandHandler.class);

    public InvoiceCommandHandler(ObjectInputStream objIs, ObjectOutputStream objOs, InvoiceManager invoiceManager) {
        this.objIs = objIs;
        this.objOs = objOs;
        this.invoiceManager = invoiceManager;
    }

    public void handleInvoiceCommands(String command) {
        try {
            switch (command) {
                case "INVOICE SHOWALL":
                    List<Invoice> invoices = invoiceManager.showAllInvoices();
                    if (invoices != null && !invoices.isEmpty()) {
                        objOs.writeObject(invoices);
                    } else {
                        objOs.writeObject("No invoices found");
                    }
                    break;

                case "INVOICE PAY":
                    Object payObj = objIs.readObject();
                    if (payObj instanceof Integer) {
                        int invoiceId = (Integer) payObj;
                        invoiceManager.recievePayment(invoiceId);
                        objOs.writeObject("Payment received for invoice ID: " + invoiceId);
                    } else {
                        objOs.writeObject("Invalid invoice ID received.");
                    }
                    break;

                case "INVOICE READ":
                    Object readObj = objIs.readObject();
                    if (readObj instanceof Integer) {
                        int invoiceId = (Integer) readObj;
                        Invoice invoice = invoiceManager.readInvoice(invoiceId);
                        if (invoice != null) {
                            objOs.writeObject(invoice);
                        } else {
                            objOs.writeObject("Invoice not found for ID: " + invoiceId);
                        }
                    } else {
                        objOs.writeObject("Invalid invoice ID received.");
                    }
                    break;

                case "INVOICE DELETE":
                    Object deleteObj = objIs.readObject();
                    if (deleteObj instanceof Integer) {
                        int invoiceId = (Integer) deleteObj;
                        invoiceManager.deleteInvoice(invoiceId);
                        objOs.writeObject("Invoice deleted.");
                    } else {
                        objOs.writeObject("Invalid invoice ID received.");
                    }
                    break;

                case "INVOICE EXPORT":
                    Object exportData = objIs.readObject();
                    if (exportData instanceof Object[]) {
                        Object[] data = (Object[]) exportData;
                        if (data.length == 2 && data[0] instanceof Integer && data[1] instanceof String) {
                            int invoiceId = (Integer) data[0];
                            String filePath = (String) data[1];
                            invoiceManager.exportInvoice(invoiceId, filePath);
                            objOs.writeObject("Invoice exported to: " + filePath);
                        } else {
                            objOs.writeObject("Invalid data for export. Expected [invoiceId, filePath].");
                        }
                    } else {
                        objOs.writeObject("Invalid export data received.");
                    }
                
                    break;

                case "INVOICE REPORT":
                    Object reportObj = objIs.readObject();
                    if (reportObj instanceof Object[]) {
                        Object[] data = (Object[]) reportObj;
                        if (data.length == 3 && data[0] instanceof Date && data[1] instanceof Date && data[2] instanceof String) {
                            Date startDate = (Date) data[0];
                            Date endDate = (Date) data[1];
                            String filePath = (String) data[2];
                            invoiceManager.invoiceReport(startDate, endDate, filePath);  // Pass the filePath to the method
                            objOs.writeObject("Invoice report generated for period: " + startDate + " to " + endDate + " and saved to: " + filePath);
                        } else {
                            objOs.writeObject("Invalid data for invoice report. Expected [startDate, endDate, filePath].");
                        }
                    } else {
                        objOs.writeObject("Invalid data for invoice report. Expected object array with three elements: [startDate, endDate, filePath].");
                    }
                    break;

                default:
                    objOs.writeObject("Unknown command: " + command);
                    break;
            }

            objOs.flush();

        } catch (Exception e) {
            logger.error("Error handling invoice command: {}", e.getMessage(), e);
            try {
                objOs.writeObject("Server error: " + e.getMessage());
                objOs.flush();
            } catch (Exception ex) {
                logger.error("Error sending error message to client: {}", ex.getMessage(), ex);
            }
        }
    }
}
