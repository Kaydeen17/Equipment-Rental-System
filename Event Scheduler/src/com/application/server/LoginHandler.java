package com.application.server;


public class LoginHandler {

	 public String authenticateUser(String username, String password) {
	        // Hardcoded users with only "Admin" and "User" roles
	        String[][] validClients = {
	            {"jb", "12345", "Admin"},
	            {"TestClient2", "abc12345", "User"},
	            {"TestClient3", "abc12345", "User"} 
	        };

	        // Check if input matches any valid client
	        for (String[] client : validClients) {
	            if (client[0].equals(username) && client[1].equals(password)) {
	                return client[2]; // Return role (Admin or User)
	            }
	        }
	        

	        return null; // Login failed
	    }
}

