package com.java.gui;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class ReportPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private JDateChooser startDateChooser;
    private JDateChooser endDateChooser;
    private JButton generateButton;

    public ReportPanel() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Generate Report");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));  // Centered layout for components
        startDateChooser = new JDateChooser();
        endDateChooser = new JDateChooser();
        generateButton = createActionButton("Generate Report");

        filterPanel.add(titleLabel);
        filterPanel.add(new JLabel("From:"));
        filterPanel.add(startDateChooser);
        filterPanel.add(new JLabel("To:"));
        filterPanel.add(endDateChooser);
        filterPanel.add(generateButton);

        add(filterPanel, BorderLayout.CENTER);  // Add the filterPanel to the center

        generateButton.addActionListener(this::generateReport);
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(160, 40));
        button.setFocusPainted(false);
        button.setBackground(new Color(33, 150, 243));
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(33, 150, 243), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(25, 118, 210));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(33, 150, 243));
            }
        });

        return button;
    }

    private void generateReport(ActionEvent event) {
        java.util.Date utilStartDate = startDateChooser.getDate();
        java.util.Date utilEndDate = endDateChooser.getDate();

        if (utilStartDate == null || utilEndDate == null) {
            JOptionPane.showMessageDialog(this, "Please select both start and end dates.", "Missing Dates", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (utilStartDate.after(utilEndDate)) {
            JOptionPane.showMessageDialog(this, "Start date must be before end date.", "Invalid Dates", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to generate the report for this date range?",
                "Confirm Report Generation",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // Open file chooser to get file path
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Save Location for Report");
            fileChooser.setSelectedFile(new File("invoice_report.pdf"));

            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection != JFileChooser.APPROVE_OPTION) {
                return; // User cancelled, no file path selected
            }

            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();

            try {
                // Send action to generate report and file path to the server
                LoginPage.client.sendAction("INVOICE REPORT");
                LoginPage.client.sendObject(new Object[] {
                    new java.sql.Date(utilStartDate.getTime()), 
                    new java.sql.Date(utilEndDate.getTime()),
                    filePath // Send the file path to the server
                });

                Object response = LoginPage.client.receiveObject();

                if (response instanceof String) {
                    JOptionPane.showMessageDialog(this, (String) response, "Report Result", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Unexpected response from server.", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to send report request: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
