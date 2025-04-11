package com.java.hibernate;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.Hibernate;
import com.java.domain.Invoice;
import com.java.domain.InvoicePDFExporter;
import com.java.domain.InvoiceReportPDFExporter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class InvoiceManager extends InvoiceManagement{

    private static final Logger logger = LogManager.getLogger(InvoiceManager.class);
    private static final SessionFactory sessionFactory = buildSessionFactory(); // Singleton

    // Build session factory using Hibernate utility
    private static SessionFactory buildSessionFactory() {
        try {
            logger.info("Building session factory...");
            return new Configuration().configure().buildSessionFactory();
        } catch (Exception e) {
            logger.error("SessionFactory creation failed: {}", e.getMessage(), e);
            System.err.println("SessionFactory creation failed: " + e);
            throw new ExceptionInInitializerError(e);
        }
    }

    // Exit method to close session factory
    @Override
    public void exit() {
        try {
            logger.info("Closing session factory...");
            sessionFactory.close();
        } catch (Exception e) {
            logger.error("Error while closing session factory: {}", e.getMessage(), e);
            System.err.println("Error while closing session factory: " + e);
        }
    }

    @Override
    public Invoice readInvoice(int invoiceId) {
        logger.info("Reading invoice with ID: {}", invoiceId);
        Session session = sessionFactory.openSession();
        try {
            // Fetch the invoice from the database using the ID
            Invoice invoice = session.get(Invoice.class, invoiceId);
            if (invoice != null) {
                // Initialize any lazy-loaded properties (if necessary)
                Hibernate.initialize(invoice);
            } else {
                logger.warn("Invoice with ID {} not found.", invoiceId);
            }
            return invoice;
        } finally {
            session.close();
        }
    }

    @Override
    public void recievePayment(int invoiceId) {
        logger.info("Processing payment for invoice ID: {}", invoiceId);
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            // Fetch the invoice using its ID
            Invoice invoice = session.get(Invoice.class, invoiceId);

            if (invoice != null) {
                // Check if the invoice is already paid
                if ("PAID".equalsIgnoreCase(invoice.getStatus())) {
                    logger.warn("Invoice ID {} is already paid. Payment cannot be processed again.", invoiceId);
                    System.out.println("⚠ Invoice ID " + invoiceId + " is already paid. Payment cannot be processed again.");
                    session.getTransaction().rollback();
                    return;
                }

                // Calculate total payment amount (price + tax)
                double totalAmount = invoice.getPrice() + invoice.getTax();

                // Set payment date and update status to PAID
                invoice.setPaymentDate(new java.util.Date());
                invoice.setStatus("PAID");
                session.merge(invoice); // Merge to update the database

                session.getTransaction().commit();
                logger.info("Payment received for invoice ID: {}", invoiceId);
                System.out.println("Payment received for invoice ID: " + invoiceId);
                System.out.printf("Total Amount Paid: %.2f (Price: %.2f + Tax: %.2f)%n", 
                                  totalAmount, invoice.getPrice(), invoice.getTax());
            } else {
                logger.warn("Invoice with ID {} not found for payment processing.", invoiceId);
                System.out.println("Invoice not found with ID: " + invoiceId);
                session.getTransaction().rollback();
            }
        } catch (Exception e) {
            session.getTransaction().rollback();
            logger.error("Error receiving payment for invoice ID {}: {}", invoiceId, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    // Show all invoices and details about them
    @Override
    public List<Invoice> showAllInvoices() {
        logger.info("Fetching all invoices...");
        List<Invoice> invoices = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            // Fetch all invoices from the database
            invoices = session.createQuery("FROM Invoice", Invoice.class).list();

            // Initialize any lazy-loaded properties (if necessary)
            for (Invoice invoice : invoices) {
                Hibernate.initialize(invoice);
            }
            logger.info("Total invoices found: {}", invoices.size());
        } catch (Exception e) {
            logger.error("Error retrieving invoices: {}", e.getMessage(), e);
            System.err.println("Error retrieving invoices: " + e);
        }
        return invoices;
    }

    @Override
    public void deleteInvoice(int invoiceId) {
        logger.info("Deleting invoice with ID: {}", invoiceId);
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            // Fetch the invoice using its ID
            Invoice invoice = session.get(Invoice.class, invoiceId);

            if (invoice != null) {
                // Check if the invoice status is PAID
                if ("PAID".equalsIgnoreCase(invoice.getStatus())) {
                    // Delete the invoice
                    session.remove(invoice);
                    session.getTransaction().commit();
                    logger.info("Invoice ID {} deleted successfully.", invoiceId);
                    System.out.println("Invoice ID " + invoiceId + " deleted successfully.");
                } else {
                    logger.warn("Invoice ID {} cannot be deleted as its status is not PAID.", invoiceId);
                    System.out.println("Invoice ID " + invoiceId + " cannot be deleted as its status is not PAID.");
                    session.getTransaction().rollback();
                }
            } else {
                logger.warn("Invoice with ID {} not found for deletion.", invoiceId);
                System.out.println("Invoice not found with ID: " + invoiceId);
                session.getTransaction().rollback();
            }
        } catch (Exception e) {
            session.getTransaction().rollback();
            logger.error("Error deleting invoice with ID {}: {}", invoiceId, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public void exportInvoice(int invoiceId, String filePath) {
        logger.info("Exporting invoice with ID: {} to path: {}", invoiceId, filePath);
        // Step 1: Fetch the invoice using the existing method
        Invoice invoice = readInvoice(invoiceId);

        // Step 2: Use the separate class to export it as a PDF
        if (invoice != null) {
            InvoicePDFExporter.generateInvoicePDF(invoice, filePath);
            logger.info("Invoice ID {} exported successfully to path: {}", invoiceId, filePath);
        } else {
            logger.warn("Invoice with ID {} not found. Cannot export.", invoiceId);
            System.out.println(" Invoice not found. Cannot export.");
        }
    }

    @Override
    public void invoiceReport(Date startDate, Date endDate, String filePath) {
        logger.info("Generating invoice report from {} to {} at path: {}", startDate, endDate, filePath);

        try (Session session = sessionFactory.openSession()) {
            List<Invoice> invoices = session.createQuery(
                    "FROM Invoice WHERE creationDate BETWEEN :startDate AND :endDate", Invoice.class)
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .list();

            if (invoices.isEmpty()) {
                logger.info("No invoices found between {} and {}", startDate, endDate);
                return;
            }

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

            logger.info("\n=========== INVOICE REPORT ===========");
            logger.info("Period: {} to {}", startDate, endDate);
            logger.info("--------------------------------------");
            logger.info("Total Invoices:        {}", invoices.size());
            logger.info("Paid Invoices:         {}", paidCount);
            logger.info("Total Amount paid:     {:.2f}", paidTotal);
            logger.info("Unpaid/Quote Invoices: {}", unpaidCount);
            logger.info("Total Unpaid Amount:   {:.2f}", unpaidTotal);
            logger.info("======================================\n");

            // Export to PDF with the specified file path
            if (filePath == null || filePath.isEmpty()) {
                filePath = "Invoice_Report_" + System.currentTimeMillis() + ".pdf";  // Default if no path is provided
            }

            InvoiceReportPDFExporter.generateReport(invoices, startDate, endDate, filePath);
            logger.info("Invoice report exported to PDF at path: {}", filePath);

            logger.info("Invoice report generated successfully.");
        } catch (Exception e) {
            logger.error("Error generating invoice report: {}", e.getMessage(), e);
            System.err.println("Error generating invoice report: " + e.getMessage());
        }
    }




}
