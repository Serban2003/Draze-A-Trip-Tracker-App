package com.example.triptracker;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import top.defaults.colorpicker.ColorPickerPopup;

public class SaveTrackedActivity extends CustomSecondaryActivity {

    private static final String TAG = "SaveTrackedActivity";
    private static final String USER = "users";
    private static final String PATH_TO_DATABASE = "https://trip-tracker-2844c-default-rtdb.europe-west1.firebasedatabase.app/";
    LocationProvider.LocationServiceBinder locationServiceBinder = null;

    EditText editTextTitle, editTextDescription;
    Button chooseColorButton;

    private View colorPreview;
    private int mDefaultColor = 0;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: ");
            locationServiceBinder = (LocationProvider.LocationServiceBinder) service;

            //Set views to data from tracked session
            editTextTitle.setText(locationServiceBinder.getTitle());
            editTextDescription.setText(locationServiceBinder.getDescription());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: ");
            locationServiceBinder = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_tracked);

        Intent intent = new Intent(this, LocationProvider.class);
        this.bindService(intent, serviceConnection, Context.BIND_ABOVE_CLIENT);

        editTextTitle = findViewById(R.id.edit_text_session_title);
        editTextDescription = findViewById(R.id.edit_text_session_description);
        colorPreview = findViewById(R.id.colorView);

        chooseColorButton = findViewById(R.id.button_choose_color);
        chooseColorButton.setOnClickListener(view -> new ColorPickerPopup.Builder(SaveTrackedActivity.this)
                .initialColor(Color.RED)
                .enableBrightness(true)
                .enableAlpha(false)
                .okTitle("Choose")
                .cancelTitle("Cancel")
                .showIndicator(true)
                .showValue(false)
                .build()
                .show(view, new ColorPickerPopup.ColorPickerObserver() {
                    @Override
                    public void
                    onColorPicked(int color) {
                        mDefaultColor = color;
                        colorPreview.setBackgroundTintList(ColorStateList.valueOf(mDefaultColor));
                    }
                }));
    }

    //Changes the title of the user given input to be nicely formatted with only the first
    //character of every word capitalised
    public String toTitleCase(String string) {
        if (string == null) return null;

        boolean space = true;
        StringBuilder builder = new StringBuilder(string);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else builder.setCharAt(i, Character.toLowerCase(c));

        }

        return builder.toString();
    }

    //Save the details to the database
    public void onSaveTrackedActivity(View v) {
        //Check if the user has set a title
        if (editTextTitle.getText().toString().trim().equals("")) {
            //Alert the user to add a title
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You can't leave the title of your run empty!")
                    .setTitle("Empty route title")
                    .setPositiveButton("OK", null);
            AlertDialog dialog = builder.create();
            dialog.show();

            return;
        }
        //Set the details to the tracked session for completeness
        saveDataToServiceBinder();

        //Save the tracked session data
        TrackDetails trackDetails = new TrackDetails();
        trackDetails.setTitle(locationServiceBinder.getTitle());
        trackDetails.setDescription(locationServiceBinder.getDescription());

        String pattern = "dd MMM yyyy HH:mm:ss";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(locationServiceBinder.getTimeCreated());

        trackDetails.setTimeCreated(date);
        trackDetails.setDistance(locationServiceBinder.getDistanceString());
        trackDetails.setDuration(locationServiceBinder.getTimeString());
        trackDetails.setElevation(locationServiceBinder.getElevationString());
        trackDetails.setColor(mDefaultColor);

        //Save the track locations
        ArrayList<Location> locations = locationServiceBinder.getLocations();

        if(locations.size() == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Couldn't save the tracked activity! No locations recorded.")
                    .setTitle("Error")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            backToMainActivity();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        for (Location location : locations) {
            LocationPointDetails locationPointDetails = new LocationPointDetails();
            locationPointDetails.setTimeCreated(location.getTime());
            locationPointDetails.setElevation(location.getAltitude());
            locationPointDetails.setLongitude(location.getLongitude());
            locationPointDetails.setLatitude(location.getLatitude());
            trackDetails.locationPoints.add(locationPointDetails);
        }

//        Executor executor = Executors.newSingleThreadExecutor();
//        executor.execute(() -> {
            ActivitiesViewModel activitiesViewModel = new ViewModelProvider(this).get(ActivitiesViewModel.class);
            activitiesViewModel.insert(trackDetails);
       // });
//        UserDao.user.addActivity(trackDetails);
//        UserDao.user.setTotalActivities(UserDao.user.getTotalActivities() + 1);
//        DatabaseReference userReference = FirebaseDatabase.getInstance(PATH_TO_DATABASE).getReference().child(USER);
//        userReference.child(UserDao.user.getKeyId()).child("activities").child("actId" + trackDetails.getId()).setValue(trackDetails);
//        userReference.child(UserDao.user.getKeyId()).child("totalActivities").setValue(UserDao.user.getTotalActivities());
//        //Clean up and go back to the main activity
        backToMainActivity();
    }

    //Button to discard the current tracked session
    public void onDiscardTrackedActivity(View v) {

        //If the user is sure they want to discard this activity, the user is safely taken back to the
        //main menu, stopping the service and losing the tracked session
        DialogInterface.OnClickListener dialogClickListener = (dialogInterface, which) -> {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                Log.d(TAG, "onSaveTrackedActivity: Stopped LocationService");
                backToMainActivity();
            }
        };

        //Show dialog to ask the user if they are sure they want to discard the current session
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage("Are you sure you want to discard this activity?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener)
                .show();
    }

    //When back is pressed, the data input by the user should be saved
    @Override
    public void onBackPressed() {
        saveDataToServiceBinder();
        super.onBackPressed();
    }

    //When up is pressed, the data input by the user should be saved
    @Override
    public boolean onSupportNavigateUp() {
        saveDataToServiceBinder();
        return super.onSupportNavigateUp();
    }

    //Set the user given strings into the tracked session for persistence
    public void saveDataToServiceBinder() {
        locationServiceBinder.setTitle(toTitleCase(editTextTitle.getText().toString()));
        locationServiceBinder.setDescription(editTextDescription.getText().toString());
    }

    //Safely go back to the main activity
    protected void backToMainActivity() {
        //Stop the location service
        stopService(new Intent(this, LocationProvider.class));

        //Go back to main activity intent
        Intent intent = new Intent(getBaseContext(), MainActivity.class);

        //Flag to clean up all other activities
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    @Override
    public String getTitleView() {
        return "Save activity";
    }

}