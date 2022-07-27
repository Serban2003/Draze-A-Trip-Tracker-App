package com.example.triptracker.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userId;
    private String displayName;
    private String email;
    private String password;

    public LoggedInUser(String userId, String displayName, String email, String password) {
        this.userId = userId;
        this.displayName = displayName;
        this.email = email;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}