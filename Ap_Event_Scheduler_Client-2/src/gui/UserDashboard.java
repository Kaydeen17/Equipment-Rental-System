package gui;


import javax.swing.*;


import java.awt.*;

public class UserDashboard extends JFrame {

    private static final long serialVersionUID = 1L;

    public UserDashboard() {
    	
        setTitle("JAVA Ent. Equipment Rental Ltd.");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set a layout manager for the content panel
        setLayout(new BorderLayout());

        // Title label
        JLabel titleLabel = new JLabel("JAVA Ent. Equipment Rental Ltd.", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        // Create a panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2, 10, 10));  // 2 rows, 2 columns, with spacing

        // Create buttons
        JButton homeButton = new JButton("Home (Dashboard)");
        JButton assetsButton = new JButton("Assets");
        JButton bookingsButton = new JButton("Bookings & Invoices");
        JButton accManagementButton = new JButton("Account Management");
       
        // Add buttons to the panel
        buttonPanel.add(homeButton);
        buttonPanel.add(assetsButton);
        buttonPanel.add(bookingsButton);
        buttonPanel.add(accManagementButton);

        // Add action listener to the Assets button to open AssetsView
        assetsButton.addActionListener(e -> {
            // Open AssetsView when clicked
            new AssetsView().setVisible(true);
            dispose();  // Close the User Dashboard after opening AssetsView
        });
        
        // Add the button panel to the center
        add(buttonPanel, BorderLayout.CENTER);

        // Add a welcome message label at the bottom
        JLabel welcomeLabel = new JLabel("Welcome, User! Limited access.", SwingConstants.CENTER);
        add(welcomeLabel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserDashboard().setVisible(true));
    }
}
