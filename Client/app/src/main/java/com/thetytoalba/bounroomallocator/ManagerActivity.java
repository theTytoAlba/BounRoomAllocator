package com.thetytoalba.bounroomallocator;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Iterator;

import static com.thetytoalba.bounroomallocator.Constants.HOST_IP;
import static com.thetytoalba.bounroomallocator.Constants.HOST_PORT;
import static com.thetytoalba.bounroomallocator.Constants.TAG_ADD_BUILDING_CONNECTION;
import static com.thetytoalba.bounroomallocator.Constants.TAG_CONNECTION_TYPE;
import static com.thetytoalba.bounroomallocator.Constants.TAG_GET_ROOMS_CONNECTION;
import static com.thetytoalba.bounroomallocator.Constants.TAG_ROOMS;
import static com.thetytoalba.bounroomallocator.Constants.TAG_SUCCESS;

public class ManagerActivity extends AppCompatActivity {

    private class GetRoomsTask extends AsyncTask<Void, Void, JSONObject> {
        JSONObject getRoomsMessage;
        private Socket socket;
        private BufferedWriter out;
        private BufferedReader in;

        GetRoomsTask(JSONObject getRoomsMessage) {
            this.getRoomsMessage = getRoomsMessage;
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject result = null;
            try {
                result = new JSONObject().put(TAG_SUCCESS, Boolean.FALSE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ;
            try {
                socket = new Socket(HOST_IP, HOST_PORT);
                out = new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream()));
                in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
            } catch (Exception e) {
                Log.e("LoginActivity", "Failed to establish connection.");
            }
            try {
                out.write(getRoomsMessage.toString());
                out.newLine();
                out.flush();

                result = new JSONObject(in.readLine());
            } catch (Exception e) {
                Log.e("LoginActivity", "Error while sending credentials to server.");
                e.printStackTrace();
            }
            return result;
        }

        protected void onPostExecute(JSONObject result) {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e("LoginActivity", "Failed to close socket.");
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("LoginActivity", "Failed to close input stream.");
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Log.e("LoginActivity", "Failed to close output stream.");
                }
            }

            try {
                if (result.getBoolean(TAG_SUCCESS)) {
                    successfulGetBuildings(result.getJSONObject(TAG_ROOMS));
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            failedGetBuildings();
        }
    }

    ProgressBar progressBar;
    ImageView refreshIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        Log.i("ManagerActivity", "Started app in manager mode.");

        progressBar = (ProgressBar) findViewById(R.id.managerActivity_progress);
        refreshIcon = (ImageView) findViewById(R.id.managerActivity_refreshIcon);

        Button buildingButton = (Button) findViewById(R.id.addBuildingButton);
        buildingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ManagerActivity.this, AddBuildingActivity.class));
            }
        });

        refreshIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    new GetRoomsTask(new JSONObject().put(TAG_CONNECTION_TYPE, TAG_GET_ROOMS_CONNECTION)).execute();
                    showProgress();
                } catch (JSONException e) {
                    e.printStackTrace();
                    failedGetBuildings();
                }
            }
        });

        refreshIcon.performClick();
    }

    @Override
    protected void onResume () {
        super.onResume();
        refreshIcon.performClick();
    }
    private void successfulGetBuildings(JSONObject rooms) {
        Log.i("ManagerActivity", "Received the rooms. Layout in progress");
        Iterator<?> buildingIterator = rooms.keys();
        LinearLayout roomsContainer = (LinearLayout) findViewById(R.id.managerActivity_buildingContainer);
        roomsContainer.removeAllViews();
        while (buildingIterator.hasNext()) {
            // Set building
            String buildingName = (String)buildingIterator.next();
            LinearLayout buildingLayout = (LinearLayout) this.getLayoutInflater().inflate(R.layout.layout_building_container, null);
            LinearLayout buildingRooms = buildingLayout.findViewById(R.id.buildingContainer_roomContainer);
            TextView buildingNameText = buildingLayout.findViewById(R.id.buildingContainer_buildingName);
            buildingNameText.setText(buildingName);
            roomsContainer.addView(buildingLayout);
            // Set rooms
            try {
                JSONObject buildingObject = rooms.getJSONObject(buildingName);
                Iterator<?> roomIterator = buildingObject.keys();
                while (roomIterator.hasNext()) {
                    String roomName = (String)roomIterator.next();
                    LinearLayout roomLayout = (LinearLayout) this.getLayoutInflater().inflate(R.layout.layout_room_container, null);
                    TextView roomNameText = roomLayout.findViewById(R.id.roomContainer_roomName);
                    roomNameText.setText(roomName);
                    buildingRooms.addView(roomLayout);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ManagerActivity", "Could not handle building object for " + buildingName);
            }
            // Add new room line
            LinearLayout roomLayout = (LinearLayout) this.getLayoutInflater().inflate(R.layout.layout_room_container, null);
            TextView roomNameText = roomLayout.findViewById(R.id.roomContainer_roomName);
            roomNameText.setText("Add new room");
            ImageView icon = roomLayout.findViewById(R.id.bulletpoint_icon);
            icon.setImageResource(R.drawable.add_icon);
            buildingRooms.addView(roomLayout);
        }
        hideProgress();
    }

    private void failedGetBuildings() {
        hideProgress();
        Toast.makeText(getApplicationContext(), "Failed to get room list", Toast.LENGTH_SHORT).show();
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        refreshIcon.setVisibility(View.GONE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
        refreshIcon.setVisibility(View.VISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
