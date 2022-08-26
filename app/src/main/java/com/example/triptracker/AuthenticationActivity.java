package com.example.triptracker;

import static com.example.triptracker.DatabaseActivities.PATH_TO_DATABASE;
import static com.example.triptracker.DatabaseActivities.USER;
import static com.example.triptracker.DatabaseActivities.deleteUserFromFirebaseDatabase;
import static com.example.triptracker.DatabaseActivities.sendEmailVerification;
import static com.example.triptracker.DatabaseActivities.signOutUserFromFirebase;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AuthenticationActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    public static final String TAG = "AuthenticationActivity";
    BottomNavigationView bottomNavigationView;
    LoginFragment loginFragment = new LoginFragment();
    RegisterFragment registerFragment = new RegisterFragment();

    FirebaseUser firebaseUser;
    UserDatabase database;
    UserViewModel userViewModel;
    ActivitiesViewModel activitiesViewModel;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        setContentView(R.layout.activity_authentication);

        database = UserDatabase.getDatabase(AuthenticationActivity.this);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null)
            startActivity(new Intent(AuthenticationActivity.this, MainActivity.class));

        bottomNavigationView = findViewById(R.id.auth_navigation);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.login);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.loading_dialog);
        builder.setCancelable(false);
        dialog = builder.create();

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        activitiesViewModel = new ViewModelProvider(this).get(ActivitiesViewModel.class);
        LocalBroadcastManager.getInstance(this).registerReceiver(userCredentials, new IntentFilter("getUserCredentials"));
    }

    private final BroadcastReceiver userCredentials = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String authType = intent.getStringExtra("authType");
            String email = intent.getStringExtra("email");
            String password = intent.getStringExtra("password");

            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            if (Objects.equals(authType, "login")) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(AuthenticationActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information

                                firebaseUser = mAuth.getCurrentUser();
                                assert firebaseUser != null;
                                Date signupDate = new Date(Objects.requireNonNull(firebaseUser.getMetadata()).getCreationTimestamp());

                                final long HOUR = 3600 * 1000;
                                Date deadline = new Date(signupDate.getTime() + 5 * HOUR);
                                Date currentSystemDate = new Date();

                                if (!firebaseUser.isEmailVerified()) {
                                    if (currentSystemDate.after(deadline)) {
                                        Log.d(TAG, "signInWithEmail:account disabled");
                                        Toast.makeText(AuthenticationActivity.this, "The account was disabled due to no validated email. You can create another one.", Toast.LENGTH_LONG).show();
                                        signOutUserFromFirebase();
                                        deleteUserFromFirebaseDatabase(firebaseUser);
                                        // deleteUserFromLocalDatabase(database, firebaseUser.getUid());
                                    } else {
                                        Log.d(TAG, "signInWithEmail:success");
                                        Toast.makeText(AuthenticationActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();

                                        activitiesViewModel.deleteAllActivities();
                                        userViewModel.deleteAllUsers();
                                        dialog.show();
                                        Executor executor = Executors.newSingleThreadExecutor();
                                        Handler handler = new Handler(Looper.getMainLooper());
                                        executor.execute(() -> {

                                            DatabaseReference userReference = FirebaseDatabase.getInstance(PATH_TO_DATABASE).getReference().child(USER);
                                            userReference.child(firebaseUser.getUid()).get().addOnCompleteListener(task1 -> {
                                                if (!task1.isSuccessful()) {
                                                    Log.e(TAG, "Error getting data from Firebase Database", task1.getException());
                                                } else {
                                                    Log.d(TAG, "Data loaded from Firebase Database" + task1.getResult().getValue());
                                                    User user = new User();
                                                    user.setKeyId(task1.getResult().getKey());
                                                    user.setUsername(Objects.requireNonNull(task1.getResult().child("username").getValue()).toString());
                                                    user.setEmail(Objects.requireNonNull(task1.getResult().child("email").getValue()).toString());
                                                    user.setPassword(Objects.requireNonNull(task1.getResult().child("password").getValue()).toString());
                                                    user.setFullName(Objects.requireNonNull(task1.getResult().child("fullName").getValue()).toString());
                                                    user.setGender(Objects.requireNonNull(task1.getResult().child("gender").getValue()).toString());
                                                    user.setPhoneNumber(Objects.requireNonNull(task1.getResult().child("phoneNumber").getValue()).toString());
                                                    user.setLocation(Objects.requireNonNull(task1.getResult().child("location").getValue()).toString());
                                                    user.setVerified(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).isEmailVerified());

                                                    if (task1.getResult().child("avatarUri").getValue() != null)
                                                        user.setAvatarUri(Objects.requireNonNull(task1.getResult().child("avatarUri").getValue()).toString());

                                                    Iterable<DataSnapshot> iterable = task1.getResult().child("activities").getChildren();

                                                    while (iterable.iterator().hasNext()) {
                                                        TrackDetails trackDetails = new TrackDetails();
                                                        trackDetails.dataSnapshotToTrackDetails(iterable.iterator().next());
                                                        activitiesViewModel.insert(trackDetails);
                                                    }
                                                    userViewModel.insertUser(user);
                                                    handler.post(() -> {
                                                        dialog.dismiss();

                                                        Intent newIntent = new Intent(AuthenticationActivity.this, MainActivity.class);
                                                        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(newIntent);
                                                        //UI Thread work here
                                                    });
                                                }
                                            });
                                        });
//                                        if(isUserInLocalDatabase(database, firebaseUser.getUid())) loadUserFromLocalDatabase(database, firebaseUser.getUid());
//                                        else loadUserFromFirebaseDatabase(firebaseUser.getUid());
                                    }
                                } else {
                                    Log.d(TAG, "signInWithEmail:success");
                                    Toast.makeText(AuthenticationActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();
                                    activitiesViewModel.deleteAllActivities();
                                    userViewModel.deleteAllUsers();
                                    dialog.show();
                                    Executor executor = Executors.newSingleThreadExecutor();
                                    Handler handler = new Handler(Looper.getMainLooper());
                                    executor.execute(() -> {

                                        DatabaseReference userReference = FirebaseDatabase.getInstance(PATH_TO_DATABASE).getReference().child(USER);
                                        userReference.child(firebaseUser.getUid()).get().addOnCompleteListener(task12 -> {
                                            if (!task12.isSuccessful()) {
                                                Log.e(TAG, "Error getting data from Firebase Database", task12.getException());
                                            } else {
                                                Log.d(TAG, "Data loaded from Firebase Database" + task12.getResult().getValue());
                                                User user = new User();
                                                user.setKeyId(task12.getResult().getKey());
                                                user.setUsername(Objects.requireNonNull(task12.getResult().child("username").getValue()).toString());
                                                user.setEmail(Objects.requireNonNull(task12.getResult().child("email").getValue()).toString());
                                                user.setPassword(Objects.requireNonNull(task12.getResult().child("password").getValue()).toString());
                                                user.setFullName(Objects.requireNonNull(task12.getResult().child("fullName").getValue()).toString());
                                                user.setGender(Objects.requireNonNull(task12.getResult().child("gender").getValue()).toString());
                                                user.setPhoneNumber(Objects.requireNonNull(task12.getResult().child("phoneNumber").getValue()).toString());
                                                user.setLocation(Objects.requireNonNull(task12.getResult().child("location").getValue()).toString());
                                                user.setVerified(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).isEmailVerified());

                                                if (task12.getResult().child("avatarUri").getValue() != null)
                                                    user.setAvatarUri(Objects.requireNonNull(task12.getResult().child("avatarUri").getValue()).toString());

                                                Iterable<DataSnapshot> iterable = task12.getResult().child("activities").getChildren();

                                                while (iterable.iterator().hasNext()) {
                                                    TrackDetails trackDetails = new TrackDetails();
                                                    trackDetails.dataSnapshotToTrackDetails(iterable.iterator().next());
                                                    activitiesViewModel.insert(trackDetails);
                                                }

                                                userViewModel.insertUser(user);
                                                handler.post(() -> {
                                                    dialog.dismiss();

                                                    Intent newIntent = new Intent(AuthenticationActivity.this, MainActivity.class);
                                                    newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(newIntent);
                                                    //UI Thread work here
                                                });
                                            }
                                        });
                                    });
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(AuthenticationActivity.this, "Login failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
                Log.d("receiver", "Got message: " + authType + " " + email + " " + password);
            } else if (Objects.equals(authType, "register")) {
                String username = intent.getStringExtra("username");
                dialog.show();
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(AuthenticationActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                Toast.makeText(AuthenticationActivity.this, "Account created successfully.", Toast.LENGTH_SHORT).show();

                                activitiesViewModel.deleteAllActivities();
                                userViewModel.deleteAllUsers();

                                firebaseUser = mAuth.getCurrentUser();
                                assert firebaseUser != null;
                                sendEmailVerification(firebaseUser, AuthenticationActivity.this);

                                User user = new User();
                                user.setKeyId(firebaseUser.getUid());
                                user.setUsername(username);
                                user.setEmail(email);
                                user.setPassword(password);

                                Executor executor = Executors.newSingleThreadExecutor();
                                Handler handler = new Handler(Looper.getMainLooper());
                                executor.execute(() -> {
                                    FirebaseDatabase.getInstance(PATH_TO_DATABASE).getReference().child(USER).child(firebaseUser.getUid()).setValue(user);
                                    userViewModel.insertUser(user);
                                            handler.post(() -> {
                                                dialog.dismiss();

                                                Intent newIntent = new Intent(AuthenticationActivity.this, MainActivity.class);
                                                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(newIntent);
                                                //UI Thread work here
                                            });
                                        });
                                    }

                             else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(AuthenticationActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                Log.d("receiver", "Got message: " + authType + " " + email + " " + username + " " + password);
            }
        }
    };

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userCredentials);
        super.onDestroy();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login:
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                        .replace(R.id.loginContainer, loginFragment)
                        .commit();
                return true;

            case R.id.register:
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                        .replace(R.id.loginContainer, registerFragment)
                        .commit();
                return true;
        }
        return false;
    }

    public void setProgressDialog() {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(this);
        tvText.setText("Loading ...");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setView(ll);

        dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
    }
}

