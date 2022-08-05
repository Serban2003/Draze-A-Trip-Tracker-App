package com.example.triptracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
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

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener{

    User user = new User();
    BottomNavigationView bottomNavigationView;
    FeedFragment feedFragment = new FeedFragment();
    RecordFragment recordFragment = new RecordFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    MapFragment mapFragment = new MapFragment();

    private FirebaseUser firebaseUser;
    private static StorageReference storageReference;
    private static DatabaseReference userReference;

    private static Bundle userBundle;

    private static final String USER = "users";
    private static final String PATH_TO_DATABASE = "https://trip-tracker-2844c-default-rtdb.europe-west1.firebasedatabase.app/";

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

        userBundle = new Bundle();

        userReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (Objects.equals(ds.child("email").getValue(), firebaseUser.getEmail())) {
                        updateUser(ds);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        userBundle.putSerializable("user", user);
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
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                        .replace(R.id.rlContainer, recordFragment)
                        .commit();
                return true;

            case R.id.profile:{
                profileFragment.setArguments(userBundle);
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

    void updateUser(DataSnapshot ds) {
        user.setKeyId(ds.getKey());
        user.setUsername(Objects.requireNonNull(ds.child("username").getValue()).toString());
        user.setEmail(Objects.requireNonNull(ds.child("email").getValue()).toString());
        user.setPassword(Objects.requireNonNull(ds.child("password").getValue()).toString());
        user.setFullName(Objects.requireNonNull(ds.child("fullName").getValue()).toString());
        user.setGender(Objects.requireNonNull(ds.child("gender").getValue()).toString());
        user.setPhoneNumber(Objects.requireNonNull(ds.child("phoneNumber").getValue()).toString());
        user.setLocation(Objects.requireNonNull(ds.child("location").getValue()).toString());
    }

    static void updateUI(User user, Context context) {
        userReference.child(user.getKeyId()).child("fullName").setValue(user.getFullName());
        userReference.child(user.getKeyId()).child("gender").setValue(user.getGender());
        userReference.child(user.getKeyId()).child("phoneNumber").setValue(user.getPhoneNumber());
        userReference.child(user.getKeyId()).child("location").setValue(user.getLocation());

        if (user.getAvatarUri() != null) {
            storageReference = FirebaseStorage.getInstance().getReference().child("images/" + user.getKeyId());
            storageReference.putFile(Uri.parse(user.getAvatarUri())).addOnCompleteListener(task -> {
                userBundle.putString("avatarUri", user.getAvatarUri().toString());
                if (task.isSuccessful())
                    Toast.makeText(context, "Image Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settingsButton:{
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}