package com.example.triptracker;

import static com.example.triptracker.TrackedSession.distanceToString;
import static com.example.triptracker.TrackedSession.timeToString;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    public static final String TAG = "Map Fragment";

    int index;
    private final String[] mapStyles = {"NORMAL", "HYBRID", "SATELLITE", "TERRAIN"};
    SharedPreferences preferences;

    private GoogleMap mMap;
    private MapView mapView;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private PolylineOptions polylineOptions;
    private LatLngBounds bound;

    ActivitiesViewModel activitiesViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        activitiesViewModel = new ViewModelProvider(this).get(ActivitiesViewModel.class);
        activitiesViewModel.getAllActivities().observe(getViewLifecycleOwner(), new Observer<List<TrackDetails>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(List<TrackDetails> activities) {
                mMap.clear();
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                for (TrackDetails activity : activities) {
                    polylineOptions = new PolylineOptions();

                    polylineOptions.color(activity.getColor());
                    if(activity.getColor() == 0) polylineOptions.color(R.color.main_color);

                    ArrayList<LocationPointDetails> locationPoints = activity.getLocationPoints();
                    for (LocationPointDetails locationPoint : locationPoints) {
                        LatLng latLng = new LatLng(locationPoint.getLatitude(), locationPoint.getLongitude());
                        builder.include(latLng);
                        polylineOptions.add(latLng);
                    }
                    if (polylineOptions != null) mMap.addPolyline(polylineOptions);
                }

                bound = builder.build();
                final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bound, 500, 500, 100);
                mMap.moveCamera(cu);
                //Toast.makeText(getContext(), "No session recorded!", Toast.LENGTH_LONG).show();
            }
        });

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

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
        toast.show();
        preferences.edit().putString("MapStyle", style).apply();
        Log.d(TAG, "MapStyle: " + style);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(false);


        preferences = requireActivity().getSharedPreferences("com.example.triptracker", Context.MODE_PRIVATE);
        String style = preferences.getString("MapStyle", "");

        if (Objects.equals(style, "")) style = "NORMAL";
        setGoogleMapsStyle(style);
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
            activity.setTitle(getString(R.string.mapFragmentTitle));
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
}