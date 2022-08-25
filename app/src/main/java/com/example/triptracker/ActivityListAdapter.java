package com.example.triptracker;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentViewHolder;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.hudomju.swipe.adapter.ViewAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityListAdapter extends RecyclerView.Adapter<ActivityListAdapter.ActivityViewHolder> implements ViewAdapter {

    private static final String TAG = "ActivityListAdapter";
    private final LayoutInflater layoutInflater;
    private List<TrackDetails> activities;
    private static RecyclerViewClickListener itemListener;

    ActivityListAdapter(Context context, RecyclerViewClickListener itemListener) {
        layoutInflater = LayoutInflater.from(context);
        ActivityListAdapter.itemListener = itemListener;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.new_row_activity, parent, false);
        return new ActivityViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        if (activities != null) {
            TrackDetails currentTrack = activities.get(position);
            holder.activityIdTextView.setText(currentTrack.getId().toString());
            Log.d(TAG, currentTrack.getId().toString());

            holder.titleTextView.setText(currentTrack.getTitle());
            holder.distanceTextView.setText(currentTrack.getDistance());
            holder.durationTextView.setText(currentTrack.getDuration());

            String pattern = "E, dd MMM yyyy";
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(new Date(currentTrack.getTimeCreated()));

            holder.datetimeTextView.setText(date);

            String description = currentTrack.getDescription();

            if (description.equals("")) {
                holder.descriptionTextView.setVisibility(View.GONE);
                holder.itemView.findViewById(R.id.textViewRowDescription).setVisibility(View.GONE);
                holder.itemView.findViewById(R.id.separatorView).setVisibility(View.GONE);
            }
            if (description.length() > 35) {
                description = description.substring(0, 35) + "...";
            }
            holder.descriptionTextView.setText(description);
            holder.elevationTextView.setText(currentTrack.getElevation());

            GoogleMap map = holder.mapCurrent;

            holder.polylineOptions = new PolylineOptions();
            holder.polylineOptions.color(R.color.main_color);

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            ArrayList<LocationPointDetails> locationPoints = currentTrack.getLocationPoints();
            //Add track points to polyline
            for (LocationPointDetails locationPoint : locationPoints) {
                LatLng latLng = new LatLng(locationPoint.getLatitude(), locationPoint.getLongitude());
                builder.include(latLng);
                holder.polylineOptions.add(latLng);
                // googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }

            holder.bound = builder.build();

            //plotTrackPoints(map, currentTrack);

            setAnimation(holder.itemView);
        }
    }

    private void setAnimation(View view) {
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(500);
        view.startAnimation(animation);
    }

    @SuppressLint("NotifyDataSetChanged")
    void setActivities(List<TrackDetails> trackDetailsList) {
        activities = trackDetailsList;
        notifyDataSetChanged();
    }

    public int getItemCount() {
        if (activities != null)
            return activities.size();
        else return 0;
    }

    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public void getLocationOnScreen(int[] locations) {

    }

    @Override
    public View getChildAt(int index) {
        return null;
    }

    @Override
    public int getChildPosition(View position) {
        return 0;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    @Override
    public void onTouchEvent(MotionEvent e) {
    }

    @Override
    public Object makeScrollListener(AbsListView.OnScrollListener listener) {
        return null;
    }


    static class ActivityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, OnMapReadyCallback {
        GoogleMap mapCurrent;

        private final TextView activityIdTextView;
        private final TextView titleTextView;
        private final TextView distanceTextView;
        private final TextView datetimeTextView;
        private final TextView durationTextView;
        private final TextView descriptionTextView;
        private final TextView elevationTextView;
        private final MapView map;
        private PolylineOptions polylineOptions;
        private LatLngBounds bound;

        private ActivityViewHolder(View itemView) {
            super(itemView);
            activityIdTextView = itemView.findViewById(R.id.activityId);
            titleTextView = itemView.findViewById(R.id.textViewRowHeaderValue);
            distanceTextView = itemView.findViewById(R.id.textViewRowDistanceValue);
            datetimeTextView = itemView.findViewById(R.id.textViewRowHeaderDateValue);
            durationTextView = itemView.findViewById(R.id.textViewRowDurationValue);
            descriptionTextView = itemView.findViewById(R.id.textViewRowDescriptionValue);
            elevationTextView = itemView.findViewById(R.id.textViewRowElevationValue);

            map = itemView.findViewById(R.id.mapView);
            if (map != null) {
                map.onCreate(null);
                map.onResume();
                map.getMapAsync(this);
            }

            itemView.setOnClickListener(this);
        }

        @SuppressLint("RestrictedApi")
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            MapsInitializer.initialize(getApplicationContext());
            mapCurrent = googleMap;
            mapCurrent.addPolyline(polylineOptions);

            final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bound, 100);
            mapCurrent.moveCamera(cu);
            mapCurrent.getUiSettings().setAllGesturesEnabled(false);
        }

        @Override
        public void onClick(View v) {
            itemListener.recyclerViewListClicked(v, this.getLayoutPosition());
        }
    }

    public TrackDetails getTrackAtPosition(int position) {
        return activities.get(position);
    }

}
