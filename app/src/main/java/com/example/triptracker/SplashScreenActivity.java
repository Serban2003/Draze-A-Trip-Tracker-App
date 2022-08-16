package com.example.triptracker;

import static com.example.triptracker.MainActivity.updateActivities;
import static com.example.triptracker.MainActivity.updateUser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "SplashScreenActivity";
    private static final String USER = "users";
    private static final String PATH_TO_DATABASE = "https://trip-tracker-2844c-default-rtdb.europe-west1.firebasedatabase.app/";
    private ImageView imageView;

    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        imageView = findViewById(R.id.logo_id);

        //FirebaseAuth.getInstance().signOut();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser != null){
            DatabaseReference rootReference = FirebaseDatabase.getInstance(PATH_TO_DATABASE).getReference();
            DatabaseReference userReference = rootReference.child(USER);

            userReference.addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren())
                        if (Objects.equals(ds.child("email").getValue(), firebaseUser.getEmail())){
                            updateActivities(ds);
                            updateUser(ds);
                        }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent;
            if(firebaseUser != null) intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            else intent = new Intent(SplashScreenActivity.this, AuthenticationActivity.class);
            startActivity(intent);
            finish();
        },3000);

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
