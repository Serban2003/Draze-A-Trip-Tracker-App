package com.example.triptracker;

import com.google.firebase.database.DataSnapshot;

import java.util.Objects;

public class LocationPointDetails {
    Long timeCreated;
    Double elevation;
    Double longitude;
    Double latitude;

    public Long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Double getElevation() {
        return elevation;
    }

    public void setElevation(Double elevation) {
        this.elevation = elevation;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void dataSnapshotToLocationPoints(DataSnapshot dataSnapshot) {
        this.timeCreated = Long.parseLong(Objects.requireNonNull(dataSnapshot.child("timeCreated").getValue()).toString());
        this.elevation = Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("elevation").getValue()).toString());
        this.longitude = Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("longitude").getValue()).toString());
        this.latitude = Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("latitude").getValue()).toString());
    }
}
