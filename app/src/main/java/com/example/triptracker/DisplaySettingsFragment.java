package com.example.triptracker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

public class DisplaySettingsFragment extends Fragment {

    public interface SendData{
        void getEvent(String s);
    }
    Bundle preferences = new Bundle();
    DisplaySettingsFragment.SendData sendData;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Activity activity;
        if (context instanceof Activity){
            activity = (Activity) context;
            sendData = (SendData) activity;
        }
    }

    RecyclerView displayRecyclerView;
    String[] displaySettings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display_settings, container, false);

        displayRecyclerView = view.findViewById(R.id.displayRecyclerView);
        displaySettings = getResources().getStringArray(R.array.display_settings);

        RecyclerViewAdapter displaySettingsAdapter = new RecyclerViewAdapter(getActivity(), displaySettings, "normal", item -> {
            if ("Dark Mode".equals(item)) {
                Toast.makeText(getActivity(), "Not Implemented!", Toast.LENGTH_SHORT).show();
            }
        });
        displayRecyclerView.setAdapter(displaySettingsAdapter);
        displayRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }
}