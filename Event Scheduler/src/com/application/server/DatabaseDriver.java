package com.application.server;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseDriver {

	Connection conn= null;
	Statement stmt = null;
	String dbName = "EventSchedulerDb";
	boolean databaseExists = false;
	
	public void ConnectToDatabase(){
		
		//try to connect to database
		 try {
	            // Connect to MySQL Server (without specifying a database)
	            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "");
	            stmt = conn.createStatement();

	            // Step 1: Check if the database exists
	            String checkDbQuery = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '" + dbName + "'";
	            ResultSet rs = stmt.executeQuery(checkDbQuery);
	            
	            System.out.println("Checking for database...");
	            if (rs.next()) {
	                databaseExists = true;
	                System.out.println("Database Found: " + dbName);
	            } else {
	                // Step 2: Create the database since it doesn't exist
	                String createDbQuery = "CREATE DATABASE `" + dbName + "`";
	                stmt.executeUpdate(createDbQuery);
	                System.out.println("Database successfully created: " + dbName);
	            }

	            // Close result set
	            rs.close();
	            stmt.close();
	            conn.close();

	            // Step 3: Reconnect using the database
	            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dbName, "root", "");
	            System.out.println("Connected to database: " + dbName);	
		 	}catch (SQLException e) {
		 		
		 		System.out.println("Database connection error: " + e.getMessage());
		 		
		 		
		 	} finally {
		 		try {
		 			if (stmt != null)
		 				stmt.close();
		 			if(conn != null)
		 				conn.close();
		 		}catch(SQLException e) {
		 			System.out.println("Error closing resources:" + e.getMessage());
		 		}
		 	}
	}
}
	
	
