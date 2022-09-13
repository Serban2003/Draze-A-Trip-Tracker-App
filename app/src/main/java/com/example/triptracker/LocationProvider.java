package com.example.triptracker;

import static android.app.PendingIntent.FLAG_MUTABLE;
import static com.example.triptracker.App.CHANNEL_ID;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.Priority;

import java.util.ArrayList;
import java.util.Date;

public final class LocationProvider extends Service {
    private static final String TAG = "LocationService";

    //Allows activities to control the service
    private final IBinder binder = new LocationServiceBinder();

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private PowerManager.WakeLock wakeLock;
    //Holds all the details about the tracked session
    private final TrackedSession trackedSession = new TrackedSession();


    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: called");
        super.onCreate();
        // PARTIAL_WAKELOCK
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"Draze:wakelock");
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1, createNotification());
        Log.d(TAG, "Started service in foreground");

        return super.onStartCommand(intent, flags, startId);
    }

    //Defines the communication functions
    public class LocationServiceBinder extends Binder {

        //Continue / or start tracking
        void resumeTracking() {
            resumeLocationTracking();
        }

        //Pause
        void stopTracking() {
            stopLocationTracking();
        }


        //Check if the session is currently paused
        boolean isPaused() {
            return trackedSession.isPaused();
        }

        //Get the tracked session
        TrackedSession getTrackedActivity() {
            return trackedSession;
        }

        //Get all the track points
        ArrayList<Location> getLocations() {
            return trackedSession.getLocations();
        }

        //Set the title of the tracked session for persistence
        void setTitle(String title) {
            trackedSession.setTitle(title);
        }

        //Get the title of the tracked session
        String getTitle() {
            return trackedSession.getTitle();
        }

        //Set description for the tracked session for persistence
        void setDescription(String description) {
            trackedSession.setDescription(description);
        }

        //Get the description of the tracked session
        String getDescription() {
            return trackedSession.getDescription();
        }

        //Get the millisecond time that this was created
        Date getTimeCreated() {
            return trackedSession.getTimeCreated();
        }

        //Get the duration of the tracked session
        long getDuration() {
            return trackedSession.getCurrentTimeMillis();
        }

        //Get the duration as a string ready for printing
        long getTime(){return trackedSession.getTime();}

        String getTimeString() {
            return trackedSession.getTimeString();
        }

        //Get the distance of the tracked session
        float getDistance() {
            return trackedSession.getDistance();
        }

        //Get the distance as a string ready for printing
        String getDistanceString() {
            return trackedSession.getDistanceString();
        }

        //Get the average speed of the tracked session
        float getAverageSpeed() {
            return trackedSession.getAverageSpeed();
        }

        //Get the average speed as a string
        String getAverageSpeedString() {
            return trackedSession.getAverageSpeedString();
        }

        //Get the elevation
        float getElevation() {
            return trackedSession.getElevation();
        }

        //Get the elevation as a string ready for printing
        String getElevationString() {
            return trackedSession.getElevationString();
        }
    }

    //Start the location requests
    @SuppressLint({"MissingPermission", "VisibleForTests"})
    private void startLocationTracking() {
        //If a tracked session already exists just resume it
        if (trackedSession.isCreated()) {
            resumeLocationTracking();
            Log.d(TAG, "startLocationTracking: Tracked Activity has already been started, resuming...");
        } else {
            //Set up location requests
            fusedLocationProviderClient = new FusedLocationProviderClient(this);

            locationRequest = LocationRequest.create();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(5000);
            locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        Log.d(TAG, "onLocationResult: Location: " + location);
                        trackedSession.addLocation(location);
                    }
                }
            };

            //Set the tracked session to start
            trackedSession.startTrackingActivity();
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

            Log.d(TAG, "startLocationTracking: requested location updates");
        }

    }

    @SuppressLint("MissingPermission")
    private void resumeLocationTracking() {
        if (trackedSession.isCreated()) {
            //If the tracked session is paused, unpause it, otherwise do nothing
            if (trackedSession.isPaused()) {
                trackedSession.resumeTrackingActivity();
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            }
            Log.d(TAG, "resumeLocationTracking: resume requested location updates");
        }
        //If a tracked session didn't already exist with location tracking, a new one is started
        else {
            startLocationTracking();
            Log.d(TAG, "resumeLocationTracking: No Tracked Activity started, starting a new one...");
        }
    }

    //Pauses the tracking and removes the location updates
    private void stopLocationTracking() {
        trackedSession.stopTrackingActivity();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

        Log.d(TAG, "stopLocationTracking: removed location updates");
    }

    //Creates the notification that allows the user to come back to the tracking screen
    @RequiresApi(api = Build.VERSION_CODES.S)
    private Notification createNotification() {
        //Take the user to the Tracking Activity when clicking on the notification
        Intent notificationIntent = new Intent(this, TrackingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, FLAG_MUTABLE);


        //Build the notification
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Trip Tracker")
                .setContentText("Tracking Drive...")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(1, notification);

        return notification;
    }

    @Override
    public void onDestroy() {
        // PARTIAL_WAKELOCK
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (wakeLock != null && !wakeLock.isHeld()) {
            wakeLock.acquire();
        }
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
