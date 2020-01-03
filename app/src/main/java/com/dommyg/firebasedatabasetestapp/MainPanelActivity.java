package com.dommyg.firebasedatabasetestapp;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides the interface for reading all room users' statuses and updating ones own.
 */
public class MainPanelActivity extends SingleFragmentActivity {
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROOM_NAME = "room_name";
    static final String KEY_PASSWORD = "password";
    private static final String KEY_OWNER = "owner";

    @Override
    protected Fragment createFragment() {
        String username = getIntent().getStringExtra(KEY_USERNAME);
        String roomName = getIntent().getStringExtra(KEY_ROOM_NAME);
        return MainPanelFragment.newInstance(roomName, username);
    }

    /**
     * Used for when the user is creating a new room. Creates a new room document in the database's
     * "rooms" collection, setting the name of the room, the password, and the owner of the room.
     * Finally, creates and returns an intent for a new MainPanelActivity.
     */
    public static Intent newIntentForCreateRoom(Context packageContext, String username, String roomName,
                                   String password) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, String> mapRoom = new HashMap<>();
        mapRoom.put(KEY_PASSWORD, password);
        mapRoom.put(KEY_ROOM_NAME, roomName);
        mapRoom.put(KEY_OWNER, username);
        Map<String, String> mapUsername = new HashMap<>();
        mapUsername.put(KEY_USERNAME, username);

        db.collection("rooms")
                .document(roomName)
                .set(mapRoom);

        db.collection("rooms")
                .document(roomName)
                .collection("users")
                .document(username)
                .set(mapUsername);

        Intent intent = new Intent(packageContext, MainPanelActivity.class);
        intent.putExtra(KEY_USERNAME, username);
        intent.putExtra(KEY_ROOM_NAME, roomName);
        return intent;
    }

    /**
     * Used for when the user is joining an existing room. Creates a new user document in the room's
     * "user" collection, setting just the name of the user in the document. Then, creates and
     * returns an intent for a new MainPanelActivity.
     */
    public static Intent newIntentForJoinRoom(Context packageContext, String username, String roomName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, String> mapUsername = new HashMap<>();
        mapUsername.put(KEY_USERNAME, username);

        db.collection("rooms")
                .document(roomName)
                .collection("users")
                .document(username)
                .set(mapUsername, SetOptions.merge());

        Intent intent = new Intent(packageContext, MainPanelActivity.class);
        intent.putExtra(KEY_USERNAME, username);
        intent.putExtra(KEY_ROOM_NAME, roomName);
        return intent;
    }
}
