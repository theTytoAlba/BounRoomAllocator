import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

public class RoomAllocatorServer {
    public static void main(String[] args) throws IOException {
        startServer();
    }

    public static void startServer() {
        (new Thread() {
            @Override
            public void run() {
                ServerSocket serverSocket = null;
                Socket socket;
                BufferedReader in = null;
                BufferedWriter out = null;
                
                // Initialize the connection
                try {
					serverSocket  = new ServerSocket(60015);
				} catch (IOException e) {
					System.out.println("Failed to create connection.");
				}
                
                String line = null;
                	while (true) {
               		try {
                   		socket = serverSocket.accept();
		                in = new BufferedReader(
		                        new InputStreamReader(socket.getInputStream()));
		                out = new BufferedWriter(
		                        new OutputStreamWriter(socket.getOutputStream()));
						while ((line = in.readLine()) != null) {
							System.out.println("Received new message.");
							String message = "FAIL";
						    	try {
									JSONObject obj = new JSONObject(line);
									if (obj.getString("connectionType") != null && logUserIn(obj.getJSONObject("credential"))) {
							    			message = "SUCCESS";
									}
								} catch (JSONException e) {
									System.out.println("Could not parse json.");
								}
								
						    	try {
							    out.write(message);
							    out.newLine();
							    out.flush();
						    	} catch (Exception e) {
						    		System.out.println("Couldn't send reply.");
						    	}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				} 
            }
        }).start();
    }
    public static boolean logUserIn (JSONObject credential) {
    		try {
				String username = credential.getString("username");
	    			String password = credential.getString("password");
	    			return username.equals("admin") && password.equals("pass");
			} catch (JSONException e) {
				System.out.println("Failed to resolve credential JSON.");
				return false;
			}
    }
}