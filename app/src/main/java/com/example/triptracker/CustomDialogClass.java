package com.example.triptracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class CustomDialogClass extends Dialog implements android.view.View.OnClickListener {

    public Activity activity;
    public String title, message;
    public Button yesButton, noButton;
    public TextView titleTextView, messageTextView;

    public CustomDialogClass(Activity activity, String title, String message) {
        super(activity);
        this.activity = activity;
        this.title = title;
        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_alert);

        titleTextView = findViewById(R.id.dialogTitle);
        titleTextView.setText(title);

        messageTextView = findViewById(R.id.dialogMessage);
        messageTextView.setText(message);

        yesButton = findViewById(R.id.yesButton);
        yesButton.setOnClickListener(this);

        noButton = findViewById(R.id.noButton);
        noButton.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.yesButton: {
                activity.finish();
                break;
            }
            case R.id.noButton: {
                dismiss();
                break;
            }
            default:
                break;
        }
        dismiss();
    }
}