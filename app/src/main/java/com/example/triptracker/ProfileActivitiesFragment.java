package com.example.triptracker;

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

public class ProfileActivitiesFragment extends Fragment {

    TextView activitiesNumberTextView, totalDurationTextView, totalKMTextView, averageSpeedTextView;
    ActivitiesViewModel activitiesViewModel;

    Double totalDuration, totalKM, averageSpeed;

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
                totalDuration = totalKM = averageSpeed = 0D;
                for (TrackDetails activity : activities) {
                    totalDuration += Double.parseDouble(activity.getDuration().replaceAll("[\\s+a-zA-Z :]", ""));
                    totalKM += Double.parseDouble(activity.getDistance().replaceAll("[\\s+a-zA-Z :]", ""));
                    //averageSpeed += Double.parseDouble(activity.getAverageSpeed().replaceAll("[\\s+a-zA-Z :]",""));
                }
                Integer size = activities.size();
                activitiesNumberTextView.setText(size.toString());
                totalDurationTextView.setText(totalDuration.toString());
                totalKMTextView.setText(totalKM.toString());
                averageSpeedTextView.setText(averageSpeed.toString());
            }
        });

        return view;
    }
}