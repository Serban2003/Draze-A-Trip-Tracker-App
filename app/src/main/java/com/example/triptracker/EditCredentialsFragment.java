package com.example.triptracker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EditCredentialsFragment extends Fragment {

    public interface SendData {
        public void getUpdatedData(String data, Bundle credentials);
    }
    Bundle credentials = new Bundle();
    SendData sendData;

    EditText usernameEditText, emailEditText, passwordEditText;
    Button saveButton, cancelButton;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Activity activity;
        if (context instanceof Activity){
            activity = (Activity) context;
            sendData = (SendData) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_credentials, container, false);

        usernameEditText = view.findViewById(R.id.editTextUsername);
        emailEditText = view.findViewById(R.id.editTextEmailAddress);
        passwordEditText = view.findViewById(R.id.editTextPassword);
        saveButton = view.findViewById(R.id.saveButtonCredentials);
        cancelButton = view.findViewById(R.id.cancelButtonCredentials);

        assert getArguments() != null;
        String username = getArguments().getString("username");
        String email = getArguments().getString("email");
        String password = getArguments().getString("password");

        usernameEditText.setText(username);
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

        cancelButton.setOnClickListener(view1 -> sendData.getUpdatedData("Cancel", credentials));
        saveButton.setOnClickListener(view1 -> {
            credentials.putString("username", usernameEditText.getText().toString());
            credentials.putString("email", emailEditText.getText().toString());

            sendData.getUpdatedData("Save", credentials);
        });

        return view;
    }
}