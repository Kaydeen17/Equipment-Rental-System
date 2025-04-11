package com.java.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import com.java.client.Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoginPage extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(LoginPage.class);

    private JTextField emailField;
    private JPasswordField passwordField;
    String username = null;
    String password = null;
    static Client client;

    public LoginPage() {
        setTitle("Client Login");
        setSize(500, 400); // Increased size for better accessibility
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(245, 245, 245)); // Soft background color

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        // Heading Label (Added "Java Equipment Rental System")
        JLabel headingLabel = new JLabel("Java Equipment Rental System");
        headingLabel.setFont(new Font("Segoe UI", Font.BOLD, 22)); // Larger font size
        headingLabel.setForeground(new Color(0, 122, 204)); // Blue color for heading
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(headingLabel, gbc);

        // Title Label (Modern font, larger size)
        JLabel titleLabel = new JLabel("Login to Your Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(50, 50, 50));
        gbc.gridy = 1;
        add(titleLabel, gbc);

        // Email Label
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        JLabel emailLabel = new JLabel("Username:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Increased font size
        emailLabel.setForeground(new Color(50, 50, 50));
        add(emailLabel, gbc);

        // Email Field
        emailField = new JTextField(20); // Larger text field
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Increased font size
        emailField.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(200, 200, 200)));
        gbc.gridx = 1;
        add(emailField, gbc);

        // Password Label
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Increased font size
        passwordLabel.setForeground(new Color(50, 50, 50));
        add(passwordLabel, gbc);

        // Password Field
        passwordField = new JPasswordField(20); // Larger text field
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Increased font size
        passwordField.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(200, 200, 200)));
        gbc.gridx = 1;
        add(passwordField, gbc);

        // Login Button (Modern flat button style)
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(140, 45)); // Larger button
        loginButton.setFocusPainted(false);
        loginButton.setBackground(new Color(0, 122, 204)); // Modern blue
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Increased font size
        loginButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        loginButton.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));

        loginButton.addActionListener(e -> {
            new Thread(() -> {
                try {
                    username = emailField.getText();
                    password = new String(passwordField.getPassword());
                    client = new Client();

                    logger.info("Attempting login for user: {}", username);

                    // Call the method to login to the server
                    boolean isLoggedIn = client.loginToServer(username, password);

                    if (isLoggedIn) {
                        logger.info("Login successful for user: {}", username);
                        JOptionPane.showMessageDialog(null, "Login Successful!");
                        dispose(); // Close the login window
                        new UserDashboard().setVisible(true); // Open the user dashboard
                    } else {
                        logger.warn("Invalid credentials for user: {}", username);
                        JOptionPane.showMessageDialog(null, "Invalid credentials, try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (Exception ex) {
                    // Handle any exceptions during login
                    logger.error("An error occurred during login", ex);
                    loginButton.setEnabled(true); // Enable the button if an error occurs
                    JOptionPane.showMessageDialog(null, "An error occurred during login: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }).start();
        });

        // Add Button to layout
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(loginButton, gbc);
    }

    public String getUsername() {
        return username;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}
