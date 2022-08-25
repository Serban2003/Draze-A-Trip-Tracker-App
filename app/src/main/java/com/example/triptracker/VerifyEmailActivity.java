package com.example.triptracker;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyEmailActivity extends CustomSecondaryActivity {

    public static final String TAG = "VerifyEmailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        TextView sendEmailTextView = findViewById(R.id.sendEmailTextView);
        sendEmailTextView.setOnClickListener(view -> {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            assert firebaseUser != null;
            DatabaseActivities.sendEmailVerification(firebaseUser, VerifyEmailActivity.this);
        });

    }

    @Override
    protected String getTitleView() {
        return "Verify Email";
    }
}