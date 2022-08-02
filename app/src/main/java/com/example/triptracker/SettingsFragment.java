package com.example.triptracker;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsFragment extends Fragment {

    RecyclerView settingsRecyclerView;
    String[] settingsMenu, accountSettings, preferencesSettings;

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

        RecyclerViewAdapter settingsMenuAdapterItem1 = new RecyclerViewAdapter(getActivity(), item1, "mainCategory");
        RecyclerViewAdapter accountSettingsAdapter = new RecyclerViewAdapter(getActivity(), accountSettings, "normal");


        item2[0] = settingsMenu[1];
        RecyclerViewAdapter settingsMenuAdapterItem2 = new RecyclerViewAdapter(getActivity(), item2, "mainCategory");
        RecyclerViewAdapter preferencesSettingsAdapter = new RecyclerViewAdapter(getActivity(), preferencesSettings, "normal");

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