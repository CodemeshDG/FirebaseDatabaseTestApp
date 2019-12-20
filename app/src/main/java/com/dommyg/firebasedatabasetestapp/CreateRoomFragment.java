package com.dommyg.firebasedatabasetestapp;

import android.content.Intent;
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

    public CreateRoomFragment(String username) {
        this.username = username;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_room, container, false);

        setUpElements(v);

        return v;
    }

    private void setUpElements(View v) {
        final EditText editTextRoomName = v.findViewById(R.id.editTextRoomName);
        final EditText editTextPassword = v.findViewById(R.id.editTextPassword);

        Button buttonCreateRoom = v.findViewById(R.id.buttonCreateRoom);

        buttonCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextRoomName.length() != 0 &&
                        editTextPassword.length() != 0) {
                    Intent intent = MainPanelActivity.newIntentForCreateRoom(getContext(),
                            username,
                            editTextRoomName.getText().toString(),
                            editTextPassword.getText().toString());
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Fill out all fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
