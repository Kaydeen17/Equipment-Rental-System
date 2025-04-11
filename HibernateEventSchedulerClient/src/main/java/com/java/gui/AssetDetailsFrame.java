package com.java.gui;

import javax.swing.*;
import com.java.domain.Asset;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AssetDetailsFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(AssetDetailsFrame.class);

    private JTextField nameTxt, serialTxt, categoryTxt, priceTxt;
    private JLabel assetIdLbl, bookingIdLbl, statusLbl;
    private Asset asset;

    public AssetDetailsFrame(JFrame windowAncestor, int assetID) {
        setTitle("Asset Details");
        setSize(500, 400);
        setLocationRelativeTo(windowAncestor);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
        add(panel);

        // Get the asset object from the server
        try {
            // Log current client trying to fetch asset
            logger.info(LoginPage.client.getCurrentClient() + " is fetching asset details for ID: " + assetID);
            LoginPage.client.sendAction("ASSET READ"); 
            Object obj = LoginPage.client.sendID(assetID);
            if (obj instanceof Asset) {
                asset = (Asset) obj;
                logger.info(LoginPage.client.getCurrentClient() + " successfully retrieved asset: " + asset.getName());
            } else {
                JOptionPane.showMessageDialog(this, "Failed to retrieve asset.");
                dispose();
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            logger.error(LoginPage.client.getCurrentClient() + " encountered an error fetching asset details for ID " + assetID, e);
            dispose();
            return;
        }

        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font valueFont = new Font("Arial", Font.PLAIN, 14);

        int labelX = 50;
        int fieldX = 200;
        int width = 220;
        int height = 30;
        int y = 30;

        // Asset ID
        JLabel idLabel = new JLabel("Asset ID:");
        idLabel.setBounds(labelX, y, 150, height);
        idLabel.setFont(labelFont);
        panel.add(idLabel);

        assetIdLbl = new JLabel(String.valueOf(asset.getAssetId()));
        assetIdLbl.setBounds(fieldX, y, width, height);
        assetIdLbl.setFont(valueFont);
        panel.add(assetIdLbl);
        y += 40;

        // Serial Number
        JLabel serialLabel = new JLabel("Serial Number:");
        serialLabel.setBounds(labelX, y, 150, height);
        serialLabel.setFont(labelFont);
        panel.add(serialLabel);

        serialTxt = new JTextField(asset.getSerialNumber());
        serialTxt.setBounds(fieldX, y, width, height);
        panel.add(serialTxt);
        y += 40;

        // Name
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(labelX, y, 150, height);
        nameLabel.setFont(labelFont);
        panel.add(nameLabel);

        nameTxt = new JTextField(asset.getName());
        nameTxt.setBounds(fieldX, y, width, height);
        panel.add(nameTxt);
        y += 40;

        // Category
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setBounds(labelX, y, 150, height);
        categoryLabel.setFont(labelFont);
        panel.add(categoryLabel);

        categoryTxt = new JTextField(asset.getCategory());
        categoryTxt.setBounds(fieldX, y, width, height);
        panel.add(categoryTxt);
        y += 40;

        // Price Per Day
        JLabel priceLabel = new JLabel("Price Per Day:");
        priceLabel.setBounds(labelX, y, 150, height);
        priceLabel.setFont(labelFont);
        panel.add(priceLabel);

        priceTxt = new JTextField(String.valueOf(asset.getPricePerDay()));
        priceTxt.setBounds(fieldX, y, width, height);
        panel.add(priceTxt);
        y += 40;

        // Booking ID
        JLabel bookingLabel = new JLabel("Booking ID:");
        bookingLabel.setBounds(labelX, y, 150, height);
        bookingLabel.setFont(labelFont);
        panel.add(bookingLabel);

        bookingIdLbl = new JLabel(asset.getBookingId() != null ? asset.getBookingId().toString() : "None");
        bookingIdLbl.setBounds(fieldX, y, width, height);
        bookingIdLbl.setFont(valueFont);
        panel.add(bookingIdLbl);
        y += 40;

        // Status
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setBounds(labelX, y, 150, height);
        statusLabel.setFont(labelFont);
        panel.add(statusLabel);

        statusLbl = new JLabel(asset.getStatus().toString());
        statusLbl.setBounds(fieldX, y, width, height);
        statusLbl.setFont(valueFont);
        panel.add(statusLbl);
        y += 50;

        // Save Changes Button
        JButton saveButton = createModernButton("Save Changes");
        saveButton.setBounds(150, y, 200, 40);
        panel.add(saveButton);

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    asset.setName(nameTxt.getText());
                    asset.setSerialNumber(serialTxt.getText());
                    asset.setCategory(categoryTxt.getText());
                    asset.setPricePerDay(Float.parseFloat(priceTxt.getText()));

                    // Log current client performing the update
                    logger.info(LoginPage.client.getCurrentClient() + " is updating asset: " + asset.getName());
                    LoginPage.client.sendAction("ASSET UPDATE");
                    LoginPage.client.sendAsset(asset);

                    Object response = LoginPage.client.receiveObject();
                    String result = (response instanceof String) ? (String) response : "Unknown response";

                    if (result.toLowerCase().contains("success")) {
                        JOptionPane.showMessageDialog(null, "Asset updated successfully!");
                        logger.info(LoginPage.client.getCurrentClient() + " successfully updated asset: " + asset.getName());
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "Update failed: " + result);
                        logger.warn(LoginPage.client.getCurrentClient() + " update failed for asset: " + asset.getName() + " - " + result);
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid number for price.");
                    logger.error(LoginPage.client.getCurrentClient() + " entered an invalid number for price: " + priceTxt.getText(), ex);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error updating asset: " + ex.getMessage());
                    logger.error(LoginPage.client.getCurrentClient() + " encountered an error updating asset: " + asset.getName(), ex);
                }
            }
        });
    }

    private JButton createModernButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(0, 123, 255));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        return button;
    }
}
