package com.example.triptracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>{

    Context context;
    String[] titles;
    String type;

    public RecyclerViewAdapter(Context context, String[] titles, String type){
        this.titles = titles;
        this.context = context;
        this.type = type;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view;

        if(Objects.equals(type, "mainCategory")) view = layoutInflater.inflate(R.layout.recycler_row_main, parent, false);
        else view = layoutInflater.inflate(R.layout.recycler_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.title.setText(titles[position]);
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView title;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.settingsTitle);
        }
    }

}
