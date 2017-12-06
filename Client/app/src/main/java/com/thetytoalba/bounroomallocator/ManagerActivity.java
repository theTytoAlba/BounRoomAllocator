package com.thetytoalba.bounroomallocator;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static android.view.View.GONE;
import static com.thetytoalba.bounroomallocator.Constants.HOST_IP;
import static com.thetytoalba.bounroomallocator.Constants.HOST_PORT;
import static com.thetytoalba.bounroomallocator.Constants.TAG_ADD_BUILDING_CONNECTION;
import static com.thetytoalba.bounroomallocator.Constants.TAG_ADD_ROOM_CONNECTION;
import static com.thetytoalba.bounroomallocator.Constants.TAG_BUILDING;
import static com.thetytoalba.bounroomallocator.Constants.TAG_BUILDING_NAME;
import static com.thetytoalba.bounroomallocator.Constants.TAG_CONNECTION_TYPE;
import static com.thetytoalba.bounroomallocator.Constants.TAG_DELETE_BUILDING_CONNECTION;
import static com.thetytoalba.bounroomallocator.Constants.TAG_GET_ROOMS_CONNECTION;
import static com.thetytoalba.bounroomallocator.Constants.TAG_ROOM;
import static com.thetytoalba.bounroomallocator.Constants.TAG_ROOMS;
import static com.thetytoalba.bounroomallocator.Constants.TAG_ROOM_NAME;
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
                Log.e("ManagerActivity", "Failed to establish connection.");
            }
            try {
                out.write(getRoomsMessage.toString());
                out.newLine();
                out.flush();

                result = new JSONObject(in.readLine());
            } catch (Exception e) {
                Log.e("ManagerActivity", "Error while sending credentials to server.");
                e.printStackTrace();
            }
            return result;
        }

        protected void onPostExecute(JSONObject result) {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e("ManagerActivity", "Failed to close socket.");
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("ManagerActivity", "Failed to close input stream.");
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Log.e("ManagerActivity", "Failed to close output stream.");
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


    private class DeleteRoomTask extends AsyncTask<Void, Void, JSONObject> {
        JSONObject deleteRoomMessage;
        private Socket socket;
        private BufferedWriter out;
        private BufferedReader in;

        DeleteRoomTask(JSONObject deleteRoomMessage) {
            this.deleteRoomMessage = deleteRoomMessage;
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
                Log.e("ManagerActivity", "Failed to establish connection.");
            }
            try {
                out.write(deleteRoomMessage.toString());
                out.newLine();
                out.flush();

                result = new JSONObject(in.readLine());
            } catch (Exception e) {
                Log.e("ManagerActivity", "Error while sending credentials to server.");
                e.printStackTrace();
            }
            return result;
        }

        protected void onPostExecute(JSONObject result) {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e("ManagerActivity", "Failed to close socket.");
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("ManagerActivity", "Failed to close input stream.");
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Log.e("ManagerActivity", "Failed to close output stream.");
                }
            }

            try {
                if (result.getBoolean(TAG_SUCCESS)) {
                    successfulDeleteRoom(result.getString(TAG_ROOM_NAME), result.getString(TAG_BUILDING_NAME));
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            failedDeleteRoom();
        }
    }


    private class DeleteBuildingTask extends AsyncTask<Void, Void, JSONObject> {
        JSONObject deleteBuildingMessage;
        private Socket socket;
        private BufferedWriter out;
        private BufferedReader in;

        DeleteBuildingTask(JSONObject deleteBuildingMessage) {
            this.deleteBuildingMessage = deleteBuildingMessage;
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
                Log.e("ManagerActivity", "Failed to establish connection.");
            }
            try {
                out.write(deleteBuildingMessage.toString());
                out.newLine();
                out.flush();

                result = new JSONObject(in.readLine());
            } catch (Exception e) {
                Log.e("ManagerActivity", "Error while sending credentials to server.");
                e.printStackTrace();
            }
            return result;
        }

        protected void onPostExecute(JSONObject result) {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e("ManagerActivity", "Failed to close socket.");
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("ManagerActivity", "Failed to close input stream.");
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Log.e("ManagerActivity", "Failed to close output stream.");
                }
            }

            try {
                if (result.getBoolean(TAG_SUCCESS)) {
                    successfulDeleteBuilding(result.getString(TAG_BUILDING_NAME));
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            failedDeleteBuilding();
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

        final Button deleteBuildingButton = (Button) findViewById(R.id.deleteBuildingButton);
        final Button deleteBuildingButtonCancel = (Button) findViewById(R.id.deleteBuildingButtonCancel);
        deleteBuildingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int index = 0; index<((ViewGroup)findViewById(R.id.managerActivity_buildingContainer)).getChildCount(); ++index) {
                    LinearLayout buildingContainer = (LinearLayout) ((ViewGroup)findViewById(R.id.managerActivity_buildingContainer)).getChildAt(index);
                    buildingContainer.findViewById(R.id.buildingContainer_delete).setVisibility(View.VISIBLE);
                    for(int index2 = 0; index2<((ViewGroup)buildingContainer.findViewById(R.id.buildingContainer_roomContainer)).getChildCount(); ++index2) {
                        LinearLayout roomContainer = (LinearLayout) ((ViewGroup)buildingContainer.findViewById(R.id.buildingContainer_roomContainer)).getChildAt(index2);
                        roomContainer.findViewById(R.id.roomContainer_delete).setVisibility(GONE);
                        roomContainer.findViewById(R.id.roomContainer_deleteForSure).setVisibility(GONE);
                        roomContainer.findViewById(R.id.roomContainer_deleteCancel).setVisibility(GONE);
                    }
                }
                deleteBuildingButtonCancel.setVisibility(View.VISIBLE);
                deleteBuildingButton.setVisibility(GONE);
            }
        });
        deleteBuildingButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int index = 0; index<((ViewGroup)findViewById(R.id.managerActivity_buildingContainer)).getChildCount(); ++index) {
                    LinearLayout buildingContainer = (LinearLayout) ((ViewGroup)findViewById(R.id.managerActivity_buildingContainer)).getChildAt(index);
                    buildingContainer.findViewById(R.id.buildingContainer_delete).setVisibility(GONE);
                    for(int index2 = 0; index2<((ViewGroup)buildingContainer.findViewById(R.id.buildingContainer_roomContainer)).getChildCount(); ++index2) {
                        LinearLayout roomContainer = (LinearLayout) ((ViewGroup)buildingContainer.findViewById(R.id.buildingContainer_roomContainer)).getChildAt(index2);
                        roomContainer.findViewById(R.id.roomContainer_delete).setVisibility(View.VISIBLE);
                        roomContainer.findViewById(R.id.roomContainer_deleteForSure).setVisibility(GONE);
                        roomContainer.findViewById(R.id.roomContainer_deleteCancel).setVisibility(GONE);
                    }
                }
                deleteBuildingButtonCancel.setVisibility(GONE);
                deleteBuildingButton.setVisibility(View.VISIBLE);
            }
        });

        refreshIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    new GetRoomsTask(new JSONObject().put(TAG_CONNECTION_TYPE, TAG_GET_ROOMS_CONNECTION)).execute();
                    showProgress();
                    findViewById(R.id.deleteBuildingButtonCancel).setVisibility(View.GONE);
                    findViewById(R.id.deleteBuildingButton).setVisibility(View.VISIBLE);
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
            final String buildingName = (String)buildingIterator.next();
            LinearLayout buildingLayout = (LinearLayout) this.getLayoutInflater().inflate(R.layout.layout_building_container, null);
            LinearLayout buildingRooms = buildingLayout.findViewById(R.id.buildingContainer_roomContainer);
            TextView buildingNameText = buildingLayout.findViewById(R.id.buildingContainer_buildingName);
            buildingNameText.setText(buildingName);
            buildingLayout.findViewById(R.id.buildingContainer_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        JSONObject objectToSend = new JSONObject();
                        objectToSend.put(TAG_CONNECTION_TYPE, TAG_DELETE_BUILDING_CONNECTION);
                        objectToSend.put(TAG_BUILDING_NAME, buildingName);
                        showProgress();
                        new DeleteBuildingTask(objectToSend).execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                        hideProgress();
                        refreshIcon.performClick();
                    }
                }
            });
            roomsContainer.addView(buildingLayout);
            // Set rooms
            try {
                JSONObject buildingObject = rooms.getJSONObject(buildingName);
                Iterator<?> roomIterator = buildingObject.keys();
                while (roomIterator.hasNext()) {
                    final String roomName = (String)roomIterator.next();
                    LinearLayout roomLayout = (LinearLayout) this.getLayoutInflater().inflate(R.layout.layout_room_container, null);
                    TextView roomNameText = roomLayout.findViewById(R.id.roomContainer_roomName);
                    roomNameText.setText(roomName);

                    final TextView roomDeleteForSure = roomLayout.findViewById(R.id.roomContainer_deleteForSure);
                    final TextView roomDeleteCancel = roomLayout.findViewById(R.id.roomContainer_deleteCancel);
                    final ImageView roomDelete = roomLayout.findViewById(R.id.roomContainer_delete);

                    roomDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            roomDelete.setVisibility(GONE);
                            roomDeleteForSure.setVisibility(View.VISIBLE);
                            roomDeleteCancel.setVisibility(View.VISIBLE);
                        }
                    });

                    roomDeleteCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            roomDelete.setVisibility(View.VISIBLE);
                            roomDeleteForSure.setVisibility(GONE);
                            roomDeleteCancel.setVisibility(GONE);
                        }
                    });
                    roomDeleteForSure.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try{
                                JSONObject objectToSend = new JSONObject();
                                objectToSend.put(TAG_CONNECTION_TYPE, Constants.TAG_DELETE_ROOM_CONNECTION);
                                objectToSend.put(TAG_ROOM, new JSONObject().put(TAG_ROOM_NAME, roomName).put(TAG_BUILDING_NAME, buildingName));
                                showProgress();
                                new DeleteRoomTask(objectToSend).execute();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Failed to delete room.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
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
            roomLayout.findViewById(R.id.roomContainer_delete).setVisibility(GONE);
            buildingRooms.addView(roomLayout);
            roomLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ManagerActivity.this, AddRoomActivity.class);
                    intent.putExtra(TAG_BUILDING_NAME, buildingName);
                    startActivity(intent);
                }
            });
        }
        hideProgress();
    }

    private void failedGetBuildings() {
        hideProgress();
        Toast.makeText(getApplicationContext(), "Failed to get room list", Toast.LENGTH_SHORT).show();
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        refreshIcon.setVisibility(GONE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(GONE);
        refreshIcon.setVisibility(View.VISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void successfulDeleteRoom(String roomName, String buildingName) {
        hideProgress();
        Toast.makeText(getApplicationContext(), "Room " + roomName + " was successfully deleted from building " + buildingName, Toast.LENGTH_LONG).show();
        refreshIcon.performClick();
    }

    private void failedDeleteRoom() {
        hideProgress();
        Toast.makeText(getApplicationContext(), "Could not delete.", Toast.LENGTH_SHORT).show();
        refreshIcon.performClick();
    }


    private void successfulDeleteBuilding(String buildingName) {
        hideProgress();
        Toast.makeText(getApplicationContext(), "Building " + buildingName + " was successfully deleted.", Toast.LENGTH_LONG).show();
        refreshIcon.performClick();
    }

    private void failedDeleteBuilding() {
        hideProgress();
        Toast.makeText(getApplicationContext(), "Could not delete.", Toast.LENGTH_SHORT).show();
        refreshIcon.performClick();
    }
}
