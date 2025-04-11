package com.java.gui;

import javax.swing.*;
import com.java.domain.Asset;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewAssetFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(NewAssetFrame.class);

    private JTextField nameTxt;
    private JTextField categoryTxt;
    private JTextField priceTxt;
    private JTextField serialNumberTxt;

    public NewAssetFrame() {
        setTitle("Create New Asset");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel createPanel = new JPanel();
        createPanel.setLayout(null);
        createPanel.setBackground(Color.WHITE);
        add(createPanel);

        // Labels
        JLabel nameLabel = new JLabel("Name:");
        JLabel categoryLabel = new JLabel("Category:");
        JLabel priceLabel = new JLabel("Price Per Day:");
        JLabel serialLabel = new JLabel("Serial Number:");

        Font labelFont = new Font("Arial", Font.BOLD, 16);
        nameLabel.setFont(labelFont);
        categoryLabel.setFont(labelFont);
        priceLabel.setFont(labelFont);
        serialLabel.setFont(labelFont);

        // Text fields
        nameTxt = new JTextField(20);
        categoryTxt = new JTextField(20);
        priceTxt = new JTextField(20);
        serialNumberTxt = new JTextField(20);

        // Save button
        JButton saveButton = createModernButton("Save Asset");
        saveButton.setBounds(210, 260, 180, 40); // Centered horizontally

        // Centering positions
        int fieldWidth = 250;
        int fieldHeight = 35;
        int labelX = 100;
        int fieldX = 250;

        // Label and field bounds
        nameLabel.setBounds(labelX, 30, 150, 30);
        nameTxt.setBounds(fieldX, 30, fieldWidth, fieldHeight);

        categoryLabel.setBounds(labelX, 80, 150, 30);
        categoryTxt.setBounds(fieldX, 80, fieldWidth, fieldHeight);

        priceLabel.setBounds(labelX, 130, 150, 30);
        priceTxt.setBounds(fieldX, 130, fieldWidth, fieldHeight);

        serialLabel.setBounds(labelX, 180, 150, 30);
        serialNumberTxt.setBounds(fieldX, 180, fieldWidth, fieldHeight);

        // Add components
        createPanel.add(nameLabel);
        createPanel.add(nameTxt);
        createPanel.add(categoryLabel);
        createPanel.add(categoryTxt);
        createPanel.add(priceLabel);
        createPanel.add(priceTxt);
        createPanel.add(serialLabel);
        createPanel.add(serialNumberTxt);
        createPanel.add(saveButton);

        // Button action
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    // Gather input
                    String name = nameTxt.getText();
                    String category = categoryTxt.getText();
                    String serialNum = serialNumberTxt.getText();
                    Float price = Float.parseFloat(priceTxt.getText());

                    // Create Asset
                    Asset asset = new Asset(name, category, price, serialNum);
                    logger.info("Creating asset: {}", asset);

                    // Send to server
                    LoginPage.client.sendAction("ASSET CREATE");
                    LoginPage.client.sendAsset(asset);

                    // Handle response
                    Object responseObj = LoginPage.client.receiveObject();
                    String response = (responseObj instanceof String) ? (String) responseObj : "Unknown response";

                    if (response.startsWith("Asset created successfully")) {
                        logger.info("Asset created successfully: {}", asset);
                        JOptionPane.showMessageDialog(null, "Asset saved successfully!");
                        // Clear fields
                        nameTxt.setText("");
                        categoryTxt.setText("");
                        priceTxt.setText("");
                        serialNumberTxt.setText("");
                    } else {
                        logger.warn("Failed to create asset: {}", response);
                        JOptionPane.showMessageDialog(null, "Failed to save asset. Server error: " + response);
                    }

                } catch (NumberFormatException ex) {
                    logger.error("Invalid price entered. Please enter a valid number.", ex);
                    JOptionPane.showMessageDialog(null, "Please enter a valid number for price.");
                } catch (Exception ex) {
                    logger.error("An error occurred while creating asset", ex);
                    JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                }
            }
        });
    }

    private JButton createModernButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(30, 144, 255));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        return button;
    }
}
