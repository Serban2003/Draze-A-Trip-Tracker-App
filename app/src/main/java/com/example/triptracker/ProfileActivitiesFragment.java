package com.example.triptracker;

import static com.example.triptracker.TrackedSession.distanceToString;
import static com.example.triptracker.TrackedSession.timeToString;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class ProfileActivitiesFragment extends Fragment {

    TextView activitiesNumberTextView, totalDurationTextView, totalKMTextView, averageSpeedTextView;
    ActivitiesViewModel activitiesViewModel;

    Long totalDuration;
    Float totalKM, averageSpeed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_activities, container, false);

        activitiesNumberTextView = view.findViewById(R.id.activitiesNumberTextView);
        totalDurationTextView = view.findViewById(R.id.totalDurationTextView);
        totalKMTextView = view.findViewById(R.id.totalKMNumberTextView);
        averageSpeedTextView = view.findViewById(R.id.averageSpeedTextView);

        activitiesViewModel = new ViewModelProvider(this).get(ActivitiesViewModel.class);
        activitiesViewModel.getAllActivities().observe(getViewLifecycleOwner(), new Observer<List<TrackDetails>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(List<TrackDetails> activities) {
                totalDuration = 0L;
                totalKM = averageSpeed = 0F;
                for (TrackDetails activity : activities) {
                    totalDuration += Long.parseLong(activity.getDuration());
                    totalKM += Float.parseFloat(activity.getDistance());
                    averageSpeed += Float.parseFloat(activity.getAverageSpeed());
                }
                Integer size = activities.size();
                averageSpeed  = averageSpeed / size;

                activitiesNumberTextView.setText(size.toString());
                totalDurationTextView.setText(timeToString(totalDuration));
                totalKMTextView.setText(distanceToString(totalKM, true));
                averageSpeedTextView.setText(String.format(Locale.UK, "%.2f", averageSpeed));
            }
        });

        return view;
    }
}