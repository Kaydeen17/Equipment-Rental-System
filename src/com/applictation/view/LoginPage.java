package com.applictation.view;

import com.application.client.Client;

import javax.swing.*;
import java.awt.*;

public class LoginPage extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField emailField;
    private JPasswordField passwordField;

    public LoginPage() {
        setTitle("Client Login");
        setSize(400, 250); // Increased size for better spacing
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout()); // Use GridBagLayout for better alignment

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding

        // Title Label
        JLabel titleLabel = new JLabel("Login to Your Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // Email Label
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(new JLabel("Username:"), gbc);

        // Email Field
        emailField = new JTextField(15);
        gbc.gridx = 1;
        add(emailField, gbc);

        // Password Label
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Password:"), gbc);

        // Password Field
        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        add(passwordField, gbc);

        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 30));
        loginButton.setFocusPainted(false);
        loginButton.setBackground(new Color(0, 102, 204));
        loginButton.setForeground(Color.WHITE);

        // Button Action
        loginButton.addActionListener(e -> {
            String username = emailField.getText();
            String password = new String(passwordField.getPassword());

            // Perform login to the server and check result
            new Thread(() -> {
                boolean isLoggedIn = Client.loginToServer(username, password);

                SwingUtilities.invokeLater(() -> {
                    if (isLoggedIn == true) {
                        JOptionPane.showMessageDialog(null, "Login Successful!");
                        dispose(); // Close the login window

                        // add logic to open the dashboard based on the user role here
                        // For now, it opens the user dashboard
                        new UserDashboard().setVisible(true); 
                    } else if(isLoggedIn == false){
                        JOptionPane.showMessageDialog(null, "Invalid credentials, try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
            }).start(); // Run login process in a separate thread to avoid UI freeze
        });

        // Add Button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(loginButton, gbc);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}

