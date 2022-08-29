package com.example.triptracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

public class DisplaySettingsActivity extends CustomSecondaryActivity {

    String[] displaySettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_settings);

        displaySettings = getResources().getStringArray(R.array.display_settings);

        ListView listViewDisplay = findViewById(R.id.listDisplaySettings);

        ListViewAdapter adapter = new ListViewAdapter(displaySettings, getApplicationContext());

        listViewDisplay.setAdapter(adapter);
        listViewDisplay.setOnItemClickListener((parent, view, position, id) -> {
            String item = displaySettings[position];
            adapter.mSelectedItem = position;
            adapter.notifyDataSetChanged();
            switch (item) {
                case "Dark Mode": {
                    Toast.makeText(this, "Not yet implemented!", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        });
    }

    @Override
    public String getTitleView() {
        return "Display";
    }
}