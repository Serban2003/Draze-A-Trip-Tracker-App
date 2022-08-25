package com.example.triptracker;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

public class App extends Application {
    public static final String CHANNEL_ID = "LocationTrackingChannel";

    @Override
    public void onCreate() {
        createNotificationChannel();
        super.onCreate();
    }

    //Creates the one channel on which the service notification will be displayed
    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Location Tracking Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }
}
