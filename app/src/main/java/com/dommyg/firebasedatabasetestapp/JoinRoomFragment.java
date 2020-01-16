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

public class JoinRoomFragment extends Fragment {
    private String username;

    public static JoinRoomFragment newInstance(String username) {
        return new JoinRoomFragment(username);
    }

    private JoinRoomFragment(String username) {
        this.username = username;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // This fragment uses the fragment_create_room layout because they use the same elements,
        // but just the create room button needs to be relabeled.
        View v = inflater.inflate(R.layout.fragment_create_room, container, false);

        Button buttonJoin = v.findViewById(R.id.buttonCreateRoom);

        buttonJoin.setText("Join Room");

        setUpElements(v);

        return v;
    }

    /**
     * Sets up all the elements for the join room process.
     */
    private void setUpElements(View v) {
        final EditText editTextRoomName = v.findViewById(R.id.editTextRoomName);
        final EditText editTextPassword = v.findViewById(R.id.editTextPassword);

        Button buttonJoinRoom = v.findViewById(R.id.buttonCreateRoom);
        buttonJoinRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextRoomName.length() != 0 &&
                        editTextPassword.length() != 0) {

                    final String roomName = editTextRoomName.getText().toString();
                    final String password = editTextPassword.getText().toString();

                    new RoomController(username, password, roomName, true,
                            getContext()).joinRoom();
                } else {
                    // User did not fill out all required fields.
                    Toast.makeText(getContext(), "Fill out all fields.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
