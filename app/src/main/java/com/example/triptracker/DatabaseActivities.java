package com.example.triptracker;

import static com.example.triptracker.UserDao.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DatabaseActivities {

    public static final String TAG = "DatabaseActivities";
    public static final String USER = "users";
    public static final String PATH_TO_DATABASE = "https://trip-tracker-2844c-default-rtdb.europe-west1.firebasedatabase.app/";

    public static void findUserInDatabase(FirebaseUser firebaseUser) {
        DatabaseReference userReference = FirebaseDatabase.getInstance(PATH_TO_DATABASE).getReference().child(USER);
        userReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren())
                    if (Objects.equals(ds.child("email").getValue(), firebaseUser.getEmail())) {
                        updateActivities(ds);
                        updateUser(ds);
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public static void updateUser(DataSnapshot dataSnapshot) {
        user.setKeyId(dataSnapshot.getKey());
        user.setUsername(Objects.requireNonNull(dataSnapshot.child("username").getValue()).toString());
        user.setEmail(Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString());
        user.setPassword(Objects.requireNonNull(dataSnapshot.child("password").getValue()).toString());
        user.setFullName(Objects.requireNonNull(dataSnapshot.child("fullName").getValue()).toString());
        user.setGender(Objects.requireNonNull(dataSnapshot.child("gender").getValue()).toString());
        user.setPhoneNumber(Objects.requireNonNull(dataSnapshot.child("phoneNumber").getValue()).toString());
        user.setLocation(Objects.requireNonNull(dataSnapshot.child("location").getValue()).toString());
        user.setVerified(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).isEmailVerified());

        if (dataSnapshot.child("avatarUri").getValue() != null)
            user.setAvatarUri(Objects.requireNonNull(dataSnapshot.child("avatarUri").getValue()).toString());

        Log.d(TAG, "User updated: " + user);
    }

    public static void updateUserToDatabase(User user) {
        DatabaseReference userReference = FirebaseDatabase.getInstance(PATH_TO_DATABASE).getReference().child(USER).child(user.getKeyId());

        userReference.child("username").setValue(UserDao.user.getUsername());
        userReference.child("fullName").setValue(UserDao.user.getFullName());
        userReference.child("gender").setValue(UserDao.user.getGender());
        userReference.child("phoneNumber").setValue(UserDao.user.getPhoneNumber());
        userReference.child("location").setValue(UserDao.user.getLocation());
        userReference.child("avatarUri").setValue(UserDao.user.getAvatarUri());
        userReference.child("verified").setValue(UserDao.user.isVerified());
    }

    public static void updateUILogin(){

    }

    public static void updateActivitiesToDatabase(List<TrackDetails> activities){
        DatabaseReference userReference = FirebaseDatabase.getInstance(PATH_TO_DATABASE).getReference().child(USER);
        userReference.child(user.getKeyId()).child("activities").setValue(activities);
    }

    public static void updateActivities(DataSnapshot dataSnapshot) {
        Iterable<DataSnapshot> iterable = dataSnapshot.child("activities").getChildren();

        ArrayList<TrackDetails> activities = new ArrayList<>();
        while (iterable.iterator().hasNext()) {
            TrackDetails trackDetails = new TrackDetails();
            trackDetails.dataSnapshotToTrackDetails(iterable.iterator().next());
            activities.add(trackDetails);
        }
    }

    public static void createUserInFirebaseDatabase(FirebaseUser firebaseUser) {
        FirebaseDatabase.getInstance(PATH_TO_DATABASE).getReference().child(USER).child(firebaseUser.getUid()).setValue(UserDao.user);
    }

    public static void sendEmailVerification(FirebaseUser firebaseUser, Activity activity) {
        firebaseUser.sendEmailVerification().addOnCompleteListener(activity, new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Toast.makeText(activity, "Verification email sent to " + firebaseUser.getEmail(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "sendEmailVerification", task.getException());
                    Toast.makeText(activity, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void deleteUserFromFirebaseDatabase(FirebaseUser firebaseUser) {
        FirebaseDatabase.getInstance(PATH_TO_DATABASE).getReference().child(USER).child(firebaseUser.getUid()).removeValue();
        firebaseUser.delete();
    }

    public static void signOutUserFromFirebase() {
        FirebaseAuth.getInstance().signOut();
    }

    public static void loadUserFromFirebaseDatabase(String keyId){
        DatabaseReference userReference = FirebaseDatabase.getInstance(PATH_TO_DATABASE).getReference().child(USER);
        userReference.child(keyId).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data from Firebase Database", task.getException());
            }
            else {
                Log.d(TAG, "Data loaded from Firebase Database" + task.getResult().getValue());
                updateUser(task.getResult());
            }
        });
    }


    //Local Room Database

//    public static void deleteUserFromLocalDatabase(UserDatabase database, String keyId){
//        if(isUserInLocalDatabase(database, keyId))
//            database.userDao().deleteUserById(keyId);
//    }

//    public static boolean isUserInLocalDatabase(UserDatabase database, String keyId){
//        User user = database.userDao().loadUserById(keyId);
//       if(user == null){
//           Log.d(TAG, "User with keyId=" + keyId + " doesn't exist in local database");
//           return false;
//       }
//       else {
//           Log.d(TAG, "User with keyId=" + keyId + " exists in local database");
//           return true;
//       }
//    }

//    public static void loadUserFromLocalDatabase(UserDatabase database, String keyId){
//        User user = database.userDao().loadUserById(keyId);
//        UserDao.user.setKeyId(user.getKeyId());
//        UserDao.user.setUsername(user.getUsername());
//        UserDao.user.setEmail(user.getEmail());
//        UserDao.user.setPassword(user.getPassword());
//        UserDao.user.setFullName(user.getFullName());
//        UserDao.user.setGender(user.getGender());
//        UserDao.user.setPhoneNumber(user.getPhoneNumber());
//        UserDao.user.setLocation(user.getLocation());
//        UserDao.user.setTotalActivities(user.getTotalActivities());
//        UserDao.user.setVerified(user.isVerified());
//        UserDao.user.setActivities(user.getActivities());
//    }



}
