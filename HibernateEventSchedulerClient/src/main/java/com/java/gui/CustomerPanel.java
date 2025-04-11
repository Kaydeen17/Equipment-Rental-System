package com.java.gui;

import com.java.domain.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CustomerPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTable customerTable;
    private JButton refreshButton;
    private JButton newCustomerButton;
    private JButton updateCustomerButton;
    private JButton deleteCustomerButton;
    private JButton searchButton;
    private JTextField searchTextField;

    public CustomerPanel() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());  // Use BorderLayout for proper content layout

        // Top Panel with buttons and search bar
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
       
        refreshButton = createActionButton("Refresh", "\u27F3", "CUSTOMER REFRESH");
        newCustomerButton = createActionButton("New Customer", "➕", "CUSTOMER CREATE");
        updateCustomerButton = createActionButton("Update Customer", "✏️", "CUSTOMER UPDATE");
        deleteCustomerButton = createActionButton("Delete Customer", "❌", "CUSTOMER DELETE");

        // Search bar setup
        searchTextField = new JTextField(15);
        searchButton = new JButton("Search");
        searchButton.setPreferredSize(new Dimension(100, 30));
        searchButton.setBackground(new Color(33, 150, 243)); // Blue color for search button
        searchButton.setForeground(Color.WHITE);

        // Handle search action
        searchButton.addActionListener(e -> performSearch());

        // Adding components to the top panel
        topPanel.add(refreshButton);
        topPanel.add(newCustomerButton);
        topPanel.add(updateCustomerButton);
        topPanel.add(deleteCustomerButton);
        topPanel.add(searchTextField);
        topPanel.add(searchButton);
    
        // Table setup for customer overview
        String[] columns = {"Customer ID", "Name", "Email", "Phone"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        customerTable = new JTable(model) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);

                // Alternate row coloring
                if (row % 2 == 0) {
                    comp.setBackground(Color.WHITE);
                } else {
                    comp.setBackground(Color.LIGHT_GRAY);
                }

                // Highlight selected row
                if (isRowSelected(row)) {
                    comp.setBackground(new Color(173, 216, 230)); // Light blue background for selected row
                }

                return comp;
            }
        };

        JScrollPane tableScroll = new JScrollPane(customerTable);

        // Disable cell editing
        customerTable.setCellSelectionEnabled(false);
        customerTable.setRowSelectionAllowed(true);

        // Handle table row selection
        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = customerTable.getSelectedRow();
                if (selectedRow != -1) {
                    int customerId = Integer.parseInt(customerTable.getValueAt(selectedRow, 0).toString());
                    showCustomerDetails(customerId);
                }
            }
        });

        // Add components to the panel
        add(topPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
    }

    private JButton createActionButton(String text, String icon, String actionCommand) {
        JButton button = new JButton(text + " " + icon);  // Add text and icon/emoji
        button.setActionCommand(actionCommand);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(new Color(33, 150, 243)); // Blue background
        button.setForeground(Color.WHITE); // White text
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        button.setPreferredSize(new Dimension(120, 40));  // Set preferred size for a smaller button

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Send client command based on the button's action command
                // Commenting out for testing UI only
                // LoginPage.client.sendAction(button.getActionCommand());
            }
        });

        return button;
    }

    private void performSearch() {
        // Placeholder method for search functionality
        String searchTerm = searchTextField.getText().trim();
        System.out.println("Searching for: " + searchTerm);
        // You can add search functionality logic here
    }

    @SuppressWarnings("unused")
	private void updateTable(List<Customer> customers) {
        DefaultTableModel model = (DefaultTableModel) customerTable.getModel();
        model.setRowCount(0); // Clear existing data

        for (Customer customer : customers) {
            model.addRow(new Object[] {
                    customer.getCustomerId(),
                    customer.getName(),
                    customer.getEmail(),
                    customer.getPhone()
            });
        }
    }

    private void showCustomerDetails(int customerId) {
        // Show customer details in a separate dialog or another view
        // Commented out for testing UI only
        // CustomerDetailsFrame customerDetailsFrame = new CustomerDetailsFrame(null, customerId);
        // customerDetailsFrame.setModal(true);
        // customerDetailsFrame.setVisible(true);
    }
}
