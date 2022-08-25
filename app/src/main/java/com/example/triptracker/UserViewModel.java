package com.example.triptracker;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class UserViewModel extends AndroidViewModel {

    private UserRepository userRepository;

    public UserViewModel(Application application){
        super(application);
        this.userRepository = new UserRepository(application);
    }

    public void insertUser(User user){
        userRepository.insertUser(user);
    }

    public LiveData<User> getUserById(String string){
       return userRepository.getUserById(string);
    }

    public void deleteAllUsers(){
        userRepository.deleteAllUsers();
    }
}
