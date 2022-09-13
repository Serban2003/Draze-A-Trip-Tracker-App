package com.example.triptracker;

import static com.example.triptracker.DatabaseActivities.PATH_TO_DATABASE;
import static com.example.triptracker.DatabaseActivities.USER;
import static com.example.triptracker.DatabaseActivities.sendEmailVerification;
import static com.example.triptracker.UserDao.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChangePasswordActivity extends AppCompatActivity {

    private static final String TAG = "ChangePasswordActivity";

    TextInputLayout passwordInput, newPasswordInput;
    Button saveButton, cancelButton;

    FirebaseUser firebaseUser;
    UserViewModel userViewModel;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        android.app.AlertDialog.Builder builderLoad = new android.app.AlertDialog.Builder(this);
        builderLoad.setView(R.layout.loading_dialog);
        builderLoad.setCancelable(false);
        dialog = builderLoad.create();

        passwordInput = findViewById(R.id.inputPassword);
        newPasswordInput = findViewById(R.id.inputNewPassword);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getUserById(firebaseUser.getUid()).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                UserDao.user.setPassword(user.getPassword());
            }
        });

        passwordInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordInput.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        newPasswordInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() < 6)
                    newPasswordInput.setError(getString(R.string.invalid_password));
                else newPasswordInput.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        cancelButton.setOnClickListener(view1 -> finish());
        saveButton.setOnClickListener(view1 -> {
            String password = passwordInput.getEditText().getText().toString();
            String newPassword = newPasswordInput.getEditText().getText().toString();

            if (verifyInput(password, newPassword)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("We must disconnect you in order to change your password")
                        .setTitle("Sing Out")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialog.show();
                                updatePassword(newPassword);
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public void updatePassword(String newPassword) {

        AuthCredential credential = EmailAuthProvider.getCredential(UserDao.user.getEmail(), UserDao.user.getPassword());
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
// Prompt the user to re-provide their sign-in credentials
        firebaseUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "User re-authenticated.");

                        firebaseUser.updatePassword(newPassword)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ChangePasswordActivity.this, "Password updated.", Toast.LENGTH_LONG).show();

                                            Executor executor = Executors.newSingleThreadExecutor();
                                            Handler handler = new Handler(Looper.getMainLooper());
                                            executor.execute(() -> {
                                                DatabaseReference userReference = FirebaseDatabase.getInstance(PATH_TO_DATABASE).getReference().child(USER);

                                                String newSaltValue = PasswordEncryption.getSaltValue(30);

                                                userReference.child(firebaseUser.getUid()).child("password").setValue(PasswordEncryption.generateSecurePassword(newPassword, newSaltValue));
                                                userReference.child(firebaseUser.getUid()).child("saltValue").setValue(newSaltValue);

                                                handler.post(() -> {
                                                    dialog.dismiss();
                                                    FirebaseAuth.getInstance().signOut();
                                                    Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                });
                                            });
                                        }
                                    }
                                });
                    }
                });

    }

    public boolean verifyInput(String password, String newPassword) {
        boolean allGood = true;

        if (Objects.equals(password, "")) {
            passwordInput.setError(getString(R.string.no_password));
            allGood = false;
        } else if (password.length() < 6) {
            passwordInput.setError(getString(R.string.incorrect_password));
            allGood = false;
        }

        if (Objects.equals(newPassword, "")) {
            newPasswordInput.setError(getString(R.string.no_new_password));
            allGood = false;
        } else if (newPassword.length() < 6) {
            newPasswordInput.setError(getString(R.string.invalid_password));
            allGood = false;
        }

        if (!PasswordEncryption.verifyUserPassword(password, UserDao.user.getPassword(), UserDao.user.getSaltValue())) {
            passwordInput.setError(getString(R.string.incorrect_password));
            allGood = false;
        }

        return allGood;
    }
}