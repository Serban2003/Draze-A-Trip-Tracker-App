package com.example.triptracker;

import static com.example.triptracker.UserDao.user;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class EditUsernameActivity extends CustomSecondaryActivity {

    EditText usernameEditText, passwordEditText;
    Button saveButton, cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_username);

        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextPasswordUsername);
        saveButton = findViewById(R.id.saveButtonUsername);
        cancelButton = findViewById(R.id.cancelButtonUsername);

        usernameEditText.setText(user.getUsername());
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (passwordEditText.getText().toString().equals(user.getPassword())) {
                    saveButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.main_color)));
                    saveButton.setEnabled(true);
                } else {
                    saveButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.main_color_inactive)));
                    saveButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        cancelButton.setOnClickListener(view1 -> {
            passwordEditText.setText("");
            sendMessage("Cancel");
        });
        saveButton.setOnClickListener(view1 -> {
            passwordEditText.setText("");
            sendMessage(usernameEditText.getText().toString());
        });
    }

    @Override
    protected String getTitleView() {
        return "Change Username";
    }

    private void sendMessage(String message) {
        Intent intent = new Intent("updated-username");
        intent.putExtra("username", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        finish();
    }
}