package com.example.triptracker;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static com.example.triptracker.UserDao.user;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class EditEmailActivity extends CustomSecondaryActivity {

    EditText emailEditText, passwordEditText;
    Button saveButton, cancelButton;

    UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email);

        emailEditText = findViewById(R.id.editTextEmailAddress);
        passwordEditText = findViewById(R.id.editTextPasswordEmail);
        saveButton = findViewById(R.id.saveButtonUsername);
        cancelButton = findViewById(R.id.cancelButtonUsername);

        emailEditText.setText(user.getEmail());

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

//        cancelButton.setOnClickListener(view1 -> sendData.getUpdatedData("Cancel", credentials));
//        saveButton.setOnClickListener(view1 -> {
//            credentials.putString("email", emailEditText.getText().toString());
//            sendData.getUpdatedData("SaveEmail", credentials);
//        });
    }

    @Override
    protected String getTitleView() {
        return "Change Email";
    }
}