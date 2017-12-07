package com.thetytoalba.bounroomallocator;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

import static com.thetytoalba.bounroomallocator.Constants.HOST_IP;
import static com.thetytoalba.bounroomallocator.Constants.HOST_PORT;
import static com.thetytoalba.bounroomallocator.Constants.TAG_ADD_ROOM_CONNECTION;
import static com.thetytoalba.bounroomallocator.Constants.TAG_CONNECTION_TYPE;
import static com.thetytoalba.bounroomallocator.Constants.TAG_DETAILS;
import static com.thetytoalba.bounroomallocator.Constants.TAG_FRIDAY;
import static com.thetytoalba.bounroomallocator.Constants.TAG_LECTURE_NAME;
import static com.thetytoalba.bounroomallocator.Constants.TAG_MONDAY;
import static com.thetytoalba.bounroomallocator.Constants.TAG_ROOMS;
import static com.thetytoalba.bounroomallocator.Constants.TAG_ROOM_CAPACITY;
import static com.thetytoalba.bounroomallocator.Constants.TAG_SATURDAY;
import static com.thetytoalba.bounroomallocator.Constants.TAG_SUCCESS;
import static com.thetytoalba.bounroomallocator.Constants.TAG_SUNDAY;
import static com.thetytoalba.bounroomallocator.Constants.TAG_THURSDAY;
import static com.thetytoalba.bounroomallocator.Constants.TAG_TUESDAY;
import static com.thetytoalba.bounroomallocator.Constants.TAG_WEDNESDAY;
import static com.thetytoalba.bounroomallocator.Constants.TAG_WEEK;

public class AddLectureActivity extends AppCompatActivity {


    private class GetAvailableRoomsTask extends AsyncTask<Void, Void, JSONObject> {
        JSONObject lectureMessage;
        private Socket socket;
        private BufferedWriter out;
        private BufferedReader in;

        GetAvailableRoomsTask(JSONObject roomMessage) {
            this.lectureMessage = roomMessage;
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
                Log.e("AddLectureActivity", "Error while sending lecture to server.");
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
                    successfulGetAvailable(result.getJSONObject(TAG_ROOMS));
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            failedGetAvailable();
        }
    }


    JSONObject week;
    ProgressBar progressBar;
    Button getAvailableRooms;
    EditText lectureName;
    EditText capacity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lecture);
        initJSONWeek();
        initCalendarView();

        lectureName = (EditText) findViewById(R.id.addLecture_lectureName);
        capacity = (EditText) findViewById(R.id.addLecture_capacity);
        progressBar = (ProgressBar) findViewById(R.id.addLecture_progress);
        getAvailableRooms = (Button) findViewById(R.id.getAvailableRooms);
        getAvailableRooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(lectureName.getText()) || TextUtils.isEmpty(capacity.getText())) {
                    Toast.makeText(getApplicationContext(), "Lecture name or capacity cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    JSONObject objectToSend = new JSONObject();
                    objectToSend.put(TAG_CONNECTION_TYPE, Constants.TAG_GET_AVAILABLE_ROOMS_CONNECTION);
                    objectToSend.put(TAG_DETAILS,
                            new JSONObject().put(TAG_WEEK, week)
                                            .put(TAG_ROOM_CAPACITY, capacity.getText()));
                    showProgress();
                    new GetAvailableRoomsTask(objectToSend).execute();
                } catch (Exception e) {
                    hideProgress();
                    Toast.makeText(getApplicationContext(), "Failed to get available rooms", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void initJSONWeek(){
        week = new JSONObject();
        try {
            week.put(TAG_MONDAY, getJSONDay());
            week.put(TAG_TUESDAY, getJSONDay());
            week.put(TAG_WEDNESDAY, getJSONDay());
            week.put(TAG_THURSDAY, getJSONDay());
            week.put(TAG_FRIDAY, getJSONDay());
            week.put(TAG_SATURDAY, getJSONDay());
            week.put(TAG_SUNDAY, getJSONDay());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject getJSONDay(){
        JSONObject dayObj = new JSONObject();
        try {
            dayObj.put("09", false);
            dayObj.put("10", false);
            dayObj.put("11", false);
            dayObj.put("12", false);
            dayObj.put("13", false);
            dayObj.put("14", false);
            dayObj.put("15", false);
            dayObj.put("16", false);
            dayObj.put("17", false);
            dayObj.put("18", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dayObj;
    }

    private void initCalendarView() {
        try {
            initDay(TAG_MONDAY);
            initDay(TAG_TUESDAY);
            initDay(TAG_WEDNESDAY);
            initDay(TAG_THURSDAY);
            initDay(TAG_FRIDAY);
            initDay(TAG_SATURDAY);
            initDay(TAG_SUNDAY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initDay(final String day) {
        LinearLayout holder = (LinearLayout) findViewById(R.id.addLecture_daysHolder);
        LinearLayout dayLayout = (LinearLayout) this.getLayoutInflater().inflate(R.layout.layout_day_continer, null);
        TextView dayName = dayLayout.findViewById(R.id.dayContainer_dayName);
        dayName.setText(day);
        final TextView clock09 = dayLayout.findViewById(R.id.clock09);
        final TextView clock10 = dayLayout.findViewById(R.id.clock10);
        final TextView clock11 = dayLayout.findViewById(R.id.clock11);
        final TextView clock12 = dayLayout.findViewById(R.id.clock12);
        final TextView clock13 = dayLayout.findViewById(R.id.clock13);
        final TextView clock14 = dayLayout.findViewById(R.id.clock14);
        final TextView clock15 = dayLayout.findViewById(R.id.clock15);
        final TextView clock16 = dayLayout.findViewById(R.id.clock16);
        final TextView clock17 = dayLayout.findViewById(R.id.clock17);
        final TextView clock18 = dayLayout.findViewById(R.id.clock18);

        clock09.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    boolean selected = week.getJSONObject(day).getBoolean("09");
                    selected = !selected;
                    if (selected) {
                        clock09.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    } else {
                        clock09.setBackgroundColor(getResources().getColor(R.color.grayLight));
                    }
                    week.put(day, week.getJSONObject(day).put("09", selected));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        clock10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    boolean selected = week.getJSONObject(day).getBoolean("10");
                    selected = !selected;
                    if (selected) {
                        clock10.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    } else {
                        clock10.setBackgroundColor(getResources().getColor(R.color.colorAccentLighter));
                    }
                    week.put(day, week.getJSONObject(day).put("10", selected));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        clock11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    boolean selected = week.getJSONObject(day).getBoolean("11");
                    selected = !selected;
                    if (selected) {
                        clock11.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    } else {
                        clock11.setBackgroundColor(getResources().getColor(R.color.grayLight));
                    }
                    week.put(day, week.getJSONObject(day).put("11", selected));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        clock12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    boolean selected = week.getJSONObject(day).getBoolean("12");
                    selected = !selected;
                    if (selected) {
                        clock12.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    } else {
                        clock12.setBackgroundColor(getResources().getColor(R.color.colorAccentLighter));
                    }
                    week.put(day, week.getJSONObject(day).put("12", selected));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        clock13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    boolean selected = week.getJSONObject(day).getBoolean("13");
                    selected = !selected;
                    if (selected) {
                        clock13.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    } else {
                        clock13.setBackgroundColor(getResources().getColor(R.color.grayLight));
                    }
                    week.put(day, week.getJSONObject(day).put("13", selected));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        clock14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    boolean selected = week.getJSONObject(day).getBoolean("14");
                    selected = !selected;
                    if (selected) {
                        clock14.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    } else {
                        clock14.setBackgroundColor(getResources().getColor(R.color.colorAccentLighter));
                    }
                    week.put(day, week.getJSONObject(day).put("14", selected));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        clock15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    boolean selected = week.getJSONObject(day).getBoolean("15");
                    selected = !selected;
                    if (selected) {
                        clock15.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    } else {
                        clock15.setBackgroundColor(getResources().getColor(R.color.grayLight));
                    }
                    week.put(day, week.getJSONObject(day).put("15", selected));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        clock16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    boolean selected = week.getJSONObject(day).getBoolean("16");
                    selected = !selected;
                    if (selected) {
                        clock16.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    } else {
                        clock16.setBackgroundColor(getResources().getColor(R.color.colorAccentLighter));
                    }
                    week.put(day, week.getJSONObject(day).put("16", selected));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        clock17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    boolean selected = week.getJSONObject(day).getBoolean("17");
                    selected = !selected;
                    if (selected) {
                        clock17.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    } else {
                        clock17.setBackgroundColor(getResources().getColor(R.color.grayLight));
                    }
                    week.put(day, week.getJSONObject(day).put("17", selected));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        clock18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    boolean selected = week.getJSONObject(day).getBoolean("18");
                    selected = !selected;
                    if (selected) {
                        clock18.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    } else {
                        clock18.setBackgroundColor(getResources().getColor(R.color.colorAccentLighter));
                    }
                    week.put(day, week.getJSONObject(day).put("18", selected));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        holder.addView(dayLayout);
    }


    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        getAvailableRooms.setVisibility(View.GONE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
        getAvailableRooms.setVisibility(View.VISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void successfulGetAvailable(JSONObject rooms) {
        hideProgress();
        Log.i("AddLectureActivity", rooms.toString());
        Intent intent = new Intent(AddLectureActivity.this, SelectRoomActivity.class);
        intent.putExtra(TAG_ROOMS, rooms.toString());
        intent.putExtra(TAG_LECTURE_NAME, lectureName.getText().toString());
        intent.putExtra(TAG_WEEK, week.toString());
        startActivity(intent);
        finish();
    }

    private void failedGetAvailable() {
        hideProgress();
    }
}
