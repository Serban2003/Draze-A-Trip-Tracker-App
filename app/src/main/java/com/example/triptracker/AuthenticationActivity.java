package com.example.triptracker;

import static com.example.triptracker.UserDao.user;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
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
        if(firebaseUser != null) startActivity(new Intent(AuthenticationActivity.this, MainActivity.class));

        bottomNavigationView = findViewById(R.id.auth_navigation);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.login);

        LocalBroadcastManager.getInstance(this).registerReceiver(userCredentials, new IntentFilter("getUserCredentials"));
    }

    private BroadcastReceiver userCredentials = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String authType = intent.getStringExtra("authType");
            String email = intent.getStringExtra("email");
            String password = intent.getStringExtra("password");

            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            if(Objects.equals(authType, "login")){
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(AuthenticationActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                Toast.makeText(AuthenticationActivity.this, "LogIn successful.",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AuthenticationActivity.this, MainActivity.class));
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(AuthenticationActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                Log.d("receiver", "Got message: " + authType + " " + email + " " + password);
            }
            else if(Objects.equals(authType, "register")){
                String username = intent.getStringExtra("username");

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(AuthenticationActivity.this, task -> {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    Toast.makeText(AuthenticationActivity.this, "Account created successfully.",Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    startActivity(new Intent(AuthenticationActivity.this, MainActivity.class));
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

