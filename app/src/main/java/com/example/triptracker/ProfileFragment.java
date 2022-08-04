package com.example.triptracker;

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
import android.widget.ViewSwitcher;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    private TextView fullNameTextView, genderTextView, phoneTextView, locationTextView;
    private EditText fullNameEditText, genderEditText, phoneEditText;
    private ImageButton imageButton;
    private Spinner locationSpinner;
    private User user;
    ArrayAdapter<String> adapter;
    private ImageView avatar;
    private ViewSwitcher switcher;
    ActivityResultLauncher<String> mGetContent;
    private Uri imagePath;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if(result != null) {
                imagePath = result;
                Picasso.get().load(imagePath).into(avatar);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        assert getArguments() != null;
        user = (User) getArguments().getSerializable("user");

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView usernameTextView = view.findViewById(R.id.usernameTextView);
        TextView emailTextView = view.findViewById(R.id.emailTextView);
        fullNameTextView = view.findViewById(R.id.fullNameTextView);
        genderTextView = view.findViewById(R.id.genderTextView);
        phoneTextView = view.findViewById(R.id.phoneTextView);
        locationTextView = view.findViewById(R.id.locationTextView);
        avatar = view.findViewById(R.id.avatarIcon);
        Button editButton = view.findViewById(R.id.editButton);
        switcher =  view.findViewById(R.id.profileSwitcher);

        String [] country_list = getResources().getStringArray(R.array.country_arrays);

        fullNameEditText = view.findViewById(R.id.fullNameEditText);
        genderEditText = view.findViewById(R.id.genderEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);
        locationSpinner = view.findViewById(R.id.locationSpinner);
        Button saveButton = view.findViewById(R.id.saveButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);
        imageButton = view.findViewById(R.id.addImageButton);
        imageButton.setVisibility(View.GONE);

        emailTextView.setText(user.getEmail());
        usernameTextView.setText(user.getUsername());
        fullNameTextView.setText(user.getFullName());
        genderTextView.setText(user.getGender());
        phoneTextView.setText(user.getPhoneNumber());
        locationTextView.setText(user.getLocation());
        Picasso.get().load(user.getAvatarUri()).into(avatar);

        editButton.setOnClickListener(view1 -> {
            switcher.showNext();

            imageButton.setVisibility(View.VISIBLE);
            fullNameEditText.setText(fullNameTextView.getText().toString());
            genderEditText.setText(genderTextView.getText().toString());
            phoneEditText.setText(phoneTextView.getText().toString());

            adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, country_list);
            locationSpinner.setAdapter(adapter);

            if(locationTextView != null) locationSpinner.setSelection(adapter.getPosition(locationTextView.getText().toString()));
        });

        saveButton.setOnClickListener(view2 -> {
            user.setFullName(fullNameEditText.getText().toString());
            user.setGender(genderEditText.getText().toString());
            user.setPhoneNumber(phoneEditText.getText().toString());
            user.setLocation(locationSpinner.getSelectedItem().toString());
            user.setAvatarUri(imagePath.toString());

            MainActivity.updateUI(user, getActivity());

            fullNameTextView.setText(fullNameEditText.getText().toString());
            genderTextView.setText(genderEditText.getText().toString());
            phoneTextView.setText(phoneEditText.getText().toString());
            locationTextView.setText(locationSpinner.getSelectedItem().toString());

            imageButton.setVisibility(View.GONE);
            switcher.showPrevious();
        });

        cancelButton.setOnClickListener(view3 -> {
            Picasso.get().load(user.getAvatarUri()).into(avatar);
            imageButton.setVisibility(View.GONE);
            switcher.showPrevious();
        });

        imageButton.setOnClickListener(view12 -> mGetContent.launch("image/*"));

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