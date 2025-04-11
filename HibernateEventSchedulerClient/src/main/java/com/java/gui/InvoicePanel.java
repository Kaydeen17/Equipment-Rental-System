package com.java.gui;

import com.java.domain.Invoice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;

public class InvoicePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(InvoicePanel.class);

    private JTable invoiceTable;
    private JButton refreshButton;
    private JButton acceptPaymentButton;
    private JButton deleteInvoiceButton;
    private JRadioButton allRadio;
    private JRadioButton pendingRadio;
    private JRadioButton paidRadio;
    private ButtonGroup filterGroup;
    private JTextField searchField;

    public InvoicePanel() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Top Panel with buttons and filters
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("Invoice Overview");

        refreshButton = new JButton("\u27F3");
        acceptPaymentButton = createActionButton("Accept Payment", "💵", "INVOICE PAY");
        deleteInvoiceButton = createActionButton("Delete Invoice", "❌", "INVOICE DELETE");

        // Initialize the radio buttons
        allRadio = new JRadioButton("All", true);  // Default to All
        pendingRadio = new JRadioButton("Pending");
        paidRadio = new JRadioButton("Paid");

        // Group the radio buttons together
        filterGroup = new ButtonGroup();
        filterGroup.add(allRadio);
        filterGroup.add(pendingRadio);
        filterGroup.add(paidRadio);

        searchField = new JTextField(20);
        searchField.setToolTipText("Search by client ID or name...");
        searchField.setPreferredSize(new Dimension(200, 30));

        // Add components to the top panel
        topPanel.add(titleLabel);
        topPanel.add(refreshButton);
        topPanel.add(acceptPaymentButton);
        topPanel.add(deleteInvoiceButton);
        topPanel.add(allRadio);  // Add the "All" radio button
        topPanel.add(pendingRadio);
        topPanel.add(paidRadio);
        topPanel.add(searchField);

        // Invoice Table setup
        String[] columns = {"Invoice ID", "Client", "User", "Booking ID", "Status", "Price", "Tax", "Total"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        invoiceTable = new JTable(model) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Disable editing for all cells
            }

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (isRowSelected(row)) {
                    comp.setBackground(new Color(173, 216, 230));
                } else {
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
                }
                return comp;
            }
        };

        invoiceTable.setRowHeight(25);
        JScrollPane tableScroll = new JScrollPane(invoiceTable);

        // Table settings
        invoiceTable.setCellSelectionEnabled(false);
        invoiceTable.setRowSelectionAllowed(true);

        invoiceTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = invoiceTable.rowAtPoint(e.getPoint());
                    if (row != -1) {
                        invoiceTable.setRowSelectionInterval(row, row); // Ensure selection
                        String invoiceIdString = invoiceTable.getValueAt(row, 0).toString().trim();
                        try {
                            int invoiceId = Integer.parseInt(invoiceIdString);
                            showInvoiceDetails(invoiceId);
                        } catch (NumberFormatException ex) {
                            logger.error("Invalid Invoice ID: " + invoiceIdString, ex);
                            JOptionPane.showMessageDialog(InvoicePanel.this, "Invalid Invoice ID: " + invoiceIdString, "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        // Actions
        ActionListener filterListener = e -> fetchInvoices();
        refreshButton.addActionListener(e -> fetchInvoices());
        allRadio.addActionListener(filterListener);  // Add listener for the "All" radio button
        pendingRadio.addActionListener(filterListener);
        paidRadio.addActionListener(filterListener);
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                fetchInvoices();
            }
        });

        add(topPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);

        fetchInvoices();
    }

    private JButton createActionButton(String text, String icon, String actionCommand) {
        JButton button = new JButton(text + " " + icon);
        button.setActionCommand(actionCommand);
        button.setPreferredSize(new Dimension(150, 50));
        button.setMaximumSize(new Dimension(150, 50));
        button.setBackground(Color.LIGHT_GRAY);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);

        button.addActionListener(e -> {
            if (invoiceTable != null) {  // Ensure invoiceTable is initialized
                int selectedRow = invoiceTable.getSelectedRow();

                // Ensure a row is selected
                if (selectedRow != -1) {
                    // Get the invoiceId from the selected row (assuming the invoiceId is in the first column)
                    Object invoiceIdObject = invoiceTable.getValueAt(selectedRow, 0); // Adjust index if invoiceId is in another column
                    if (invoiceIdObject != null) {
                        try {
                            // Convert the invoiceId to an integer
                            int invoiceId = Integer.parseInt(invoiceIdObject.toString());
                            // Get the status from column 4
                            String status = invoiceTable.getValueAt(selectedRow, 4).toString().trim();

                            // Logic based on actionCommand
                            if (actionCommand.equalsIgnoreCase("INVOICE PAY")) {
                                if (status.equalsIgnoreCase("PAID")) {
                                    JOptionPane.showMessageDialog(null, "This invoice has already been paid.", "Payment Status", JOptionPane.INFORMATION_MESSAGE);
                                    return;  // Exit early
                                }
                            } else if (actionCommand.equalsIgnoreCase("INVOICE DELETE")) {
                                if (status.equalsIgnoreCase("PENDING")) {
                                    JOptionPane.showMessageDialog(null, "You cannot delete a PENDING invoice.", "Delete Restriction", JOptionPane.WARNING_MESSAGE);
                                    return;  // Exit early
                                }
                            }
                            // Confirmation dialog before proceeding with the action
                            int confirmation = JOptionPane.showConfirmDialog(
                                    null,
                                    "Are you sure you want to " + actionCommand + " for this invoice (ID: " + invoiceId + ")?",
                                    "Confirmation",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE
                            );

                            // If user clicks "Yes", send the action to the server
                            if (confirmation == JOptionPane.YES_OPTION) {
                                // Send the invoiceId to the server
                                LoginPage.client.sendAction(button.getActionCommand());
                                LoginPage.client.sendInvoiceId(invoiceId);

                                try {
                                    // Receive the response from the server
                                    Object serverResponse = LoginPage.client.receiveObject();

                                    // Check if the response is a String and show it in a popup
                                    if (serverResponse instanceof String) {
                                        String responseMessage = (String) serverResponse;
                                        // Show the response in a popup message
                                        JOptionPane.showMessageDialog(
                                                null,
                                                responseMessage,
                                                "Server Response",
                                                JOptionPane.INFORMATION_MESSAGE
                                        );

                                        fetchInvoices();
                                    } else {
                                        // If not a String, show a generic message
                                        JOptionPane.showMessageDialog(null, "Unexpected response from server.", "Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                } catch (Exception ex) {
                                    logger.error("Error occurred during server communication.", ex);
                                    JOptionPane.showMessageDialog(
                                            null,
                                            "An error occurred while communicating with the server.",
                                            "Error",
                                            JOptionPane.ERROR_MESSAGE
                                    );
                                }
                            }
                        } catch (NumberFormatException ex) {
                            logger.error("Invalid invoice ID format.", ex);
                            JOptionPane.showMessageDialog(null, "Invalid invoice ID format.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "No invoice selected.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select an invoice from the table.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invoice table is not initialized.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return button;
    }

    private void fetchInvoices() {
        try {
            // Determine the filter for Pending, Paid, or All invoices
            String filter = "ALL"; // Default filter
            if (pendingRadio.isSelected()) {
                filter = "PENDING";
            } else if (paidRadio.isSelected()) {
                filter = "PAID";
            }

            // Request to show all invoices from the server
            LoginPage.client.sendAction("INVOICE SHOWALL");

         // Fetch the list of invoices from the server
            List<Invoice> invoices = LoginPage.client.getAllInvoices();

            // Check if the list is empty before calling the updateTable() method
            if (invoices != null && !invoices.isEmpty()) {
                // Update the table with the invoices
                updateTable(invoices, filter);
            }

        } catch (Exception e) {
            logger.error("Failed to load invoices.", e);
            JOptionPane.showMessageDialog(this, "Failed to load invoices.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Invoice> invoices, String filter) {
        DefaultTableModel model = (DefaultTableModel) invoiceTable.getModel();
        model.setRowCount(0); // Clear existing table rows

        // Filter and add rows to the table
        for (Invoice invoice : invoices) {
            if ("ALL".equalsIgnoreCase(filter) || invoice.getStatus().equalsIgnoreCase(filter)) {
                model.addRow(new Object[]{
                        invoice.getInvoiceId(),
                        invoice.getClientName(),
                        invoice.getUserId(),
                        invoice.getBooking(),
                        invoice.getStatus(),
                        invoice.getPrice(),
                        invoice.getTax(),
                        invoice.getTotal()
                });
            }
        }
    }

    private void showInvoiceDetails(int invoiceId) {
        try {
            // Request the invoice details from the server
            Object response = LoginPage.client.readInvoice(invoiceId);

            if (response instanceof Invoice) {
                Invoice invoice = (Invoice) response;
                JFrame detailsFrame = new JFrame("Invoice #" + invoiceId);
                detailsFrame.setSize(400, 400);
                detailsFrame.setLayout(new BorderLayout());
                detailsFrame.setLocationRelativeTo(this);

                JPanel contentPanel = new JPanel(new GridLayout(0, 1, 5, 5));
                contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

                contentPanel.add(new JLabel("Client Name: " + invoice.getClientName()));
                contentPanel.add(new JLabel("User ID: " + invoice.getUserId()));
                contentPanel.add(new JLabel("Booking ID: " + invoice.getBooking().getBookingId()));
                contentPanel.add(new JLabel("Status: " + invoice.getStatus()));
                contentPanel.add(new JLabel("Price: " + invoice.getPrice()));
                contentPanel.add(new JLabel("Tax: " + invoice.getTax()));
                contentPanel.add(new JLabel("Total: " + invoice.getTotal()));

                JButton printButton = new JButton("🖨️ Print Invoice");
                printButton.setFocusPainted(false);
                printButton.setBackground(new Color(200, 230, 255));

                printButton.addActionListener(ev -> {
                    try {
                        // Open a file chooser for the user to select the file path to save the invoice
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setDialogTitle("Save Invoice PDF");
                        fileChooser.setSelectedFile(new File("invoice_" + invoiceId + ".pdf"));

                        
                        int userSelection = fileChooser.showSaveDialog(detailsFrame);
                        if (userSelection == JFileChooser.APPROVE_OPTION) {
                            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                            // Send the invoiceId and filePath using the new method
                            LoginPage.client.sendAction("INVOICE EXPORT");
                            LoginPage.client.sendInvoiceExportRequest(invoiceId, filePath);
                            
                            Object serverResponse = LoginPage.client.receiveObject();
                            if (serverResponse instanceof String) {
                                JOptionPane.showMessageDialog(detailsFrame, (String) serverResponse, "Print Result", JOptionPane.INFORMATION_MESSAGE);
                                logger.info("Invoice #" + invoiceId + " saved to " + filePath);
                                
                            } else {
                                JOptionPane.showMessageDialog(detailsFrame, "Unexpected server response.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            
                             }
                    } catch (Exception ex) {
                        logger.error("Error occurred while generating the invoice PDF.", ex);
                    }
                });

                contentPanel.add(printButton);
                detailsFrame.add(contentPanel, BorderLayout.CENTER);
                detailsFrame.setVisible(true);
            }
        } catch (Exception e) {
            logger.error("Failed to fetch invoice details.", e);
            JOptionPane.showMessageDialog(this, "Failed to fetch invoice details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
