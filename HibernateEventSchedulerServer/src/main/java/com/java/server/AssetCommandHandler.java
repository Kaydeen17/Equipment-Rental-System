package com.java.server;

import java.io.*;
import java.util.List;
import com.java.domain.Asset;
import com.java.hibernate.AssetManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AssetCommandHandler {
    private static final Logger logger = LogManager.getLogger(AssetCommandHandler.class);
    private final ObjectInputStream objIs;
    private final ObjectOutputStream objOs;
    private final AssetManager assetManager;

    public AssetCommandHandler(ObjectInputStream objIs, ObjectOutputStream objOs, AssetManager assetManager) {
        this.objIs = objIs;
        this.objOs = objOs;
        this.assetManager = assetManager;
    }

    public void handleAssetCommands(String message) {
        try {
            switch (message) {
                case "ASSET CREATE":
                    logger.info("Creating asset....");
                    System.out.println("Creating asset....");
                    createAsset();
                    break;
                case "ASSET READ":
                    logger.info("Reading asset....");
                    System.out.println("Reading asset....");
                    readAsset();
                    break;
                case "ASSET UPDATE":
                    logger.info("Updating asset....");
                    System.out.println("Updating asset....");
                    updateAsset();
                    break;
                case "ASSET DELETE":
                    logger.info("Deleting asset....");
                    System.out.println("Deleting asset....");
                    deleteAsset();
                    break;
                case "ASSET SHOWALL":
                    logger.info("Showing all assets....");
                    System.out.println("Showing all asset....");
                    viewAllAssets();
                    break;
                case "ASSET TOTAL":
                    int totalAssets = assetManager.totalAssets();
                    System.out.println("Total number of assets: " + totalAssets);
                    objOs.writeObject(totalAssets);
                    objOs.flush();
                    break;
                case "ASSET AVAILABLE":
                    int availableAssets = assetManager.availableAssets();
                    System.out.println("Total number of available assets: " + availableAssets);
                    objOs.writeObject(availableAssets);
                    objOs.flush();
                    break;
                default:
                    logger.warn("Unknown command received: {}", message);
                    objOs.writeObject("Unknown command: " + message);
                    objOs.flush();
                    break;
            }
        } catch (IOException e) {
            logger.error("Error handling client request: {}", e.getMessage(), e);
            System.err.println("Error handling client request: " + e.getMessage());
        }
    }

    private void createAsset() {
        try {
            Asset asset = (Asset) objIs.readObject();
            logger.info("Creating Asset: {}", asset.getName());
            System.out.println("Creating Asset: " + asset.getName());

            assetManager.create(asset);
            objOs.writeObject("Asset created successfully: " + asset.getName());
            objOs.flush();
        } catch (Exception e) {
            handleError("Failed to create asset.", e);
        }
    }

    private void readAsset() {
        try {
            int assetId = (int) objIs.readObject();
            logger.info("Reading Asset with ID: {}", assetId);
            Asset asset = assetManager.read(assetId);
            objOs.writeObject(asset != null ? asset : "Asset not found");
            objOs.flush();
        } catch (Exception e) {
            handleError("Failed to read asset.", e);
        }
    }

    private void updateAsset() {
        try {
            Asset updatedAsset = (Asset) objIs.readObject();
            int assetId = updatedAsset.getAssetId();
            logger.info("Updating Asset with ID: {}", assetId);
            Asset asset = assetManager.read(assetId);

            if (asset != null) {
                asset.setName(updatedAsset.getName());
                asset.setCategory(updatedAsset.getCategory());
                asset.setSerialNumber(updatedAsset.getSerialNumber());
                asset.setPricePerDay(updatedAsset.getPricePerDay());

                assetManager.update(asset);
                objOs.writeObject("Asset updated successfully");
                
            } else {
                objOs.writeObject("Asset not found");
            }
            objOs.flush();
        } catch (Exception e) {
            handleError("Failed to update asset.", e);
        }
    }

    private void deleteAsset() {
        try {
            int assetId = (int) objIs.readObject();
            logger.info("Deleting Asset with ID: {}", assetId);
            Asset asset = assetManager.read(assetId);

            if (asset != null) {
                assetManager.delete(assetId);
                objOs.writeObject("Asset deleted.");
            } else {
                objOs.writeObject("Asset not found");
            }
            objOs.flush();
        } catch (Exception e) {
            handleError("Failed to delete asset.", e);
        }
    }

    private void viewAllAssets() {
        try {
            logger.info("Retrieving all assets...");
            List<Asset> assets = assetManager.showAll();
            objOs.writeObject(assets != null && !assets.isEmpty() ? assets : "No assets found.");
            objOs.flush();
        } catch (Exception e) {
            handleError("Failed to retrieve assets.", e);
        }
    }

    private void handleError(String errorMessage, Exception e) {
        logger.error(errorMessage, e);
        System.err.println(errorMessage);
        e.printStackTrace();
    }
}
