package com.example.triptracker;

import android.net.Uri;

public class User {
    private String keyId;
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String gender;
    private String phoneNumber;
    private String location;
    private Uri avatarUri;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(String keyId, String username, String email, String password, String fullName, String gender, String phoneNumber, String location, Uri avatarUri){
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

    public Uri getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(Uri avatarUri) {
        this.avatarUri = avatarUri;
    }
}
