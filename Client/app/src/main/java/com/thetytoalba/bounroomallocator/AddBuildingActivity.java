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
import static com.thetytoalba.bounroomallocator.Constants.TAG_BUILDING;
import static com.thetytoalba.bounroomallocator.Constants.TAG_BUILDING_NAME;
import static com.thetytoalba.bounroomallocator.Constants.TAG_SUCCESS;

public class AddBuildingActivity extends AppCompatActivity {

    private class AddBuildingTask extends AsyncTask<Void, Void, JSONObject> {
        JSONObject buildingMessage;
        private Socket socket;
        private BufferedWriter out;
        private BufferedReader in;

        AddBuildingTask(JSONObject buildingMessage) {
            this.buildingMessage = buildingMessage;
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
                out.write(buildingMessage.toString());
                out.newLine();
                out.flush();

                result = new JSONObject(in.readLine());
            } catch (Exception e) {
                Log.e("LoginActivity", "Error while sending building to server.");
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
                    successfulAdd();
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            failedAdd();
        }
    }

    EditText buildingName;
    ProgressBar progressBar;
    Button addBuildingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_building);

        progressBar = (ProgressBar) findViewById(R.id.addBuildingActivity_progress);
        buildingName = (EditText) findViewById(R.id.addBuildingActivity_buildingNameEditText);
        addBuildingButton = (Button) findViewById(R.id.addBuildingActivity_addBuildingButton);
        addBuildingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = buildingName.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Building name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                showProgress();
                try {
                    new AddBuildingTask(new JSONObject()
                            .put(Constants.TAG_CONNECTION_TYPE, Constants.TAG_ADD_BUILDING_CONNECTION)
                            .put(TAG_BUILDING, new JSONObject().put(TAG_BUILDING_NAME,name))).execute();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed to send request to server.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
    private void successfulAdd() {
        hideProgress();
        Toast.makeText(getApplicationContext(), "Added building successfully.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void failedAdd() {
        hideProgress();
        Toast.makeText(getApplicationContext(), "Could not add new building.", Toast.LENGTH_SHORT).show();
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        addBuildingButton.setVisibility(View.GONE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
        addBuildingButton.setVisibility(View.VISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
