package com.example.triptracker;

import static com.example.triptracker.UserDao.user;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChangeUsernameActivity extends CustomSecondaryActivity {

    TextInputLayout usernameInput, passwordInput;
    Button saveButton, cancelButton;

    FirebaseUser firebaseUser;
    UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_username);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        usernameInput = findViewById(R.id.inputUsername);
        passwordInput = findViewById(R.id.inputPassword);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getUserById(firebaseUser.getUid()).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                UserDao.user.setUsername(user.getUsername());
                UserDao.user.setPassword(user.getPassword());
                UserDao.user.setSaltValue(user.getSaltValue());
            }
        });

        usernameInput.getEditText().setText(user.getUsername());

        usernameInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() < 4)
                    usernameInput.setError(getString(R.string.invalid_username));
                else usernameInput.setError(null);
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
            String username = usernameInput.getEditText().getText().toString();
            String password = passwordInput.getEditText().getText().toString();

            if(verifyInput(username, password)){
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    UserDatabase userDatabase = UserDatabase.getDatabase(getApplication());
                    userDatabase.userDao().updateUsername(username, firebaseUser.getUid());
                });
                finish();
            }
        });
    }

    public boolean verifyInput(String username, String password) {
        boolean allGood = true;

        if (Objects.equals(username, "")) {
            usernameInput.setError(getString(R.string.no_username));
            allGood = false;
        } else if (username.length() < 4) {
            usernameInput.setError(getString(R.string.invalid_username));
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
        return "Change Username";
    }
}