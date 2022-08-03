package com.example.triptracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

public class EditEmailActivity extends CustomSecondaryActivity {

    EditText emailEditText, passwordEditText;
    Button saveButton, cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email);

        Bundle bundle = getIntent().getExtras();
        String email = bundle.getString("email");
        String password = bundle.getString("password");

        emailEditText = findViewById(R.id.editTextEmailAddress);
        passwordEditText = findViewById(R.id.editTextPasswordEmail);
        saveButton = findViewById(R.id.saveButtonUsername);
        cancelButton = findViewById(R.id.cancelButtonUsername);

        emailEditText.setText(email);

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

//        cancelButton.setOnClickListener(view1 -> sendData.getUpdatedData("Cancel", credentials));
//        saveButton.setOnClickListener(view1 -> {
//            credentials.putString("email", emailEditText.getText().toString());
//            sendData.getUpdatedData("SaveEmail", credentials);
//        });
    }
}