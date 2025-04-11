package com.java.gui;

import com.java.domain.Asset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AssetGui extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(AssetGui.class);

    private JTable assetTable;
    private JButton refreshButton, newAssetButton, deleteAssetButton;
    private JRadioButton availableAssets, bookedAssets;
    private ButtonGroup filterGroup;
    private JTextField searchField;
    private JButton searchButton;

    public AssetGui() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Top Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("Asset Management");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24)); 
        titleLabel.setForeground(Color.WHITE); // White text
        titleLabel.setOpaque(true); 
        titleLabel.setBackground(new Color(33, 150, 243)); // Blue background
        titleLabel.setPreferredSize(new Dimension(100, 60));

        

        refreshButton = new JButton("\u27F3"); // RefreshButton
        refreshButton.addActionListener(e -> {
            // Log current client refreshing assets
            logger.info(LoginPage.client.getCurrentClient() + " is refreshing the asset list.");
            fetchAssets();
        });

        newAssetButton = createModernButton("New Asset", "NewAssetPanel");
        newAssetButton.addActionListener(e -> {
            
            logger.info(LoginPage.client.getCurrentClient() + " is creating a new asset.");
            NewAssetFrame frame = new NewAssetFrame();
            frame.setVisible(true);
        });

        deleteAssetButton = createModernButton("Delete Asset", null);
        deleteAssetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
           
                logger.info(LoginPage.client.getCurrentClient() + " is deleting an asset.");
                handleAssetAction("ASSET DELETE");
            }
        });

        availableAssets = new JRadioButton("Available", true);
        bookedAssets = new JRadioButton("Booked");

        filterGroup = new ButtonGroup();
        filterGroup.add(availableAssets);
        filterGroup.add(bookedAssets);

        ActionListener filterAction = e -> {
            // Log current client filter action
            logger.info(LoginPage.client.getCurrentClient() + " changed asset filter.");
            fetchAssets();
        };
        availableAssets.addActionListener(filterAction);
        bookedAssets.addActionListener(filterAction);

        searchField = new JTextField(15);
        searchButton = new JButton("Search");
        searchButton.addActionListener(e -> {
            // Log the current client performing a search
            logger.info(LoginPage.client.getCurrentClient() + " is searching for assets.");
            searchAssets();
        });

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(refreshButton);
        topPanel.add(newAssetButton);
        topPanel.add(deleteAssetButton);
        topPanel.add(availableAssets);
        topPanel.add(bookedAssets);
        topPanel.add(searchField);
        topPanel.add(searchButton);

        // Table setup
        String[] columns = {"Asset ID", "Name", "Category", "Price Per Day", "Serial Number", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Prevent editing of any cell
            }
        };
        assetTable = new JTable(model) {
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

        assetTable.setRowSelectionAllowed(true);
        assetTable.setCellSelectionEnabled(false);
        assetTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        assetTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = assetTable.getSelectedRow();
                    if (row != -1) {
                        int assetID = Integer.parseInt(assetTable.getValueAt(row, 0).toString());
                        // Log current client opening asset details
                        logger.info(LoginPage.client.getCurrentClient() + " is viewing details for asset ID: " + assetID);
                        showAssetDetails(assetID);  // Call method to show details
                    }
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(assetTable);

        add(topPanel, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);

        fetchAssets(); // Load data initially
    }

    private void handleAssetAction(String command) {
        int row = assetTable.getSelectedRow();
        if (row != -1) {
            int assetId = Integer.parseInt(assetTable.getValueAt(row, 0).toString());
            String status = assetTable.getValueAt(row, 5).toString();
            // Check if the status is BOOKED
            if (status.equalsIgnoreCase("BOOKED") && command.contains("DELETE")) {
                JOptionPane.showMessageDialog(this,
                        "You cannot delete a booked asset.",
                        "Deletion Not Allowed",
                        JOptionPane.WARNING_MESSAGE);
                return; // Exit the method
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to " + (command.contains("UPDATE") ? "update" : "delete") + " asset #" + assetId + "?",
                    "Confirm Action", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                logger.info(LoginPage.client.getCurrentClient() + " confirmed deletion of asset #" + assetId);
                LoginPage.client.sendAction(command);
                Object obj = LoginPage.client.sendID(assetId);

                if (obj instanceof String) {
                    String response = (String) obj;

                    if (response.equalsIgnoreCase("Asset Deleted")) {
                        JOptionPane.showMessageDialog(null, "Asset deleted successfully!");
                        logger.info(LoginPage.client.getCurrentClient() + " successfully deleted asset #" + assetId);
                    } else {
                        JOptionPane.showMessageDialog(null, "Deletion failed: " + response);
                        logger.warn(LoginPage.client.getCurrentClient() + " failed to delete asset #" + assetId + ": " + response);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Asset Deleted Successfully.");
                    logger.info(LoginPage.client.getCurrentClient() + " successfully deleted asset #" + assetId);
                }

                fetchAssets(); // refresh the table after action
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an asset from the table.");
            logger.warn(LoginPage.client.getCurrentClient() + " attempted to delete without selecting an asset.");
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

    private void fetchAssets() {
        try {
            String filter = availableAssets.isSelected() ? "AVAILABLE"
                          : "BOOKED";

            LoginPage.client.sendAction("ASSET SHOWALL");
            // Retrieve asset list from server
            List<Asset> assetList = LoginPage.client.getAssetList();
            
            if (assetList != null) {
                // Convert List<Asset> to a format suitable for JTable
                updateTable(assetList, filter);
                logger.info(LoginPage.client.getCurrentClient() + " fetched asset list with filter: " + filter);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load assets.", "Error", JOptionPane.ERROR_MESSAGE);
            logger.error(LoginPage.client.getCurrentClient() + " encountered an error while fetching asset list.", e);
        }
    }

    private void updateTable(List<Asset> assets, String filter) {
        DefaultTableModel model = (DefaultTableModel) assetTable.getModel();
        model.setRowCount(0);
        for (Asset asset : assets) {
            if (filter.equals("AVAILABLE") || asset.getStatus().name().equalsIgnoreCase(filter)) {
                model.addRow(new Object[] {
                    asset.getAssetId(),
                    asset.getName(),
                    asset.getCategory(),
                    asset.getPricePerDay(),
                    asset.getSerialNumber(),
                    asset.getStatus().name(),
                });
            }
        }
    }

    private void searchAssets() {
        String idText = searchField.getText().trim();
        try {
            // Try to parse the ID from the search field
            int id = Integer.parseInt(idText);
            
            // Send the ID to the server
            LoginPage.client.sendAction("ASSET READ");
            
            Asset asset = LoginPage.client.sendID(id);  // Send the ID to the server
            
            // If the asset is found, update the table with the retrieved asset
            if (asset != null) {
                List<Asset> assetList = new ArrayList<>();
                assetList.add(asset);  // Add the single asset to the list for updating the table
                updateTable(assetList, availableAssets.isSelected() ? "AVAILABLE" : "BOOKED");
                logger.info(LoginPage.client.getCurrentClient() + " searched and found asset #" + id);
            } else {
                JOptionPane.showMessageDialog(this, "No asset found with the provided ID.", "Error", JOptionPane.WARNING_MESSAGE);
                logger.warn(LoginPage.client.getCurrentClient() + " searched for asset #" + id + " but none was found.");
            }
        } catch (NumberFormatException e) {
            // Handle invalid ID input
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric asset ID.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            logger.error(LoginPage.client.getCurrentClient() + " entered an invalid asset ID during search: " + idText, e);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to search assets.", "Error", JOptionPane.ERROR_MESSAGE);
            logger.error(LoginPage.client.getCurrentClient() + " encountered an error during asset search.", e);
        }
    }

    private void showAssetDetails(int assetID) {
        AssetDetailsFrame assetDetailsFrame = new AssetDetailsFrame(
                (JFrame) SwingUtilities.getWindowAncestor(AssetGui.this),
                assetID
        );
        assetDetailsFrame.setVisible(true);
    }
}
