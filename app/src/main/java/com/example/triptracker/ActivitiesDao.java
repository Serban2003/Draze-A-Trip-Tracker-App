package com.example.triptracker;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;

@Dao
public interface ActivitiesDao {

    @Insert
    void insert(TrackDetails trackDetails);

    @Delete
    void deleteActivity(TrackDetails trackDetails);

    @Query("DELETE FROM activities_table")
    void deleteAllActivities();

    @Query("SELECT * FROM activities_table WHERE id = :activityId")
    LiveData<TrackDetails> getActivityById(Integer activityId);

    @Query("SELECT * FROM activities_table ORDER BY id ASC")
    LiveData<List<TrackDetails>> getAllActivities();
}
