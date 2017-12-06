package com.thetytoalba.bounroomallocator;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
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
import static com.thetytoalba.bounroomallocator.Constants.TAG_ADD_BUILDING_CONNECTION;
import static com.thetytoalba.bounroomallocator.Constants.TAG_CONNECTION_TYPE;
import static com.thetytoalba.bounroomallocator.Constants.TAG_GET_ROOMS_CONNECTION;
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
                    successfulGetBuildings(result);
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            failedGetBuildings();
        }
    }

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        Log.i("ManagerActivity", "Started app in manager mode.");

        progressBar = (ProgressBar) findViewById(R.id.managerActivity_progress);
        Button buildingButton = (Button) findViewById(R.id.addBuildingButton);
        buildingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ManagerActivity.this, AddBuildingActivity.class));
            }
        });

        try {
            new GetRoomsTask(new JSONObject().put(TAG_CONNECTION_TYPE, TAG_GET_ROOMS_CONNECTION)).execute();
            showProgress();
        } catch (JSONException e) {
            e.printStackTrace();
            failedGetBuildings();
        }
    }

    private void successfulGetBuildings(JSONObject result) {
        hideProgress();
        Log.i("ManagerActivity", result.toString());
    }

    private void failedGetBuildings() {
        hideProgress();
        Toast.makeText(getApplicationContext(), "Failed to get room list", Toast.LENGTH_SHORT).show();
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
