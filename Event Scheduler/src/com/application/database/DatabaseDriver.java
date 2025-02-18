package com.application.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class DatabaseDriver {

    private static Connection conn = null;  // Holds the connection to the database
    private static Statement stmt = null;   // Statement object to execute queries
    private static final String dbName = "EventSchedulerDb";  // Database name

    // Synchronized method to ensure thread safety when connecting to the database
    public synchronized boolean connectToDatabase() {
        try {
            if (getConn() == null || getConn().isClosed()) {
                System.out.println("Attempting to connect to the database...");

                // Try to establish a connection to the MySQL server
                try {
                    setConn(DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", ""));
                } catch (SQLException e) {
                    System.err.println("Database connection failed: Server might be offline.");
                    return false;
                }

                stmt = getConn().createStatement();

                // Check if the database exists
                String checkDbQuery = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '" + dbName + "'";
                ResultSet rs = stmt.executeQuery(checkDbQuery);

                // If the database doesn't exist, create it
                if (!rs.next()) {
                    String createDbQuery = "CREATE DATABASE `" + dbName + "`";
                    stmt.executeUpdate(createDbQuery);
                    System.out.println("Database successfully created: " + dbName);
                }

                // Reconnect to the specific database
                setConn(DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dbName, "root", ""));
                System.out.println("Connected to database: " + dbName);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
        return false;
    }

 // Execute SQL script from a .sql file
    public synchronized boolean executeSQLScript(String filePath) {
        try {
            if (getConn() != null && !getConn().isClosed()) {
                // Select the database to use
                String useDbQuery = "USE " + dbName;
                stmt.executeUpdate(useDbQuery); // Use the 'EventSchedulerDb' database

                // Proceed with reading the SQL file
                BufferedReader reader = new BufferedReader(new FileReader(filePath));
                StringBuilder queryBuilder = new StringBuilder();
                String line;

                // Read the SQL file line by line
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty() || line.startsWith("--")) {
                        continue;
                    }
                    queryBuilder.append(line);

                    // If we encounter a semicolon, it's the end of a query
                    if (line.endsWith(";")) {
                        String query = queryBuilder.toString();
                        try {
                            stmt.executeUpdate(query); // Execute the query
                        } catch (SQLException e) {
                            // If the error is related to the table already existing, just continue
                            if (e.getMessage().contains("already exists")) {
                                System.out.println("Table already exists, continuing...");
                            } else {
                                System.err.println("Error executing query: " + e.getMessage());
                                return false; // If the error is something else, return false
                            }
                        }
                        queryBuilder.setLength(0); // Reset the query builder
                    }
                }

                reader.close(); // Close the file reader

                // We don't need to check if tables exist anymore because we are using "IF NOT EXISTS" in the SQL script
                System.out.println("SQL Script executed successfully.");
                return true;
            } else {
                System.err.println("No active database connection.");
            }
        } catch (IOException | SQLException e) {
            System.err.println("Error executing SQL script: " + e.getMessage());
        }
        return false;  // Return false if any error occurs
    }


 // Method to check if a table exists in the database
    public boolean checkIfTableExists(String tableName) {
        try {
            if (getConn() != null && !getConn().isClosed()) {
                String checkTableQuery = "SHOW TABLES LIKE '" + tableName + "'";
                ResultSet rs = stmt.executeQuery(checkTableQuery);
                return rs.next();  // Returns true if the table exists
            }
        } catch (SQLException e) {
            System.err.println("Error checking table existence: " + e.getMessage());
        }
        return false;
    }

    // Getter for the database connection
    public static Connection getConn() {
        return conn;
    }

    // Setter for the database connection
    public static void setConn(Connection conn) {
        DatabaseDriver.conn = conn;
    }

    // Close the database connection and clean up resources
    public synchronized void closeConnection() {
        try {
            if (stmt != null) {
                stmt.close();  // Close the statement
            }
            if (getConn() != null && !getConn().isClosed()) {
                getConn().close();  // Close the database connection
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}

	
	
