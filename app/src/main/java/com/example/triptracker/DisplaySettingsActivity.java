package com.example.triptracker;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class DisplaySettingsActivity extends CustomSecondaryActivity {

    String[] displaySettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_settings);

        displaySettings = getResources().getStringArray(R.array.display_settings);

        ListView listViewDisplay = findViewById(R.id.listAccountSettings);

        ListViewAdapter adapter = new ListViewAdapter(displaySettings, getApplicationContext());


        listViewDisplay.setAdapter(adapter);
        listViewDisplay.setOnItemClickListener((parent, view, position, id) -> {
            String item = displaySettings[position];
            adapter.mSelectedItem = position;
            adapter.notifyDataSetChanged();
            switch (item) {
                case "Dark Mode": {
                    startActivity(new Intent(DisplaySettingsActivity.this, EditUsernameActivity.class));
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