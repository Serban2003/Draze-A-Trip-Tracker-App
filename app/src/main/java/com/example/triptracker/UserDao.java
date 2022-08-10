package com.example.triptracker;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {
    User user = new User();

    @Query("SELECT * FROM user")
    List<User> getAll();
    @Query("SELECT * FROM user WHERE localId = :id")
    List<User> loadAllById(int id);
    @Insert
    void insertAll(User... users);

    @Delete
    void delete(User user);

    @Query("DELETE FROM User")
    void deleteAll();
}