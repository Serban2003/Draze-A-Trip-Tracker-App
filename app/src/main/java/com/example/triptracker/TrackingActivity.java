package com.example.triptracker;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TrackingActivity extends AppCompatActivity {

    private static final String TAG = "TrackingActivity";
    LocationProvider.LocationServiceBinder locationServiceBinder;
    TextView timeTextView, distanceTextView, averageSpeedTextView;
    Handler updateTimeHandler = new Handler();
    Handler updateStatsHandler = new Handler();
    boolean stopped = false, isBound = false;

    Runnable updateTimerThread = new Runnable() {
        @Override
        public void run() {
            String timeString = locationServiceBinder.getTimeString();
            timeTextView.setText(timeString);

            if (!stopped) updateTimeHandler.postDelayed(this, 0);
        }
    };

    Runnable updateStatsThread = new Runnable() {
        @Override
        public void run() {
            distanceTextView.setText(locationServiceBinder.getDistanceString());
            averageSpeedTextView.setText(locationServiceBinder.getAverageSpeedString());

            if (!stopped) updateStatsHandler.postDelayed(this, 1000);
        }
    };

    private final BroadcastReceiver locationSettingStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
                if (!stopped) {
                    stopTracking();
                    Toast.makeText(context, "Location must be on to track session", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: ");
            isBound = true;
            locationServiceBinder = (LocationProvider.LocationServiceBinder) service;
            locationServiceBinder.resumeTracking();

            //When the service is connected, make sure the receiver is listening for location setting changes
            registerReceiver(locationSettingStateReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

            updateTimeHandler.postDelayed(updateTimerThread, 0);
            updateStatsHandler.postDelayed(updateStatsThread, 0);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: ");
            locationServiceBinder = null;
            isBound = false;
            unregisterReceiver(locationSettingStateReceiver);
        }
    };

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        //Start the location tracking service
        Intent intent = new Intent(this, LocationProvider.class);
        getApplicationContext().bindService(intent, serviceConnection, Context.BIND_ABOVE_CLIENT);

        timeTextView = findViewById(R.id.text_view_time_tracking);
        distanceTextView = findViewById(R.id.text_view_distance_tracking);
        averageSpeedTextView = findViewById(R.id.text_view_speed_tracking);
    }

    public void onStopTrackingPressed(View view) {
        stopTracking();
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopped = false;
    }

    public void stopTracking() {
        stopped = true;
        if (isBound) {
            locationServiceBinder.stopTracking();
//            getApplicationContext().unbindService(serviceConnection);
            isBound = false;
        }

        startActivity(new Intent(this, EndTrackingActivity.class));
    }


    @Override
    public void onBackPressed() {
        stopTracking();
    }

    @Override
    protected void onDestroy() {
        if (isBound) {
            getApplicationContext().unbindService(serviceConnection);
            isBound = false;
        }
        super.onDestroy();
    }


}

