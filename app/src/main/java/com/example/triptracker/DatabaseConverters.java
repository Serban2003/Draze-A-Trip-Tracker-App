package com.example.triptracker;

import androidx.room.TypeConverter;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class DatabaseConverters {
    @TypeConverter
    public static ArrayList<TrackDetails> fromString(String value) {
        Type listType = new TypeToken<ArrayList<TrackDetails>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<TrackDetails> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}
