package com.example.triptracker;

import static com.example.triptracker.UserDao.user;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class FirebaseActivities {

    public static  final String TAG = "FirebaseActivities";
    private static final String USER = "users";
    private static final String PATH_TO_DATABASE = "https://trip-tracker-2844c-default-rtdb.europe-west1.firebasedatabase.app/";

//    public FirebaseActivities(){
//        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        storageReference = FirebaseStorage.getInstance().getReference().child("images/");
//        userReference = FirebaseDatabase.getInstance(PATH_TO_DATABASE).getReference().child(USER);
//    }

    public static void findUser(FirebaseUser firebaseUser) {
        DatabaseReference userReference = FirebaseDatabase.getInstance(PATH_TO_DATABASE).getReference().child(USER);
        userReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren())
                    if (Objects.equals(ds.child("email").getValue(), firebaseUser.getEmail())){
                        updateActivities(ds);
                        updateUser(ds);
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public static void updateUser(DataSnapshot dataSnapshot){
        user.setKeyId(dataSnapshot.getKey());
        user.setUsername(Objects.requireNonNull(dataSnapshot.child("username").getValue()).toString());
        user.setEmail(Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString());
        user.setPassword(Objects.requireNonNull(dataSnapshot.child("password").getValue()).toString());
        user.setFullName(Objects.requireNonNull(dataSnapshot.child("fullName").getValue()).toString());
        user.setGender(Objects.requireNonNull(dataSnapshot.child("gender").getValue()).toString());
        user.setPhoneNumber(Objects.requireNonNull(dataSnapshot.child("phoneNumber").getValue()).toString());
        user.setLocation(Objects.requireNonNull(dataSnapshot.child("location").getValue()).toString());
        user.setTotalActivities(Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("totalActivities").getValue()).toString()));
        user.setVerified(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified());

        if(dataSnapshot.child("avatarUri").getValue() != null) user.setAvatarUri(Objects.requireNonNull(dataSnapshot.child("avatarUri").getValue()).toString());

        Log.d(TAG, "User updated: " + user);
    }

    public static void updateDatabase() {
        DatabaseReference userReference = FirebaseDatabase.getInstance(PATH_TO_DATABASE).getReference().child(USER);
        userReference.child(UserDao.user.getKeyId()).child("fullName").setValue(UserDao.user.getFullName());
        userReference.child(UserDao.user.getKeyId()).child("gender").setValue(UserDao.user.getGender());
        userReference.child(UserDao.user.getKeyId()).child("phoneNumber").setValue(UserDao.user.getPhoneNumber());
        userReference.child(UserDao.user.getKeyId()).child("location").setValue(UserDao.user.getLocation());
        userReference.child(UserDao.user.getKeyId()).child("avatarUri").setValue(UserDao.user.getAvatarUri());
        userReference.child(UserDao.user.getKeyId()).child("activitiesNumber").setValue(UserDao.user.getActivitiesNumber());
        userReference.child(UserDao.user.getKeyId()).child("verified").setValue(UserDao.user.isVerified());
        userReference.child(UserDao.user.getKeyId()).child("totalActivities").setValue(UserDao.user.getTotalActivities());
    }


    public static void updateActivities(DataSnapshot dataSnapshot){
        Iterable<DataSnapshot> iterable = dataSnapshot.child("activities").getChildren();

        ArrayList<TrackDetails> activities = new ArrayList<>();
        while (iterable.iterator().hasNext()) {
            TrackDetails trackDetails = new TrackDetails();
            trackDetails.dataSnapshotToTrackDetails(iterable.iterator().next());
            activities.add(trackDetails);
        }
        user.setActivities(activities);
    }

    public static void deleteUser(){

    }
}
