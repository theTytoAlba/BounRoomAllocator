package com.thetytoalba.bounroomallocator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import static com.thetytoalba.bounroomallocator.Constants.TAG_BUILDING;
import static com.thetytoalba.bounroomallocator.Constants.TAG_BUILDING_NAME;
import static com.thetytoalba.bounroomallocator.Constants.TAG_ROOM_NAME;

public class RoomCalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_calendar);

        Log.i("RoomCalendarActivity", "Started calendar activity for room " + getIntent().getStringExtra(TAG_ROOM_NAME) + " in building " + getIntent().getStringExtra(TAG_BUILDING_NAME));
    }
}
