package com.example.triptracker;

import static com.example.triptracker.DatabaseActivities.PATH_TO_DATABASE;
import static com.example.triptracker.DatabaseActivities.USER;
import static com.example.triptracker.DatabaseActivities.sendEmailVerification;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    TextInputLayout usernameLayout, emailLayout, passwordLayout;
    Dialog dialog;
    UserViewModel userViewModel;
    ActivitiesViewModel activitiesViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.loading_dialog);
        builder.setCancelable(false);
        dialog = builder.create();

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        activitiesViewModel = new ViewModelProvider(this).get(ActivitiesViewModel.class);

        usernameLayout = findViewById(R.id.inputUsername);
        emailLayout = findViewById(R.id.inputEmailAddress);
        passwordLayout = findViewById(R.id.inputPassword);

        usernameLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() < 4)
                    usernameLayout.setError(getString(R.string.invalid_username));
                else usernameLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        emailLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!EmailValidator.getInstance().isValid(s.toString()))
                    emailLayout.setError(getString(R.string.invalid_email));
                else emailLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passwordLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() < 6)
                    passwordLayout.setError(getString(R.string.invalid_password));
                else passwordLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> {
            String username = usernameLayout.getEditText().getText().toString();
            String email = emailLayout.getEditText().getText().toString();
            String password = passwordLayout.getEditText().getText().toString();

            if (verifyInput(username, email, password)) registerUser(username, email, password);
        });

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            Intent newIntent = new Intent(RegisterActivity.this, LoginActivity.class);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(newIntent);
        });
    }

    public boolean verifyInput(String username, String email, String password) {
        boolean allGood = true;

        if (Objects.equals(username, "")) {
            usernameLayout.setError(getString(R.string.no_username));
            allGood = false;
        } else if (username.length() < 4) {
            usernameLayout.setError(getString(R.string.invalid_username));
            allGood = false;
        }

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

    public void registerUser(String username, String email, String password) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, task -> {
                    if (task.isSuccessful()) {
                        dialog.show();
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        Toast.makeText(RegisterActivity.this, "Account created successfully.", Toast.LENGTH_SHORT).show();

                        activitiesViewModel.deleteAllActivities();
                        userViewModel.deleteAllUsers();

                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        assert firebaseUser != null;
                        sendEmailVerification(firebaseUser, RegisterActivity.this);

                        User user = new User();
                        user.setKeyId(firebaseUser.getUid());
                        user.setUsername(username);
                        user.setEmail(email);

                        String saltValue = PasswordEncryption.getSaltValue(30);
                        user.setSaltValue(saltValue);
                        user.setPassword(PasswordEncryption.generateSecurePassword(password, saltValue));

                        Executor executor = Executors.newSingleThreadExecutor();
                        Handler handler = new Handler(Looper.getMainLooper());
                        executor.execute(() -> {
                            DatabaseReference userReference = FirebaseDatabase.getInstance(PATH_TO_DATABASE).getReference().child(USER).child(firebaseUser.getUid());
                            userReference.setValue(user);
                            userReference.child("emailChanged").setValue(new Date().toString());

                            user.setPassword(password);
                            userViewModel.insertUser(user);
                            handler.post(() -> {
                                dialog.dismiss();

                                Intent newIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(newIntent);
                            });
                        });
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}