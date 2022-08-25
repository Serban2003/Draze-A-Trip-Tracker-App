package com.example.triptracker;

import android.net.Uri;

import androidx.room.TypeConverter;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class DatabaseConverters {
    @TypeConverter
    public static ArrayList<TrackDetails> trackDetailsFromString(String value) {
        Type listType = new TypeToken<ArrayList<TrackDetails>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String trackDetailsFromArrayList(ArrayList<TrackDetails> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @TypeConverter
    public static ArrayList<LocationPointDetails> locationPointDetailsFromString(String value){
        Type listType = new TypeToken<ArrayList<LocationPointDetails>>(){}.getType();
        return  new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String locationPointDetailsFromArrayList(ArrayList<LocationPointDetails> list){
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @TypeConverter
    public static Uri fromUriString(String value) {
        String url = new Gson().toJson(value);
        return Uri.parse(url);
    }

    @TypeConverter
    public static String fromUri(Uri uri) {
        Gson gson = new Gson();
        return gson.toJson(uri);
    }

}
