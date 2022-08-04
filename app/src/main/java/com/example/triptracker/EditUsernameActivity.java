package com.example.triptracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

public class EditUsernameActivity extends CustomSecondaryActivity {

    EditText usernameEditText, passwordEditText;
    Button saveButton, cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_username);

        Bundle bundle = getIntent().getExtras();

        String username = bundle.getString("username");
        String password = bundle.getString("password");

        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextPasswordUsername);
        saveButton = findViewById(R.id.saveButtonUsername);
        cancelButton = findViewById(R.id.cancelButtonUsername);

        usernameEditText.setText(username);
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(passwordEditText.getText().toString().equals(password)){
                    saveButton.setBackgroundColor(getResources().getColor(R.color.main_color));
                    saveButton.setClickable(true);
                }
                else {
                    saveButton.setBackgroundColor(getResources().getColor(R.color.main_color_inactive));
                    saveButton.setClickable(false);
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
            sendMessage("usernameEditText.getText().toString()");
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