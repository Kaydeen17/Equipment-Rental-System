package com.applictation.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserDashboard extends JInternalFrame {

    private static final long serialVersionUID = 1L;

    public UserDashboard() {
        super("Classes", true, true, true, true); // Title, resizable, closable, maximizable, iconifiable
        setTitle("JAVA Ent. Equipment Rental Ltd.");
        setSize(700, 600);

        // Set a layout manager for the content panel
        setLayout(new BorderLayout(10, 10)); // Add spacing between components

        // Gradient background for the panel
        JPanel gradientPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(135, 206, 250), 0, getHeight(), new Color(255, 140, 0)); // Light Blue to Orange Gradient
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        gradientPanel.setLayout(new BorderLayout(10, 10));
        setContentPane(gradientPanel);

        // Title label
        JLabel titleLabel = new JLabel("JAVA Ent. Equipment Rental Ltd.", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        add(titleLabel, BorderLayout.NORTH);

        // Create a panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 2, 10, 10)); // 3 rows, 2 columns, with spacing
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding

        // Create buttons
        JButton homeButton = createButton("Home (Dashboard)");
        JButton assetsButton = createButton("Assets");
        JButton bookingsButton = createButton("Bookings");
        JButton invoicesButton = createButton("Invoices");
        JButton accManagementButton = createButton("Account Management");
        JButton reportingButton = createButton("Reporting");

        // Add buttons to the panel
        buttonPanel.add(homeButton);
        buttonPanel.add(assetsButton);
        buttonPanel.add(bookingsButton);
        buttonPanel.add(accManagementButton);
        buttonPanel.add(invoicesButton);
        buttonPanel.add(reportingButton);

        // Add action listener to the Assets button as a placeholder
        assetsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AssetsView assetsView = new AssetsView();

                // Make sure the AssetsView is visible
                assetsView.setVisible(true);

                // Print a confirmation to the console
                System.out.println("AssetsView GUI opened");
            }
        });

        bookingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        // Add the button panel to the center
        add(buttonPanel, BorderLayout.CENTER);

        // Add a welcome message label at the bottom
        JLabel welcomeLabel = new JLabel("Welcome, User! Limited access.", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Verdana", Font.ITALIC, 16));
        welcomeLabel.setForeground(Color.BLACK);
        add(welcomeLabel, BorderLayout.SOUTH);

        // Ensure the internal frame is visible
        setVisible(true);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(100, 149, 237)); // Cornflower blue
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Times New Roman", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        return button;
    }
}
