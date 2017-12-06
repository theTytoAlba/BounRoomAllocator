package com.thetytoalba.bounroomallocator;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import static com.thetytoalba.bounroomallocator.Constants.HOST_IP;
import static com.thetytoalba.bounroomallocator.Constants.HOST_PORT;
import static com.thetytoalba.bounroomallocator.Constants.TAG_ADD_ROOM_CONNECTION;
import static com.thetytoalba.bounroomallocator.Constants.TAG_BUILDING_NAME;
import static com.thetytoalba.bounroomallocator.Constants.TAG_CONNECTION_TYPE;
import static com.thetytoalba.bounroomallocator.Constants.TAG_ROOM;
import static com.thetytoalba.bounroomallocator.Constants.TAG_ROOM_CAPACITY;
import static com.thetytoalba.bounroomallocator.Constants.TAG_ROOM_NAME;
import static com.thetytoalba.bounroomallocator.Constants.TAG_SUCCESS;

public class AddRoomActivity extends AppCompatActivity {

    private class AddRoomTask extends AsyncTask<Void, Void, JSONObject> {
        JSONObject roomMessage;
        private Socket socket;
        private BufferedWriter out;
        private BufferedReader in;

        AddRoomTask(JSONObject roomMessage) {
            this.roomMessage = roomMessage;
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
                Log.e("AddRoomActivity", "Failed to establish connection.");
            }
            try {
                out.write(roomMessage.toString());
                out.newLine();
                out.flush();

                result = new JSONObject(in.readLine());
            } catch (Exception e) {
                Log.e("AddRoomActivity", "Error while sending building to server.");
                e.printStackTrace();
            }
            return result;
        }

        protected void onPostExecute(JSONObject result) {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e("AddRoomActivity", "Failed to close socket.");
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("AddRoomActivity", "Failed to close input stream.");
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Log.e("AddRoomActivity", "Failed to close output stream.");
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

    private ProgressBar progressBar;
    Button addRoom;
    EditText roomName, roomCapacity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);

        TextView addingTo = (TextView) findViewById(R.id.addRoomActivity_addingRoomToText);
        addingTo.setText(addingTo.getText() + getIntent().getStringExtra(TAG_BUILDING_NAME));

        progressBar = (ProgressBar) findViewById(R.id.addRoomActivity_progress);
        roomName = (EditText) findViewById(R.id.addRoomActivity_RoomNameEditText);
        roomCapacity = (EditText) findViewById(R.id.addRoomActivity_roomCapacityEditText);

        addRoom = (Button) findViewById(R.id.addRoomActivity_addRoomButton);
        addRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(roomName.getText()) || TextUtils.isEmpty(roomCapacity.getText())) {
                    Toast.makeText(getApplicationContext(), "You cannot add a new room without name or capacity.", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    JSONObject objectToSend = new JSONObject();
                    objectToSend.put(TAG_CONNECTION_TYPE, TAG_ADD_ROOM_CONNECTION);
                    objectToSend.put(TAG_ROOM, new JSONObject()
                            .put(TAG_BUILDING_NAME, getIntent().getStringExtra(TAG_BUILDING_NAME))
                            .put(TAG_ROOM_NAME, roomName.getText().toString())
                            .put(TAG_ROOM_CAPACITY, roomCapacity.getText().toString()));
                    showProgress();
                    new AddRoomTask(objectToSend).execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Could not send data to server.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void successfulAdd() {
        hideProgress();
        Toast.makeText(getApplicationContext(), "Added room successfully.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void failedAdd() {
        hideProgress();
        Toast.makeText(getApplicationContext(), "Could not add new room.", Toast.LENGTH_SHORT).show();
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        addRoom.setVisibility(View.GONE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
        addRoom.setVisibility(View.VISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
