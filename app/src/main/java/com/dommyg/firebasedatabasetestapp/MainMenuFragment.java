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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainMenuFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference USERNAME_REFERENCE = db.collection("master username list");
    private final CollectionReference USER_REFERENCE = db.collection("master user list");
    private final String KEY_USERNAME = "username";
    private final String KEY_UID = "uid";

    private boolean hasUsername = false;

    private Button buttonCreateRoom;
    private Button buttonJoinRoom;
    private Button buttonSetUsername;

    private TextView textViewUsername;

    private EditText editTextUsername;

    private String username;
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public static MainMenuFragment newInstance() {
        return new MainMenuFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_menu, container, false);

        checkIfHasUsername();

        setUpUsernameElements(v);
        setUpButtons(v);

        return v;
    }

    private void setUpButtons(View v) {
        buttonCreateRoom = v.findViewById(R.id.buttonMainCreateRoom);
        buttonCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = CreateRoomActivity.newIntent(getContext(), username);
                startActivity(intent);
            }
        });

        buttonJoinRoom = v.findViewById(R.id.buttonMainJoinRoom);
        buttonJoinRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = JoinRoomActivity.newIntent(getContext(), username);
                startActivity(intent);
            }
        });

        buttonSetUsername = v.findViewById(R.id.buttonSetUsername);
        buttonSetUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextUsername.length() == 0) {
                    Toast.makeText(getContext(), "Enter a username.", Toast.LENGTH_SHORT).show();
                } else {
                    String enteredUsername = editTextUsername.getText().toString();
                    if (checkIfUsernameExists(enteredUsername)) {
                        Toast.makeText(getContext(), "This username already exists.", Toast.LENGTH_SHORT).show();
                    } else {
                        Map<String, String> mapUsername = new HashMap<>();
                        mapUsername.put(KEY_USERNAME, enteredUsername);

                        Map<String, String> mapUid = new HashMap<>();
                        mapUid.put(KEY_UID, uid);

                        USERNAME_REFERENCE.document(enteredUsername).set(mapUid);
                        USER_REFERENCE.document(uid).set(mapUsername);

                        setUsername();
                    }
                }
            }
        });

        toggleRoomButtons();
        if (hasUsername) {
            setUsername();
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

    private boolean checkIfUsernameExists(String enteredUsername) {
        return USERNAME_REFERENCE.document(enteredUsername)
                .get()
                .isSuccessful();
    }

    private void checkIfHasUsername() {
        USER_REFERENCE.document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        hasUsername = (documentSnapshot.getString(KEY_USERNAME) != null);
                        setUsername();
                    }
                });
    }

    private void setUsername() {
        USER_REFERENCE.document(uid)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        username = documentSnapshot.getString(KEY_USERNAME);
                        toggleOffUsernameElements();
                        toggleRoomButtons();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "ERROR: Could not check database.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
