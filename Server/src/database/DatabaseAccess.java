package database;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseAccess {
	private static String USERS_DATABASE = "src/database/users.dat";
	private static String ROOMS_DATABASE = "src/database/rooms.dat";
	private static String WEEK_DATABASE = "src/database/week.dat";
	private static Lock usersLock = new ReentrantLock();
	private static Lock roomsLock = new ReentrantLock();
	private static Lock weekLock = new ReentrantLock();;
	
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

	public static JSONObject getRooms () {
		roomsLock.lock();
		JSONObject result = new JSONObject();
		
		JSONObject database = DatabaseHelper.getDatabase(ROOMS_DATABASE);
		try {
			result.put("success", true);
			result.put("rooms", database);
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
		roomsLock.unlock();
		return result;
	}
	public static JSONObject getWeek () {
		weekLock.lock();
		JSONObject result = new JSONObject();
		
		JSONObject database = DatabaseHelper.getDatabase(WEEK_DATABASE);
		try {
			result.put("success", true);
			result.put("week", database);
			weekLock.unlock();
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		try {
			result.put("success", false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		weekLock.unlock();
		return result;
	}

	public static JSONObject addRoom(JSONObject room) {
		roomsLock.lock();
		JSONObject result = new JSONObject();
		String buildingName = "";
		String roomName = "";
		int roomCapacity = 0;
		try {
			buildingName = room.getString("buildingName");
			roomName = room.getString("roomName");
			roomCapacity = room.getInt("roomCapacity");
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
			if (!database.has(buildingName)) {
				result.put("success", false);
				System.out.println("Building does not exists.");
				roomsLock.unlock();
				return result;
			}
			if (database.getJSONObject(buildingName).has(roomName)) {
				result.put("success", false);
				System.out.println("Room already exists in the building.");
				roomsLock.unlock();
				return result;
			}
			
			database.getJSONObject(buildingName).put(roomName, new JSONObject().put("capacity", roomCapacity));
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
		roomsLock.unlock();
		return result;
	}
	
	public static JSONObject deleteRoom(JSONObject room) {
		roomsLock.lock();
		JSONObject result = new JSONObject();
		String buildingName = "";
		String roomName = "";
		try {
			buildingName = room.getString("buildingName");
			roomName = room.getString("roomName");
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
			if (!database.has(buildingName)) {
				result.put("success", false);
				System.out.println("Building does not exists.");
				roomsLock.unlock();
				return result;
			}
			if (!database.getJSONObject(buildingName).has(roomName)) {
				result.put("success", false);
				System.out.println("Room doesnt exist in the building.");
				roomsLock.unlock();
				return result;
			}
			
			database.getJSONObject(buildingName).remove(roomName);
			DatabaseHelper.updateDatabase(ROOMS_DATABASE, database);
			result.put("success", true);
			result.put("roomName", roomName);
			result.put("buildingName", buildingName);
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
		roomsLock.unlock();
		return result;
	}
	
	public static JSONObject deleteBuilding(String buildingName) {
		roomsLock.lock();
		JSONObject result = new JSONObject();
		JSONObject database = DatabaseHelper.getDatabase(ROOMS_DATABASE);
		try {
			if (!database.has(buildingName)) {
				result.put("success", false);
				System.out.println("Building does not exists.");
				roomsLock.unlock();
				return result;
			}
			database.remove(buildingName);
			DatabaseHelper.updateDatabase(ROOMS_DATABASE, database);
			result.put("success", true);
			result.put("buildingName", buildingName);
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
		roomsLock.unlock();
		return result;
	}

	public static JSONObject getAvailableRooms(JSONObject details) {
		weekLock.lock();
		roomsLock.lock();
		
		int requiredCapacity = 0;
		try {
			requiredCapacity = details.getInt("roomCapacity");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONObject result = new JSONObject();
		
		// Filter rooms by capacity.
		JSONObject roomsDatabase = DatabaseHelper.getDatabase(ROOMS_DATABASE);
		JSONObject weekDatabase = DatabaseHelper.getDatabase(WEEK_DATABASE);
		
		try {
			Iterator<?> buildingIterator = roomsDatabase.keys();
	        while (buildingIterator.hasNext()) {
	        		boolean buildingIsIn = false;
	            final String buildingName = (String)buildingIterator.next();
	            Iterator<?> roomIterator = roomsDatabase.getJSONObject(buildingName).keys();
		        while (roomIterator.hasNext()) {
		        		final String roomName = (String)roomIterator.next();
		        		System.out.println("Room " + roomName);
		        		int capacity = roomsDatabase.getJSONObject(buildingName).getJSONObject(roomName).getInt("capacity");
		        		if (capacity >= requiredCapacity) {
		        			if (!buildingIsIn) {
		        				result.put(buildingName, new JSONObject());
		        				buildingIsIn = true;
		        			}
		        			result.getJSONObject(buildingName).put(roomName, capacity);
		        		}
		        }
	        }
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		// Filter rooms by availability
		try {
			Iterator<?> weekDayIterator = details.getJSONObject("week").keys();
			while (weekDayIterator.hasNext()) {
        			String weekDay = (String)weekDayIterator.next();
        			Iterator<?> hourIterator = details.getJSONObject("week").getJSONObject(weekDay).keys();
        			while (hourIterator.hasNext()) {
                			String hour = (String)hourIterator.next();
                			if (!details.getJSONObject("week").getJSONObject(weekDay).getBoolean(hour) || !weekDatabase.getJSONObject(weekDay).has(hour)) {
                			//	System.out.println("skipping " + weekDay + hour);
                    			continue;
                			}
                			System.out.println("Checking " + weekDay + hour);
                			// This day/hour is needed and exists in database.
                			JSONObject occupied = weekDatabase.getJSONObject(weekDay).getJSONObject(hour);
                			Iterator<?> occupiedBuildingIterator = occupied.keys();
                			while (occupiedBuildingIterator.hasNext()) {
                        			String occupiedBuilding = (String)occupiedBuildingIterator.next();
                        	//		System.out.println("Checking " + occupiedBuilding + weekDatabase.getJSONObject(weekDay).getJSONObject(hour).toString());
                        			Iterator<?> occupiedRoomIterator = occupied.getJSONObject(occupiedBuilding).keys();
                        			while (occupiedRoomIterator.hasNext()) {
                        				String occupiedRoom = (String)occupiedRoomIterator.next();
                         //   			System.out.println("Checking " + occupiedRoom);	
                        				if (result.has(occupiedBuilding) && result.getJSONObject(occupiedBuilding).has(occupiedRoom)) {
                        					result.getJSONObject(occupiedBuilding).remove(occupiedRoom);
                        				}
                        			}
                        			if (result.has(occupiedBuilding) && result.getJSONObject(occupiedBuilding).length() == 0) {
                        				result.remove(occupiedBuilding);
                        			}
                			}
        			}
			}
			System.out.println("saglam");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			JSONObject res = new JSONObject();
			res.put("success", true);
			res.put("rooms", result);
			roomsLock.unlock();
			weekLock.unlock();
			return res;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		try {
			result.put("success", false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		roomsLock.unlock();
		weekLock.unlock();
		return result;
	}
	

	public static JSONObject addLecture(JSONObject details) {
		weekLock.lock();
		
		JSONObject result = new JSONObject();
		String buildingName = "";
		String roomName = "";
		String lectureName = "";
		JSONObject week = new JSONObject();
		
		try {
			buildingName = details.getString("buildingName");
			roomName = details.getString("roomName");
			lectureName = details.getString("lectureName");
			week = details.getJSONObject("week");
		} catch (JSONException e) {
			System.out.println("Failed to resolve building JSON.");
			try {
				result.put("success", false);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			weekLock.unlock();
			return result;
		}
		
		JSONObject database = DatabaseHelper.getDatabase(WEEK_DATABASE);
		try {
			
			Iterator<?> dayIterator = week.keys();
            while (dayIterator.hasNext()) {
                final String day = (String)dayIterator.next();
                Iterator<?> hourIterator = week.getJSONObject(day).keys();
                while (hourIterator.hasNext()) {
                    final String hour = (String)hourIterator.next();
                    //System.out.println(day + hour);
                    if (week.getJSONObject(day).getBoolean(hour)) {
	                    	if (!database.getJSONObject(day).has(hour)) {
	                			database.getJSONObject(day).put(hour, new JSONObject());
	                		}
                    		if (!database.getJSONObject(day).getJSONObject(hour).has(buildingName)) {
                    			database.getJSONObject(day).getJSONObject(hour).put(buildingName, new JSONObject());
                    		}
                    		database.getJSONObject(day).getJSONObject(hour).getJSONObject(buildingName).put(roomName, lectureName);
                    		
                    }
                }
            }
			DatabaseHelper.updateDatabase(WEEK_DATABASE, database);
			result.put("success", true);
			weekLock.unlock();
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		try {
			result.put("success", false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		weekLock.unlock();
		return result;
	}
	
}
