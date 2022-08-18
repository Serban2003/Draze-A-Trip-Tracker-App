package com.example.triptracker;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity
public class User {

    private final static String NOT = "Not provided";

    @PrimaryKey(autoGenerate = true)
    private Long localId;

    private int totalActivities;

    public Long getLocalId() {
        return localId;
    }

    public void setLocalId(Long localId) {
        this.localId = localId;
    }

    public ArrayList<TrackDetails> getActivities() {
        return activities;
    }

    public void setActivities(ArrayList<TrackDetails> activities) {
        this.activities = activities;
    }

    @ColumnInfo(name = "keyId")
    private String keyId;

    @ColumnInfo(name = "username")
    private String username;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "password")
    private String password;

    @ColumnInfo(name = "fullName")
    private String fullName;

    @ColumnInfo(name = "gender")
    private String gender;

    @ColumnInfo(name = "phoneNumber")
    private String phoneNumber;

    @ColumnInfo(name = "location")
    private String location;

    @ColumnInfo(name = "avatarUri")
    private String avatarUri;

    private boolean verified;

    private static final String DEFAULT_PATH_AVATAR = "https://firebasestorage.googleapis.com/v0/b/trip-tracker-2844c.appspot.com/o/images%2Fno_image_1%400.75x.png?alt=media&token=81168beb-f6aa-44c2-8349-cd4c0490357a";

    @ColumnInfo(name = "activities")
    private ArrayList<TrackDetails> activities = new ArrayList<>();

    public User() {
        this.keyId = NOT;
        this.username = NOT;
        this.email = NOT;
        this.password = NOT;
        this.fullName = NOT;
        this.gender = NOT;
        this.phoneNumber = NOT;
        this.location = NOT;
        this.avatarUri = DEFAULT_PATH_AVATAR;
        this.verified = false;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phone) {
        this.phoneNumber = phone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public int getActivitiesNumber() {
        return activities.size();
    }

    public TrackDetails getActivityFromId(int id) {
        return activities.get(id);
    }

    public void addActivity(TrackDetails trackDetails) {
        activities.add(trackDetails);
    }

    public int getTotalActivities() {
        return totalActivities;
    }

    public void setTotalActivities(int totalActivities) {
        this.totalActivities = totalActivities;
    }

    public void setNewUser() {
        this.keyId = NOT;
        this.username = NOT;
        this.email = NOT;
        this.password = NOT;
        this.fullName = NOT;
        this.gender = NOT;
        this.phoneNumber = NOT;
        this.location = NOT;
        this.avatarUri = DEFAULT_PATH_AVATAR;
        this.verified = false;
        this.totalActivities = 0;
        activities = new ArrayList<>();
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "keyId='" + keyId + '\'' +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", fullName='" + fullName + '\'' +
                ", gender='" + gender + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", location='" + location + '\'' +
                ", avatarUri=" + avatarUri +
                '}';
    }
}
