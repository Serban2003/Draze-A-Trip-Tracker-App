package com.example.triptracker;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserDao {
    User user = new User();

    @Query("SELECT * FROM users_table")
    LiveData<List<User>> getAllUsers();

    @Query("SELECT * FROM users_table WHERE keyId = :id")
    LiveData<User> getUserById(String id);

    @Query("DELETE FROM users_table WHERE keyId = :id")
    void deleteUserById(String id);

    @Insert
    void insertUser(User user);

    @Query("UPDATE users_table SET fullName = :fullName, gender = :gender, phoneNumber = :phoneNumber, location = :location WHERE keyId = :id")
    void updateUser(String fullName, String gender, String phoneNumber, String location, String id);

    @Query("UPDATE users_table SET avatarUri = :avatarUri WHERE keyId = :id")
    void updateAvatarUri(String avatarUri, String id);

    @Query("UPDATE users_table SET username = :username WHERE keyId = :id")
    void updateUsername(String username,  String id);

    @Query("UPDATE users_table SET email = :email WHERE keyId = :id")
    void updateEmail(String email, String id);

    @Delete
    void delete(User user);

    @Query("DELETE FROM users_table")
    void deleteAllUsers();
}