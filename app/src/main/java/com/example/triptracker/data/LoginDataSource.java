package com.example.triptracker.data;

import com.example.triptracker.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    LoggedInUser user;
    public Result<LoggedInUser> login(String username, String email, String password) {

        try {
            user = new LoggedInUser(java.util.UUID.randomUUID().toString(), username, email, password);
            // TODO: handle loggedInUser authentication

            return new Result.Success<>(user);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }

    public LoggedInUser getUser(){
        return user;
    }
}