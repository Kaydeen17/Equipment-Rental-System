package server;

import java.io.*;
import java.net.*;

import database.DatabaseDriver;

public class Server {
    // Define the server port number
    private static final int PORT = 2323;
    private static final LoginHandler loginHandler = new LoginHandler();  // Handler for user login
    private static final DatabaseDriver dbDriver = new DatabaseDriver();  // Database connection handler

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {  // Create a server socket that listens on the defined port
            // **Connect to the database before accepting client connections**
            if (dbDriver.connectToDatabase()) {
                System.out.println("Database connected successfully.");
                
                // create tables if they do not exist
                boolean tablesCreated = dbDriver.executeSQLScript("src\\sql\\DatabaseSchema.sql");
                if (tablesCreated) {
                    System.out.println("Tables created successfully.");
                } else {
                    System.err.println("Error: Tables could not be created or some tables are missing.");
                }
            } else {
                System.err.println("Database connection failed. Exiting server.");
                return; // Exit the server if the database connection fails
            }

            while (true) {
                try {
                    System.out.println("Server started. Waiting for clients...");
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
        private String clientEmail;

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
                    try {
                        // Read email and password sent by the client
                        clientEmail = bufferedReader.readLine();
                        String password = bufferedReader.readLine();

                        // Log the login attempt (can replace with log4j2 for logging)
                        System.out.println("Login attempt - Email: " + clientEmail + ", Password: " + password);

                        // Authenticate user using the provided credentials
                        if (loginHandler.authenticateUser(clientEmail, password) != null) {
                            authenticated = true;
                            System.out.println("Login successful: " + clientEmail);
                            bufferedWriter.write("Login successful " + clientEmail + "\n");
                            bufferedWriter.flush();
                        } else {
                            bufferedWriter.write("Invalid credentials, try again.\n");
                            bufferedWriter.flush();
                            System.out.println("Invalid login attempt - Email: " + clientEmail);
                        }
                    } catch (IOException e) {
                        System.out.println("Error reading login credentials: " + e.getMessage());
                        e.printStackTrace();
                        break;
                    }
                }

                // Keep session active while the client is connected
                while (authenticated) {
                    try {
                        String message = bufferedReader.readLine();
                        if (message == null || message.equalsIgnoreCase("logout")) {
                            System.out.println("Client logged out: " + clientEmail);
                            break;
                        }
                    } catch (IOException e) {
                    	//if client logs out by closing window
                        System.out.println("Client exited window abruptly: " + clientEmail);
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error during client handling: " + e.getMessage());
                e.printStackTrace();
            } finally {
                logoutUser();
            }
        }

        private void logoutUser() {
            if (clientEmail != null) {
                System.out.println("Cleaning up session for: " + clientEmail);
            }
            try {
                if (bufferedReader != null) bufferedReader.close();
                if (bufferedWriter != null) bufferedWriter.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.out.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}


