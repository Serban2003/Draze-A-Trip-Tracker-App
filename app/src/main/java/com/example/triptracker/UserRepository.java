package com.example.triptracker;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

public class UserRepository {
    private UserDao userDao;

    UserRepository(Application application){
        UserDatabase database = UserDatabase.getDatabase(application);
        this.userDao = database.userDao();
    }


    LiveData<User> getUserById(String keyId){
        return userDao.getUserById(keyId);
    }

    public void insertUser(User user){
        new insertUserAsyncTask(userDao).execute(user);
    }

    private static class insertUserAsyncTask extends AsyncTask<User, Void, Void> {

        private UserDao mAsyncTaskDao;

        insertUserAsyncTask(UserDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final User... users) {
            mAsyncTaskDao.insertUser(users[0]);
            return null;
        }
    }

    public void deleteAllUsers(){
        new deleteAllUsersAsyncTask(userDao).execute();
    }

    private static class deleteAllUsersAsyncTask extends AsyncTask<Void, Void, Void> {

        private UserDao mAsyncTaskDao;

        deleteAllUsersAsyncTask(UserDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.deleteAllUsers();
            return null;
        }
    }

//    public void updateUser(User user) {
//        new UserRepository.updateUserByIdAsyncTask(userDao, user).execute();
//    }
//
//    public static class updateUserByIdAsyncTask extends AsyncTask<Void, Void, Void> {
//
//        private UserDao mAsyncTaskDao;
//        private User user;
//
//        updateUserByIdAsyncTask(UserDao dao, User user) {
//
//            mAsyncTaskDao = dao;
//            this.user = user;
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            mAsyncTaskDao.updateUser(user);
//            return null;
//        }
//    }
}
