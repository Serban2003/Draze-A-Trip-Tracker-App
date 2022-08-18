package com.example.triptracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.apache.commons.validator.routines.EmailValidator;

public class LoginFragment extends Fragment {

    private final static String TAG = "LoginFragment";

    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        emailEditText = view.findViewById(R.id.inputEmailAddress);
        passwordEditText = view.findViewById(R.id.inputPassword);

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String email = emailEditText.getText().toString();
                if (!EmailValidator.getInstance().isValid(email))
                    emailEditText.setError("Not a valid email");
                else emailEditText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (passwordEditText.getText().toString().length() < 6)
                    passwordEditText.setError("Password must be over at least 6 characters long");
                else passwordEditText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        Button loginButton = view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (verifyInput(email, password))
                sendMessage(email, password);
        });

        return view;
    }

    public boolean verifyInput(String email, String password) {
        if (!EmailValidator.getInstance().isValid(email)) return false;
        else return password.length() >= 6;
    }

    private void sendMessage(String email, String password) {
        Log.d(TAG, "Broadcasting message");
        Intent intent = new Intent("getUserCredentials");
        // You can also include some extra data.
        intent.putExtra("authType", "login");
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        emailEditText.setText(null);
        emailEditText.setError(null);

        passwordEditText.setText(null);
        passwordEditText.setError(null);
    }
}