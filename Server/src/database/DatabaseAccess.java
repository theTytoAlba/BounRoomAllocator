package database;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseAccess {
	private static String USERS_DATABASE = "src/database/users.dat";
	private static String ROOMS_DATABASE = "src/database/rooms.dat";
	private static Lock usersLock = new ReentrantLock();;
	private static Lock roomsLock = new ReentrantLock();;
	
	public static JSONObject logUserIn (JSONObject credential) {
		usersLock.lock();
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
			usersLock.unlock();
			return result;
		}
		
		JSONObject database = DatabaseHelper.getDatabase(USERS_DATABASE);
		try {
			if (database.has(username) && database.getJSONObject(username).has("password") && database.getJSONObject(username).getString("password").equals(password)) {
				result.put("success", true);
				result.put("userType", database.getJSONObject(username).getString("userType"));
				usersLock.unlock();
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
		usersLock.unlock();
		return result;
	}
	
	public static boolean signUserUp (JSONObject credential) {
		usersLock.lock();
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
			usersLock.unlock();
			return false;
		}
		// Get database
		JSONObject database = DatabaseHelper.getDatabase(USERS_DATABASE);
		// Modify database
		try {
			if (database.has(username)) {
				usersLock.unlock();
				return false;
			}
			database.put(username, (new JSONObject()).put("password", password).put("userType", userType));
		} catch (Exception e) {
			e.printStackTrace();
			usersLock.unlock();
			return false;
		}
		// Save database and return
		boolean res = DatabaseHelper.updateDatabase(USERS_DATABASE, database); 
		usersLock.unlock();
		return res;
	}
	
	public static JSONObject addBuilding (JSONObject building) {
		roomsLock.lock();
		JSONObject result = new JSONObject();
		String buildingName = "";
		try {
			buildingName = building.getString("buildingName");
		} catch (JSONException e) {
			System.out.println("Failed to resolve building JSON.");
			try {
				result.put("success", false);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			roomsLock.unlock();
			return result;
		}
		
		JSONObject database = DatabaseHelper.getDatabase(ROOMS_DATABASE);
		try {
			if (database.has(buildingName)) {
				result.put("success", false);
				System.out.println("Building already exists.");
				roomsLock.unlock();
				return result;
			}
			database.put(buildingName, new JSONObject());
			DatabaseHelper.updateDatabase(ROOMS_DATABASE, database);
			result.put("success", true);
			roomsLock.unlock();
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		try {
			result.put("success", false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		usersLock.unlock();
		return result;
	}
	
}
