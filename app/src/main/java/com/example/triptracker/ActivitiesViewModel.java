package com.example.triptracker;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ActivitiesViewModel extends AndroidViewModel {

    private ActivitiesRepository activitiesRepository;

    private LiveData<List<TrackDetails>> allActivities;

    public ActivitiesViewModel(Application application) {
        super(application);
        activitiesRepository = new ActivitiesRepository(application);
        allActivities = activitiesRepository.getAllActivities();
    }

    LiveData<List<TrackDetails>> getAllActivities() {
        return allActivities;
    }

    public void insert(TrackDetails trackDetails) {
        activitiesRepository.insert(trackDetails);
    }

    public void deleteActivity(TrackDetails trackDetails) {activitiesRepository.deleteActivity(trackDetails);}

    public void deleteAllActivities(){
        activitiesRepository.deleteAllActivities();
    }

    public LiveData<TrackDetails> getActivityById(Integer id){
        return activitiesRepository.getActivityById(id);
    }
}