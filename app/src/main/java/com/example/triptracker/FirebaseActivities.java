package com.example.triptracker;

import android.annotation.SuppressLint;

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

import java.util.Objects;

public class FirebaseActivities {

    boolean found = false;
    public User user = new User();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseStorage storage;
    private DataSnapshot currentDataSnapshot;
    private static StorageReference storageReference;
    private static DatabaseReference userReference;
    private DatabaseReference rootReference;

    private static final String USER = "users";
    private static final String PATH_TO_DATABASE = "https://trip-tracker-2844c-default-rtdb.europe-west1.firebasedatabase.app/";


    public FirebaseActivities(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference().child("images/");
        rootReference = FirebaseDatabase.getInstance(PATH_TO_DATABASE).getReference();
        userReference = rootReference.child(USER);
    }

    public void findUser() {
        userReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (Objects.equals(ds.child("email").getValue(), firebaseUser.getEmail())) {
                        currentDataSnapshot = ds;
                        createUser();
                    }
                }
//                storageReference = storage.getReference().child("images/" + currentDataSnapshot.getKey());
//                storageReference.getDownloadUrl().addOnSuccessListener(uri -> userBundle.putString("avatarUri", uri.toString()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public DataSnapshot getCurrentDataSnapshot() {
        return currentDataSnapshot;
    }

    public void createUser(){
        user.setKeyId(currentDataSnapshot.getKey());
        user.setUsername(Objects.requireNonNull(currentDataSnapshot.child("username").getValue()).toString());
        user.setEmail(Objects.requireNonNull(currentDataSnapshot.child("email").getValue()).toString());
        user.setPassword(Objects.requireNonNull(currentDataSnapshot.child("password").getValue()).toString());
        user.setFullName(Objects.requireNonNull(currentDataSnapshot.child("fullName").getValue()).toString());
        user.setGender(Objects.requireNonNull(currentDataSnapshot.child("gender").getValue()).toString());
        user.setPhoneNumber(Objects.requireNonNull(currentDataSnapshot.child("phoneNumber").getValue()).toString());
        user.setLocation(Objects.requireNonNull(currentDataSnapshot.child("location").getValue()).toString());
    }

    public User getUser(){
        findUser();
        return user;
    }
}
