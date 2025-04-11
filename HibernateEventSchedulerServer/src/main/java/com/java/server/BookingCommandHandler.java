package com.java.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import com.java.domain.Booking;
import com.java.hibernate.BookingManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BookingCommandHandler {

    private final ObjectInputStream objIs;
    private final ObjectOutputStream objOs;
    private final BookingManager bookingManager;
    private static final Logger logger = LogManager.getLogger(BookingCommandHandler.class);

    public BookingCommandHandler(ObjectInputStream objIs, ObjectOutputStream objOs, BookingManager bookingManager) {
        this.objIs = objIs;
        this.objOs = objOs;
        this.bookingManager = bookingManager;
    }

    public void handleBookingCommands(String command) {
        try {
            switch (command) {
                case "BOOKING CREATE":
                    Object bookingCreateObj = objIs.readObject();
                    if (bookingCreateObj instanceof Booking) {
                        Booking newBooking = (Booking) bookingCreateObj;
                        bookingManager.createBooking(newBooking);
                        objOs.writeObject("Booking created successfully.");
                        objOs.flush();
                    } else {
                        objOs.writeObject("Invalid booking object received.");
                        objOs.flush();
                    }
                    break;

                case "BOOKING UPDATE":
                    Object bookingUpdateObj = objIs.readObject();
                    if (bookingUpdateObj instanceof Booking) {
                        Booking updatedBooking = (Booking) bookingUpdateObj;
                        bookingManager.updateBooking(updatedBooking);
                        objOs.writeObject("Booking updated successfully.");
                        objOs.flush();
                    } else {
                        objOs.writeObject("Invalid booking object received.");
                        objOs.flush();
                    }
                    break;

                case "BOOKING READ":
                	int bookingId = (int) objIs.readInt();
                    Booking booking = bookingManager.readBooking(bookingId);
                	objOs.writeObject(booking != null ? booking : "Booking not found");
                	objOs.flush();
                    break;

                case "BOOKING SHOWALL":
                    List<Booking> bookings = bookingManager.showAllBooking();
                    if (!bookings.isEmpty()) {
                        objOs.writeObject(bookings);
                        objOs.flush();
                    } else {
                        objOs.writeObject("No bookings found.");
                        objOs.flush();
                    }
                    break;

                case "BOOKING CLOSE":
                	int closeObj = (int) objIs.readInt();
                    if (closeObj != 0) {
                        bookingManager.closeBooking(closeObj);
                        objOs.writeObject("Booking closed successfully.");
                        objOs.flush();
                    } else {
                        objOs.writeObject("Invalid booking ID received.");
                        objOs.flush();
                    }
                    break;

                case "BOOKING DELETE":
                	int deleteObj = (int) objIs.readInt();
                    if (deleteObj != 0) {
                        bookingManager.delete(deleteObj);
                        objOs.writeObject("Booking deleted successfully.");
                        objOs.flush();
                    } else {
                        objOs.writeObject("Invalid booking ID received.");
                        objOs.flush();
                    }
                    break;
                case "BOOKING ONGOING":
                    int ongoingCount = bookingManager.ongoingBookings();
                    objOs.writeObject(ongoingCount);
                    objOs.flush();
                    break;

                case "BOOKING COMPLETED":
                    int completedCount = bookingManager.completedBookings();
                    objOs.writeObject(completedCount);
                    objOs.flush();
                    break;
                default:
                    objOs.writeObject("Unknown command: " + command);
                    objOs.flush();
                    break;
            }

            objOs.flush();

        } catch (Exception e) {
            logger.error("Error handling booking command: {}", e.getMessage(), e);
            try {
                objOs.writeObject("Server error: " + e.getMessage());
                objOs.flush();
            } catch (Exception ex) {
                logger.error("Error sending error message to client: {}", ex.getMessage(), ex);
            }
        }
    }
}
