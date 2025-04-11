package com.java.gui;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

import com.java.domain.Asset;
import com.java.domain.Booking;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BookingDetailsFrame extends JDialog {
    private static final long serialVersionUID = 1L;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final Logger logger = LogManager.getLogger(BookingDetailsFrame.class);

    public BookingDetailsFrame(JFrame parent, int bookingID) {
        super(parent, "Booking Details - ID: " + bookingID, true);
        setSize(700, 500); // Increased size for better spacing
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // Main content panel using BoxLayout for cleaner structure
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("Booking Details - ID: " + bookingID);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);

        // Define fields with labels and dynamically updated values
        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        fieldsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel statusLabel = new JLabel("Loading...");
        JLabel clientLabel = new JLabel("Loading...");
        JLabel userLabel = new JLabel("Loading...");
        JLabel bookDateLabel = new JLabel("Loading...");
        JLabel returnDateLabel = new JLabel("Loading...");
        JLabel invoiceLabel = new JLabel("Loading...");
        JLabel isLateLabel = new JLabel("Loading...");

        addField(fieldsPanel, "User ID:", userLabel);
        addField(fieldsPanel, "Booking Date:", bookDateLabel);
        addField(fieldsPanel, "Return Date:", returnDateLabel);
        addField(fieldsPanel, "Invoice ID:", invoiceLabel);
        addField(fieldsPanel, "Status:", statusLabel);
        addField(fieldsPanel, "Delinquent:", isLateLabel);

        contentPanel.add(fieldsPanel);

        // Asset list with JList for better display and interaction
        JLabel assetsLabel = new JLabel("Assets:");
        assetsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(assetsLabel);

        JList<String> assetList = new JList<>();
        assetList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane assetScrollPane = new JScrollPane(assetList);
        contentPanel.add(assetScrollPane);

        // Buttons panel with modern styling
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = createModernButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);

        // Adding all components to the main frame
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Fetch the booking details and update the UI
        fetchBookingDetails(bookingID, statusLabel, clientLabel, userLabel, bookDateLabel, returnDateLabel, invoiceLabel, isLateLabel, assetList);

        setVisible(true);
    }

    private void addField(JPanel panel, String labelText, JLabel valueLabel) {
        panel.add(new JLabel(labelText));
        panel.add(valueLabel);
    }

    private JButton createModernButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(new Color(33, 150, 243));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    private void fetchBookingDetails(int bookingID, JLabel statusLabel, JLabel clientLabel, JLabel userLabel,
                                     JLabel bookDateLabel, JLabel returnDateLabel, JLabel invoiceLabel,
                                     JLabel isLateLabel, JList<String> assetList) {
        try {
            logger.info("Fetching details for booking ID: {}", bookingID);

            LoginPage.client.sendAction("BOOKING READ");
            Booking booking = LoginPage.client.sendBookingID(bookingID);

            if (booking == null) {
                logger.warn("No booking found with ID: {}", bookingID);
                JOptionPane.showMessageDialog(this, "No booking found with ID " + bookingID, "Not Found", JOptionPane.WARNING_MESSAGE);
                return;
            }

            userLabel.setText(booking.getUserId());
            bookDateLabel.setText(dateFormat.format(booking.getBookDate()));
            returnDateLabel.setText(dateFormat.format(booking.getReturnDate()));
            invoiceLabel.setText(booking.getInvoiceId() != null ? booking.getInvoiceId().toString() : "N/A");
            statusLabel.setText(booking.getStatus().name());
            isLateLabel.setText(booking.isLate() ? "Yes" : "No");

            // Formatting the asset list for the JList component
            List<Asset> assets = booking.getAssetList();
            DefaultListModel<String> assetListModel = new DefaultListModel<>();
            for (Asset asset : assets) {
                assetListModel.addElement("- " + asset.getAssetId());
            }
            assetList.setModel(assetListModel);

            logger.info("Booking details for ID: {} fetched successfully.", bookingID);

        } catch (Exception e) {
            logger.error("Error fetching booking details for ID: {}", bookingID, e);
            JOptionPane.showMessageDialog(this, "Failed to load booking details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
