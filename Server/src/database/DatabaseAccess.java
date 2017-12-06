package database;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseAccess {
	private static String USERS_DATABASE = "src/database/users.dat";
	
	synchronized public static JSONObject logUserIn (JSONObject credential) {
		JSONObject result = new JSONObject();
		String username = "";
		String password = "";
		try {
			username = credential.getString("username");
			password = credential.getString("password");
		} catch (JSONException e) {
			System.out.println("Failed to resolve credential JSON.");
			try {
				result.put("success", false);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			return result;
		}
		
		JSONObject database = getDatabase(USERS_DATABASE);
		try {
			if (database.has(username) && database.getJSONObject(username).has("password") && database.getJSONObject(username).getString("password").equals(password)) {
				result.put("success", true);
				result.put("userType", database.getJSONObject(username).getString("userType"));
				return result;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			result.put("success", false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	synchronized public static boolean signUserUp (JSONObject credential) {
		String username = "";
		String password = "";
		String userType = "";
		// Get user info
		try {
			username = credential.getString("username");
			password = credential.getString("password");
			userType = credential.getString("userType");
		} catch (JSONException e) {
			System.out.println("Failed to resolve credential JSON.");
			return false;
		}
		// Get database
		JSONObject database = getDatabase(USERS_DATABASE);
		// Modify database
		try {
			if (database.has(username)) {
				return false;
			}
			database.put(username, (new JSONObject()).put("password", password).put("userType", userType));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		// Save database and return
		return updateDatabase(USERS_DATABASE, database);
	}
	
	public static JSONObject getDatabase(String path) {
		try {
			BufferedReader dbReader = new BufferedReader(new FileReader(path));
			JSONObject database = new JSONObject(dbReader.readLine());
			dbReader.close();
			return database;
		} catch (Exception e) {
			System.out.println("Failed to read database");
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean updateDatabase(String path, JSONObject newDatabase) {
		try {
			BufferedWriter dbWriter = new BufferedWriter(new FileWriter(path + ".temp"));
			dbWriter.write(newDatabase.toString());
			dbWriter.close();
			
			File fileTemp = new File(path + ".temp");
			File fileDat = new File(path);
			boolean success = fileTemp.renameTo(fileDat);
			if (!success) {
				System.out.println("Could not rename database.");
				return false;
			}
			
			System.out.println("Database updated.");
			return true;
		} catch (Exception e) {
			System.out.println("Failed to update database");
			e.printStackTrace();
			return false;
		}
	}
}
