package com.application.client;


import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";  // Address of the server to connect to
    private static final int SERVER_PORT = 2323;  // Port number on which the server is listening
    private static Socket socket;  // Socket object for client-server communication
    private static BufferedReader bufferedReader;  // Reader for receiving data from the server
    private static BufferedWriter bufferedWriter;  // Writer for sending data to the server

    /**
     * Attempts to log in to the server with the provided credentials.
     * 
     * @param email The email address of the user trying to log in.
     * @param password The password of the user.
     * @return Returns true if login is successful, false otherwise.
     */
    public static boolean loginToServer(String email, String password) {
        Socket socket = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;

        try {
            // Step 1: Establish connection to the server using provided address and port
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);  // Open socket connection to server
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
            bufferedReader = new BufferedReader(inputStreamReader);  // Set up buffered reader
            bufferedWriter = new BufferedWriter(outputStreamWriter);  // Set up buffered writer

            // Step 2: Send login credentials (email and password) to the server
            bufferedWriter.write(email + "\n");  // Send email
            bufferedWriter.write(password + "\n");  // Send password
            bufferedWriter.flush();  // Ensure the data is sent to the server

            // Step 3: Receive server's response
            String response = bufferedReader.readLine();
            System.out.println("Server response: " + response);  // Log the response for debugging purposes

            // Step 4: Check if the server response indicates a successful login
            return response.contains("Login successful");  // Return true if login was successful

        } catch (IOException e) {
            // Handle IO exceptions that occur during the login process (e.g., network issues)
            e.printStackTrace();
            return false;  // Return false if an error occurs
        } finally {
            // Step 5: Close the resources in the finally block to ensure cleanup, even if an error occurs
            try {
                if (bufferedReader != null) bufferedReader.close();  // Close the reader
                if (bufferedWriter != null) bufferedWriter.close();  // Close the writer
                if (socket != null) socket.close();  // Close the socket
            } catch (IOException e) {
                e.printStackTrace();  // Handle errors while closing resources
            }
        }
    }

    /**
     * Sends a query to the server and prints the server's response.
     * 
     * @param query The query to send to the server.
     */
    public static void queryServer(String query) {
        try {
            // Step 1: Send the query to the server
            bufferedWriter.write(query + "\n");  // Send query to the server
            bufferedWriter.flush();  // Ensure the query is sent

            // Step 2: Read and print the server's response
            String result = bufferedReader.readLine();  // Read server response
            System.out.println("Server Response: " + result);  // Log the response

        } catch (IOException e) {
            // Handle IO exceptions (e.g., network issues, server not responding)
            e.printStackTrace();
        }
    }

    /**
     * Closes the client connection by closing the socket and associated readers/writers.
     */
    public static void closeConnection() {
        try {
            // Close the resources safely
            if (bufferedReader != null) bufferedReader.close();  // Close the reader
            if (bufferedWriter != null) bufferedWriter.close();  // Close the writer
            if (socket != null) socket.close();  // Close the socket
        } catch (IOException e) {
            // Handle errors while closing the connection
            e.printStackTrace();
        }
    }
}


