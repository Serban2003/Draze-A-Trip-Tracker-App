package com.example.triptracker;

import static com.example.triptracker.UserDao.user;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputLayout;
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

    private TextView usernameTextView, emailTextView;
    private ImageButton imageButton;
    private ImageView avatar;
    ActivityResultLauncher<String> mGetContent;
    private Uri imagePath;
    private final String[] titles = new String[]{"PROFILE", "ACTIVITIES"};

    UserViewModel userViewModel;

    TabLayout tabLayout;
    ViewPager2 viewPager;

    FirebaseUser firebaseUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null) {
                imagePath = result;
                uploadImage();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        viewPager.setAdapter(new ViewPagerFragmentStateAdapter(getActivity()));

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(titles[position])
        ).attach();

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getUserById(firebaseUser.getUid()).observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                emailTextView.setText(user.getEmail());
                usernameTextView.setText(user.getUsername());
                Picasso.get().load(Uri.parse(user.getAvatarUri())).placeholder(R.drawable.progress_animation).into(avatar);
            }
        });

        usernameTextView = view.findViewById(R.id.usernameTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        imageButton = view.findViewById(R.id.addImageButton);
        avatar = view.findViewById(R.id.avatarIcon);

        imageButton.setOnClickListener(view12 -> mGetContent.launch("image/*"));

        return view;
    }

    public void uploadImage() {
        if (imagePath != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + firebaseUser.getUid());
            storageReference.putFile(imagePath).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Image Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {

                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(() -> {
                            UserDatabase userDatabase = UserDatabase.getDatabase(getContext());
                            userDatabase.userDao().updateAvatarUri(uri.toString(), firebaseUser.getUid());
                        });
                    }).addOnFailureListener(exception -> Toast.makeText(getActivity(), "Upload failed!", Toast.LENGTH_SHORT).show());
                } else
                    Toast.makeText(getActivity(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        viewPager.setCurrentItem(0);
        Activity activity = getActivity();
        if (activity != null) {
            activity.setTitle(getString(R.string.profileFragmentTitle));
        }
    }

    public class ViewPagerFragmentStateAdapter extends FragmentStateAdapter {
        public ViewPagerFragmentStateAdapter(FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new ProfileDetailsFragment();
                case 1:
                    return new ProfileActivitiesFragment();
            }
            return new ProfileDetailsFragment();
        }

        @Override
        public int getItemCount() {
            return titles.length;
        }
    }
}