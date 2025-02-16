package com.application.server;


import java.sql.*;

public class DatabaseDriver {

    private static Connection conn = null;  // Holds the connection to the database
    private static Statement stmt = null;   // Statement object to execute queries
    private static final String dbName = "EventSchedulerDb";  // Database name

    // Synchronized method to ensure thread safety when connecting to the database
    public synchronized boolean connectToDatabase() {
        try {
            // Check if a connection already exists or if the existing connection is closed
            if (getConn() == null || getConn().isClosed()) {
                System.out.println("Attempting to connect to the database...");

                // Try to establish a connection to the MySQL server
                try {
                    setConn(DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", ""));
                } catch (SQLException e) {
                    // Log error if the connection to MySQL server fails
                    System.err.println("Database connection failed: Server might be offline.");
                    return false;  // **Return false if connection fails**
                }

                // Check if the connection is still null or closed
                if (getConn() == null || getConn().isClosed()) {
                    System.err.println("Database connection is null or closed. Exiting...");
                    return false;  // **Return false if connection is still not established**
                }

                stmt = getConn().createStatement();

                // Step 1: Check if the database exists by querying the information schema
                String checkDbQuery = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '" + dbName + "'";
                ResultSet rs = stmt.executeQuery(checkDbQuery);

                // If the database is found
                if (rs.next()) {
                    System.out.println("Database found: " + dbName);
                } else {
                    // Step 2: If the database doesn't exist, try creating it
                    try {
                        String createDbQuery = "CREATE DATABASE `" + dbName + "`";
                        stmt.executeUpdate(createDbQuery);
                        System.out.println("Database successfully created: " + dbName);
                    } catch (SQLException e) {
                        // Log error if creating the database fails
                        System.err.println("Error creating database: " + e.getMessage());
                        return false;  // **Return false if database creation fails**
                    }
                }

                // Step 3: Reconnect using the actual database
                try {
                    setConn(DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dbName, "root", ""));
                } catch (SQLException e) {
                    // Log error if final connection to the database fails
                    System.err.println("Failed to connect to the database '" + dbName + "': " + e.getMessage());
                    return false;  // **Return false if final connection fails**
                }

                // Check if the connection is still null or closed
                if (getConn() == null || getConn().isClosed()) {
                    System.err.println("Final database connection is null or closed. Exiting...");
                    return false;
                }

                // Log successful connection to the database
                System.out.println("Connected to database: " + dbName);
                return true;  // **Return true if everything was successful**
            }
        } catch (SQLException e) {
            // Log any SQL exceptions that occur while connecting to the database
            System.err.println("Database connection error: " + e.getMessage());
        }
        return false;  // **Return false if an exception occurs**
    }

    /**
     * Executes a SQL query and processes the results
     * 
     * @param query The SQL query to execute (e.g., SELECT, UPDATE, DELETE)
     */
    public synchronized void executeQuery(String query) {
        try {
            // Ensure that there is an active database connection before executing the query
            if (getConn() != null && !getConn().isClosed()) {
                // If the query is a SELECT statement
                if (query.toUpperCase().startsWith("SELECT")) {
                    ResultSet rs = stmt.executeQuery(query);
                    // Process the results of the SELECT query
                    while (rs.next()) {
                        System.out.println("Query Result: " + rs.getString(1)); // Example: output the first column
                    }
                } else {
                    // If the query is an UPDATE, DELETE, or INSERT statement
                    stmt.executeUpdate(query);
                    System.out.println("Query executed successfully.");
                }
            } else {
                // Log if there is no active connection to execute the query
                System.err.println("No active database connection.");
            }
        } catch (SQLException e) {
            // Log errors that occur during the execution of the query (e.g., invalid SQL)
            System.err.println("Query execution error: " + e.getMessage());
        }
    }

    /**
     * Executes an SQL update/insert query (for modifying data)
     * 
     * @param query The SQL query to execute (e.g., UPDATE, INSERT)
     */
    public synchronized void executeUpdate(String query) {
        try {
            // Ensure that there is an active database connection before executing the update/insert query
            if (getConn() != null && !getConn().isClosed()) {
                stmt.executeUpdate(query);
                System.out.println("Update/Insert successful.");
            } else {
                // Log if there is no active connection to execute the update/insert query
                System.err.println("No active database connection.");
            }
        } catch (SQLException e) {
            // Log errors that occur during the update/insert operation
            System.err.println("Error executing update/insert: " + e.getMessage());
        }
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
            // Log error if there is an issue closing the connection
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    // Getter for the database connection
    public static Connection getConn() {
        return conn;
    }

    // Setter for the database connection
    public static void setConn(Connection conn) {
        DatabaseDriver.conn = conn;
    }
}

	
	
