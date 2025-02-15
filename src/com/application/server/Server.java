package com.application.server;


import java.io.*;
import java.net.*;

public class Server {
    // Define the server port number
    private static final int PORT = 2323;
    private static final LoginHandler loginHandler = new LoginHandler();  // Handler for user login
    private static final DatabaseDriver dbDriver = new DatabaseDriver();  // Database connection handler

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {  // Create a server socket that listens on the defined port
            System.out.println("Server started. Waiting for clients...");

            while (true) {
                try {
                    // Accept an incoming client connection
                    Socket socket = serverSocket.accept();
                    new ClientHandler(socket).start();  // Start a new thread for each connected client
                } catch (IOException e) {
                    // Log error if there is an issue accepting the connection
                    System.out.println("Error accepting client connection: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            // Log error if there is an issue starting the server
            System.out.println("Error starting the server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ClientHandler class is responsible for handling client login and session
    static class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader bufferedReader;
        private BufferedWriter bufferedWriter;
        private boolean authenticated = false;

        // Constructor to initialize socket and set up I/O streams
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                // Initialize input and output streams for communication with the client
                InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
                bufferedReader = new BufferedReader(inputStreamReader);
                bufferedWriter = new BufferedWriter(outputStreamWriter);

                // Login loop for authenticating the client
                while (!authenticated) {
                    String email = null;
                    String password = null;

                    try {
                        // Read email and password sent by the client
                        email = bufferedReader.readLine();
                        password = bufferedReader.readLine();
                    } catch (IOException e) {
                        // Log error if there is an issue reading the login credentials
                        System.out.println("Error reading login credentials: " + e.getMessage());
                        e.printStackTrace();
                        break;
                    }

                    // Log the login attempt (can replace with log4j2 for logging)
                    System.out.println("Login attempt - Email: " + email + ", Password: " + password);

                    // Authenticate user using the provided credentials
                    if (loginHandler.authenticateUser(email, password) != null) {
                        try {
                            // Inform the client that login is successful
                            System.out.println("Login successful! Connecting to database...");
                            bufferedWriter.flush();
                        } catch (IOException e) {
                            // Log error if there is an issue sending the success message
                            System.out.println("Error sending success message: " + e.getMessage());
                            e.printStackTrace();
                        }
                        authenticated = true;

                        // Log successful login (can replace with log4j2 for logging)
                        System.out.println("Login successful for user: " + email);

                        // **Connect to the database**
                        try {
                            // Attempt to connect to the database
                            if (dbDriver.connectToDatabase() == true) {
                                // If database connection is successful, log it
                                System.out.println("Database connected successfully.");
                            } else {
                                // Log database connection failure and break out of the loop
                                System.err.println("Database connection failed. Please check the server status.");
                                break; // Stop processing if database connection fails
                            }
                        } catch (Exception e) {
                            // Log unexpected errors during database connection (can replace with log4j2)
                            System.err.println("Unexpected error while connecting to the database: " + e.getMessage());
                            e.printStackTrace();
                            break;
                        }

                        // Keep the session active while the user is authenticated
                        while (authenticated) {
                            // Placeholder for further communication with the client
                            // This loop could be used for more client-server interactions, e.g., data exchange
                        }
                    } else {
                        try {
                            // Inform the client of invalid credentials and prompt for retry
                            bufferedWriter.write("Invalid credentials, try again.\n");
                            bufferedWriter.flush();
                        } catch (IOException e) {
                            // Log error if there is an issue sending the invalid credentials message
                            System.out.println("Error sending invalid credentials message: " + e.getMessage());
                            e.printStackTrace();
                        }

                        // Log failed login attempt (can replace with log4j2 for logging)
                        System.out.println("Invalid login attempt - Email: " + email);
                    }
                }
            } catch (IOException e) {
                // Log error if there are issues with communication (e.g., network failure)
                System.out.println("Error during client handling: " + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    // Close resources when done (e.g., socket, streams)
                    if (bufferedReader != null) bufferedReader.close();
                    if (bufferedWriter != null) bufferedWriter.close();
                    if (socket != null) socket.close();
                } catch (IOException e) {
                    // Log error if there is an issue closing resources (can replace with log4j2 for logging)
                    System.out.println("Error closing resources: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}



