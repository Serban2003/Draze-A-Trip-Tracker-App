package com.example.triptracker;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Objects;

@Entity(tableName = "activities_table")
public class TrackDetails {
    
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    Integer id;

    @ColumnInfo(name = "title")
    String title;

    @ColumnInfo(name = "description")
    String description;

    @ColumnInfo(name = "timeCreated")
    String timeCreated;

    @ColumnInfo(name = "distance")
    String distance;

    @ColumnInfo(name = "duration")
    String duration;

    @ColumnInfo(name = "averageSpeed")
    String averageSpeed;

    @ColumnInfo(name = "elevation")
    String elevation;

    @ColumnInfo(name = "color")
    int color;

    @ColumnInfo(name = "locationPoints")
    ArrayList<LocationPointDetails> locationPoints = new ArrayList<>();


    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ArrayList<LocationPointDetails> getLocationPoints() {
        return locationPoints;
    }

    public void setLocationPoints(ArrayList<LocationPointDetails> locationPoints) {
        this.locationPoints = locationPoints;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(String averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public String getElevation() {
        return elevation;
    }

    public void setElevation(String elevation) {
        this.elevation = elevation;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void dataSnapshotToTrackDetails(DataSnapshot dataSnapshot) {
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(this.getClass().getName());
        logger.warning(dataSnapshot.getKey());
        this.title = Objects.requireNonNull(dataSnapshot.child("title").getValue()).toString();
        this.description = Objects.requireNonNull(dataSnapshot.child("description").getValue()).toString();
        this.timeCreated = Objects.requireNonNull(dataSnapshot.child("timeCreated").getValue()).toString();
        this.duration = Objects.requireNonNull(dataSnapshot.child("duration").getValue()).toString();
        this.averageSpeed = Objects.requireNonNull(dataSnapshot.child("averageSpeed").getValue()).toString();
        this.distance = Objects.requireNonNull(dataSnapshot.child("distance").getValue()).toString();
        this.elevation = Objects.requireNonNull(dataSnapshot.child("elevation").getValue()).toString();
        this.color = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("color").getValue()).toString());

        Iterable<DataSnapshot> iterable = dataSnapshot.child("locationPoints").getChildren();
        while (iterable.iterator().hasNext()) {
            LocationPointDetails locationPointDetails = new LocationPointDetails();
            locationPointDetails.dataSnapshotToLocationPoints(iterable.iterator().next());
            this.locationPoints.add(locationPointDetails);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "TrackDetails{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", timeCreated='" + timeCreated + '\'' +
                ", distance='" + distance + '\'' +
                ", duration='" + duration + '\'' +
                ", averageSpeed='" + averageSpeed + '\'' +
                ", elevation='" + elevation + '\'' +
                ", color=" + color +
                ", locationPoints=" + locationPoints +
                '}';
    }
}
