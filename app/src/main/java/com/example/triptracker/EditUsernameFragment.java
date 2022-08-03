package com.example.triptracker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class EditUsernameFragment extends Fragment {

    public interface SendData {
        public void getUpdatedData(String data, Bundle credentials);
    }
    Bundle credentials = new Bundle();
    SendData sendData;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Activity activity;
        if (context instanceof Activity){
            activity = (Activity) context;
            sendData = (SendData) activity;
        }
    }

    EditText usernameEditText, passwordEditText;
    Button saveButton, cancelButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_username, container, false);

        usernameEditText = view.findViewById(R.id.editTextUsername);
        passwordEditText = view.findViewById(R.id.editTextPasswordUsername);
        saveButton = view.findViewById(R.id.saveButtonUsername);
        cancelButton = view.findViewById(R.id.cancelButtonUsername);

        assert getArguments() != null;
        String username = getArguments().getString("username");
        String password = getArguments().getString("password");

        usernameEditText.setText(username);

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(passwordEditText.getText().toString().equals(password)){
                    saveButton.setBackgroundColor(getResources().getColor(R.color.main_color));
                    saveButton.setClickable(true);
                }
                else {
                    saveButton.setBackgroundColor(getResources().getColor(R.color.main_color_inactive));
                    saveButton.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        cancelButton.setOnClickListener(view1 -> {
            passwordEditText.setText("");
            sendData.getUpdatedData("Cancel", credentials);
        });
        saveButton.setOnClickListener(view1 -> {
            passwordEditText.setText("");
            credentials.putString("username", usernameEditText.getText().toString());
            sendData.getUpdatedData("SaveUsername", credentials);
        });

        return view;
    }
}
