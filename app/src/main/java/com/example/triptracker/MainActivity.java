package com.example.triptracker;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener, SettingsFragment.SendData, EditUsernameFragment.SendData, EditEmailFragment.SendData, DisplaySettingsFragment.SendData {

    BottomNavigationView bottomNavigationView;
    FeedFragment feedFragment = new FeedFragment();
    RecordFragment recordFragment = new RecordFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    SettingsFragment settingsFragment = new SettingsFragment();
    EditUsernameFragment editUsernameFragment = new EditUsernameFragment();
    DisplaySettingsFragment displaySettingsFragment = new DisplaySettingsFragment();
    FAQFragment faqFragment = new FAQFragment();
    LegalFragment legalFragment = new LegalFragment();
    AboutFragment aboutFragment = new AboutFragment();

    private DataSnapshot currentDataSnapshot;
    private FirebaseUser user;
    private FirebaseStorage storage;
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

        user = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();

        DatabaseReference rootReference = FirebaseDatabase.getInstance(PATH_TO_DATABASE).getReference();
        userReference = rootReference.child(USER);

        userBundle = new Bundle();

        userReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (Objects.equals(ds.child("email").getValue(), user.getEmail())) {
                        currentDataSnapshot = ds;
                        updateBundle(ds);
                    }
                }
                storageReference = storage.getReference().child("images/" + currentDataSnapshot.getKey());
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> userBundle.putString("avatarUri", uri.toString()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        profileFragment.setArguments(userBundle);
        editUsernameFragment.setArguments(userBundle);
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

            case R.id.record:
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                        .replace(R.id.rlContainer, recordFragment)
                        .commit();
                return true;

            case R.id.profile:
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                        .replace(R.id.rlContainer, profileFragment)
                        .commit();
                return true;

            case R.id.settings:
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                        .replace(R.id.rlContainer, settingsFragment)
                        .commit();
                return true;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_about_app) {// Do something

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void updateBundle(DataSnapshot ds) {
        userBundle.putString("keyId", ds.getKey());
        userBundle.putString("username", Objects.requireNonNull(ds.child("username").getValue()).toString());
        userBundle.putString("email", Objects.requireNonNull(ds.child("email").getValue()).toString());
        userBundle.putString("password", Objects.requireNonNull(ds.child("password").getValue()).toString());

        if (ds.child("fullName").getValue() != null)
            userBundle.putString("fullName", Objects.requireNonNull(ds.child("fullName").getValue()).toString());
        else userBundle.putString("fullName", "Not provided");

        if (ds.child("gender").getValue() != null)
            userBundle.putString("gender", Objects.requireNonNull(ds.child("gender").getValue()).toString());
        else userBundle.putString("gender", "Not provided");

        if (ds.child("phoneNumber").getValue() != null)
            userBundle.putString("phoneNumber", Objects.requireNonNull(ds.child("phoneNumber").getValue()).toString());
        else userBundle.putString("phoneNumber", "Not provided");

        if (ds.child("location").getValue() != null)
            userBundle.putString("location", Objects.requireNonNull(ds.child("location").getValue()).toString());
        else userBundle.putString("location", "Not provided");
    }

    static void updateUI(User user, Context context) {
        userReference.child(user.getKeyId()).child("fullName").setValue(user.getFullName());
        userReference.child(user.getKeyId()).child("gender").setValue(user.getGender());
        userReference.child(user.getKeyId()).child("phoneNumber").setValue(user.getPhoneNumber());
        userReference.child(user.getKeyId()).child("location").setValue(user.getLocation());

        if (user.getAvatarUri() != null)
            storageReference.putFile(user.getAvatarUri()).addOnCompleteListener(task -> {
                userBundle.putString("avatarUri", user.getAvatarUri().toString());
                if (task.isSuccessful())
                    Toast.makeText(context, "Image Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    @Override
    public void getEvent(String event) {
        switch (event) {
            case "Change Username": {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                        .replace(R.id.rlContainer, editUsernameFragment)
                        .commit();
                return;
            }
            case "Change Email": {
                Toast.makeText(this, "Not Implemented!", Toast.LENGTH_SHORT).show();
                return;
            }
            case "Change Password": {
                Toast.makeText(this, "Not Implemented!", Toast.LENGTH_SHORT).show();
                return;
            }
            case "Display": {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                        .replace(R.id.rlContainer, displaySettingsFragment)
                        .commit();
                return;
            }
            case "FAQ": {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                        .replace(R.id.rlContainer, faqFragment)
                        .commit();
                return;
            }
            case "Legal": {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                        .replace(R.id.rlContainer, legalFragment)
                        .commit();
                return;
            }
            case "About": {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                        .replace(R.id.rlContainer, aboutFragment)
                        .commit();
                return;
            }
            case "Delete Your Account": {
                CustomDialogClass customDialog = new CustomDialogClass(MainActivity.this);
                customDialog.show();
                customDialog.yesButton.setOnClickListener(view -> deleteUser());
//                Button yesButton = (Button) customDialog.findViewById(R.id.yesButton);
//                yesButton.setOnClickListener(view -> deleteUser());
                return;
            }
            case "Log Out": {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, AuthenticationActivity.class));
            }
        }
    }

    public void singOut(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, AuthenticationActivity.class));
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void getUpdatedData(String action, Bundle bundle) {
        if (Objects.equals(action, "SaveEmail")) {
//            Doesn't work!!
//            FirebaseUserActivities firebaseUserActivities = new FirebaseUserActivities(null, rootReference, null);
//            User newUser = user;
//            newUser.setEmail(bundle.getString("email"));
//            firebaseUserActivities.changeEmailAddress(user, newUser);
        } else if (Objects.equals(action, "SaveUsername")) {
            userReference.child(Objects.requireNonNull(currentDataSnapshot.getKey())).child("username").setValue(bundle.getString("username"));
        }
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                .replace(R.id.rlContainer, settingsFragment)
                .commit();
    }

    @SuppressLint("RestrictedApi")
    public void deleteUser() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        assert user != null;
        AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), Objects.requireNonNull(currentDataSnapshot.child("password").getValue()).toString());

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential).addOnCompleteListener(task -> {
                    singOut();
                    currentDataSnapshot.getRef().removeValue();
                    user.delete().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) Log.d(TAG, "User account deleted.");
                    });
        });
    }
}