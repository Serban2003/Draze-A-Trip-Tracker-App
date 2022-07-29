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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.validator.routines.EmailValidator;

public class RegisterFragment extends Fragment{

    private EditText usernameEditText, emailEditText, passwordEditText;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private User user;
    private static final String USER = "users";
    private static final String TAG = "RegisterFragment";

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

        database = FirebaseDatabase.getInstance("https://trip-tracker-2844c-default-rtdb.europe-west1.firebasedatabase.app/");
        mDatabase = database.getReference(USER);
        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(v ->  {

            String username = usernameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if(verifyInput(username, email, password)){
                user = new User(username, email, password);
                registerUser(email, password);
            }
        });
        return view;
    }

    public boolean verifyInput(String username, String email, String password){
        if(username.length() < 4) return false;
        else if(!EmailValidator.getInstance().isValid(email)) return false;
        else return password.length() >= 6;
    }

    public void registerUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(getActivity(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void updateUI(FirebaseUser currentUser){
        String keyId = mDatabase.push().getKey();
        assert keyId != null;
        mDatabase.child(keyId).setValue(user);
        startActivity(new Intent(getActivity(), MainActivity.class));
    }

}