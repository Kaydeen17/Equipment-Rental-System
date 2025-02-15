package com.applictation.view;


import javax.swing.*;

public class UserDashboard extends JFrame {

	private static final long serialVersionUID = 1L;

	public UserDashboard() {
		
		
        setTitle("User Dashboard");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        JLabel label = new JLabel("Welcome, User! Limited access.", SwingConstants.CENTER);
        add(label);
        
        
    }
}