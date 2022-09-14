package com.example.triptracker;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class EndTrackingActivity extends FragmentActivity implements OnMapReadyCallback{
    private static final String TAG = "EndTrackingActivity";
    TextView timeTextView, distanceTextView, averageSpeedTextView, elevationTextView;

    GoogleMap map;

    Boolean mapReady = false, isBound = false;
    LocationProvider.LocationServiceBinder locationServiceBinder = null;

    //Holds the connection
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: ");
            locationServiceBinder = (LocationProvider.LocationServiceBinder) service;

            isBound = true;
            updateStats();
            plotLocations();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: ");
            locationServiceBinder = null;
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_tracking);

        Intent intent = new Intent(this, LocationProvider.class);

        //Set views
        timeTextView = findViewById(R.id.text_view_time);
        distanceTextView = findViewById(R.id.text_view_distance);
        averageSpeedTextView = findViewById(R.id.text_view_speed);
        elevationTextView = findViewById(R.id.text_view_elevation);
        com.google.android.gms.maps.MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Bind to service
        getApplicationContext().bindService(intent, serviceConnection, Context.BIND_ABOVE_CLIENT);
    }


    void updateStats() {
        timeTextView.setText(locationServiceBinder.getTimeString());
        distanceTextView.setText(locationServiceBinder.getDistanceString());
        averageSpeedTextView.setText(locationServiceBinder.getAverageSpeedString());
        elevationTextView.setText(locationServiceBinder.getElevationString());
    }

    void plotLocations() {
        if (!(mapReady & isBound)) {
            return;
        }

        Log.d(TAG, "plotTrackPoints: plotting points");
        ArrayList<Location> locations = locationServiceBinder.getLocations();

        LatLngBounds bound;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        PolylineOptions polylineOptions = new PolylineOptions();

        for (Location location : locations) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            polylineOptions.add(latLng);
            builder.include(latLng);
        }
        map.addPolyline(polylineOptions);
        bound = builder.build();
        final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bound, 500, 500, 100);
        map.moveCamera(cu);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapReady = true;
        map = googleMap;
        //When the map is ready go to try and plot the points
        plotLocations();
    }

    public void onResumeTrackingPressed(View v) {
        //Building the location request
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(locationSettingsResponse -> resumeTracking());

        task.addOnFailureListener(this, this::promptToChangeLocationSettings);
    }

    //Show user a dialog box to change their location setting
    private void promptToChangeLocationSettings(Exception e) {
        if (e instanceof ResolvableApiException) {
            try {
                ResolvableApiException resolvable = (ResolvableApiException) e;
                resolvable.startResolutionForResult(this, MainActivity.REQUEST_CHECK_SETTINGS);
            } catch (IntentSender.SendIntentException ignored) {
            }
        }
    }

    public void resumeTracking() {
        Intent intent = new Intent(this, TrackingActivity.class);
        startActivity(intent);

        Log.d(TAG, "onResumeTracking: ");
    }

    public void onEndTrackingPressed(View v) {
        endTracking();
    }

    public void endTracking() {
        //If the service is bound, stop tracking and unbind
        if (isBound) {
            getApplicationContext().unbindService(serviceConnection);
            isBound = false;
        }
        //Go to save the tracked session
        startActivity(new Intent(this, SaveTrackedActivity.class));
    }

    @Override
    public void onBackPressed() {
        //Back is set up to end the tracking session and go to the save screen
        endTracking();
    }

    @Override
    protected void onDestroy() {
        //If the service is still bound, unbind but don't stop tracking - could be in the background
        if (isBound) {
            getApplicationContext().unbindService(serviceConnection);
            isBound = false;
        }
        super.onDestroy();
    }
}