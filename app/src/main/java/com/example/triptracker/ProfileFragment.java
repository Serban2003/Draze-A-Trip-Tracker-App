package com.example.triptracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private TextView usernameTextView, emailTextView;
    private TextView fullNameTextView, genderTextView, phoneTextView, locationTextView;
    private ImageView avatar;
    private FirebaseUser user;
    private String email;
    private static final String USER = "users";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        DatabaseReference rootRef = FirebaseDatabase.getInstance("https://trip-tracker-2844c-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        DatabaseReference userRef = rootRef.child(USER);

        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        email = user.getEmail();

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        usernameTextView = view.findViewById(R.id.usernameTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        fullNameTextView = view.findViewById(R.id.fullNameTextView);
        genderTextView = view.findViewById(R.id.genderTextView);
        phoneTextView = view.findViewById(R.id.phoneTextView);
        locationTextView = view.findViewById(R.id.locationTextView);
        avatar = view.findViewById(R.id.avatarIcon);

        userRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (Objects.equals(ds.child("email").getValue(), email)) {
                        usernameTextView.setText(Objects.requireNonNull(ds.child("username").getValue()).toString());
                        emailTextView.setText(Objects.requireNonNull(ds.child("email").getValue()).toString());

                        if (ds.child("fullName").getValue() != null)
                            fullNameTextView.setText(Objects.requireNonNull(ds.child("fullName").getValue()).toString());
                        else fullNameTextView.setText("Not provided");

                        if (ds.child("gender").getValue() != null)
                            genderTextView.setText(Objects.requireNonNull(ds.child("gender").getValue()).toString());
                        else genderTextView.setText("Not provided");

                        if (ds.child("phoneNumber").getValue() != null)
                            phoneTextView.setText(Objects.requireNonNull(ds.child("phoneNumber").getValue()).toString());
                        else phoneTextView.setText("Not provided");

                        if (ds.child("location").getValue() != null)
                            locationTextView.setText(Objects.requireNonNull(ds.child("location").getValue()).toString());
                        else locationTextView.setText("Not provided");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Activity activity = getActivity();
        if (activity != null) {
            activity.setTitle(getString(R.string.profileFragmentTitle));
        }
    }
}