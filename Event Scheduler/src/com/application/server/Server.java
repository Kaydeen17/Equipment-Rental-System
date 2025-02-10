package com.application.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		
		// Create an instance of DatabaseDriver
        DatabaseDriver dbDriver = new DatabaseDriver();

        // Call the method to connect to the database
        dbDriver.ConnectToDatabase();
		
		Socket socket= null;
		InputStreamReader inputStreamReader= null;
		OutputStreamWriter outputStreamWriter= null;
		BufferedReader bufferedReader = null;
		BufferedWriter bufferedWriter= null;
		ServerSocket serverSocket = null;
		
		serverSocket= new ServerSocket(2323);
		
		while(true) {
			try {
				socket = serverSocket.accept();
				inputStreamReader= new InputStreamReader(socket.getInputStream());
				outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
				 
				bufferedReader = new BufferedReader(inputStreamReader);
				bufferedWriter = new BufferedWriter(outputStreamWriter);
				
				while(true) {
					String msgFromclient = bufferedReader.readLine();
					
					System.out.println("Client: "+ msgFromclient);
					
					bufferedWriter.write("MSG RECIEVED");
					bufferedWriter.newLine();
					bufferedWriter.flush();
					
					if(msgFromclient.equalsIgnoreCase("BYE"))
						break;
				}
				
				socket.close();
				inputStreamReader.close();
				outputStreamWriter.close();
				bufferedReader.close();
				bufferedWriter.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
}
