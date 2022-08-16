package com.example.triptracker;

import static com.example.triptracker.UserDao.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.Objects;

public class SettingsActivity extends CustomSecondaryActivity {

    BroadcastReceiver editUsernameReceiver;
    RecyclerView settingsRecyclerView;
    String[] settingsMenu, accountSettings, preferencesSettings;

    private static DatabaseReference userReference;
    private static final String USER = "users";
    private static final String PATH_TO_DATABASE = "https://trip-tracker-2844c-default-rtdb.europe-west1.firebasedatabase.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingsRecyclerView = findViewById(R.id.settingsRecyclerView);

        settingsMenu = getResources().getStringArray(R.array.settings_menu);
        accountSettings = getResources().getStringArray(R.array.account_settings);
        preferencesSettings = getResources().getStringArray(R.array.preferences_settings);

        userReference = FirebaseDatabase.getInstance(PATH_TO_DATABASE).getReference().child(USER);

        String[] item1 = {""}, item2 = {""};
        item1[0] = settingsMenu[0];

        RecyclerViewAdapter settingsMenuAdapterItem1 = new RecyclerViewAdapter(this, item1, "mainCategory", null);
        RecyclerViewAdapter accountSettingsAdapter = new RecyclerViewAdapter(this, accountSettings, "normal", item ->{
            switch (item) {
                case "Change Username": {
                    Intent intent = new Intent(this, EditUsernameActivity.class);
                    intent.putExtra("username", UserDao.user.getUsername());
                    intent.putExtra("password", UserDao.user.getPassword());
                    startActivity(intent);
                    break;
                }
                case "Change Email": {
                    Intent intent = new Intent(this, EditEmailActivity.class);
                    intent.putExtra("email",UserDao.user.getEmail());
                    intent.putExtra("password", UserDao.user.getPassword());
                    startActivity(intent);
                    break;
                }
                case "Change Password": {
                    Toast.makeText(this, "Not Implemented!", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        });

        item2[0] = settingsMenu[1];
        RecyclerViewAdapter settingsMenuAdapterItem2 = new RecyclerViewAdapter(this, item2, "mainCategory", null);
        RecyclerViewAdapter preferencesSettingsAdapter = new RecyclerViewAdapter(this, preferencesSettings, "normal", item ->{
            switch (item) {
                case "Display": {
                    startActivity(new Intent(this, DisplaySettingsActivity.class));
                    break;
                }
                case "Legal": {
                    Uri uri = Uri.parse("http://github.com/Serban2003/TripTracker/blob/master/LICENSE"); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    break;
                }
                case "About": {
                    Uri uri = Uri.parse("http://github.com/Serban2003/TripTracker"); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    break;
                }
                case "Delete Your Account": {
                    CustomDialogClass customDialog = new CustomDialogClass(SettingsActivity.this, "Delete Account", "Are you sure you want to delete this account?");
                    customDialog.show();

                    //customDialog.yesButton.setOnClickListener(view -> deleteUser());
                    break;
                }
                case "Log Out": {
                    FirebaseAuth.getInstance().signOut();
                    finish();
                    startActivity(new Intent(this, AuthenticationActivity.class));
                }
            }

        });

        ConcatAdapter concatAdapter = new ConcatAdapter(settingsMenuAdapterItem1, accountSettingsAdapter, settingsMenuAdapterItem2, preferencesSettingsAdapter);

        settingsRecyclerView.setAdapter(concatAdapter);
        settingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        editUsernameReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String username = intent.getStringExtra("username");
                user.setUsername(username);
                userReference.child(UserDao.user.getKeyId()).child("username").setValue(username);
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(editUsernameReceiver, new IntentFilter("updated-username"));
    }

    @Override
    public String getTitleView() {
        return "Settings";
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(editUsernameReceiver);
        super.onDestroy();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
