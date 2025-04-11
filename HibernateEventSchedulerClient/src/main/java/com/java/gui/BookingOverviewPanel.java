package com.java.gui;

import com.java.domain.Booking;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class BookingOverviewPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTable bookingTable;
    private JButton refreshButton, newBookingButton, closeBookingButton, deleteBookingButton;
    private JRadioButton allBookings, ongoingBookings, completedBookings;
    private ButtonGroup filterGroup;
    private static final Logger logger = LogManager.getLogger(BookingOverviewPanel.class);

    public BookingOverviewPanel() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Top Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("Booking Overview");

        refreshButton = new JButton("\u27F3"); // Refresh
        refreshButton.addActionListener(e -> fetchBookings());

        newBookingButton = createModernButton("New Booking", "NewBookingPanel");
        newBookingButton.addActionListener(e -> {
            NewBookingFrame frame = new NewBookingFrame();
            frame.setVisible(true);
        });
        closeBookingButton = createModernButton("Close Booking", null);
        deleteBookingButton = createModernButton("Delete Booking", null);

        // Separate action listeners for Close and Delete buttons
        closeBookingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleBookingAction("BOOKING CLOSE");
            }
        });

        deleteBookingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleBookingAction("BOOKING DELETE");
            }
        });

        allBookings = new JRadioButton("All", true);
        ongoingBookings = new JRadioButton("Ongoing");
        completedBookings = new JRadioButton("Completed");

        filterGroup = new ButtonGroup();
        filterGroup.add(allBookings);
        filterGroup.add(ongoingBookings);
        filterGroup.add(completedBookings);

        ActionListener filterAction = e -> fetchBookings();
        allBookings.addActionListener(filterAction);
        ongoingBookings.addActionListener(filterAction);
        completedBookings.addActionListener(filterAction);

        topPanel.add(titleLabel);
        topPanel.add(refreshButton);
        topPanel.add(newBookingButton);
        topPanel.add(closeBookingButton);
        topPanel.add(deleteBookingButton);
        topPanel.add(allBookings);
        topPanel.add(ongoingBookings);
        topPanel.add(completedBookings);

        // Table setup
        String[] columns = {"Booking ID", "Client Name", "Staff", "Booking Date", "Return Date", "Status", "Delinquent", "No. Of Days"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Prevent editing of any cell
            }
        };
        bookingTable = new JTable(model) {
            private static final long serialVersionUID = 1L;

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (row % 2 == 0) comp.setBackground(new Color(240, 240, 240));
                else comp.setBackground(Color.WHITE);
                if (isRowSelected(row)) comp.setBackground(new Color(173, 216, 230));
                return comp;
            }
        };

        bookingTable.setRowSelectionAllowed(true);
        bookingTable.setCellSelectionEnabled(false);
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        bookingTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = bookingTable.getSelectedRow();
                    if (row != -1) {
                        int bookingID = Integer.parseInt(bookingTable.getValueAt(row, 0).toString());
                        showBookingDetails(bookingID);  // Call method to show details
                    }
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(bookingTable);

        add(topPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);

        fetchBookings(); // Load data initially
    }
    
    private void handleBookingAction(String command) {
        int row = bookingTable.getSelectedRow();
        if (row != -1) {
            int bookingId = Integer.parseInt(bookingTable.getValueAt(row, 0).toString());
            String status = bookingTable.getValueAt(row, 5).toString();  // Get the status of the selected booking
            
            if (command.contains("DELETE")) {
                // Check if the booking status is ONGOING
                if (status.equalsIgnoreCase("ONGOING")) {
                    logger.warn("Attempt to delete ongoing booking with ID: {}", bookingId);
                    JOptionPane.showMessageDialog(this, "You cannot delete an ongoing booking.", "Error", JOptionPane.ERROR_MESSAGE);
                    return; // Do not proceed with deletion
                }
            } else if (command.contains("CLOSE")) {
                // Check if the booking is already CLOSED
                if (status.equalsIgnoreCase("CLOSED")) {
                    logger.warn("Attempt to close already closed booking with ID: {}", bookingId);
                    JOptionPane.showMessageDialog(this, "Booking is already closed.", "Error", JOptionPane.ERROR_MESSAGE);
                    return; // Do not proceed with closing
                }
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to " + (command.contains("CLOSE") ? "close" : "delete") + " booking #" + bookingId + "?",
                    "Confirm Action", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    // Send action command to server
                    LoginPage.client.sendAction(command);
                    
                    // Send booking ID for the action to the server
                    Object response = LoginPage.client.sendBookingID(bookingId);
                    
                    // If the response is null, show a success message
                    if (response == null) {
                        fetchBookings(); // Refresh the table after action
                        logger.info("Booking action '{}' completed successfully for booking ID: {}", command, bookingId);
                        JOptionPane.showMessageDialog(this, command + " operation successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        // If response is not null, show unknown server response message
                        logger.error("Unknown response from server for booking action '{}': {}", command, response);
                        JOptionPane.showMessageDialog(this, "Unknown response from server.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    logger.error("Error performing action '{}' for booking ID: {}", command, bookingId, e);
                    JOptionPane.showMessageDialog(this, "Error performing action on booking.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a booking from the table.");
        }
    }


    private JButton createModernButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(new Color(33, 150, 243));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        button.setPreferredSize(new Dimension(160, 40));
        return button;
    }

    private void fetchBookings() {
        try {
            String filter = allBookings.isSelected() ? "ALL"
                          : ongoingBookings.isSelected() ? "ONGOING"
                          : "COMPLETED";

            logger.info("Fetching bookings with filter: {}", filter);
            LoginPage.client.sendAction("BOOKING SHOWALL");
            List<Booking> bookings = LoginPage.client.getBookingList();
            updateTable(bookings, filter);
            logger.info("Bookings fetched successfully.");
        } catch (Exception e) {
            logger.error("Failed to load bookings.", e);
            JOptionPane.showMessageDialog(this, "Failed to load bookings.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Booking> bookings, String filter) {
        DefaultTableModel model = (DefaultTableModel) bookingTable.getModel();
        model.setRowCount(0);
        for (Booking booking : bookings) {
            if (filter.equals("ALL") || booking.getStatus().name().equalsIgnoreCase(filter)) {
                model.addRow(new Object[] {
                    booking.getBookingId(),
                    booking.getClientName(),
                    booking.getUserId(),
                    booking.getBookDate(),
                    booking.getReturnDate(),
                    booking.getStatus().name(),
                    booking.isLate() ? "Yes" : "No",
                    booking.getDayCount()
                });
            }
        }
    }

    private void showBookingDetails(int bookingID) {
        BookingDetailsFrame bookingDetailsFrame = new BookingDetailsFrame(
                (JFrame) SwingUtilities.getWindowAncestor(BookingOverviewPanel.this),
                bookingID
        );
        bookingDetailsFrame.setVisible(true);
    }
}
