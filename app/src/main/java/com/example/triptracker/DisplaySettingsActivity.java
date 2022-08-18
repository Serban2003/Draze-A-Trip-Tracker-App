package com.example.triptracker;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

public class DisplaySettingsActivity extends CustomSecondaryActivity {

    RecyclerView displayRecyclerView;
    String[] displaySettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_settings);

        displayRecyclerView = findViewById(R.id.displayRecyclerView);
        displaySettings = getResources().getStringArray(R.array.display_settings);

        RecyclerViewAdapter displaySettingsAdapter = new RecyclerViewAdapter(this, displaySettings, "normal", item -> {
            if ("Dark Mode".equals(item)) {
                Toast.makeText(this, "Not Implemented!", Toast.LENGTH_SHORT).show();
            }
        });
        displayRecyclerView.setAdapter(displaySettingsAdapter);
        displayRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public String getTitleView() {
        return "Display";
    }
}