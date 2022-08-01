package com.example.triptracker;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    private DataSnapshot currentDataSnapshot;
    private TextView usernameTextView, emailTextView;
    private TextView fullNameTextView, genderTextView, phoneTextView, locationTextView;
    private EditText fullNameEditText, genderEditText, phoneEditText;
    private ImageButton imageButton;
    private Spinner locationSpinner;
    ArrayAdapter adapter;
    private ImageView avatar;
    private Button editButton, saveButton, cancelButton;
    private ViewSwitcher switcher;
    ActivityResultLauncher<String> mGetContent;

    private FirebaseUser user;
    FirebaseStorage storage;
    StorageReference storageReference;

    private String email;
    private static final String USER = "users";

    private Uri imagePath;
    private Uri imagePathPrevious;
    private final int PICK_IMAGE_REQUEST = 22;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if(result != null) {
                imagePath = result;
                avatar.setImageURI(imagePath);
            }
        });
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
        editButton = view.findViewById(R.id.editButton);
        switcher =  view.findViewById(R.id.profileSwitcher);

        String [] country_list = getResources().getStringArray(R.array.country_arrays);

        fullNameEditText = view.findViewById(R.id.fullNameEditText);
        genderEditText = view.findViewById(R.id.genderEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);
        locationSpinner = view.findViewById(R.id.locationSpinner);
        saveButton = view.findViewById(R.id.saveButton);
        cancelButton = view.findViewById(R.id.cancelButton);
        imageButton = view.findViewById(R.id.addImageButton);
        imageButton.setVisibility(View.GONE);

        storage = FirebaseStorage.getInstance();

        userRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (Objects.equals(ds.child("email").getValue(), email)) {
                        currentDataSnapshot = ds;
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
                storageReference = storage.getReference().child("images/" + currentDataSnapshot.getKey());
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    imagePathPrevious = uri;
                    Picasso.get().load(uri).into(avatar);
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        editButton.setOnClickListener(view1 -> {
            switcher.showNext();

            imageButton.setVisibility(View.VISIBLE);
            fullNameEditText.setText(fullNameTextView.getText().toString());
            genderEditText.setText(genderTextView.getText().toString());
            phoneEditText.setText(phoneTextView.getText().toString());

            adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, country_list);
            locationSpinner.setAdapter(adapter);

            if(locationTextView != null) locationSpinner.setSelection(adapter.getPosition(locationTextView.getText().toString()));

        });

        saveButton.setOnClickListener(view2 -> {
            String key = currentDataSnapshot.getKey();
            assert key != null;
            userRef.child(key).child("fullName").setValue(fullNameEditText.getText().toString());
            userRef.child(key).child("gender").setValue(genderEditText.getText().toString());
            userRef.child(key).child("phoneNumber").setValue(phoneEditText.getText().toString());
            userRef.child(key).child("location").setValue(locationSpinner.getSelectedItem().toString());

            fullNameTextView.setText(fullNameEditText.getText().toString());
            genderTextView.setText(genderEditText.getText().toString());
            phoneTextView.setText(phoneEditText.getText().toString());
            locationTextView.setText(locationSpinner.getSelectedItem().toString());

            uploadImage();
            imageButton.setVisibility(View.GONE);
            switcher.showPrevious();
        });

        cancelButton.setOnClickListener(view3 -> {
            Picasso.get().load(imagePathPrevious).into(avatar);
            imageButton.setVisibility(View.GONE);
            switcher.showPrevious();
        });

        imageButton.setOnClickListener(view12 -> {
            mGetContent.launch("image/*");
        });

        return view;
    }

    public void uploadImage(){
        if(imagePath != null){
            storageReference = storage.getReference().child("images/" + currentDataSnapshot.getKey());
            storageReference.putFile(imagePath).addOnCompleteListener(task -> {
                if(task.isSuccessful()) Toast.makeText(getActivity(), "Image Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                else Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
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