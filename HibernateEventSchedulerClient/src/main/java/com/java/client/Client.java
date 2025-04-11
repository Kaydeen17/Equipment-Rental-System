package com.java.client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.java.domain.Asset;
import com.java.domain.Booking;
import com.java.domain.Invoice;

public class Client {

    private Socket socket;
    private ObjectOutputStream objOs; // For sending objects
    private ObjectInputStream objIs;  // For receiving objects
    private boolean isAuthenticated = false; // Track login status
	private String currentClient;
	private static final Logger logger = LogManager.getLogger(Client.class);

	// Default constructor to initialize connection and streams
    public Client() {
        createConnection();
        configureStreams();
    }

    //Creates a socket connection to the server
    private void createConnection() {
        try {
            socket = new Socket("localhost", 2323); // Connect to the server
            System.out.println("Connected to the server.");
            logger.info("Client {} connected to the server.", currentClient); // Log the client that has connected
        } catch (IOException e) {
            System.out.println("Error connecting to the server: " + e.getMessage());
            logger.error("Error connecting to the server for client {}: {}", currentClient, e.getMessage(), e);
        }
    }

    //Configures the input and output streams.
    private void configureStreams() {
        try {
            objOs = new ObjectOutputStream(socket.getOutputStream());
            objOs.flush();  // Ensure the stream is ready for use
            objIs = new ObjectInputStream(socket.getInputStream());
            System.out.println("Stream configuration successful.");
            logger.info("Stream configuration successful for client {}", currentClient); // Log the stream configuration for the client
        } catch (IOException e) {
            System.out.println("Error configuring streams: " + e.getMessage());
            logger.error("Error configuring streams for client {}: {}", currentClient, e.getMessage(), e);
        }
    }


    /**
     * Attempts to log in to the server with the provided credentials.
     *
     * @param username The username of the user trying to log in.
     * @param password The password of the user.
     * @return Returns true if login is successful, false otherwise.
     */
    public boolean loginToServer(String username, String password) {
        try {
            // Send login credentials
            objOs.writeObject(username);
            objOs.writeObject(password);
            objOs.flush();
            currentClient = username;
            
            // Receive server response
            String response = (String) objIs.readObject();
            System.out.println("Server response: " + response);
            logger.info("Login attempt by client {}. Server response: {}", currentClient, response);  // Log the login attempt

            if (response.contains("Login successful")) {
                isAuthenticated = true;
                logger.info("Login successful for client {}", currentClient);  // Log successful login
                return true;
            } else {
                System.out.println("Login failed: " + response);
                logger.warn("Login failed for client {}: {}", currentClient, response);  // Log failed login attempt
                return false;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error during login: " + e.getMessage());
            logger.error("Error during login for client {}: {}", currentClient, e.getMessage(), e);  // Log error during login
            e.printStackTrace();
            return false;
        }
    }


    public String getCurrentClient() {
		return currentClient;
	}

    /**
     * Sends an action to the server.
     *
     * @param action The action to send.
     */
    public void sendAction(String action) {
        try {
            objOs.writeObject(action);
            objOs.flush();
            logger.info("Client {} sent action '{}' to the server.", currentClient, action);  // Log the action sent by the client
        } catch (IOException e) {
            System.out.println("Error sending action: " + e.getMessage());
            logger.error("Error sending action '{}' for client {}: {}", action, currentClient, e.getMessage(), e);  // Log error with client info
            e.printStackTrace();
        }
    }

    /**
     * Sends an object (Asset) to the server.
     *
     * @param asset The asset object to send.
     */
    public void sendAsset(Asset asset) {
        if (!isAuthenticated) {
            System.out.println("You must be logged in to send an asset.");
            logger.warn("Client {} is not authenticated. Cannot send asset.", currentClient);  // Log unauthenticated access attempt
            return;
        }

        try {
            objOs.writeObject(asset);
            objOs.flush();
            logger.info("Client {} sent asset to the server: {}", currentClient, asset);  // Log the asset sent by the client
        } catch (IOException e) {
            System.out.println("Error sending asset: " + e.getMessage());
            logger.error("Error sending asset '{}' for client {}: {}", asset, currentClient, e.getMessage(), e);  // Log error with asset details and client info
            e.printStackTrace();
        }
    }


    /**
     * Sends an integer ID to the server.
     *
     * @param id The integer ID to send.
     * @return The server's response.
     */
    /**
     * Sends an integer ID to the server.
     *
     * @param id The integer ID to send.
     * @return The Asset object or null if an error occurs.
     */
    public Asset sendID(int id) {
        if (!isAuthenticated) {
            // Return null or throw an exception if not authenticated
            System.out.println("Error: Not logged in.");
            logger.warn("Client {} is not authenticated. Cannot send ID.", currentClient);  // Log unauthenticated access attempt
            return null;
        }

        try {
            objOs.writeObject(id);
            objOs.flush();
            logger.info("Client {} sent ID '{}' to the server.", currentClient, id);  // Log the ID sent by the client
            
            Object response = objIs.readObject(); // Receive response from server

            if (response instanceof String) {
                System.out.println(response); // Log the message
                logger.info("Client {} received response: {}", currentClient, response);  // Log the server response
                return null; // No asset found
            } else if (response instanceof Asset) {
                logger.info("Client {} received asset: {}", currentClient, response);  // Log the received asset
                return (Asset) response; // Asset found, return it
            }else {
                System.out.println("Unexpected response type from server: " + response.getClass().getName());
                logger.warn("Client {} received unexpected response type: {}", currentClient, response.getClass().getName());  // Log unexpected response
                return null; // Handle unexpected cases safely
            }

        } catch (IOException | ClassNotFoundException e) {
            // Log the error and return null if something goes wrong
            System.out.println("Error sending ID: " + e.getMessage());
            logger.error("Error sending ID '{}' for client {}: {}", id, currentClient, e.getMessage(), e);  // Log error with client info
            e.printStackTrace();
            return null; // Return null in case of error
        }
    }

    /**
     * Sends a logout request to the server before closing the connection.
     */
    public void logout() {
        if (!isAuthenticated) {
            System.out.println("You are not logged in.");
            logger.warn("Client {} attempted to logout without being logged in.", currentClient);  // Log logout attempt without authentication
            return;
        }

        try {
            objOs.writeObject("logout");
            objOs.flush();
            isAuthenticated = false;
            System.out.println("Logged out successfully.");
            logger.info("Client {} logged out successfully.", currentClient);  // Log successful logout
            closeConnection(); 
        } catch (IOException e) {
            System.out.println("Error sending logout message: " + e.getMessage());
            logger.error("Error logging out for client {}: {}", currentClient, e.getMessage(), e);  // Log logout error
        } finally {
            closeConnection();
        }
    }

    /**
     * Closes the client connection by closing the socket and associated streams.
     */
    public void closeConnection() {
        try {
            if (objOs != null) objOs.close();
            if (objIs != null) objIs.close();
            if (socket != null) socket.close();
            isAuthenticated = false;
            System.out.println("Client disconnected from server.");
            logger.info("Client {} disconnected from the server.", currentClient);  // Log disconnection
        } catch (IOException e) {
            System.out.println("Error closing connection: " + e.getMessage());
            logger.error("Error closing connection for client {}: {}", currentClient, e.getMessage(), e);  // Log connection close error
            e.printStackTrace();
        }
    }

    /**
     * Receives an object from the server.
     *
     * @return The object received from the server.
     */
    //polymorphism
    public Object receiveObject() {
        try {
            Object receivedObject = objIs.readObject();  // Read and return the object sent by the server
            logger.info("Client {} received object: {}", currentClient, receivedObject);  // Log the received object
            return receivedObject;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error receiving object: " + e.getMessage());
            logger.error("Error receiving object for client {}: {}", currentClient, e.getMessage(), e);  // Log error with client info
            e.printStackTrace();
            return null;  // Return null if an error occurs
        }
    }

    
    /**
     * Sends a booking object to the server.
     *
     * @param booking The booking object to send.
     */
    public void sendBooking(Booking booking) {
        if (!isAuthenticated) {
            // Log a warning if the client is not authenticated and return early
            System.out.println("You must be logged in to send a booking.");
            logger.warn("Client {} is not authenticated. Cannot send booking: {}", currentClient, booking);  // Log the attempt
            return;
        }

        try {
            objOs.writeObject(booking);  // Send the booking object to the server
            objOs.flush();  // Ensure the data is flushed and sent
            System.out.println("Booking sent to server: " + booking);
            logger.info("Client {} sent booking to the server: {}", currentClient, booking);  // Log the booking sent by the client
        } catch (IOException e) {
            // Log any IO exception that occurs during the sending process
            System.out.println("Error sending booking: " + e.getMessage());
            logger.error("Error sending booking for client {}: {}", currentClient, e.getMessage(), e);  // Log the error
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a booking by ID from the server.
     *
     * @param id The ID of the booking to retrieve.
     * @return The Booking object, or null if not found or an error occurs.
     */
    public Booking sendBookingID(int id) {
        if (!isAuthenticated) {
            // Log and return early if the client is not authenticated
            System.out.println("Error: Not logged in.");
            logger.warn("Client {} is not authenticated. Cannot send booking ID '{}'.", currentClient, id);  // Log the failed attempt
            return null;
        }

        try {
            objOs.writeInt(id);  // Send the booking ID to the server
            objOs.flush();  // Ensure the ID is sent immediately
            
            // Receive the response from the server
            Object response = objIs.readObject();  // Read the object returned by the server

            if (response instanceof String) {
                // If the response is a string, log the message and return null
                System.out.println(response);
                logger.info("Client {} received response: {}", currentClient, response);  // Log the response
                return null;  // No booking found
            } else if (response instanceof Booking) {
                // If the response is an instance of Booking, return the Booking object
                logger.info("Client {} received booking with ID '{}': {}", currentClient, id, response);  // Log the received booking
                return (Booking) response;  // Return the found booking
            } else {
                // Log and handle unexpected responses
                System.out.println("Unexpected response type from server: " + response.getClass().getName());
                logger.warn("Client {} received unexpected response type: {}", currentClient, response.getClass().getName());  // Log the unexpected type
                return null;  // Return null in case of unexpected response
            }

        } catch (IOException | ClassNotFoundException e) {
            // Log any exceptions that occur during the process
            System.out.println("Error sending booking ID: " + e.getMessage());
            logger.error("Error sending booking ID '{}' for client {}: {}", id, currentClient, e.getMessage(), e);  // Log the error
            e.printStackTrace();
            return null;  // Return null if an error occurs
        }
    }

    /**
     * Retrieves the list of assets from the server.
     *
     * @return A list of assets, or an empty list if an error occurs or the response is invalid.
     */
    @SuppressWarnings("unchecked")
    public List<Asset> getAssetList() {
        try {
            // Receive the response from the server
            Object response = objIs.readObject();  // Read the object sent by the server
            
            if (response instanceof List<?>) {
                // If the response is a valid list, cast it and return it
                logger.info("Client {} received asset list: {}", currentClient, response);  // Log the received asset list
                return (List<Asset>) response;  // Return the list of assets
            } else {
                // Log and handle invalid or unexpected responses
                System.out.println("Unexpected response: " + response);
                logger.warn("Client {} received unexpected response: {}", currentClient, response);  // Log the invalid response
                return new ArrayList<>();  // Return an empty list if the response is invalid
            }

        } catch (IOException | ClassNotFoundException e) {
            // Log any errors during the process
            System.out.println("Error receiving object: " + e.getMessage());
            logger.error("Error receiving asset list for client {}: {}", currentClient, e.getMessage(), e);  // Log the error
            e.printStackTrace();
            return new ArrayList<>();  // Return an empty list to prevent crashes
        }
    }

    /**
     * Retrieves a list of bookings from the server.
     *
     * @return A list of bookings, or an empty list if the user is not authenticated or an error occurs.
     */
    @SuppressWarnings("unchecked")
    public List<Booking> getBookingList() {
        // Check if the user is authenticated before making the request
        if (!isAuthenticated) {
            // Log the warning when the user is not authenticated
            System.out.println("You must be logged in to retrieve bookings.");
            logger.warn("Client {} is not authenticated. Cannot retrieve booking list.", currentClient);  // Log the failed attempt
            return new ArrayList<>();  // Return an empty list if not authenticated
        }

        try {
            // Receive the server's response
            Object response = objIs.readObject();

            // Check if the response is a List of Booking objects
            if (response instanceof List<?>) {
                logger.info("Client {} received booking list: {}", currentClient, response);  // Log the received booking list
                return (List<Booking>) response;  // Safely cast the response to List<Booking> and return
            } else {
                // Handle unexpected response types
                System.out.println("Unexpected response type: " + response.getClass().getName());
                logger.warn("Client {} received unexpected response type: {}", currentClient, response.getClass().getName());  // Log the unexpected response type
                return new ArrayList<>();  // Return an empty list if the response type is invalid
            }

        } catch (IOException | ClassNotFoundException e) {
            // Handle any exceptions that occur during the process
            System.out.println("Error receiving object: " + e.getMessage());
            logger.error("Error receiving booking list for client {}: {}", currentClient, e.getMessage(), e);  // Log the error
            e.printStackTrace();
            return new ArrayList<>();  // Return an empty list instead of null to prevent crashes
        }
    }

    /**
     * Retrieves all invoices from the server.
     *
     * @return A list of invoices, or null if an error occurs.
     */
    @SuppressWarnings("unchecked")
    public List<Invoice> getAllInvoices() {
        try {
            // Receive the server's response
            Object response = objIs.readObject();

            // Check if the response is a List of Invoice objects
            if (response instanceof List) {
                logger.info("Client {} received invoice list: {}", currentClient, response);  // Log the received invoice list
                return (List<Invoice>) response;  // Cast and return the list of invoices
            } else {
                // Handle unexpected response types
                System.out.println("Server response: " + response);
                logger.warn("Client {} received unexpected response: {}", currentClient, response);  // Log the unexpected response
            }
        } catch (Exception e) {
            // Log the error if an exception occurs
            e.printStackTrace();
            logger.error("Error receiving invoice list for client {}: {}", currentClient, e.getMessage(), e);  // Log the exception
        }
        return null;  // Return null if an error occurs
    }

 // Read a specific invoice by ID
    public Object readInvoice(int invoiceId) {
        try {
            objOs.writeObject("INVOICE READ");
            objOs.flush();
            objOs.writeObject(invoiceId);
            objOs.flush();

            // Receive the response from the server (could be an Invoice or String error)
            Object response = objIs.readObject();
            logger.info("Client {} read invoice with ID {}: {}", currentClient, invoiceId, response);  // Log invoice read request
            return response;
        } catch (Exception e) {
            logger.error("Client {} error reading invoice ID {}: {}", currentClient, invoiceId, e.getMessage(), e);  // Log error
            e.printStackTrace();
            return "Error reading invoice.";
        }
    }

    // Receive payment for an invoice
    public Object receiveInvoicePayment(int invoiceId) {
        try {
            objOs.writeObject(invoiceId);
            objOs.flush();

            // Receive the response (could be a success message or error message)
            Object response = objIs.readObject();
            logger.info("Client {} received payment for invoice ID {}: {}", currentClient, invoiceId, response);  // Log payment received
            return response;
        } catch (Exception e) {
            logger.error("Client {} error receiving payment for invoice ID {}: {}", currentClient, invoiceId, e.getMessage(), e);  // Log error
            e.printStackTrace();
            return "Error receiving payment.";
        }
    }

    // Send the invoice object to the server for processing
    public void sendInvoice(Invoice invoice) {
        try {
            objOs.writeObject(invoice);
            objOs.flush();
            logger.info("Client {} sent invoice for processing: {}", currentClient, invoice);  // Log the invoice sent
        } catch (Exception e) {
            logger.error("Client {} error sending invoice for export: {}", currentClient, e.getMessage(), e);  // Log error
            e.printStackTrace();
            try {
                objOs.writeObject("Error sending invoice for export.");
                objOs.flush();
            } catch (Exception ex) {
                logger.error("Client {} error while sending error message: {}", currentClient, ex.getMessage(), ex);  // Log the error of sending error message
                ex.printStackTrace();
            }
        }
    }

    // Send the invoice ID to the server
    public void sendInvoiceId(int invoiceId) {
        try {
            objOs.writeObject(invoiceId);  // Send the integer invoiceId to the server
            objOs.flush();
            logger.info("Client {} sent invoice ID to server: {}", currentClient, invoiceId);  // Log the invoice ID sent
        } catch (IOException e) {
            logger.error("Client {} failed to send invoice ID to server: {}", currentClient, e.getMessage(), e);  // Log error
            e.printStackTrace();
            System.err.println("Failed to send invoice ID to server.");
        }
    }

    // Send a request to export an invoice (with ID and file path)
    public void sendInvoiceExportRequest(int invoiceId, String filePath) {
        try {
            Object[] data = new Object[]{invoiceId, filePath};  // Create an array to hold both the invoice ID and file path
            objOs.writeObject(data);  // Send the array object to the server
            objOs.flush();
            logger.info("Client {} sent invoice export request: invoiceId={} filePath={}", currentClient, invoiceId, filePath);  // Log export request
        } catch (IOException e) {
            logger.error("Client {} failed to send invoice export request: {}", currentClient, e.getMessage(), e);  // Log error
            e.printStackTrace();
        }
    }

    public void sendObject(Object[] objects) {
        try {
            if (objOs != null) {
                objOs.writeObject(objects);
                objOs.flush();
                logger.info("Object array sent to server successfully.");
            } else {
                System.err.println("ObjectOutputStream is null. Cannot send objects.");
                logger.error("ObjectOutputStream is null. Cannot send objects.");
            }
        } catch (Exception e) {
        	logger.error("Error sending object array to server: {}", e.getMessage(), e);
            System.err.println("Error sending object array: " + e.getMessage());
            e.printStackTrace();
        }
    }




}
