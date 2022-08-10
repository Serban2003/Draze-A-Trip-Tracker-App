package com.example.triptracker;

import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.database.DataSnapshot;

import java.io.Serializable;
import java.util.ArrayList;

@Entity
public class User implements Serializable {

    private final static String NOT = "Not provided";

    @PrimaryKey(autoGenerate = true)
    private Long localId;

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

    private static final String DEFAULT_PATH_AVATAR = "https://firebasestorage.googleapis.com/v0/b/trip-tracker-2844c.appspot.com/o/images%2F-N8UaMxF3Kw2GWqLl8ZN.png?alt=media&token=722fb0f6-a3c7-4e85-8f2a-b8e51408ce6a";

    @ColumnInfo(name = "activities")
    private ArrayList<TrackDetails> activities = new ArrayList<>();

    public User(){
        this.keyId = NOT;
        this.username = NOT;
        this.email = NOT;
        this.password = NOT;
        this.fullName = NOT;
        this.gender = NOT;
        this.phoneNumber = NOT;
        this.location = NOT;
        this.avatarUri = DEFAULT_PATH_AVATAR;
    }
    @Ignore
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;

        this.keyId = NOT;
        this.fullName = NOT;
        this.gender = NOT;
        this.phoneNumber = NOT;
        this.location = NOT;
        this.avatarUri = DEFAULT_PATH_AVATAR;
    }
    @Ignore
    public User(String keyId, String username, String email, String password, String fullName, String gender, String phoneNumber, String location, String avatarUri){
        this.keyId = keyId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.avatarUri = avatarUri;
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

    public int getActivitiesNumber() {
        return activities.size();
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
