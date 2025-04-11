package com.java.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserDashboard extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel leftPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Icons for the buttons
    private ImageIcon dashboardIcon;
    private ImageIcon bookingsIcon;
    private ImageIcon assetsIcon;
    private ImageIcon customersIcon;
    private ImageIcon invoicesIcon;
    private ImageIcon staffIcon;
    private ImageIcon reportsIcon;
    private ImageIcon logoutIcon;

    public UserDashboard() {
        setTitle("User Dashboard");
        setSize(1200, 800); // Increased size for better visibility
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load and scale icons for buttons
        loadAndScaleIcons();

        // Initialize layout manager for content area
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Initialize panels for each content area
        JPanel dashboardPanel = createDashboardPanel();
        JPanel bookingsPanel = createBookingsPanel();
        JPanel assetsPanel = createAssetsPanel();
        JPanel customersPanel = createCustomersPanel();
        JPanel invoicesPanel = createInvoicesPanel();
        JPanel staffPanel = createStaffPanel();
        JPanel reportsPanel = createReportsPanel();

        // Add content panels to the content panel
        contentPanel.add(dashboardPanel, "Dashboard");
        contentPanel.add(bookingsPanel, "Bookings");
        contentPanel.add(assetsPanel, "Assets");
        contentPanel.add(customersPanel, "Customers");
        contentPanel.add(invoicesPanel, "Invoices");
        contentPanel.add(staffPanel, "Staff");
        contentPanel.add(reportsPanel, "Reports");

        // Create the left navigation panel
        leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(33, 150, 243)); // Blue background

        // Create buttons for the left panel using the modern button style
        JButton dashboardButton = createModernButton("Dashboard", "Dashboard", dashboardIcon);
        JButton bookingsButton = createModernButton("Bookings", "Bookings", bookingsIcon);
        JButton assetsButton = createModernButton("Assets", "Assets", assetsIcon);
        JButton customersButton = createModernButton("Customers", "Customers", customersIcon);
        JButton invoicesButton = createModernButton("Invoices", "Invoices", invoicesIcon);
        JButton staffButton = createModernButton("Staff", "Staff", staffIcon);
        JButton reportsButton = createModernButton("Reports", "Reports", reportsIcon);
        JButton logoutButton = createModernButton("Logout", "Logout", logoutIcon);

        // Add ActionListener for the logout action
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show confirmation dialog
                int option = JOptionPane.showConfirmDialog(
                    UserDashboard.this,
                    "Are you sure you want to log out?", // Message
                    "Log Out Confirmation", // Dialog title
                    JOptionPane.YES_NO_OPTION, // Options (Yes and No)
                    JOptionPane.QUESTION_MESSAGE // Message type
                );

                if (option == JOptionPane.YES_OPTION) {
                    LoginPage.client.sendAction("LOGOUT");

                    dispose();
                }
            }
        });

        // Add buttons to the left panel
        leftPanel.add(dashboardButton);
        leftPanel.add(bookingsButton);
        leftPanel.add(assetsButton);
        leftPanel.add(customersButton);
        leftPanel.add(invoicesButton);
        leftPanel.add(staffButton);
        leftPanel.add(reportsButton);
        leftPanel.add(logoutButton);

        // Layout setup
        setLayout(new BorderLayout());
        add(leftPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void loadAndScaleIcons() {
        int iconSize = 14;
        
        dashboardIcon = loadImageIcon("/res/dashboard_icon.png");
        bookingsIcon = loadImageIcon("/res/bookings_icon.png");
        assetsIcon = loadImageIcon("/res/assets_icon.png");
        customersIcon = loadImageIcon("/res/customers_staff_icon.png");
        invoicesIcon = loadImageIcon("/res/invoices_icon.png");
        staffIcon = loadImageIcon("/res/customers_staff_icon.png");
        reportsIcon = loadImageIcon("/res/reports_icon.png");
        logoutIcon = loadImageIcon("/res/logout_icon.png");

        // Resize icons
        dashboardIcon = new ImageIcon(dashboardIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH));
        bookingsIcon = new ImageIcon(bookingsIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH));
        assetsIcon = new ImageIcon(assetsIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH));
        customersIcon = new ImageIcon(customersIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH));
        invoicesIcon = new ImageIcon(invoicesIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH));
        staffIcon = new ImageIcon(staffIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH));
        reportsIcon = new ImageIcon(reportsIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH));
        logoutIcon = new ImageIcon(logoutIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH));
    }

    private ImageIcon loadImageIcon(String path) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Icon not found: " + path);
            return null;  // Handle this case in a meaningful way
        }
    }

    private JButton createModernButton(String text, String cardName, ImageIcon icon) {
        JButton button = new JButton(text, icon);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 18)); // Increased font size
        button.setBackground(new Color(33, 150, 243)); // Blue background
        button.setForeground(Color.WHITE); // White text
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        button.setPreferredSize(new Dimension(250, 60)); // Larger button size
        button.setMaximumSize(new Dimension(250, 60));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Button action listener to switch content
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Switch content panel based on the button clicked
                cardLayout.show(contentPanel, cardName);
            }
        });

        return button;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DashboardPanel dashboardPanel = new DashboardPanel();
        panel.add(dashboardPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        BookingOverviewPanel bookingOverviewPanel = new BookingOverviewPanel();
     // Create a label with custom styling
        JLabel titleLabel = new JLabel("Booking Management", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24)); 
        titleLabel.setForeground(Color.WHITE); // White text
        titleLabel.setOpaque(true); 
        titleLabel.setBackground(new Color(33, 150, 243)); // Blue background
        titleLabel.setPreferredSize(new Dimension(100, 60));

        // Add the title label to the panel
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(bookingOverviewPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAssetsPanel() {
        AssetGui assetGui = new AssetGui();
        return assetGui;
    }

    private JPanel createCustomersPanel() {
        CustomerPanel customerPanel = new CustomerPanel();
        JPanel panel = new JPanel(new BorderLayout());
     // Create a label with custom styling
        JLabel titleLabel = new JLabel("Customer Management", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24)); 
        titleLabel.setForeground(Color.WHITE); // White text
        titleLabel.setOpaque(true); 
        titleLabel.setBackground(new Color(33, 150, 243)); // Blue background
        titleLabel.setPreferredSize(new Dimension(100, 60));

        // Add the title label to the panel
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(customerPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createInvoicesPanel() {
        InvoicePanel invoicePanel = new InvoicePanel();
        JPanel panel = new JPanel(new BorderLayout());

        // Create a label with custom styling
        JLabel titleLabel = new JLabel("Invoice Management", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24)); 
        titleLabel.setForeground(Color.WHITE); // White text
        titleLabel.setOpaque(true); 
        titleLabel.setBackground(new Color(33, 150, 243)); // Blue background
        titleLabel.setPreferredSize(new Dimension(100, 60));

        // Add the title label to the panel
        panel.add(titleLabel, BorderLayout.NORTH);

        // Add the invoice panel
        panel.add(invoicePanel, BorderLayout.CENTER);

        return panel;
    }


    private JPanel createStaffPanel() {
        StaffPanel staffPanel = new StaffPanel();
        JPanel panel = new JPanel(new BorderLayout());
     // Create a label with custom styling
        JLabel titleLabel = new JLabel("Staff Management", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24)); 
        titleLabel.setForeground(Color.WHITE); // White text
        titleLabel.setOpaque(true); 
        titleLabel.setBackground(new Color(33, 150, 243)); // Blue background
        titleLabel.setPreferredSize(new Dimension(100, 60));

        // Add the title label to the panel
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(staffPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createReportsPanel() {
        ReportPanel reportPanel = new ReportPanel();
        JPanel panel = new JPanel(new BorderLayout());
     // Create a label with custom styling
        JLabel titleLabel = new JLabel("Reports", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24)); 
        titleLabel.setForeground(Color.WHITE); // White text
        titleLabel.setOpaque(true); 
        titleLabel.setBackground(new Color(33, 150, 243)); // Blue background
        titleLabel.setPreferredSize(new Dimension(100, 60));

        // Add the title label to the panel
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(reportPanel, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new UserDashboard();
            }
        });
    }
}
