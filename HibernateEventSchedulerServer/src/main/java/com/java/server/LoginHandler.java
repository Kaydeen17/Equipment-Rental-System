package com.java.server;


public class LoginHandler {

	 public String authenticateUser(String username, String password) {
	        // Hardcoded users with only "Admin" and "User" roles
	        String[][] validClients = {
	            {"j.bogle", "12345", "Admin"},
	            {"k.walker", "12345", "User"},
	            {"a.harris", "12345", "User"},
	            {"e.muhammad", "12345", "User"},
	            {"s.henry", "12345", "User"},
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