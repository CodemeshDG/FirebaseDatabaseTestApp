package com.dommyg.firebasedatabasetestapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MainMenuFragment extends Fragment {
    private boolean hasUsername = false;

    private Button buttonCreateRoom;
    private Button buttonJoinRoom;
    private Button buttonSetUsername;

    private TextView textViewUsername;

    private EditText editTextUsername;

    private String username;

    public static MainMenuFragment newInstance() {
        return new MainMenuFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_menu, container, false);

        setUpButtons(v);

        return v;
    }

    private void setUpButtons(View v) {
        buttonCreateRoom = v.findViewById(R.id.buttonMainCreateRoom);
        buttonCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = CreateRoomActivity.newIntent(getContext());
                startActivity(intent);
            }
        });

        buttonJoinRoom = v.findViewById(R.id.buttonMainJoinRoom);
        buttonJoinRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = JoinRoomActivity.newIntent(getContext());
                startActivity(intent);
            }
        });

        buttonSetUsername = v.findViewById(R.id.buttonSetUsername);
        buttonSetUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textViewUsername.length() == 0) {
                    Toast.makeText(getContext(), "Enter a username.", Toast.LENGTH_SHORT).show();
                } else {
                    // Create username in database and set username String. ToggleUsernameElements. ToggleRoomButtons.
                }
            }
        });

        toggleRoomButtons();
        if (hasUsername) {
            toggleOffUsernameElements();
        }
    }

    private void setUpUsernameElements(View v) {
        textViewUsername = v.findViewById(R.id.textViewUsername);
        editTextUsername = v.findViewById(R.id.editTextUsername);
    }

    private void toggleRoomButtons() {
        if (hasUsername) {
            buttonCreateRoom.setEnabled(true);
            buttonJoinRoom.setEnabled(true);
        } else {
            buttonCreateRoom.setEnabled(false);
            buttonJoinRoom.setEnabled(false);
        }
    }

    private void toggleOffUsernameElements() {
            textViewUsername.setText("Welcome " + username + "!");
            editTextUsername.setVisibility(View.GONE);
            buttonSetUsername.setVisibility(View.GONE);
    }

    private boolean checkIfUsernameExists() {

    }

    private boolean checkIfHasUsername() {

    }
}
