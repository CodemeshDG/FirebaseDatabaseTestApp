package com.dommyg.firebasedatabasetestapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
    private final CollectionReference USERNAME_REFERENCE = db.collection("masterUsernameList");
    private final CollectionReference USER_REFERENCE = db.collection("masterUserList");
    private final String KEY_USERNAME = "username";
    private final String KEY_UID = "uid";

    private boolean hasUsername = false;

    private Button buttonCreateRoom;
    private Button buttonJoinRoom;
    private Button buttonSetUsername;

    private TextView textViewUsername;

    private EditText editTextUsername;

    private ProgressBar progressBarMainMenu;

    private String username;
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public static MainMenuFragment newInstance() {
        return new MainMenuFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_menu, container, false);

        initializeViews(v);
        setUpButtons();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        checkIfHasUsername();
    }

    /**
     * Sets all views for the fragment.
     */
    private void initializeViews(View v) {
        buttonCreateRoom = v.findViewById(R.id.buttonMainCreateRoom);
        buttonJoinRoom = v.findViewById(R.id.buttonMainJoinRoom);
        buttonSetUsername = v.findViewById(R.id.buttonSetUsername);
        textViewUsername = v.findViewById(R.id.textViewUsername);
        editTextUsername = v.findViewById(R.id.editTextUsername);
        progressBarMainMenu = v.findViewById(R.id.progressBarMainMenu);
    }

    /**
     * Sets all views to visibility GONE and sets the ProgressBar to VISIBLE.
     */
    private void showProgressBar() {
        buttonCreateRoom.setVisibility(View.GONE);
        buttonJoinRoom.setVisibility(View.GONE);
        buttonSetUsername.setVisibility(View.GONE);
        textViewUsername.setVisibility(View.GONE);
        editTextUsername.setVisibility(View.GONE);
        progressBarMainMenu.setVisibility(View.VISIBLE);
    }

    /**
     * Sets the ProgressBar's visibility to GONE. Should be used in conjunction with
     * showUsernameElements() or showCreateUsernameElements() to provide appropriate views on the
     * screen.
     */
    private void hideProgressBar() {
        progressBarMainMenu.setVisibility(View.GONE);
    }

    /**
     * Sets the two views for the username sign up process (editTextUsername and buttonSetUsername)
     * to GONE, and sets the other views to VISIBLE. Also enables the create and join room buttons.
     * Used once the database confirms that the use has a username and can create or join rooms.
     */
    private void showUsernameElements() {
        editTextUsername.setVisibility(View.GONE);
        buttonSetUsername.setVisibility(View.GONE);

        textViewUsername.setText("Welcome " + username + "!");
        textViewUsername.setVisibility(View.VISIBLE);

        buttonCreateRoom.setEnabled(true);
        buttonJoinRoom.setEnabled(true);
        buttonCreateRoom.setVisibility(View.VISIBLE);
        buttonJoinRoom.setVisibility(View.VISIBLE);
    }

    /**
     * Sets all views to VISIBLE and disables the create and join room buttons. Used once the
     * database confirms that the user does not have a username and must create one.
     */
    private void showCreateUsernameElements() {
        editTextUsername.setVisibility(View.VISIBLE);
        buttonSetUsername.setVisibility(View.VISIBLE);
        textViewUsername.setVisibility(View.VISIBLE);

        buttonCreateRoom.setEnabled(false);
        buttonJoinRoom.setEnabled(false);
        buttonCreateRoom.setVisibility(View.VISIBLE);
        buttonJoinRoom.setVisibility(View.VISIBLE);
    }

    private void setUpButtons() {
        buttonCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = CreateRoomActivity.newIntent(getContext(), username);
                startActivity(intent);
            }
        });

        buttonJoinRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = JoinRoomActivity.newIntent(getContext(), username);
                startActivity(intent);
            }
        });

        buttonSetUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextUsername.length() == 0) {
                    // Did not enter a username.
                    Toast.makeText(getContext(), "Enter a username.", Toast.LENGTH_SHORT).show();
                } else {
                    String enteredUsername = editTextUsername.getText().toString();
                    if (checkIfUsernameExists(enteredUsername)) {
                        // Username exists.
                        Toast.makeText(getContext(), "This username already exists.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Create username in database.
                        showProgressBar();
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
    }

    /**
     * Checks and returns if a username exists in the database.
     */
    private boolean checkIfUsernameExists(String enteredUsername) {
        return USERNAME_REFERENCE.document(enteredUsername)
                .get()
                .isSuccessful();
    }

    /**
     * Checks if the user had a username registered in the database.
     */
    private void checkIfHasUsername() {
        showProgressBar();
        USER_REFERENCE.document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        hasUsername = (documentSnapshot.getString(KEY_USERNAME) != null);
                        if (hasUsername) {
                            setUsername();
                        } else {
                            hideProgressBar();
                            showCreateUsernameElements();
                        }
                    }
                });
    }

    /**
     * Retrieves and sets the username variable from database info.
     */
    private void setUsername() {
        USER_REFERENCE.document(uid)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        username = documentSnapshot.getString(KEY_USERNAME);
                        hideProgressBar();
                        showUsernameElements();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "ERROR: Could not check database.", Toast.LENGTH_SHORT).show();
                hideProgressBar();
                showCreateUsernameElements();
            }
        });
    }
}
