package com.java.gui;

import com.java.domain.Asset;
import com.java.domain.Booking;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewBookingFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(NewBookingFrame.class);

    private JTextField clientNameField;
    private JTextField clientContactField;
    private JDateChooser returnDateChooser;
    private JPanel checkboxPanel;
    private List<JCheckBox> assetCheckBoxes = new ArrayList<>();
    private List<Asset> availableAssets = new ArrayList<>();
    private JButton createBookingButton;
    private JLabel statusLabel;

    public NewBookingFrame() {
        setTitle("Create New Booking");
        setSize(650, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Root panel
        JPanel rootPanel = new JPanel(new BorderLayout(15, 15));
        rootPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        setContentPane(rootPanel);

        // Title
        JLabel titleLabel = new JLabel("Create New Booking", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        rootPanel.add(titleLabel, BorderLayout.NORTH);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(33, 150, 243)); // Same as button
        titleLabel.setForeground(Color.WHITE);

        // New Form Panel with 2 columns
        JPanel formPanel = new JPanel(new BorderLayout(20, 0));
        rootPanel.add(formPanel, BorderLayout.CENTER);

        // ===== LEFT SIDE FORM =====
        JPanel formPanelLeft = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanelLeft.add(new JLabel("Client Name:"), gbc);

        gbc.gridx = 1;
        clientNameField = new JTextField(12);
        formPanelLeft.add(clientNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanelLeft.add(new JLabel("Client Contact:"), gbc);

        gbc.gridx = 1;
        clientContactField = new JTextField(12);
        formPanelLeft.add(clientContactField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanelLeft.add(new JLabel("Return Date:"), gbc);

        gbc.gridx = 1;
        returnDateChooser = new JDateChooser();
        returnDateChooser.setDateFormatString("yyyy-MM-dd");
        formPanelLeft.add(returnDateChooser, gbc);

        formPanel.add(formPanelLeft, BorderLayout.WEST);

        // ===== RIGHT SIDE ASSET LIST =====
        JPanel formPanelRight = new JPanel(new BorderLayout(5, 5));
        formPanelRight.add(new JLabel("Select Assets:"), BorderLayout.NORTH);

        checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
        checkboxPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JScrollPane scrollPane = new JScrollPane(checkboxPanel);
        scrollPane.setPreferredSize(new Dimension(400, 250));

        formPanelRight.add(scrollPane, BorderLayout.CENTER);
        formPanel.add(formPanelRight, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new BorderLayout());

        statusLabel = new JLabel("Please fill in all details", JLabel.CENTER);
        statusLabel.setForeground(Color.RED);
        bottomPanel.add(statusLabel, BorderLayout.NORTH);

        createBookingButton = createModernButton("Create Booking");
        bottomPanel.add(createBookingButton, BorderLayout.SOUTH);

        rootPanel.add(bottomPanel, BorderLayout.SOUTH);

        loadAssets();

        // Action listener
        createBookingButton.addActionListener(this::createNewBooking);
    }

    private void loadAssets() {
        try {
            LoginPage.client.sendAction("ASSET SHOWALL");
            List<Asset> allAssets = (List<Asset>) LoginPage.client.getAssetList();

            availableAssets.clear();
            assetCheckBoxes.clear();
            checkboxPanel.removeAll();
            checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));

            // Header row
            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            headerPanel.setBackground(new Color(220, 220, 220));
            headerPanel.add(fixedWidthLabel("Select", 60, true));
            headerPanel.add(fixedWidthLabel("ID", 50, true));
            headerPanel.add(fixedWidthLabel("Name", 120, true));
            headerPanel.add(fixedWidthLabel("Category", 100, true));
            headerPanel.add(fixedWidthLabel("Price/Day", 80, true));
            checkboxPanel.add(headerPanel);

            for (Asset asset : allAssets) {
                if (asset.getStatus() == Asset.Status.AVAILABLE) {
                    availableAssets.add(asset);

                    JCheckBox checkBox = new JCheckBox();
                    assetCheckBoxes.add(checkBox);

                    JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    rowPanel.add(fixedWidthComponent(checkBox, 60));
                    rowPanel.add(fixedWidthLabel(String.valueOf(asset.getAssetId()), 50, false));
                    rowPanel.add(fixedWidthLabel(asset.getName(), 120, false));
                    rowPanel.add(fixedWidthLabel(asset.getCategory(), 100, false));
                    rowPanel.add(fixedWidthLabel(String.format("$%.2f", asset.getPricePerDay()), 80, false));

                    checkboxPanel.add(rowPanel);
                }
            }

            checkboxPanel.revalidate();
            checkboxPanel.repaint();

        } catch (Exception e) {
            logger.error("Failed to load assets from server.", e);
            JOptionPane.showMessageDialog(this, "Failed to load assets from server.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel fixedWidthLabel(String text, int width, boolean isHeader) {
        JLabel label = new JLabel(text);
        label.setPreferredSize(new Dimension(width, 25));
        label.setFont(isHeader ? new Font("Segoe UI", Font.BOLD, 13) : new Font("Segoe UI", Font.PLAIN, 12));
        return label;
    }

    private JComponent fixedWidthComponent(JComponent comp, int width) {
        comp.setPreferredSize(new Dimension(width, 25));
        return comp;
    }

    private void createNewBooking(ActionEvent e) {
        String clientName = clientNameField.getText().trim();
        String clientContact = clientContactField.getText().trim();
        Date returnDate = returnDateChooser.getDate();
        List<Asset> selectedAssets = new ArrayList<>();

        for (int i = 0; i < assetCheckBoxes.size(); i++) {
            if (assetCheckBoxes.get(i).isSelected()) {
                selectedAssets.add(availableAssets.get(i));
            }
        }

        if (clientName.isEmpty() || clientContact.isEmpty() || returnDate == null || selectedAssets.isEmpty()) {
            statusLabel.setText("Please fill in all fields and select at least one asset.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        try {
            // Create a Booking object
            Booking booking = new Booking();
            booking.setUserId(LoginPage.client.getCurrentClient());
            booking.setClientName(clientName);
            booking.setClientContact(clientContact);
            booking.setBookDate(new Date()); // Today
            booking.setReturnDate(returnDate);
            booking.setAssetList(selectedAssets);
            booking.setStatus(Booking.Status.ONGOING);

            // Log the booking creation attempt
            logger.info("Attempting to create booking for client: {} with {} assets.", clientName, selectedAssets.size());

            LoginPage.client.sendAction("BOOKING CREATE");
            LoginPage.client.sendBooking(booking);
            String success = (String) LoginPage.client.receiveObject();

            if ("Booking created successfully.".equals(success)) {
                logger.info("Booking created successfully for client: {}", clientName);
                JOptionPane.showMessageDialog(this, "Booking created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                this.dispose(); // Close the form
            } else {
                logger.warn("Failed to create booking for client: {}. Response: {}", clientName, success);
                JOptionPane.showMessageDialog(this, "Failed to create booking.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            logger.error("Error creating booking for client: {}", clientName, ex);
            JOptionPane.showMessageDialog(this, "Error communicating with server.", "Server Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton createModernButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(33, 150, 243));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }
}
