package com.example.triptracker;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;

import java.util.ArrayList;

public class FeedFragment extends Fragment {

    private ListView activitiesListView;
    View view;
    ArrayList<TrackDetails> trackDetailsArrayList;

    private ShimmerFrameLayout mShimmerViewContainer;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_feed, container, false);
        activitiesListView = view.findViewById(R.id.list_view_activities);

        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);

        trackDetailsArrayList = new ArrayList<>();
        setUpActivityList();

        return view;
    }

    public void setUpActivityList(){
        if(UserDao.user.getActivitiesNumber() == 0) view.findViewById(R.id.text_view_no_activities).setVisibility(View.VISIBLE);

        for(int id = 0; id < UserDao.user.getActivitiesNumber(); ++id){
            TrackDetails trackDetails = UserDao.user.getActivityFromId(id);
            trackDetailsArrayList.add(trackDetails);
        }


        ActivityListAdapter activityListAdapter = new ActivityListAdapter(getActivity(), trackDetailsArrayList);
        activitiesListView = view.findViewById(R.id.list_view_activities);
        activitiesListView.setAdapter(activityListAdapter);

        activitiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                //goToSessionDetailsActivity.putExtra("id", "" + id);
                //Starting for result to be able to check if it was deleted so the listview can be refreshed
                //startActivityForResult(goToSessionDetailsActivity, 0);
            }
        });

        mShimmerViewContainer.stopShimmerAnimation();
        mShimmerViewContainer.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();

        mShimmerViewContainer.startShimmerAnimation();

        Activity activity = getActivity();
        if (activity != null) {
            activity.setTitle(getString(R.string.feedFragmentTitle));
        }
    }

    @Override
    public void onPause() {
        //mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }
}