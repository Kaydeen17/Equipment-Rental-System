package com.java.gui;

import com.java.domain.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class StaffPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTable staffTable;
    private JButton addStaffButton;
    private JButton editStaffButton;
    private JButton deleteStaffButton;
    private JButton searchButton;
    private JTextField searchTextField;

    public StaffPanel() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());  // Use BorderLayout for proper content layout

        // Top Panel with buttons and search bar
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
       
        addStaffButton = createActionButton("Add", "➕", "STAFF ADD");
        editStaffButton = createActionButton("Edit", "✏️", "STAFF EDIT");
        deleteStaffButton = createActionButton("Delete", "❌", "STAFF DELETE");

        // Search bar setup
        searchTextField = new JTextField(15);
        searchButton = new JButton("Search");
        searchButton.setPreferredSize(new Dimension(100, 30));
        searchButton.setBackground(new Color(33, 150, 243)); // Blue color for search button
        searchButton.setForeground(Color.WHITE);

        // Handle search action
        searchButton.addActionListener(e -> performSearch());

        // Adding components to the top panel
        topPanel.add(addStaffButton);
        topPanel.add(editStaffButton);
        topPanel.add(deleteStaffButton);
        topPanel.add(searchTextField);
        topPanel.add(searchButton);
    
        // Table setup for staff overview
        String[] columns = {"Staff ID", "Name", "Email", "Phone", "Role"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        staffTable = new JTable(model) {
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

        JScrollPane tableScroll = new JScrollPane(staffTable);

        // Disable cell editing
        staffTable.setCellSelectionEnabled(false);
        staffTable.setRowSelectionAllowed(true);

        // Handle table row selection
        staffTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = staffTable.getSelectedRow();
                if (selectedRow != -1) {
                    int staffId = Integer.parseInt(staffTable.getValueAt(selectedRow, 0).toString());
                    showStaffDetails(staffId);
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

    private void fetchStaff() {
        try {
            // Placeholder for sending action to the server
            // LoginPage.client.sendAction("STAFF SHOWALL");

            // Placeholder: Simulating fetching staff data for testing UI
            List<Staff> staffList = List.of(new Staff(1, "John Doe", "john@example.com", "123-4567", "Manager", "password123"),
                                             new Staff(2, "Jane Smith", "jane@example.com", "987-6543", "Cashier", "password456"));
            // Update the table with the fetched staff
            updateTable(staffList);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load staff.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Staff> staffList) {
        DefaultTableModel model = (DefaultTableModel) staffTable.getModel();
        model.setRowCount(0); // Clear existing data

        for (Staff staff : staffList) {
            model.addRow(new Object[] {
                    staff.getStaffId(),
                    staff.getName(),
                    staff.getEmail(),
                    staff.getPhone(),
                    staff.getRole()
            });
        }
    }

    private void showStaffDetails(int staffId) {
        // Show staff details in a separate dialog or another view
        // Commented out for testing UI only
        // StaffDetailsFrame staffDetailsFrame = new StaffDetailsFrame(null, staffId);
        // staffDetailsFrame.setModal(true);
        // staffDetailsFrame.setVisible(true);
    }
}
