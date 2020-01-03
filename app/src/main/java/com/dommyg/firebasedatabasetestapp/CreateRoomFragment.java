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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

                    String password = editTextPassword.getText().toString();
                    String roomName = editTextRoomName.getText().toString();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("masterRoomList")
                            .document(editTextRoomName.getText().toString())
                            .get()
                            .addOnCompleteListener(new MasterRoomListSearchOnCompleteListener(
                                    password, roomName));
                } else {
                    // User did not enter a room name and/or password.
                    Toast.makeText(getContext(), "Fill out all fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Listener for searching for a room in the masterRoomList collection.
     */
    private class MasterRoomListSearchOnCompleteListener implements OnCompleteListener<DocumentSnapshot> {
        private final String password;
        private final String roomName;

        MasterRoomListSearchOnCompleteListener(String password, String roomName) {
            this.password = password;
            this.roomName = roomName;
        }

        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    Toast.makeText(getContext(), "This room name already exists.", Toast.LENGTH_SHORT).show();
                } else {
                    // User entered a room name which does not exist and a password; finish CreateRoomActivity and start MainPanelActivity.
                    Intent intent = MainPanelActivity.newIntentForCreateRoom(getContext(), username,
                            roomName, password);
                    startActivity(intent);
                }
            }
        }
    }
}
