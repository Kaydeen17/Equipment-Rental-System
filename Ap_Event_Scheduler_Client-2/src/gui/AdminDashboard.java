package gui;

import javax.swing.*;

public class AdminDashboard extends JFrame {

	private static final long serialVersionUID = 1L;

	public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        JLabel label = new JLabel("Welcome, Admin! You have full access.", SwingConstants.CENTER);
        add(label);
    }
	
	
	
}