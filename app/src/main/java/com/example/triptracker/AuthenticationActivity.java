package com.example.triptracker;

import static com.example.triptracker.FirebaseActivities.createUser;
import static com.example.triptracker.FirebaseActivities.deleteUser;
import static com.example.triptracker.FirebaseActivities.sendEmailVerification;
import static com.example.triptracker.FirebaseActivities.signOutUser;
import static com.example.triptracker.UserDao.user;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;
import java.util.Objects;

public class AuthenticationActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    public static final String TAG = "AuthenticationActivity";
    BottomNavigationView bottomNavigationView;
    LoginFragment loginFragment = new LoginFragment();
    RegisterFragment registerFragment = new RegisterFragment();

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        setContentView(R.layout.activity_authentication);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null)
            startActivity(new Intent(AuthenticationActivity.this, MainActivity.class));

        bottomNavigationView = findViewById(R.id.auth_navigation);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.login);

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
                                        signOutUser();
                                        deleteUser(firebaseUser);
                                    } else {
                                        Log.d(TAG, "signInWithEmail:success");
                                        Toast.makeText(AuthenticationActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();
                                        Intent newIntent = new Intent(AuthenticationActivity.this, MainActivity.class);
                                        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(newIntent);
                                    }
                                } else {
                                    Log.d(TAG, "signInWithEmail:success");
                                    Toast.makeText(AuthenticationActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();
                                    Intent newIntent = new Intent(AuthenticationActivity.this, MainActivity.class);
                                    newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(newIntent);
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

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(AuthenticationActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                Toast.makeText(AuthenticationActivity.this, "Account created successfully.", Toast.LENGTH_SHORT).show();

                                firebaseUser = mAuth.getCurrentUser();
                                assert firebaseUser != null;
                                sendEmailVerification(firebaseUser, AuthenticationActivity.this);

                                user.setNewUser();
                                user.setKeyId(firebaseUser.getUid());
                                user.setUsername(username);
                                user.setEmail(email);
                                user.setPassword(password);

                                OnSuccessListener<?> onSuccessListener = o -> {
                                    Intent newIntent = new Intent(AuthenticationActivity.this, MainActivity.class);
                                    newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(newIntent);
                                };
                                createUser(firebaseUser, onSuccessListener);
                            } else {
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
}

