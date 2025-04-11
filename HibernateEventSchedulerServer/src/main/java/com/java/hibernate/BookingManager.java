package com.java.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.Hibernate;

import com.java.domain.Asset;
import com.java.domain.Booking;
import com.java.domain.Invoice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BookingManager implements BookingManagement {

    private static final Logger logger = LogManager.getLogger(BookingManager.class);
    private static final SessionFactory sessionFactory = buildSessionFactory(); // Singleton

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
            logger.info("Closing session factory...");
            sessionFactory.close();
        } catch (Exception e) {
            logger.error("Error while closing session factory: {}", e.getMessage(), e);
            System.err.println("Error while closing session factory: " + e);
        }
    }

    // Centralized session management method
    private Session openSession() {
        return sessionFactory.openSession();
    }

    @Override
    public void createBooking(Booking booking) {
        logger.info("Creating booking: {}", booking);
        System.out.println("Creating booking: " + booking);
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            // Save the booking first and retrieve the managed instance
            booking = session.merge(booking);

            List<Asset> validAssets = new ArrayList<>();
            List<Asset> alreadyBookedAssets = new ArrayList<>();

            for (Asset asset : booking.getAssetList()) {
                asset = session.merge(asset); // Ensure it's managed by Hibernate
                
                // Check if the asset is available
                if (asset.getBookingId() == null && asset.getStatus().equals(Asset.Status.AVAILABLE)) {
                    asset.setBooking(booking); // Associate asset with booking
                    asset.setBookingId(booking.getBookingId()); // Set the booking ID
                    asset.setStatus(Asset.Status.BOOKED); // Update status to BOOKED
                    validAssets.add(asset);
                } else {
                    alreadyBookedAssets.add(asset); // Track assets that are already booked
                }
            }

            // If no valid assets, rollback and return
            if (validAssets.isEmpty()) {
                session.getTransaction().rollback();
                logger.warn("No assets were added. All selected assets are already booked.");
                System.out.println("❌ No assets were added. All selected assets are already booked.");
                return;
            }

            // Update only valid assets in the database
            for (Asset validAsset : validAssets) {
                session.merge(validAsset);
            }

            // Automatically create an invoice for this booking
            Invoice invoice = new Invoice();
            invoice.setBooking(booking);
            invoice.setClientName(booking.getClientName());
            invoice.setUserId(booking.getUserId());
            invoice.setBookingId(booking.getBookingId());
            invoice.setStatus("PENDING");
            invoice.setQuote(false);
            invoice.setCreationDate();
            invoice.calculateInvoiceTotal();

            session.persist(invoice); // Save the invoice

            // Merge invoiceId into the booking after persisting the invoice
            booking.setInvoiceId(invoice.getInvoiceId());
            session.merge(booking);

            session.getTransaction().commit();
            logger.info("Booking and invoice created successfully.");
            System.out.println("✅ Booking and invoice created successfully.");
            logger.info("Added assets: {}", validAssets);
            System.out.println("🔹 Added assets: " + validAssets);
            
            if (!alreadyBookedAssets.isEmpty()) {
                logger.warn("Some assets were not added because they are already booked: {}", alreadyBookedAssets);
                System.out.println("⚠️ Some assets were not added because they are already booked: " + alreadyBookedAssets);
            }
        } catch (Exception e) {
            session.getTransaction().rollback();
            logger.error("Error creating booking: {}", e.getMessage(), e);
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public void updateBooking(Booking booking) {
        logger.info("Updating booking: {}", booking);
        System.out.println("Updating booking: " + booking);
        Session session = openSession();
        session.beginTransaction();

        try {
            Booking existingBooking = session.get(Booking.class, booking.getBookingId());

            if (existingBooking != null) {
                existingBooking.setReturnDate(booking.getReturnDate());

                List<Asset> existingAssets = existingBooking.getAssetList();
                List<Asset> newAssets = booking.getAssetList();

                // Remove assets that are no longer associated
                existingAssets.removeIf(asset -> !newAssets.contains(asset));

                // Add new assets (prevent duplicates)
                for (Asset newAsset : newAssets) {
                    if (!existingAssets.contains(newAsset)) {
                        existingAssets.add(newAsset);
                        newAsset.setBooking(existingBooking);
                    }
                }

                session.merge(existingBooking);
                session.getTransaction().commit();
                logger.info("Booking with ID {} updated successfully.", booking.getBookingId());
                System.out.println("Booking with ID " + booking.getBookingId() + " has been updated.");
            } else {
                logger.warn("Booking with ID {} not found for update.", booking.getBookingId());
                System.out.println("Booking with ID " + booking.getBookingId() + " not found.");
            }
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            logger.error("Error updating booking with ID {}: {}", booking.getBookingId(), e.getMessage(), e);
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public Booking readBooking(int bookingId) {
        logger.info("Reading booking with ID: {}", bookingId);
        System.out.println("Reading booking with ID: " + bookingId);
        Session session = sessionFactory.openSession();
        try {
            Booking booking = session.get(Booking.class, bookingId);
            if (booking != null) {
                Hibernate.initialize(booking.getAssetList()); // Load asset list before closing session
            } else {
                logger.warn("Booking not found with ID: {}", bookingId);
                System.out.println("Booking not found with ID: " + bookingId);
            }
            return booking;
        } finally {
            session.close();
        }
    }

    @Override
    public List<Booking> showAllBooking() {
        logger.info("Fetching all bookings...");
        System.out.println("Fetching all bookings...");
        try (Session session = sessionFactory.openSession()) {
            List<Booking> bookings = session.createQuery("FROM Booking", Booking.class).list();
            for (Booking b : bookings) {
                Hibernate.initialize(b.getAssetList());
            }
            logger.info("Total bookings found: {}", bookings.size());
            return bookings;
        } catch (Exception e) {
            logger.error("Error retrieving bookings: {}", e.getMessage(), e);
            System.err.println("Error retrieving bookings: " + e);
            return new ArrayList<>();
        }
    }

    @Override
    public void closeBooking(int bookingId) {
        logger.info("Closing booking with ID: {}", bookingId);
        System.out.println("Closing booking with ID: " + bookingId);
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();

            // Fetch the booking
            Booking existingBooking = session.get(Booking.class, bookingId);
            if (existingBooking == null) {
                logger.warn("Booking with ID {} not found.", bookingId);
                System.out.println("Booking with ID " + bookingId + " not found.");
                session.getTransaction().rollback();
                return;
            }

            Hibernate.initialize(existingBooking.getAssetList()); // Ensure asset list is loaded
            
            // Update booking status
            existingBooking.setStatus(Booking.Status.CLOSED);
            
            // Update each asset: set booking reference to NULL and status to AVAILABLE
            for (Asset asset : existingBooking.getAssetList()) {
                asset.setBooking(null);
                asset.setBookingId(null); // Explicitly clear booking ID
                asset.setStatus(Asset.Status.AVAILABLE);
                session.merge(asset); // Ensure asset update is recognized
            }

            session.merge(existingBooking); // Ensure booking update is saved
            session.flush(); // Force Hibernate to apply updates to the database
            session.getTransaction().commit();

            logger.info("Booking with ID {} has been closed and assets updated.", bookingId);
            System.out.println("Booking with ID " + bookingId + " has been closed and assets updated.");
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            logger.error("Error closing booking with ID {}: {}", bookingId, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public void delete(int bookingId) {
        logger.info("Deleting booking with ID: {}", bookingId);
        System.out.println("Deleting booking with ID: " + bookingId);
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();

            Booking booking = session.get(Booking.class, bookingId);
            if (booking != null) {
                if (booking.getStatus() == Booking.Status.ONGOING) {
                    logger.warn("Cannot delete ongoing booking with ID: {}", bookingId);
                    System.out.println("Cannot delete ongoing booking.");
                } else {
                    session.remove(booking);
                    session.getTransaction().commit();
                    logger.info("Booking with ID {} deleted successfully.", bookingId);
                    System.out.println("Booking deleted successfully.");
                }
            } else {
                logger.warn("Booking not found with ID: {}", bookingId);
                System.out.println("Booking not found.");
            }
        } catch (Exception e) {
            session.getTransaction().rollback();
            logger.error("Error deleting booking with ID {}: {}", bookingId, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public int ongoingBookings() {
        logger.info("Fetching count of ongoing bookings...");
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery("SELECT COUNT(b) FROM Booking b WHERE b.status = :status", Long.class)
                                .setParameter("status", Booking.Status.ONGOING)
                                .getSingleResult();
            logger.info("Ongoing bookings count: {}", count);
            return count.intValue();
        } catch (Exception e) {
            logger.error("Error fetching ongoing bookings count: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public int completedBookings() {
        logger.info("Fetching count of completed bookings...");
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery("SELECT COUNT(b) FROM Booking b WHERE b.status = :status", Long.class)
                                .setParameter("status", Booking.Status.CLOSED)
                                .getSingleResult();
            logger.info("Completed bookings count: {}", count);
            return count.intValue();
        } catch (Exception e) {
            logger.error("Error fetching completed bookings count: {}", e.getMessage(), e);
            return 0;
        }
    }

}
