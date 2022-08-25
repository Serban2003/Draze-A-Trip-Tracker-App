package com.example.triptracker;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class RouteDetailsActivity extends CustomSecondaryActivity implements OnMapReadyCallback {

    private static final String TAG = "RouteDetailsActivity";

    Integer sessionId;
    TextView sessionTitleTextView, sessionDescriptionTextView, sessionDistanceTextView, sessionDateTextView, sessionDurationTextView, sessionElevationTextView, sessionAverageSpeedTextView;
    GoogleMap map;
    TrackDetails trackDetails;
    ActivitiesViewModel activitiesViewModel;
    ActivitiesDatabase activitiesDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_details);

        sessionId = Integer.parseInt(getIntent().getExtras().getString("id"));
        sessionTitleTextView = findViewById(R.id.textViewRowTitleValue);
        sessionDateTextView = findViewById(R.id.textViewRowDateValue);
        sessionDescriptionTextView = findViewById(R.id.textViewRowDescriptionValue);
        sessionDurationTextView = findViewById(R.id.textViewRowDurationValue);
        sessionDistanceTextView = findViewById(R.id.textViewRowDistanceValue);
        sessionAverageSpeedTextView = findViewById(R.id.textViewRowAverageSpeedValue);
        sessionElevationTextView = findViewById(R.id.textViewRowElevationValue);

        activitiesViewModel = new ViewModelProvider(this).get(ActivitiesViewModel.class);
        activitiesViewModel.getActivityById(sessionId).observe(this, new Observer<TrackDetails>() {
            @Override
            public void onChanged(TrackDetails trackDetails) {
                sessionTitleTextView.setText(trackDetails.getTitle());
                sessionDateTextView.setText(trackDetails.getTimeCreated());
                sessionDescriptionTextView.setText(trackDetails.getDescription());
                sessionDurationTextView.setText(trackDetails.getDescription());
                sessionDistanceTextView.setText(trackDetails.getDistance());
                sessionAverageSpeedTextView.setText(trackDetails.getAverageSpeed());
                sessionElevationTextView.setText(trackDetails.getElevation());
                plotTrackPoints(trackDetails.getLocationPoints());
            }
        });

        com.google.android.gms.maps.MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
    }

    //Get the points from the database and plot them onto the map
    public void plotTrackPoints(ArrayList<LocationPointDetails> locationsPointDetails) {
        //Get track points

        PolylineOptions polylineOptions = new PolylineOptions();

        //Add track points to polyline
        for (LocationPointDetails locationPointDetails : locationsPointDetails) {
            LatLng latLng = new LatLng(locationPointDetails.getLatitude(), locationPointDetails.getLongitude());
            polylineOptions.add(latLng);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
        //Add polyline to map
        map.addPolyline(polylineOptions);

    }

    @Override
    public String getTitleView() {
        return "Track Details";
    }
}