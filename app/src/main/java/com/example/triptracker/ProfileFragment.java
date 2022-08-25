package com.example.triptracker;

import static com.example.triptracker.UserDao.user;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Executable;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    private TextView usernameTextView, emailTextView, fullNameTextView, genderTextView, phoneTextView, locationTextView;
    private EditText fullNameEditText, genderEditText, phoneEditText;
    private ImageButton imageButton;
    private Spinner locationSpinner;
    ArrayAdapter<String> adapter;
    private ImageView avatar;
    private ViewSwitcher switcher;
    ActivityResultLauncher<String> mGetContent;
    private Uri imagePath;

    StorageReference storageReference;
    UserViewModel userViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null) {
                imagePath = result;
                Picasso.get().load(imagePath).placeholder(R.drawable.progress_animation).into(avatar);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        storageReference = FirebaseStorage.getInstance().getReference().child("images/" + user.getKeyId());

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getUserById(firebaseUser.getUid()).observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                emailTextView.setText(user.getEmail());
                usernameTextView.setText(user.getUsername());
                fullNameTextView.setText(user.getFullName());
                genderTextView.setText(user.getGender());
                phoneTextView.setText(user.getPhoneNumber());
                locationTextView.setText(user.getLocation());

                fullNameEditText.setText(user.getFullName());
                genderEditText.setText(user.getGender());
                phoneEditText.setText(user.getPhoneNumber());

                Picasso.get().load(Uri.parse(user.getAvatarUri())).placeholder(R.drawable.progress_animation).into(avatar);
            }
        });

        usernameTextView = view.findViewById(R.id.usernameTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        fullNameTextView = view.findViewById(R.id.fullNameTextView);
        genderTextView = view.findViewById(R.id.genderTextView);
        phoneTextView = view.findViewById(R.id.phoneTextView);
        locationTextView = view.findViewById(R.id.locationTextView);
        avatar = view.findViewById(R.id.avatarIcon);
        Button editButton = view.findViewById(R.id.editButton);
        switcher = view.findViewById(R.id.profileSwitcher);

        String[] country_list = getResources().getStringArray(R.array.country_arrays);

        fullNameEditText = view.findViewById(R.id.fullNameEditText);
        genderEditText = view.findViewById(R.id.genderEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);
        locationSpinner = view.findViewById(R.id.locationSpinner);
        Button saveButton = view.findViewById(R.id.saveButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);
        imageButton = view.findViewById(R.id.addImageButton);
        imageButton.setVisibility(View.GONE);

        //Picasso.get().load(Uri.parse(UserDao.user.getAvatarUri())).placeholder(R.drawable.progress_animation).into(avatar);
        //Picasso.get().load(Uri.parse(UserDao.user.getAvatarUri())).into(avatar);
        //updateUI();

        editButton.setOnClickListener(view1 -> {
            switcher.showNext();

            imageButton.setVisibility(View.VISIBLE);
//            fullNameEditText.setText(fullNameTextView.getText().toString());
//            genderEditText.setText(genderTextView.getText().toString());
//            phoneEditText.setText(phoneTextView.getText().toString());

            adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, country_list);
            locationSpinner.setAdapter(adapter);

            if (locationTextView != null)
                locationSpinner.setSelection(adapter.getPosition(locationTextView.getText().toString()));
        });

        saveButton.setOnClickListener(view2 -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    UserDatabase userDatabase = UserDatabase.getDatabase(getContext());
                    userDatabase.userDao().updateUser(fullNameEditText.getText().toString(), genderEditText.getText().toString(), phoneEditText.getText().toString(), locationSpinner.getSelectedItem().toString(), firebaseUser.getUid());

                    if(imagePath != null) userDatabase.userDao().updateAvatarUri(imagePath.toString(), firebaseUser.getUid());
                }
            });

            imageButton.setVisibility(View.GONE);
            switcher.showPrevious();
        });


           // uploadImage();


        cancelButton.setOnClickListener(view3 -> {
            Picasso.get().load(Uri.parse(user.getAvatarUri())).placeholder(R.drawable.progress_animation).into(avatar);
            imageButton.setVisibility(View.GONE);
            switcher.showPrevious();
        });

        imageButton.setOnClickListener(view12 -> mGetContent.launch("image/*"));

        return view;
    }

    private void updateUI() {
        emailTextView.setText(user.getEmail());
        usernameTextView.setText(user.getUsername());
        fullNameTextView.setText(user.getFullName());
        genderTextView.setText(user.getGender());
        phoneTextView.setText(user.getPhoneNumber());
        locationTextView.setText(user.getLocation());
    }

    public void uploadImage() {
        if (imagePath != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + user.getKeyId());
            storageReference.putFile(imagePath).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Image Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> user.setAvatarUri(uri.toString())).addOnFailureListener(exception -> Toast.makeText(getActivity(), "Upload failed!", Toast.LENGTH_SHORT).show());
                    updateUI();
                    DatabaseActivities.updateDatabase();
                } else
                    Toast.makeText(getActivity(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            updateUI();
            DatabaseActivities.updateDatabase();
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