package com.java.hibernate;

import com.java.domain.Asset;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.util.List;

public class AssetManager implements AssetManagement {

    private static final Logger logger = LogManager.getLogger(AssetManager.class);
    private static SessionFactory sessionFactory = null;

    public AssetManager() {
        sessionFactory = buildSessionFactory();
    }

    private static SessionFactory buildSessionFactory() {
        try {
            logger.info("Building session factory...");
            return new Configuration().configure().buildSessionFactory();
        } catch (Exception e) {
            logger.error("SessionFactory creation failed: {}", e.getMessage(), e);
            System.err.println("SessionFactory creation failed: " + e);
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    public void exit() {
        try {
            if (sessionFactory != null) {
                logger.info("Closing session factory...");
                sessionFactory.close();
            }
        } catch (Exception e) {
            logger.error("Error while closing session factory: {}", e.getMessage(), e);
            System.err.println("Error while closing session factory: " + e);
        }
    }

    @Override
    public void create(Asset asset) {
        logger.info("Creating asset: {}", asset);
        System.out.println("Creating asset: " + asset);
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
        	//save asset to db
            session.persist(asset);
            session.getTransaction().commit();
            logger.info("Asset created successfully: {}", asset.getAssetId());
            System.out.println("Asset created successfully: " + asset.getAssetId());
        } catch (Exception e) {
            logger.error("Error creating asset: {}", e.getMessage(), e);
            System.err.println("Error creating asset: " + e.getMessage());
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        } finally {
            session.close();
        }
    }

    @Override
    public Asset read(int assetId) {
        logger.info("Reading asset with ID: {}", assetId);
        System.out.println("Reading asset with ID: " + assetId);
        Session session = sessionFactory.openSession();
        try {
            Asset asset = session.get(Asset.class, assetId);
            if (asset != null) {
                logger.info("Asset found: {}", asset);
                System.out.println("Asset found: " + asset);
                return asset;
            } else {
                logger.warn("Asset with ID {} not found.", assetId);
                System.out.println("Asset not found with ID: " + assetId);
            }
        } catch (Exception e) {
            logger.error("Error reading asset with ID {}: {}", assetId, e.getMessage(), e);
            System.err.println("Error reading asset with ID " + assetId + ": " + e.getMessage());
        } finally {
            session.close();
        }
        return null;
    }

    @Override
    public void update(Asset asset) {
        logger.info("Updating asset: {}", asset);
        System.out.println("Updating asset: " + asset);
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            Asset existingAsset = session.get(Asset.class, asset.getAssetId());

            if (existingAsset != null) {
                existingAsset.setName(asset.getName());
                existingAsset.setCategory(asset.getCategory());
                existingAsset.setSerialNumber(asset.getSerialNumber());
                existingAsset.setPricePerDay(asset.getPricePerDay());

                session.merge(existingAsset);
                session.getTransaction().commit();
                logger.info("Asset with ID {} updated successfully.", asset.getAssetId());
                System.out.println("Asset with ID " + asset.getAssetId() + " has been updated.");
            } else {
                logger.warn("Asset with ID {} not found for update.", asset.getAssetId());
                System.out.println("Asset with ID " + asset.getAssetId() + " not found.");
            }
        } catch (Exception e) {
            logger.error("Error updating asset with ID {}: {}", asset.getAssetId(), e.getMessage(), e);
            System.err.println("Error updating asset with ID " + asset.getAssetId() + ": " + e.getMessage());
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        } finally {
            session.close();
        }
    }

    @Override
    public void delete(int assetId) {
        logger.info("Deleting asset with ID: {}", assetId);
        System.out.println("Deleting asset with ID: " + assetId);
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            Asset asset = session.get(Asset.class, assetId);
            if (asset != null) {
                session.remove(asset);
                session.getTransaction().commit();
                logger.info("Asset with ID {} deleted successfully.", assetId);
                System.out.println("Asset with ID " + assetId + " deleted successfully.");
            } else {
                logger.warn("Asset with ID {} not found for deletion.", assetId);
                System.out.println("Asset with ID " + assetId + " not found.");
            }
        } catch (Exception e) {
            logger.error("Error deleting asset with ID {}: {}", assetId, e.getMessage(), e);
            System.err.println("Error deleting asset with ID " + assetId + ": " + e.getMessage());
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        } finally {
            session.close();
        }
    }

    @Override
    public List<Asset> showAll() {
        logger.info("Showing all assets...");
        System.out.println("SHOWING ASSETS...");
        try (Session session = sessionFactory.openSession()) {
            List<Asset> assets = session.createQuery("FROM Asset", Asset.class).list();
            logger.info("Total assets found: {}", assets.size());
            System.out.println("Total assets found: " + assets.size());
            return assets;
        } catch (Exception e) {
            logger.error("Error retrieving all assets: {}", e.getMessage(), e);
            System.err.println("Error retrieving all assets: " + e.getMessage());
            return null;
        }
    }

    @Override
    public int totalAssets() {
        logger.info("Fetching total number of assets...");
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery("SELECT COUNT(a) FROM Asset a", Long.class).uniqueResult();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            logger.error("Error fetching total assets: {}", e.getMessage(), e);
            System.err.println("Error fetching total assets: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public int availableAssets() {
        logger.info("Fetching total number of available assets...");
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery("SELECT COUNT(a) FROM Asset a WHERE a.status = 'AVAILABLE'", Long.class).uniqueResult();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            logger.error("Error fetching available assets: {}", e.getMessage(), e);
            System.err.println("Error fetching available assets: " + e.getMessage());
            return 0;
        }
    }



}
