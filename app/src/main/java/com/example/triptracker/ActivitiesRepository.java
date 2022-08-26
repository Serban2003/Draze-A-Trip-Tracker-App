package com.example.triptracker;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import java.util.List;

public class ActivitiesRepository {
    private ActivitiesDao activitiesDao;
    private LiveData<List<TrackDetails>> allActivities;

    ActivitiesRepository(Application application) {
        ActivitiesDatabase activitiesDatabase = ActivitiesDatabase.getDatabase(application);
        activitiesDao = activitiesDatabase.activitiesDao();
        allActivities = activitiesDao.getAllActivities();
    }

    LiveData<List<TrackDetails>> getAllActivities() {
        return allActivities;
    }

    public void insert(TrackDetails trackDetails) {
        new insertAsyncTask(activitiesDao).execute(trackDetails);
    }
    LiveData<TrackDetails> getActivityById(Integer id){
        return activitiesDao.getActivityById(id);
    }


    private static class insertAsyncTask extends AsyncTask<TrackDetails, Void, Void> {

        private ActivitiesDao mAsyncTaskDao;

        insertAsyncTask(ActivitiesDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final TrackDetails... trackDetails) {
            mAsyncTaskDao.insert(trackDetails[0]);
            return null;
        }
    }

    public void deleteActivity(TrackDetails trackDetails)  {
        new deleteActivityAsyncTask(activitiesDao).execute(trackDetails);
    }

    private static class deleteActivityAsyncTask extends AsyncTask<TrackDetails, Void, Void> {
        private ActivitiesDao mAsyncTaskDao;

        deleteActivityAsyncTask(ActivitiesDao activitiesDao) {
            mAsyncTaskDao = activitiesDao;
        }

        @Override
        protected Void doInBackground(final TrackDetails... params) {
            mAsyncTaskDao.deleteActivity(params[0]);
            return null;
        }
    }

    public void deleteAllActivities()  {
        new deleteAllActivitiesAsyncTask(activitiesDao).execute();
    }

    private static class deleteAllActivitiesAsyncTask extends AsyncTask<Void, Void, Void> {
        private ActivitiesDao mAsyncTaskDao;

        deleteAllActivitiesAsyncTask(ActivitiesDao activitiesDao) {
            mAsyncTaskDao = activitiesDao;
        }

        @Override
        protected Void doInBackground(Void ... voids) {
            mAsyncTaskDao.deleteAllActivities();
            return null;
        }
    }
}
