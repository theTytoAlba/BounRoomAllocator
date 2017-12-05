package connection;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import database.DatabaseAccess;

public class ConnectionHandleThread extends Thread {
	Socket socket;
	BufferedReader in;
	BufferedWriter out;

	public ConnectionHandleThread(Socket socket) {
		// Get the socket
		this.socket = socket;
	}

	@Override
	public void run() {
		// Initialize streams.
		initStreams();

		// Get JSONObject.
		String line = "";
		try {
			line = in.readLine();
			System.out.println(line);
		} catch (IOException e1) {
			System.out.println("Failed to read the line.");
			e1.printStackTrace();
		}
		System.out.println("Received message.");
		try {
			JSONObject obj = new JSONObject(line);
			handleConnection(obj);
		} catch (JSONException e) {
			System.out.println("Could not parse JSON. Leaving");
		}
		// Finalize.
		try {
			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tries to send reply to the connected socket. If the output stream fails 3
	 * times, gives up and kills the thread.
	 * 
	 * @param message
	 *            message to be sent
	 */
	private void sendReply(String message) {
		for (int i = 0; i < 3; i++) {
			try {
				out.write(message);
				out.newLine();
				out.flush();
				System.out.println("Reply sent.");
				return;
			} catch (Exception e) {
				if (i < 2) {
					System.out.println("Couldn't send reply.");
				} else {
					System.out.println("Couldn't send reply. Leaving");
				}
			}
		}
	}

	/**
	 * Sends a failure message.
	 */
	private void sendFailReply() {
		JSONObject reply = new JSONObject();
		try {
			reply.put("success", false);
		} catch (JSONException e1) {
			System.out.println("Failed to create fail reply.");
			e1.printStackTrace();
		}
		sendReply(reply.toString());
	}

	/**
	 * Takes in a JSONObject and handles the request accordingly.
	 * 
	 * @param obj
	 *            the JSON sent by client.
	 */
	private void handleConnection(JSONObject obj) {
		String connectionType = "";

		// Try to read connection type.
		try {
			connectionType = obj.getString("connectionType");
		} catch (JSONException e) {
			System.out.println("Connection type empty. Sending fail reply.");
			sendFailReply();
			e.printStackTrace();
		}

		// Switch the connection type.
		switch (connectionType) {
			case "loginConnection":
				try {
					if (DatabaseAccess.logUserIn(obj.getJSONObject("credential"))) {
						try {
							JSONObject reply = new JSONObject();
							reply.put("success", true);
							sendReply(reply.toString());
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
					} else {
						sendFailReply();
					}
				} catch (JSONException e) {
					System.out.println("Failed to get credentials.");
					sendFailReply();
				}
				break;
			default:
				break;
		}
	}

	/**
	 * Initiates the in and out streams.
	 */
	private void initStreams() {
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			System.out.println("ConnectionHandleThread: Failed to create input stream. Leaving.");
			e.printStackTrace();
			this.destroy();
		}
		try {
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			System.out.println("ConnectionHandleThread: Failed to create output stream. Leaving.");
			e.printStackTrace();
			this.destroy();
		}
	}
}