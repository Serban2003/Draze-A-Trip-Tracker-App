package com.example.triptracker;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Database(entities = {TrackDetails.class}, version = 1, exportSchema = false)
@TypeConverters(DatabaseConverters.class)
public abstract class ActivitiesDatabase extends RoomDatabase {
    private static final String TAG = "ActivitiesDatabase";
    public abstract ActivitiesDao activitiesDao();
    private static ActivitiesDatabase INSTANCE;

    static ActivitiesDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ActivitiesDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                   ActivitiesDatabase.class, "activities_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){

                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase database){
                    super.onOpen(database);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final ActivitiesDao activitiesDao;

        PopulateDbAsync(ActivitiesDatabase database) {
            activitiesDao = database.activitiesDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            return null;
        }
    }
}