package com.dommyg.firebasedatabasetestapp;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainPanelActivity extends SingleFragmentActivity {
    static final String KEY_USERNAME = "username";
    private static final String KEY_ROOM_NAME = "room_name";
    static final String KEY_PASSWORD = "password";

    @Override
    protected Fragment createFragment() {
        String username = getIntent().getStringExtra(KEY_USERNAME);
        String roomName = getIntent().getStringExtra(KEY_ROOM_NAME);
        return MainPanelFragment.newInstance(roomName, username);
    }

    public static Intent newIntentForCreateRoom(Context packageContext, String username, String roomName,
                                   String password) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, String> mapPassword = new HashMap<>();
        mapPassword.put(KEY_PASSWORD, password);
        Map<String, String> mapUsername = new HashMap<>();
        mapUsername.put(KEY_USERNAME, username);
        Map<String, String> mapRoomName = new HashMap<>();
        mapPassword.put(KEY_ROOM_NAME, roomName);

        db.collection("rooms")
                .document(roomName)
                .set(mapRoomName);

        db.collection("rooms")
                .document(roomName)
                .set(mapPassword);

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

    public static Intent newIntentForJoinRoom(Context packageContext, String username, String roomName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, String> mapUsername = new HashMap<>();
        mapUsername.put(KEY_USERNAME, username);

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
}
