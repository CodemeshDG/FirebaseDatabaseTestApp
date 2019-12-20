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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class JoinRoomFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference roomsReference = db.collection("rooms");

    private String username;

    public static JoinRoomFragment newInstance(String username) {
        return new JoinRoomFragment(username);
    }

    public JoinRoomFragment(String username) {
        this.username = username;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_room, container, false);

        Button buttonJoin = v.findViewById(R.id.buttonCreateRoom);

        buttonJoin.setText("Join Room");

        setUpElements(v);

        return v;
    }

    private void setUpElements(View v) {
        final EditText editTextRoomName = v.findViewById(R.id.editTextRoomName);
        final EditText editTextPassword = v.findViewById(R.id.editTextPassword);

        Button buttonJoinRoom = v.findViewById(R.id.buttonCreateRoom);
        buttonJoinRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextRoomName.length() != 0 &&
                        editTextPassword.length() != 0) {

                    String roomName = editTextRoomName.getText().toString();
                    final String password = editTextPassword.getText().toString();

                    roomsReference.document(roomName).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();

                                if (document.exists()) {
                                    if (password.equals(document.getString(MainPanelActivity.KEY_PASSWORD))) {
                                        Intent intent = MainPanelActivity.newIntentForJoinRoom(getContext(),
                                                username,
                                                editTextRoomName.getText().toString());
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(getContext(), "ERROR: Incorrect password.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getContext(), "ERROR: No such room exists.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getContext(), "ERROR: Could not search from room.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Fill out all fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
