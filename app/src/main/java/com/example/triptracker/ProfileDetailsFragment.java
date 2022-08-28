package com.example.triptracker;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileDetailsFragment extends Fragment {

    TextView fullNameTextView, genderTextView, phoneNumberTextView, locationTextView;

    TextInputLayout fullNameInput, genderInput, phoneNumberInput, locationInput;

    UserViewModel userViewModel;

    FirebaseUser firebaseUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_details, container, false);

        String[] country_list = getResources().getStringArray(R.array.country_arrays);
        String[] gender_list = getResources().getStringArray(R.array.gender_options);

        fullNameTextView = view.findViewById(R.id.fullNameTextView);
        genderTextView = view.findViewById(R.id.genderTextView);
        phoneNumberTextView = view.findViewById(R.id.phoneTextView);
        locationTextView = view.findViewById(R.id.locationTextView);

        fullNameInput = view.findViewById(R.id.inputFullName);
        genderInput = view.findViewById(R.id.inputGender);
        phoneNumberInput = view.findViewById(R.id.inputPhoneNumber);
        locationInput = view.findViewById(R.id.inputLocation);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getUserById(firebaseUser.getUid()).observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                fullNameTextView.setText(user.getFullName());
                genderTextView.setText(user.getGender());
                phoneNumberTextView.setText(user.getPhoneNumber());
                locationTextView.setText(user.getLocation());

                fullNameInput.getEditText().setText(user.getFullName());
                genderInput.getEditText().setText(user.getGender());
                phoneNumberInput.getEditText().setText(user.getPhoneNumber());
                locationInput.getEditText().setText(user.getLocation());
            }
        });

        ViewSwitcher viewSwitcher = view.findViewById(R.id.profileSwitcher);

        Button editProfileButton = view.findViewById(R.id.editButton);
        editProfileButton.setOnClickListener(view1 -> {
           viewSwitcher.showNext();

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_list_item, country_list);
            AutoCompleteTextView locationDropdown = view.findViewById(R.id.locationDropdown);
            locationDropdown.setAdapter(adapter);

            adapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_list_item, gender_list);
            AutoCompleteTextView genderDropdown = view.findViewById(R.id.genderDropdown);
            genderDropdown.setAdapter(adapter);
        });

        Button cancelEditButton = view.findViewById(R.id.cancelButton);
        cancelEditButton.setOnClickListener(view2 -> viewSwitcher.showPrevious());

        Button saveEditButton = view.findViewById(R.id.saveButton);
        saveEditButton.setOnClickListener(view3 -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                UserDatabase userDatabase = UserDatabase.getDatabase(getContext());
                userDatabase.userDao().updateUser(fullNameInput.getEditText().getText().toString(), genderInput.getEditText().getText().toString(), phoneNumberInput.getEditText().getText().toString(), locationInput.getEditText().getText().toString(), firebaseUser.getUid());
            });
            viewSwitcher.showPrevious();
        });

        return view;
    }
}