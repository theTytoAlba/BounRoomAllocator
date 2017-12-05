package database;
import java.io.BufferedReader;
import java.io.FileReader;

import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseAccess {
	private static String USERS_DATABASE = "src/database/users.dat";
	
	synchronized public static boolean logUserIn (JSONObject credential) {
		String username = "";
		String password = "";
		try {
			username = credential.getString("username");
			password = credential.getString("password");
		} catch (JSONException e) {
			System.out.println("Failed to resolve credential JSON.");
			return false;
		}
		JSONObject database;
		try {
			BufferedReader dbReader = new BufferedReader(new FileReader(USERS_DATABASE));
			database = new JSONObject(dbReader.readLine());
			dbReader.close();
		} catch (Exception e) {
			System.out.println("Failed to read database");
			e.printStackTrace();
			return false;
		};
		try {
			return database.has(username) && database.getJSONObject(username).has("password") && database.getJSONObject(username).getString("password").equals(password);
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	synchronized public static boolean signUserUp (JSONObject credential) {
		try {
			String username = credential.getString("username");
			String password = credential.getString("password");
			// TODO: Create database and save the values in there.
			return true;
		} catch (JSONException e) {
			System.out.println("Failed to resolve credential JSON.");
			return false;
		}
	}
}
