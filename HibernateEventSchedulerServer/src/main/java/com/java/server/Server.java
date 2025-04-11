package com.java.server;

import java.io.*;
import java.net.*;
import org.hibernate.cfg.Configuration;
import com.java.hibernate.AssetManager;
import com.java.hibernate.BookingManager;
import com.java.hibernate.InvoiceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Server {
    private static final int PORT = 2323;
    private static final LoginHandler loginHandler = new LoginHandler();
    private static final AssetManager assetManager = new AssetManager();
    private static final BookingManager bookingManager = new BookingManager();
    private static final InvoiceManager invoiceManager = new InvoiceManager();
    
    private static final Logger logger = LogManager.getLogger(Server.class); // Logger instance

    public static void startServer() {
        new Thread(() -> {
            try {
                new Configuration()
                        .configure("hibernate.cfg.xml")
                        .addAnnotatedClass(com.java.domain.Asset.class)
                        .buildSessionFactory();

                try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                    logger.info("Server started. Waiting for clients...");
                    while (true) {
                        try {
                            Socket socket = serverSocket.accept();
                            new ClientHandler(socket).start();
                        } catch (IOException e) {
                            logger.error("Error accepting client connection: " + e.getMessage(), e);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Error starting the server: " + e.getMessage(), e);
            }
        }).start(); // start in a new thread so GUI doesn't freeze
    }

    public static void main(String[] args) {
        startServer();
    }

    static class ClientHandler extends Thread {
        private Socket socket;
        private ObjectOutputStream objOs;
        private ObjectInputStream objIs;
        private boolean authenticated = false;
        private String clientEmail;
        private AssetCommandHandler assetCommandHandler;
        private BookingCommandHandler bookingCommandHandler;
        private InvoiceCommandHandler invoiceCommandHandler;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                configureStreams();
                authenticateUser();
                if (!authenticated) return; // Stop execution if authentication failed

                // Initialize command handlers
                assetCommandHandler = new AssetCommandHandler(objIs, objOs, assetManager);
                bookingCommandHandler = new BookingCommandHandler(objIs, objOs, bookingManager);
                invoiceCommandHandler = new InvoiceCommandHandler(objIs, objOs, invoiceManager);

                while (authenticated) {
                    try {
                        logger.info("Waiting for commands....");
                        String message = (String) objIs.readObject();
                        message = message.toUpperCase();
                        if (message == null || message.equalsIgnoreCase("logout")) {
                            logger.info("Client logged out: " + clientEmail);
                            break;
                        }

                        logger.info("Received Command: " + message);

                        // Determine which handler should process the command
                        if (message.startsWith("ASSET")) {
                            assetCommandHandler.handleAssetCommands(message);
                        } else if (message.startsWith("BOOKING")) {
                            bookingCommandHandler.handleBookingCommands(message);
                        } else if (message.startsWith("INVOICE")) {
                            invoiceCommandHandler.handleInvoiceCommands(message);
                        }else if (message.equalsIgnoreCase("LOGOUT")) {
                            logoutUser();
                        }else {
                            objOs.writeObject("Unknown command: " + message);
                            objOs.flush();
                        }

                    } catch (EOFException | SocketException e) {
                        logger.info("Client disconnected: " + clientEmail);
                        break;
                    } catch (ClassNotFoundException e) {
                        logger.error("Error reading client message: " + e.getMessage(), e);
                        break;
                    }
                }
            } catch (IOException e) {
                logger.error("Client connection closed unexpectedly: " + e.getMessage(), e);
            } finally {
                logoutUser();
            }
        }

        private void configureStreams() throws IOException {
            objOs = new ObjectOutputStream(socket.getOutputStream());
            objOs.flush(); // Prevent client from blocking
            objIs = new ObjectInputStream(socket.getInputStream());
            logger.info("Object streams configured successfully.");
        }

        private void authenticateUser() {
            try {
                for (int i = 0; i < 3; i++) { // Allow 3 login attempts
                    clientEmail = (String) objIs.readObject();
                    String password = (String) objIs.readObject();

                    if (clientEmail == null || password == null) {
                        logger.warn("Received null credentials for login attempt.");
                        objOs.writeObject("Invalid credentials, try again.");
                        objOs.flush();
                        continue;
                    }

                    logger.info("Login attempt - Email: " + clientEmail);

                    if (loginHandler.authenticateUser(clientEmail, password) != null) {
                        authenticated = true;
                        objOs.writeObject("Login successful");
                        objOs.flush();
                        logger.info("Login successful: " + clientEmail);
                        return;
                    } else {
                        objOs.writeObject("Invalid credentials, try again.");
                        objOs.flush();
                        logger.warn("Invalid login attempt - Email: " + clientEmail);
                    }
                }
                logger.error("Too many failed login attempts. Closing connection.");
                objOs.writeObject("Too many failed attempts. Connection closed.");
                objOs.flush();
                logoutUser();
            } catch (IOException | ClassNotFoundException e) {
                logger.error("Error reading login credentials: " + e.getMessage(), e);
                logoutUser();
            }
        }

        private void logoutUser() {
            logger.info("Cleaning up session for: " + clientEmail);
            try {
                if (objOs != null) objOs.close();
                if (objIs != null) objIs.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                logger.error("Error closing resources: " + e.getMessage(), e);
            }
        }
    }
}
