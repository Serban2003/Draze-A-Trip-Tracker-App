package com.example.triptracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    private ImageView imageView;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        imageView = findViewById(R.id.logo_id);

        //FirebaseAuth.getInstance().signOut();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent;
            if (firebaseUser != null)
                intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            else intent = new Intent(SplashScreenActivity.this, AuthenticationActivity.class);
            startActivity(intent);
            finish();
        }, 2000);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Drawable drawable = imageView.getDrawable();
        if (drawable instanceof AnimatedVectorDrawable) {
            AnimatedVectorDrawable animation = (AnimatedVectorDrawable) drawable;
            animation.start();
        }
    }

}
