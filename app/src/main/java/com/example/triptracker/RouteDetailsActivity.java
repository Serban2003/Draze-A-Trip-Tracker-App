package com.example.triptracker;

import static com.example.triptracker.TrackedSession.distanceToString;
import static com.example.triptracker.TrackedSession.timeToString;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class RouteDetailsActivity extends CustomSecondaryActivity implements OnMapReadyCallback {

    private static final String TAG = "RouteDetailsActivity";

    Integer sessionId;
    TextView sessionTitleTextView, sessionDescriptionTextView, sessionDistanceTextView, sessionDateTextView, sessionDurationTextView, sessionElevationTextView, sessionAverageSpeedTextView;
    GoogleMap map;
    ActivitiesViewModel activitiesViewModel;
    TrackDetails track;

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
                if(trackDetails != null){
                    track = trackDetails;
                    sessionTitleTextView.setText(trackDetails.getTitle());
                    sessionDateTextView.setText(trackDetails.getTimeCreated());

                    if(trackDetails.getDescription().equals("")){
                        findViewById(R.id.textViewRowDescription).setVisibility(View.GONE);
                        findViewById(R.id.separatorView6).setVisibility(View.GONE);
                        sessionDescriptionTextView.setVisibility(View.GONE);
                    }

                    sessionDescriptionTextView.setText(trackDetails.getDescription());
                    sessionDurationTextView.setText(timeToString(Long.parseLong(trackDetails.getDuration())));
                    sessionDistanceTextView.setText(distanceToString(Float.parseFloat(trackDetails.getDistance()), true));
                    sessionAverageSpeedTextView.setText(String.format(Locale.UK, "%.2f", Float.parseFloat(trackDetails.getAverageSpeed())));
                    sessionElevationTextView.setText(distanceToString(Float.parseFloat(trackDetails.getElevation()), true));
                    plotTrackPoints(trackDetails.getLocationPoints());
                }
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
        polylineOptions.color(R.color.main_color);
        //Add polyline to map
        map.addPolyline(polylineOptions);

    }

    @Override
    public String getTitleView() {
        return "Track Details";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.session_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.deleteButton) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to delete this track?")
                    .setTitle("Delete")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();

                            activitiesViewModel.deleteActivity(track);
                        }
                    })
                    .setNegativeButton("Cancel", null);
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}