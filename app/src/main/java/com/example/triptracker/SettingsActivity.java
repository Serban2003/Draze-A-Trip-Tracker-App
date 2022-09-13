package com.example.triptracker;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends CustomSecondaryActivity {

    String[] accountSettings, preferencesSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ListView listViewAccount = findViewById(R.id.listAccountSettings);
        ListView listViewPreferences = findViewById(R.id.listPreferencesSettings);

        if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified())
            accountSettings = getResources().getStringArray(R.array.account_settings_verified);
        else accountSettings = getResources().getStringArray(R.array.account_settings_not_verified);

        preferencesSettings = getResources().getStringArray(R.array.preferences_settings);

        ListViewAdapter adapter1 = new ListViewAdapter(accountSettings, getApplicationContext());
        ListViewAdapter adapter2 = new ListViewAdapter(preferencesSettings, getApplicationContext());

        listViewAccount.setAdapter(adapter1);
        listViewAccount.setOnItemClickListener((parent, view, position, id) -> {
            String item = accountSettings[position];
            adapter1.mSelectedItem = position;
            adapter1.notifyDataSetChanged();
            switch (item) {
                case "Change Username": {
                    startActivity(new Intent(SettingsActivity.this, ChangeUsernameActivity.class));
                    break;
                }
                case "Change Email": {
                    startActivity(new Intent(SettingsActivity.this, ChangeEmailActivity.class));
                    break;
                }
                case "Verify Email": {
                    view.findViewById(R.id.alertView).setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.incorrect)));
                    startActivity(new Intent(SettingsActivity.this, VerifyEmailActivity.class));
                    break;
                }

                case "Change Password": {
                    startActivity(new Intent(SettingsActivity.this, ChangePasswordActivity.class));
                    break;
                }
            }
        });

        listViewPreferences.setAdapter(adapter2);
        listViewPreferences.setOnItemClickListener((parent, view, position, id) -> {

            String item = preferencesSettings[position];
            adapter2.mSelectedItem = position;
            adapter2.notifyDataSetChanged();

            switch (item) {
                case "Display": {
                    startActivity(new Intent(SettingsActivity.this, DisplaySettingsActivity.class));
                    break;
                }
                case "Legal": {
                    Uri uri = Uri.parse("http://github.com/Serban2003/TripTracker/blob/master/LICENSE");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    break;
                }
                case "About": {
                    Uri uri = Uri.parse("http://github.com/Serban2003/TripTracker");
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
                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public String getTitleView() {
        return "Settings";
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
