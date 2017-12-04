import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseAccess {
	synchronized public static boolean logUserIn (JSONObject credential) {
		try {
			String username = credential.getString("username");
			String password = credential.getString("password");
			// TODO: Create database and look the values up from there.
			return username.equals("admin") && password.equals("pass");
		} catch (JSONException e) {
			System.out.println("Failed to resolve credential JSON.");
			return false;
		}
	}
}
