package com.example.triptracker;

import static com.example.triptracker.DatabaseActivities.PATH_TO_DATABASE;
import static com.example.triptracker.DatabaseActivities.USER;
import static com.example.triptracker.DatabaseActivities.deleteUserFromFirebaseDatabase;
import static com.example.triptracker.DatabaseActivities.signOutUserFromFirebase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.validator.routines.EmailValidator;
import org.w3c.dom.Text;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private final static String TAG = "LoginActivity";

    FirebaseUser firebaseUser;
    Dialog dialog;
    UserViewModel userViewModel;
    ActivitiesViewModel activitiesViewModel;

    TextInputLayout emailLayout, passwordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) startActivity(new Intent(LoginActivity.this, MainActivity.class));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.loading_dialog);
        builder.setCancelable(false);
        dialog = builder.create();

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        activitiesViewModel = new ViewModelProvider(this).get(ActivitiesViewModel.class);

        emailLayout = findViewById(R.id.inputEmailAddress);
        passwordLayout = findViewById(R.id.inputPassword);

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            emailLayout.setError(null);
            passwordLayout.setError(null);

            String email = emailLayout.getEditText().getText().toString();
            String password = passwordLayout.getEditText().getText().toString();

            if (verifyInput(email, password))
                loginUser(email, password);
        });

        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> {
            Intent newIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(newIntent);
        });
    }

    public boolean verifyInput(String email, String password) {
        boolean allGood = true;

        if (Objects.equals(email, "")) {
            emailLayout.setError(getString(R.string.no_email));
            allGood = false;
        } else if (!EmailValidator.getInstance().isValid(email)) {
            emailLayout.setError(getString(R.string.invalid_email));
            allGood = false;
        }

        if (Objects.equals(password, "")) {
            passwordLayout.setError(getString(R.string.no_password));
            allGood = false;
        } else if (password.length() < 6) {
            passwordLayout.setError(getString(R.string.invalid_password));
            allGood = false;
        }
        return allGood;
    }

    public void loginUser(String email, String password) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information

                        firebaseUser = mAuth.getCurrentUser();
                        assert firebaseUser != null;
                        Date signupDate = new Date(Objects.requireNonNull(firebaseUser.getMetadata()).getCreationTimestamp());

                        final long HOUR = 3600 * 1000;
                        Date deadline = new Date(signupDate.getTime() + 5 * HOUR);
                        Date currentSystemDate = new Date();

                        if (!firebaseUser.isEmailVerified() && currentSystemDate.after(deadline)) {
                            Log.d(TAG, "signInWithEmail:account disabled");
                            Toast.makeText(LoginActivity.this, "The account was disabled due to no validated email. You can create another one.", Toast.LENGTH_LONG).show();
                            signOutUserFromFirebase();
                            deleteUserFromFirebaseDatabase(firebaseUser);
                        } else {
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(LoginActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();

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

                                            Intent newIntent = new Intent(LoginActivity.this, MainActivity.class);
                                            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(newIntent);
                                        });
                                    }
                                });
                            });
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Login failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}