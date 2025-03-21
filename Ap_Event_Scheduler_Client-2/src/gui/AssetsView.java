package gui;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;

import domain.Booking;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AssetsView extends JFrame {

	
	
    private static final long serialVersionUID = 1L;

	private JTextField idTxt;
    
    public AssetsView() {
        setTitle("Assets Management");
        setSize(800, 600);  // Adjusted size for better view
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Title label at the top
        JLabel titleLabel = new JLabel("Assets Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setPreferredSize(new Dimension(800, 40));
        add(titleLabel, BorderLayout.NORTH);

        // Table with 5 columns: Asset ID, Name, Category, Qty, Available
        String[] columnNames = {"Asset ID", "Name", "Category", "Qty", "Available"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable assetsTable = new JTable(tableModel);

        // Scroll pane for the table
        JScrollPane scrollPane = new JScrollPane(assetsTable);
        scrollPane.setPreferredSize(new Dimension(750, 300)); // Adjust size
        add(scrollPane, BorderLayout.CENTER);

        // Panel for buttons at the bottom
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 3, 10, 10)); // 2 rows, 3 columns with spacing

        // Buttons for CRUD operations
        JButton createButton = new JButton("Create");
        
        createButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		//pop up to create an asset
        		
        	}
        	
        });
        
        
        JButton readButton = new JButton("Read");
        readButton.addActionListener(e -> {
         
        });
        
        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> {
            
        });
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
            
        });
        JButton categoriesButton = new JButton("Categories");
        categoriesButton.addActionListener(e -> {
            
        });

        // Button for going back to the dashboard (Home)
        JButton homeButton = new JButton("Home (Dashboard)");
        homeButton.addActionListener(e -> {
            new UserDashboard().setVisible(true);  // Open the dashboard
            dispose();  // Close the AssetsView
        });

        // Add buttons to the panel
        buttonPanel.add(createButton);
        buttonPanel.add(readButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(categoriesButton);
        buttonPanel.add(homeButton);

        // Add button panel to the bottom of the frame
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AssetsView().setVisible(true));
    }
}


