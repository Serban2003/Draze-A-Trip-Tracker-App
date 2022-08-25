package com.example.triptracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class FeedFragment extends Fragment implements RecyclerViewClickListener {

    private ActivitiesViewModel activitiesViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void recyclerViewListClicked(View view, int position) {

        TextView idTextView = view.findViewById(R.id.activityId);
        String id = idTextView.getText().toString();

        Intent intent = new Intent(getActivity(), RouteDetailsActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        view.findViewById(R.id.text_view_no_activities).setVisibility(View.GONE);
        RecyclerView recyclerView = view.findViewById(R.id.list_view_activities);


        ActivityListAdapter adapter = new ActivityListAdapter(getContext(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        activitiesViewModel = new ViewModelProvider(this).get(ActivitiesViewModel.class);
        activitiesViewModel.getAllActivities().observe(getViewLifecycleOwner(), new Observer<List<TrackDetails>>() {
            @Override
            public void onChanged(List<TrackDetails> trackDetails) {
                adapter.setActivities(trackDetails);
                if (adapter.getItemCount() == 0)
                    view.findViewById(R.id.text_view_no_activities).setVisibility(View.VISIBLE);
            }
        });


        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder,
                                         int direction) {
                        int position = viewHolder.getAbsoluteAdapterPosition();
                        TrackDetails trackDetails = adapter.getTrackAtPosition(position);
                        ///Toast.makeText(getActivity(), "Deleting " + trackDetails.getTitle(), Toast.LENGTH_LONG).show();
                        activitiesViewModel.deleteActivity(trackDetails);

                        Snackbar.make(view, "Deleting " + trackDetails.getTitle() + "...", Snackbar.LENGTH_LONG)
                                .setAnchorView(view.findViewById(R.id.bottom_navigation))
                                .setTextColor(getResources().getColor(R.color.white))
                                .setActionTextColor(getResources().getColor(R.color.accent_color))
                                .setAction("Undo", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        activitiesViewModel.insert(trackDetails);
                                        view.findViewById(R.id.text_view_no_activities).setVisibility(View.GONE);
                                    }
                                }).show();
                    }
                });
        helper.attachToRecyclerView(recyclerView);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Activity activity = getActivity();
        if (activity != null) {
            activity.setTitle(getString(R.string.feedFragmentTitle));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}