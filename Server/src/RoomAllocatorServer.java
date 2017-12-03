import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONObject;

public class RoomAllocatorServer {
    public static void main(String[] args) throws IOException {
        startServer();
    }

    public static void startServer() {
        (new Thread() {
            @Override
            public void run() {
                ServerSocket ss;
                try {
                    ss = new ServerSocket(60015);

                    Socket s = ss.accept();

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(s.getInputStream()));
                    String line = null;
                    while ((line = in.readLine()) != null) {
                        JSONObject obj = new JSONObject(line);
                        System.out.println(obj.getString("type"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}