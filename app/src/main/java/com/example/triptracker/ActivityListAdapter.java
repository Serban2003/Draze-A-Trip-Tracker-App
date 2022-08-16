package com.example.triptracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ActivityListAdapter extends ArrayAdapter<TrackDetails> {

    private final Activity context;
    ArrayList<TrackDetails> trackDetails;

    public ActivityListAdapter(Activity context, ArrayList<TrackDetails> trackDetails) {
        super(context, R.layout.row_activity, trackDetails);

        this.context = context;
        this.trackDetails = trackDetails;
    }

    // Inflates the view
    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View view, ViewGroup parent)  {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint({"ViewHolder", "InflateParams"})
        View rowView = inflater.inflate(R.layout.row_activity, null,true);

        TextView titleTextView = rowView.findViewById(R.id.textViewRowHeaderValue);
        TextView distanceTextView = rowView.findViewById(R.id.textViewRowDistanceValue);
        TextView datetimeTextView = rowView.findViewById(R.id.textViewRowHeaderDateValue);
        TextView durationTextView = rowView.findViewById(R.id.textViewRowTitleValue);
        TextView descriptionTextView = rowView.findViewById(R.id.textViewRowDescriptionValue);
        TextView elevationTextView = rowView.findViewById(R.id.textViewRowElevationValue);
        View colorView = rowView.findViewById(R.id.colorView);

        titleTextView.setText(trackDetails.get(position).title);

        String description = trackDetails.get(position).description;

        if(description.equals("")){
            descriptionTextView.setVisibility(View.GONE);
            rowView.findViewById(R.id.textViewRowDescription).setVisibility(View.GONE);
            rowView.findViewById(R.id.separatorView).setVisibility(View.GONE);
        }
        if(description.length() > 35){
            description = description.substring(0, 35) + "...";
        }


        descriptionTextView.setText(description);

        distanceTextView.setText(trackDetails.get(position).distance);
        datetimeTextView.setText(trackDetails.get(position).timeCreated);
        durationTextView.setText(trackDetails.get(position).duration);
        elevationTextView.setText(trackDetails.get(position).elevation);
        colorView.setBackgroundTintList(ColorStateList.valueOf(trackDetails.get(position).color));
        return rowView;
    }
}
