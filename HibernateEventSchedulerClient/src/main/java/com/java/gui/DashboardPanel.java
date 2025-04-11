package com.java.gui;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header label
        JLabel headerLabel = new JLabel("Java Equipment Rental System", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28)); // Increased font size
        headerLabel.setForeground(Color.WHITE); // White color for the header text
        headerLabel.setPreferredSize(new Dimension(1200, 70)); // Increased size for header
        headerLabel.setOpaque(true);
        headerLabel.setBackground(new Color(33, 150, 243)); // Blue background for header

        // Subheading with welcome message
        JLabel welcomeLabel = new JLabel("Welcome " + LoginPage.client.getCurrentClient(), JLabel.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18)); // Smaller font for welcome message
        welcomeLabel.setForeground(Color.GRAY); // Light gray color for subheading

        // Create the panel for the dashboard content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(2, 2, 30, 30)); // Larger grid gaps
        contentPanel.setBackground(Color.WHITE);

        // Create the shapes with numbers inside JPanel
        contentPanel.add(createDashboardItem("Total Assets", "Loading..."));
        contentPanel.add(createDashboardItem("Available Assets", "Loading..."));
        contentPanel.add(createDashboardItem("Ongoing Bookings", "Loading..."));
        contentPanel.add(createDashboardItem("Completed Bookings", "Loading..."));

        // Add header, welcome message, and content panel to the main panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(headerLabel, BorderLayout.NORTH);
        topPanel.add(welcomeLabel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // Fetch all required data synchronously and update the labels
        getDashboardData();
    }

    private JPanel createDashboardItem(String title, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255)); // Light blue background for each item
        panel.setBorder(BorderFactory.createLineBorder(new Color(33, 150, 243), 2)); // Blue border

        // Title label
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24)); // Increased font size
        titleLabel.setForeground(new Color(33, 150, 243)); // Blue color for title

        // Value label now replaced with the value as the central element
        JLabel valueLabel = new JLabel(value, JLabel.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 40)); // Larger value font
        valueLabel.setForeground(new Color(0, 0, 0)); // Black color for the value
        valueLabel.setPreferredSize(new Dimension(250, 100)); // Adjusted size to center the value

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);

        panel.setPreferredSize(new Dimension(250, 150)); // Larger size for the dashboard items
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding inside each item
        return panel;
    }

    private void getDashboardData() {
        try {
            // Fetch the data synchronously
            LoginPage.client.sendAction("ASSET TOTAL");
            int totalAssets = (int) LoginPage.client.receiveObject(); // Receive total assets

            LoginPage.client.sendAction("ASSET AVAILABLE");
            int availableAssets = (int) LoginPage.client.receiveObject(); // Receive available assets

            LoginPage.client.sendAction("BOOKING ONGOING");
            int ongoingBookings = (int) LoginPage.client.receiveObject(); // Receive ongoing bookings

            LoginPage.client.sendAction("BOOKING COMPLETED");
            int completedBookings = (int) LoginPage.client.receiveObject(); // Receive completed bookings

            // Once the data is fetched, update the dashboard items
            updateDashboardItems(new int[] { totalAssets, availableAssets, ongoingBookings, completedBookings });
        } catch (Exception e) {
            updateDashboardItemsOnError();
            e.printStackTrace();
        }
    }

    private void updateDashboardItems(int[] data) {
        // Update the values in the content panel
        JPanel contentPanel = (JPanel) getComponent(1);
        Component[] components = contentPanel.getComponents();

        // Update each panel with the fetched data
        ((JLabel) ((JPanel) components[0]).getComponent(1)).setText(String.valueOf(data[0]));
        ((JLabel) ((JPanel) components[1]).getComponent(1)).setText(String.valueOf(data[1]));
        ((JLabel) ((JPanel) components[2]).getComponent(1)).setText(String.valueOf(data[2]));
        ((JLabel) ((JPanel) components[3]).getComponent(1)).setText(String.valueOf(data[3]));
    }

    private void updateDashboardItemsOnError() {
        // Update the values on error
        JPanel contentPanel = (JPanel) getComponent(1);
        Component[] components = contentPanel.getComponents();

        // Set error text
        ((JLabel) ((JPanel) components[0]).getComponent(1)).setText("Failed to load data.");
        ((JLabel) ((JPanel) components[1]).getComponent(1)).setText("Failed to load data.");
        ((JLabel) ((JPanel) components[2]).getComponent(1)).setText("Failed to load data.");
        ((JLabel) ((JPanel) components[3]).getComponent(1)).setText("Failed to load data.");
    }
}
