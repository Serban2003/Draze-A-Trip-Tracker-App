package com.example.triptracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

public class SettingsFragment extends Fragment {

    public interface SendData{
        public void someEvent(String s);

        @SuppressLint("RestrictedApi")
        void getUpdatedData(String action, Bundle bundle);
    }

    SendData sendData;

    RecyclerView settingsRecyclerView;
    String[] settingsMenu, accountSettings, preferencesSettings;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Activity activity;
        if (context instanceof Activity){
            activity = (Activity) context;
            sendData = (SendData) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        settingsRecyclerView = view.findViewById(R.id.settingsRecyclerView);

        settingsMenu = getResources().getStringArray(R.array.settings_menu);
        accountSettings = getResources().getStringArray(R.array.account_settings);
        preferencesSettings = getResources().getStringArray(R.array.preferences_settings);

        String[] item1 = {""}, item2 = {""};
        item1[0] = settingsMenu[0];

        RecyclerViewAdapter settingsMenuAdapterItem1 = new RecyclerViewAdapter(getActivity(), item1, "mainCategory", null);
        RecyclerViewAdapter accountSettingsAdapter = new RecyclerViewAdapter(getActivity(), accountSettings, "normal", item -> sendData.someEvent("Change Credentials"));

        item2[0] = settingsMenu[1];
        RecyclerViewAdapter settingsMenuAdapterItem2 = new RecyclerViewAdapter(getActivity(), item2, "mainCategory", null);
        RecyclerViewAdapter preferencesSettingsAdapter = new RecyclerViewAdapter(getActivity(), preferencesSettings, "normal", item -> sendData.someEvent("Change Credentials"));

        ConcatAdapter concatAdapter = new ConcatAdapter(settingsMenuAdapterItem1, accountSettingsAdapter, settingsMenuAdapterItem2, preferencesSettingsAdapter);

        settingsRecyclerView.setAdapter(concatAdapter);
        settingsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Activity activity = getActivity();
        if (activity != null) {
            activity.setTitle(getString(R.string.settingsFragmentTitle));
        }
    }
}