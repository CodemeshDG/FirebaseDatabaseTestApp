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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class JoinRoomFragment extends Fragment {
    private final String KEY_INPUT_PASSWORD = "inputPassword";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference roomsReference = db.collection("rooms");
    private CollectionReference userReference = db.collection("masterUserList");

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

                    new RoomController(username, password, roomName,
                            getContext()).joinRoom();
                } else {
                    // User did not fill out all required fields.
                    Toast.makeText(getContext(), "Fill out all fields.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Listener for searching for a room in the database.
     */
    private class RoomSearchOnCompleteListener implements OnCompleteListener<DocumentSnapshot> {
        private final String password;
        private final String roomName;

        RoomSearchOnCompleteListener(String password, String editTextRoomName) {
            this.password = password;
            this.roomName = editTextRoomName;
        }

        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isSuccessful()) {
                // Was able to search for room.
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    // Room document exists.
                    if (password.equals(document.getString(MainPanelActivity.KEY_PASSWORD))) {
                        // Password is correct; start the MainPanelActivity.
                        Intent intent = MainPanelActivity.newIntentForJoinRoom(getContext(),
                                username,
                                roomName,
                                password);
                        startActivity(intent);
                    } else {
                        // User entered the wrong password for the room.
                        Toast.makeText(getContext(), "ERROR: Incorrect password.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // User entered a room name which does not exist.
                    Toast.makeText(getContext(), "ERROR: No such room exists.",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                // Could not search for rooms for some reason.
                Toast.makeText(getContext(), "ERROR: Could not search for room.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
