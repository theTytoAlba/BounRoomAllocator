package com.thetytoalba.bounroomallocator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ManagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        Log.i("ManagerActivtiy", "Started app in manager mode.");

        Button buildingButton = (Button) findViewById(R.id.addBuildingButton);
        buildingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ManagerActivity.this, AddBuildingActivity.class));
            }
        });
    }
}
