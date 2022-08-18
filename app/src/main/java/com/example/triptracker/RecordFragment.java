package com.example.triptracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

public class RecordFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    public static final String TAG = "Record Fragment";

    int index;
    private final String[] mapStyles = {"NORMAL", "HYBRID", "SATELLITE", "TERRAIN"};
    SharedPreferences preferences;

    private GoogleMap mMap;
    private Button buttonStartTracking;
    private MapView mapView;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_record, container, false);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mapView = view.findViewById(R.id.map);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        buttonStartTracking = view.findViewById(R.id.button_start_tracking);
        buttonStartTracking.setOnClickListener(view1 -> {
            if (getContext() != null) {

                attemptStartTrackingService();

                Log.d(TAG, "Started LocationService");
            } else {
                Log.d(TAG, "Cannot start service, getContext() returned null");
            }
        });

        ImageButton buttonLocation = view.findViewById(R.id.locationButton);
        buttonLocation.setOnClickListener(view2 -> getMyLocation(mMap));

        ImageButton buttonMapStyle = view.findViewById(R.id.styleButton);
        buttonMapStyle.setOnClickListener(view12 -> {
            if (index == 3) index = -1;
            setGoogleMapsStyle(mapStyles[++index]);
        });

        return view;
    }

    @SuppressLint("CommitPrefEdits")
    void setGoogleMapsStyle(String style) {
        Toast toast;
        switch (style) {
            case "NORMAL": {
                toast = Toast.makeText(getActivity(), "Standard Map", Toast.LENGTH_SHORT);
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                index = 0;
                break;
            }
            case "HYBRID": {
                toast = Toast.makeText(getActivity(), "Hybrid Map", Toast.LENGTH_SHORT);
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                index = 1;
                break;
            }
            case "SATELLITE": {
                toast = Toast.makeText(getActivity(), "Satellite Map", Toast.LENGTH_SHORT);
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                index = 2;
                break;
            }
            case "TERRAIN": {
                toast = Toast.makeText(getActivity(), "Terrain Map", Toast.LENGTH_SHORT);
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                index = 3;
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + style);
        }
        toast.setGravity(Gravity.CENTER, 0, requireView().getHeight() / 2 - buttonStartTracking.getHeight() - 100);
        toast.show();
        preferences.edit().putString("MapStyle", style).apply();
        Log.d(TAG, "MapStyle: " + style);
    }

    private void attemptStartTrackingService() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(requireContext());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(locationSettingsResponse -> startTrackingActivity());
        task.addOnFailureListener(requireActivity(), this::promptToChangeLocationSettings);
    }


    public void startTrackingActivity() {
        Intent intent = new Intent(getContext(), TrackingActivity.class);
        startActivity(intent);

        Intent serviceIntent = new Intent(getContext(), LocationProvider.class);
        requireContext().startService(serviceIntent);

    }

    //Ask the user to turn on their location settings
    private void promptToChangeLocationSettings(Exception e) {
        if (e instanceof ResolvableApiException) {
            try {
                ResolvableApiException resolvable = (ResolvableApiException) e;
                resolvable.startResolutionForResult(requireActivity(), MainActivity.REQUEST_CHECK_SETTINGS);
            } catch (IntentSender.SendIntentException ignored) {
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull final GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        preferences = requireActivity().getSharedPreferences("com.example.triptracker", Context.MODE_PRIVATE);
        String style = preferences.getString("MapStyle", "");

        if (Objects.equals(style, "")) style = "NORMAL";
        setGoogleMapsStyle(style);
        getMyLocation(mMap);
    }

    @SuppressLint("MissingPermission")
    public void getMyLocation(GoogleMap googleMap) {
        @SuppressLint("VisibleForTests") FusedLocationProviderClient fusedLocationProviderClient = new FusedLocationProviderClient(requireActivity());
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
                        googleMap.animateCamera(cameraUpdate);
                        googleMap.moveCamera(cameraUpdate);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                        builder.setMessage("Can't find location!")
                                .setTitle("Error")
                                .setPositiveButton("OK", null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        Activity activity = getActivity();
        if (activity != null) {
            activity.setTitle(getString(R.string.recordFragmentTitle));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
    }
}
