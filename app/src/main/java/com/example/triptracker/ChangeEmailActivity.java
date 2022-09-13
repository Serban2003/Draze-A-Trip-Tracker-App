package com.example.triptracker;

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

import static com.example.triptracker.DatabaseActivities.PATH_TO_DATABASE;
import static com.example.triptracker.DatabaseActivities.USER;
import static com.example.triptracker.DatabaseActivities.sendEmailVerification;
import static com.example.triptracker.UserDao.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChangeEmailActivity extends CustomSecondaryActivity {

    private static final String TAG = "ChangeEmailActivity";

    TextInputLayout emailAddressInput, passwordInput;
    Button saveButton, cancelButton;

    FirebaseUser firebaseUser;
    UserViewModel userViewModel;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email);


        android.app.AlertDialog.Builder builderLoad = new android.app.AlertDialog.Builder(this);
        builderLoad.setView(R.layout.loading_dialog);
        builderLoad.setCancelable(false);
        dialog = builderLoad.create();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        emailAddressInput = findViewById(R.id.inputEmailAddress);
        passwordInput = findViewById(R.id.inputPassword);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getUserById(firebaseUser.getUid()).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                UserDao.user.setEmail(user.getEmail());
                UserDao.user.setPassword(user.getPassword());
                UserDao.user.setSaltValue(user.getSaltValue());
            }
        });

        emailAddressInput.getEditText().setText(user.getEmail());

        emailAddressInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!EmailValidator.getInstance().isValid(s.toString()))
                    emailAddressInput.setError(getString(R.string.invalid_email));
                else emailAddressInput.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

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

        cancelButton.setOnClickListener(view1 -> finish());
        saveButton.setOnClickListener(view1 -> {
            String email = emailAddressInput.getEditText().getText().toString();
            String password = passwordInput.getEditText().getText().toString();

            if(verifyInput(email, password)){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("We must disconnect you in order to change your email")
                        .setTitle("Sing Out")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialog.show();
                                updateEmail(email);
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public void updateEmail(String email){
        dialog.show();
        AuthCredential credential = EmailAuthProvider
                .getCredential(UserDao.user.getEmail(), UserDao.user.getPassword());

// Prompt the user to re-provide their sign-in credentials
        firebaseUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "User re-authenticated.");
                        firebaseUser.updateEmail(email)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ChangeEmailActivity.this, "Email address updated.", Toast.LENGTH_LONG).show();


                                            Executor executor = Executors.newSingleThreadExecutor();
                                            Handler handler = new Handler(Looper.getMainLooper());
                                            executor.execute(() -> {
                                                DatabaseReference userReference = FirebaseDatabase.getInstance(PATH_TO_DATABASE).getReference().child(USER);
                                                userReference.child(firebaseUser.getUid()).child("email").setValue(email);
                                                userReference.child(firebaseUser.getUid()).child("emailChanged").setValue(new Date().toString());


                                                handler.post(() -> {
                                                    sendEmailVerification(firebaseUser, ChangeEmailActivity.this);
                                                    dialog.dismiss();
                                                    FirebaseAuth.getInstance().signOut();
                                                    Intent intent = new Intent(ChangeEmailActivity.this, LoginActivity.class);
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

    public boolean verifyInput(String email, String password) {
        boolean allGood = true;

        if (Objects.equals(email, "")) {
            emailAddressInput.setError(getString(R.string.no_email));
            allGood = false;
        } else if (!EmailValidator.getInstance().isValid(email)) {
            emailAddressInput.setError(getString(R.string.invalid_email));
            allGood = false;
        }

        if (Objects.equals(password, "")) {
            passwordInput.setError(getString(R.string.no_password));
            allGood = false;
        } else if (password.length() < 6) {
            passwordInput.setError(getString(R.string.incorrect_password));
            allGood = false;
        }

        if (!PasswordEncryption.verifyUserPassword(password, UserDao.user.getPassword(), UserDao.user.getSaltValue())){
            passwordInput.setError(getString(R.string.incorrect_password));
            allGood = false;
        }

        return allGood;
    }

    @Override
    protected String getTitleView() {
        return "Change Email";
    }
}