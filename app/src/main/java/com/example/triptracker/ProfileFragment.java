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
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if(result != null) {
                imagePath = result;
                Picasso.get().load(imagePath).placeholder( R.drawable.progress_animation).into(avatar);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        storageReference = FirebaseStorage.getInstance().getReference().child("images/" + UserDao.user.getKeyId());

        usernameTextView = view.findViewById(R.id.usernameTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
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

        Picasso.get().load(Uri.parse(UserDao.user.getAvatarUri())).placeholder( R.drawable.progress_animation).into(avatar);
        //Picasso.get().load(Uri.parse(UserDao.user.getAvatarUri())).into(avatar);
        updateUI();

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

            UserDao.user.setFullName(fullNameEditText.getText().toString());
            UserDao.user.setGender(genderEditText.getText().toString());
            UserDao.user.setPhoneNumber(phoneEditText.getText().toString());
            UserDao.user.setLocation(locationSpinner.getSelectedItem().toString());

            uploadImage();

            imageButton.setVisibility(View.GONE);
            switcher.showPrevious();
        });

        cancelButton.setOnClickListener(view3 -> {
            Picasso.get().load(Uri.parse(UserDao.user.getAvatarUri())).placeholder( R.drawable.progress_animation).into(avatar);
            imageButton.setVisibility(View.GONE);
            switcher.showPrevious();
        });

        imageButton.setOnClickListener(view12 -> mGetContent.launch("image/*"));

        return view;
    }

    private void updateUI(){
        emailTextView.setText(UserDao.user.getEmail());
        usernameTextView.setText(UserDao.user.getUsername());
        fullNameTextView.setText(UserDao.user.getFullName());
        genderTextView.setText(UserDao.user.getGender());
        phoneTextView.setText(UserDao.user.getPhoneNumber());
        locationTextView.setText(UserDao.user.getLocation());
    }

    public void uploadImage(){
        if(imagePath != null){
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + UserDao.user.getKeyId());
            storageReference.putFile(imagePath).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(), "Image Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            UserDao.user.setAvatarUri(uri.toString());
                        }
                    }).addOnFailureListener(exception -> {
                        Toast.makeText(getActivity(), "Upload failed!", Toast.LENGTH_SHORT).show();
                    });
                    updateUI();
                    MainActivity.updateDatabase();
                }
                else Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
        else{
            updateUI();
            MainActivity.updateDatabase();
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