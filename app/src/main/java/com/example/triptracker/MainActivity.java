package com.example.triptracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener{

    public static final String TAG = "MainActivity";

    BottomNavigationView bottomNavigationView;
    FeedFragment feedFragment = new FeedFragment();
    RecordFragment recordFragment = new RecordFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    MapFragment mapFragment = new MapFragment();

    private FirebaseUser firebaseUser;
    private static DatabaseReference userReference;

    private static final String USER = "users";
    private static final String PATH_TO_DATABASE = "https://trip-tracker-2844c-default-rtdb.europe-west1.firebasedatabase.app/";

    public static final int REQUEST_CHECK_SETTINGS = 1;
    private static final int ACCESS_FINE_LOCATION = 1;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.feed);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference rootReference = FirebaseDatabase.getInstance(PATH_TO_DATABASE).getReference();
        userReference = rootReference.child(USER);

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

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.feed:
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                        .replace(R.id.rlContainer, feedFragment)
                        .commit();
                return true;

            case R.id.map:
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                        .replace(R.id.rlContainer, mapFragment)
                        .commit();
                return true;

            case R.id.record:
                if (checkLocationPermission()){
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                            .replace(R.id.rlContainer, recordFragment)
                            .commit();
                }
                return true;

            case R.id.profile:{
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                        .replace(R.id.rlContainer, profileFragment)
                        .commit();
                return true;
            }

        }
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    static void updateUser(DataSnapshot dataSnapshot){
        UserDao.user.setKeyId(dataSnapshot.getKey());
        UserDao.user.setUsername(Objects.requireNonNull(dataSnapshot.child("username").getValue()).toString());
        UserDao.user.setEmail(Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString());
        UserDao.user.setPassword(Objects.requireNonNull(dataSnapshot.child("password").getValue()).toString());
        UserDao.user.setFullName(Objects.requireNonNull(dataSnapshot.child("fullName").getValue()).toString());
        UserDao.user.setGender(Objects.requireNonNull(dataSnapshot.child("gender").getValue()).toString());
        UserDao.user.setPhoneNumber(Objects.requireNonNull(dataSnapshot.child("phoneNumber").getValue()).toString());
        UserDao.user.setLocation(Objects.requireNonNull(dataSnapshot.child("location").getValue()).toString());
        UserDao.user.setTotalActivities(Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("totalActivities").getValue()).toString()));

        if(dataSnapshot.child("avatarUri").getValue() != null) UserDao.user.setAvatarUri(Uri.parse(Objects.requireNonNull(dataSnapshot.child("avatarUri").getValue()).toString()));
    }

    static void updateActivities(DataSnapshot dataSnapshot){
        Iterable<DataSnapshot> iterable = dataSnapshot.child("activities").getChildren();

        ArrayList<TrackDetails> activities = new ArrayList<>();
        while (iterable.iterator().hasNext()) {
            TrackDetails trackDetails = new TrackDetails();
            trackDetails.dataSnapshotToTrackDetails(iterable.iterator().next());
            activities.add(trackDetails);
        }
        UserDao.user.setActivities(activities);
    }

    static void updateDatabase() {
        userReference.child(UserDao.user.getKeyId()).child("fullName").setValue(UserDao.user.getFullName());
        userReference.child(UserDao.user.getKeyId()).child("gender").setValue(UserDao.user.getGender());
        userReference.child(UserDao.user.getKeyId()).child("phoneNumber").setValue(UserDao.user.getPhoneNumber());
        userReference.child(UserDao.user.getKeyId()).child("location").setValue(UserDao.user.getLocation());
        userReference.child(UserDao.user.getKeyId()).child("avatarUri").setValue(UserDao.user.getAvatarUri().toString());
        userReference.child(UserDao.user.getKeyId()).child("activitiesNumber").setValue(UserDao.user.getActivitiesNumber());
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settingsButton) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION);
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS){
            //If the user turned on their setting, start tracking the activity
            if (resultCode == RESULT_OK){
                recordFragment.startTrackingActivity();
            } else {
                //If the user didn't turn their setting on, the activity cannot be started to track
                Toast.makeText(this, "Location must be turned on to track a session", Toast.LENGTH_SHORT).show();
            }
        }
    }
}