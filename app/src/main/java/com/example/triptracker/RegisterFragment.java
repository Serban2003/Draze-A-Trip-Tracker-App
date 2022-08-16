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

public class RegisterFragment extends Fragment{

    private static final String TAG = "RegisterFragment";
    private EditText usernameEditText, emailEditText, passwordEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        usernameEditText = view.findViewById(R.id.inputUsername);
        emailEditText = view.findViewById(R.id.inputEmailAddress);
        passwordEditText = view.findViewById(R.id.inputPassword);
        Button loginButton = view.findViewById(R.id.registerButton);

        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(usernameEditText.getText().toString().length() < 4)  usernameEditText.setError("Not a valid username");
                else  usernameEditText.setError(null);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String email = emailEditText.getText().toString();
                if(!EmailValidator.getInstance().isValid(email)) emailEditText.setError("Not a valid email");
                else  emailEditText.setError(null);
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
                 if(passwordEditText.getText().toString().length() < 6) passwordEditText.setError("Password must be over at least 6 characters long");
                 else passwordEditText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });



        loginButton.setOnClickListener(v ->  {
            String username = usernameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if(verifyInput(username, email, password)) sendMessage(email, username, password);

        });
        return view;
    }

    public boolean verifyInput(String username, String email, String password){
        if(username.length() < 4) return false;
        else if(!EmailValidator.getInstance().isValid(email)) return false;
        else return password.length() >= 6;
    }

    private void sendMessage(String email, String username,String password) {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("getUserCredentials");
        // You can also include some extra data.
        intent.putExtra("authType", "register");
        intent.putExtra("email", email);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        usernameEditText.setText(null);
        usernameEditText.setError(null);

        emailEditText.setText(null);
        emailEditText.setError(null);

        passwordEditText.setText(null);
        passwordEditText.setError(null);
    }
}