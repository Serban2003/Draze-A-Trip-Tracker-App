package com.example.triptracker;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Objects;

public class ListViewAdapter extends ArrayAdapter<String> implements View.OnClickListener {

    private String[] settings;
    Context mContext;
    int mSelectedItem = -1;

    // View lookup cache
    private static class ViewHolder {
        TextView settingsName;
        View alertView;
    }

    public ListViewAdapter(String[] data, Context context) {
        super(context, R.layout.list_view_row, data);
        this.settings = data;
        this.mContext = context;
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        String settings = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_view_row, parent, false);
            viewHolder.settingsName = convertView.findViewById(R.id.settingsTitle);
            viewHolder.alertView = convertView.findViewById(R.id.alertView);

            if (Objects.equals(settings, "Verify Email"))
                viewHolder.alertView.setBackgroundTintList(ColorStateList.valueOf(-56064));
            else viewHolder.alertView.setVisibility(View.GONE);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

//        if (position == mSelectedItem) {
//            Animation animation = new TranslateAnimation(0, 200,0, 0);
//            animation.setDuration(500);
//            animation.setFillAfter(true);
//            result.startAnimation(animation);
//        }




        viewHolder.settingsName.setText(settings);

        // Return the completed view to render on screen
        return convertView;
    }
}
