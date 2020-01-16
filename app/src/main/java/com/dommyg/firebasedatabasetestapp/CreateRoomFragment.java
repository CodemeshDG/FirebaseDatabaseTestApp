package com.dommyg.firebasedatabasetestapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CreateRoomFragment extends Fragment {
    private String username;

    public static CreateRoomFragment newInstance(String username) {
        return new CreateRoomFragment(username);
    }

    private CreateRoomFragment(String username) {
        this.username = username;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_room, container, false);

        setUpElements(v);

        return v;
    }

    /**
     * Sets up all the elements for the create room process.
     */
    private void setUpElements(View v) {
        final EditText editTextRoomName = v.findViewById(R.id.editTextRoomName);
        final EditText editTextPassword = v.findViewById(R.id.editTextPassword);

        Button buttonCreateRoom = v.findViewById(R.id.buttonCreateRoom);

        buttonCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextRoomName.length() != 0 &&
                        editTextPassword.length() != 0) {
                    // User filled out both fields; attempt to create a room.

                    String password = editTextPassword.getText().toString();
                    String roomName = editTextRoomName.getText().toString();

                    new RoomController(username, password, roomName, false, getContext())
                            .createRoom();
                } else {
                    // User did not enter a room name and/or password.
                    Toast.makeText(getContext(), "Fill out all fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
