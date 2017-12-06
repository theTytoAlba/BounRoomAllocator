package database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.json.JSONObject;

public class DatabaseHelper {
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
