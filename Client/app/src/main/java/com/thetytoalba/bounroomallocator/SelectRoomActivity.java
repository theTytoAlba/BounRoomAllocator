package com.thetytoalba.bounroomallocator;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import static com.thetytoalba.bounroomallocator.Constants.TAG_ADD_ROOM_CONNECTION;
import static com.thetytoalba.bounroomallocator.Constants.TAG_BUILDING_NAME;
import static com.thetytoalba.bounroomallocator.Constants.TAG_CONNECTION_TYPE;
import static com.thetytoalba.bounroomallocator.Constants.TAG_DETAILS;
import static com.thetytoalba.bounroomallocator.Constants.TAG_LECTURE_NAME;
import static com.thetytoalba.bounroomallocator.Constants.TAG_ROOMS;
import static com.thetytoalba.bounroomallocator.Constants.TAG_ROOM_NAME;
import static com.thetytoalba.bounroomallocator.Constants.TAG_SUCCESS;
import static com.thetytoalba.bounroomallocator.Constants.TAG_WEEK;

public class SelectRoomActivity extends AppCompatActivity {

    private class AddLectureTask extends AsyncTask<Void, Void, JSONObject> {
        JSONObject lectureMessage;
        private Socket socket;
        private BufferedWriter out;
        private BufferedReader in;

        AddLectureTask(JSONObject lectureMessage) {
            this.lectureMessage = lectureMessage;
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
                Log.e("AddLectureActivity", "Failed to establish connection.");
            }
            try {
                out.write(lectureMessage.toString());
                out.newLine();
                out.flush();

                result = new JSONObject(in.readLine());
            } catch (Exception e) {
                Log.e("AddLectureActivity", "Error while sending building to server.");
                e.printStackTrace();
            }
            return result;
        }

        protected void onPostExecute(JSONObject result) {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e("AddLectureActivity", "Failed to close socket.");
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("AddLectureActivity", "Failed to close input stream.");
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Log.e("AddLectureActivity", "Failed to close output stream.");
                }
            }

            try {
                if (result.getBoolean(TAG_SUCCESS)) {
                    successfulAdd();
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            failedAdd();
        }
    }


    Button addLecture;
    String buildingName = "";
    String roomName = "";
    String lecture = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_room);
        try {
            initRoomList(new JSONObject(getIntent().getStringExtra(TAG_ROOMS)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        lecture = getIntent().getStringExtra(TAG_LECTURE_NAME);
        addLecture = (Button) findViewById(R.id.selectRoom_addLectureButton);
        addLecture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    JSONObject objectToSend = new JSONObject();
                    objectToSend.put(TAG_CONNECTION_TYPE, Constants.TAG_ADD_LECTURE_CONNECTION);
                    JSONObject details = new JSONObject()
                            .put(TAG_LECTURE_NAME, lecture)
                            .put(TAG_BUILDING_NAME, buildingName)
                            .put(TAG_ROOM_NAME, roomName)
                            .put(TAG_WEEK, new JSONObject(getIntent().getStringExtra(TAG_WEEK)));
                    objectToSend.put(TAG_DETAILS, details);
                    new AddLectureTask(objectToSend).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void initRoomList(JSONObject rooms) {
        Log.i("SelectRoomActivity", "Received the rooms. Layout in progress");
        Iterator<?> buildingIterator = rooms.keys();
        LinearLayout roomsContainer = (LinearLayout) findViewById(R.id.selectRoom_roomContainer);
        roomsContainer.removeAllViews();
        while (buildingIterator.hasNext()) {
            // Set building
            final String buildingNameB = (String)buildingIterator.next();
            LinearLayout buildingLayout = (LinearLayout) this.getLayoutInflater().inflate(R.layout.layout_building_container, null);
            LinearLayout buildingRooms = buildingLayout.findViewById(R.id.buildingContainer_roomContainer);
            TextView buildingNameText = buildingLayout.findViewById(R.id.buildingContainer_buildingName);
            buildingNameText.setText(buildingNameB);
            roomsContainer.addView(buildingLayout);
            // Set rooms
            try {
                JSONObject buildingObject = rooms.getJSONObject(buildingNameB);
                Iterator<?> roomIterator = buildingObject.keys();
                while (roomIterator.hasNext()) {
                    final String roomNameB = (String)roomIterator.next();
                    final LinearLayout roomLayout = (LinearLayout) this.getLayoutInflater().inflate(R.layout.layout_room_container, null);
                    TextView roomNameText = roomLayout.findViewById(R.id.roomContainer_roomName);
                    roomNameText.setText(roomNameB);
                    // Fix action icon
                    ImageView roomAction = roomLayout.findViewById(R.id.roomContainer_action);
                    roomAction.setImageResource(R.drawable.selected);
                    ColorStateList csl;
                    if (buildingName.equals(buildingNameB) && roomName.equals(roomNameB)) {
                        csl = AppCompatResources.getColorStateList(getApplicationContext(), R.color.green);
                    } else {
                        csl = AppCompatResources.getColorStateList(getApplicationContext(), R.color.midGray);
                    }
                    Drawable drawable = DrawableCompat.wrap(roomAction.getDrawable());
                    DrawableCompat.setTintList(drawable, csl);
                    roomAction.setImageDrawable(drawable);
                    roomAction.setOnClickListener(new View.OnClickListener() {
                        boolean selected = false;
                        @Override
                        public void onClick(View view) {
                            buildingName = buildingNameB;
                            roomName = roomNameB;
                            try {
                                initRoomList(new JSONObject(getIntent().getStringExtra(TAG_ROOMS)));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    buildingRooms.addView(roomLayout);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("StudentActivity", "Could not handle building object for " + buildingNameB);
            }
        }
    }


    private void successfulAdd() {
        Toast.makeText(getApplicationContext(), "Lecture added successfully", Toast.LENGTH_SHORT);
        finish();
    }

    private void failedAdd() {
        Toast.makeText(getApplicationContext(), "Failed to add lecture", Toast.LENGTH_SHORT);
    }
}
