package com.thetytoalba.bounroomallocator;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
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

import static android.view.View.GONE;
import static com.thetytoalba.bounroomallocator.Constants.HOST_IP;
import static com.thetytoalba.bounroomallocator.Constants.HOST_PORT;
import static com.thetytoalba.bounroomallocator.Constants.TAG_BUILDING;
import static com.thetytoalba.bounroomallocator.Constants.TAG_BUILDING_NAME;
import static com.thetytoalba.bounroomallocator.Constants.TAG_CONNECTION_TYPE;
import static com.thetytoalba.bounroomallocator.Constants.TAG_FRIDAY;
import static com.thetytoalba.bounroomallocator.Constants.TAG_GET_WEEK_CONNECTION;
import static com.thetytoalba.bounroomallocator.Constants.TAG_MONDAY;
import static com.thetytoalba.bounroomallocator.Constants.TAG_ROOMS;
import static com.thetytoalba.bounroomallocator.Constants.TAG_ROOM_NAME;
import static com.thetytoalba.bounroomallocator.Constants.TAG_SATURDAY;
import static com.thetytoalba.bounroomallocator.Constants.TAG_SUCCESS;
import static com.thetytoalba.bounroomallocator.Constants.TAG_SUNDAY;
import static com.thetytoalba.bounroomallocator.Constants.TAG_THURSDAY;
import static com.thetytoalba.bounroomallocator.Constants.TAG_TUESDAY;
import static com.thetytoalba.bounroomallocator.Constants.TAG_WEDNESDAY;
import static com.thetytoalba.bounroomallocator.Constants.TAG_WEEK;

public class RoomCalendarActivity extends AppCompatActivity {
    String buildingName = "";
    String roomName = "";
    ProgressBar progressBar;

    private class GetWeekTask extends AsyncTask<Void, Void, JSONObject> {
        JSONObject getWeekMessage;
        private Socket socket;
        private BufferedWriter out;
        private BufferedReader in;

        GetWeekTask(JSONObject getWeekMessage) {
            this.getWeekMessage = getWeekMessage;
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
                Log.e("RoomCalendarActivity", "Failed to establish connection.");
            }
            try {
                out.write(getWeekMessage.toString());
                out.newLine();
                out.flush();

                result = new JSONObject(in.readLine());
            } catch (Exception e) {
                Log.e("RoomCalendarActivity", "Error while sending credentials to server.");
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
                    successfulGetWeek(result.getJSONObject(TAG_WEEK));
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            failedGetWeek();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_calendar);

        roomName = getIntent().getStringExtra(TAG_ROOM_NAME);
        buildingName = getIntent().getStringExtra(TAG_BUILDING_NAME);
        progressBar = (ProgressBar) findViewById(R.id.roomCalendar_progress);

        try {

            JSONObject objectToSend = new JSONObject();
            objectToSend.put(TAG_CONNECTION_TYPE, TAG_GET_WEEK_CONNECTION);
            showProgress();
            new GetWeekTask(objectToSend).execute();
        } catch (Exception e) {

        }
    }

    private void initCalendarView(JSONObject week) {
        try {
            initDay(TAG_MONDAY, week.getJSONObject(TAG_MONDAY));
            initDay(TAG_TUESDAY, week.getJSONObject(TAG_TUESDAY));
            initDay(TAG_WEDNESDAY, week.getJSONObject(TAG_WEDNESDAY));
            initDay(TAG_THURSDAY, week.getJSONObject(TAG_THURSDAY));
            initDay(TAG_FRIDAY, week.getJSONObject(TAG_FRIDAY));
            initDay(TAG_SATURDAY, week.getJSONObject(TAG_SATURDAY));
            initDay(TAG_SUNDAY, week.getJSONObject(TAG_SUNDAY));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initDay(String day, JSONObject dayObject) {
        LinearLayout holder = (LinearLayout) findViewById(R.id.calendarActivity_daysHolder);
        LinearLayout dayLayout = (LinearLayout) this.getLayoutInflater().inflate(R.layout.layout_day_continer, null);
        TextView dayName = dayLayout.findViewById(R.id.dayContainer_dayName);
        dayName.setText(day);
        TextView clock09 = dayLayout.findViewById(R.id.clock09);
        TextView clock10 = dayLayout.findViewById(R.id.clock10);
        TextView clock11 = dayLayout.findViewById(R.id.clock11);
        TextView clock12 = dayLayout.findViewById(R.id.clock12);
        TextView clock13 = dayLayout.findViewById(R.id.clock13);
        TextView clock14 = dayLayout.findViewById(R.id.clock14);
        TextView clock15 = dayLayout.findViewById(R.id.clock15);
        TextView clock16 = dayLayout.findViewById(R.id.clock16);
        TextView clock17 = dayLayout.findViewById(R.id.clock17);
        TextView clock18 = dayLayout.findViewById(R.id.clock18);

        try {
            if (dayObject.has("09") && dayObject.getJSONObject("09").has(buildingName) && dayObject.getJSONObject("09").getJSONObject(buildingName).has(roomName)) {
                clock09.setText(dayObject.getJSONObject("09").getJSONObject(buildingName).getString(roomName));
                clock09.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
            if (dayObject.has("10") && dayObject.getJSONObject("10").has(buildingName) && dayObject.getJSONObject("10").getJSONObject(buildingName).has(roomName)) {
                clock10.setText(dayObject.getJSONObject("10").getJSONObject(buildingName).getString(roomName));
                clock10.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
            if (dayObject.has("11") && dayObject.getJSONObject("11").has(buildingName) && dayObject.getJSONObject("11").getJSONObject(buildingName).has(roomName)) {
                clock11.setText(dayObject.getJSONObject("11").getJSONObject(buildingName).getString(roomName));
                clock11.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
            if (dayObject.has("12") && dayObject.getJSONObject("12").has(buildingName) && dayObject.getJSONObject("12").getJSONObject(buildingName).has(roomName)) {
                clock12.setText(dayObject.getJSONObject("12").getJSONObject(buildingName).getString(roomName));
                clock12.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
            if (dayObject.has("13") && dayObject.getJSONObject("13").has(buildingName) && dayObject.getJSONObject("13").getJSONObject(buildingName).has(roomName)) {
                clock13.setText(dayObject.getJSONObject("13").getJSONObject(buildingName).getString(roomName));
                clock13.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
            if (dayObject.has("14") && dayObject.getJSONObject("14").has(buildingName) && dayObject.getJSONObject("14").getJSONObject(buildingName).has(roomName)) {
                clock14.setText(dayObject.getJSONObject("14").getJSONObject(buildingName).getString(roomName));
                clock14.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
            if (dayObject.has("15") && dayObject.getJSONObject("15").has(buildingName) && dayObject.getJSONObject("15").getJSONObject(buildingName).has(roomName)) {
                clock15.setText(dayObject.getJSONObject("15").getJSONObject(buildingName).getString(roomName));
                clock15.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
            if (dayObject.has("16") && dayObject.getJSONObject("16").has(buildingName) && dayObject.getJSONObject("16").getJSONObject(buildingName).has(roomName)) {
                clock16.setText(dayObject.getJSONObject("16").getJSONObject(buildingName).getString(roomName));
                clock16.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
            if (dayObject.has("17") && dayObject.getJSONObject("17").has(buildingName) && dayObject.getJSONObject("17").getJSONObject(buildingName).has(roomName)) {
                clock17.setText(dayObject.getJSONObject("17").getJSONObject(buildingName).getString(roomName));
                clock17.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
            if (dayObject.has("18") &&  dayObject.getJSONObject("18").has(buildingName) && dayObject.getJSONObject("18").getJSONObject(buildingName).has(roomName)) {
                clock18.setText(dayObject.getJSONObject("18").getJSONObject(buildingName).getString(roomName));
                clock18.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.addView(dayLayout);
    }
    private void successfulGetWeek(JSONObject week) {
        hideProgress();
        initCalendarView(week);
    }

    private void failedGetWeek() {
        hideProgress();
        Toast.makeText(getApplicationContext(), "Failed to fetch week schedule", Toast.LENGTH_SHORT).show();
    }


    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.INVISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
