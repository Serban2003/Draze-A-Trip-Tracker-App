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
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.apache.commons.validator.routines.EmailValidator;

public class LoginFragment extends Fragment {

    private EditText emailEditText;
    private EditText passwordEditText;
    private User user;
    private FirebaseAuth mAuth;
    private final String TAG = "LoginFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FirebaseAuth.getInstance().signOut();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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

        mAuth = FirebaseAuth.getInstance();

        Button loginButton = view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v ->  {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if(verifyInput(email, password)){
                logInUser(email, password);
            }
        });

        return view;
    }

    public boolean verifyInput(String email, String password){
        if(!EmailValidator.getInstance().isValid(email)) return false;
        else return password.length() >= 6;
    }

    public void logInUser(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "singInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "singInWithEmail:failure", task.getException());
                        Toast.makeText(getActivity(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) updateUI(currentUser);
    }

    public void updateUI(FirebaseUser currentUser){
        startActivity(new Intent(getActivity(), MainActivity.class));
    }
}