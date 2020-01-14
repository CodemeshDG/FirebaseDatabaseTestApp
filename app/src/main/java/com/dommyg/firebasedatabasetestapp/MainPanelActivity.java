package com.dommyg.firebasedatabasetestapp;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides the interface for reading all room users' statuses and updating ones own.
 */
public class MainPanelActivity extends SingleFragmentActivity {
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROOM_NAME = "roomName";
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
        mapRoom.put(KEY_ROOM_NAME, roomName);

        db.collection("masterRoomList")
                .document(roomName)
                .set(mapRoom);

        mapRoom.put(KEY_PASSWORD, password);
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

        storeRoomInfoForUser(db, roomName, password, true);

        Intent intent = new Intent(packageContext, MainPanelActivity.class);
        intent.putExtra(KEY_USERNAME, username);
        intent.putExtra(KEY_ROOM_NAME, roomName);
        return intent;
    }

    /**
     * Used for when the user is joining an existing room. Creates a new user document in the room's
     * "user" collection, setting just the name of the user in the document, and creates a new
     * document in the user's "joinedRooms" collection which stores the room name and password so
     * that the user may rejoin the room in the future from the main menu. Then, creates and returns
     * an intent for a new MainPanelActivity.
     */
    public static Intent newIntentForJoinRoom(Context packageContext, String username, String roomName,
                                              String password) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, String> mapUsername = new HashMap<>();
        mapUsername.put(KEY_USERNAME, username);

        db.collection("rooms")
                .document(roomName)
                .collection("users")
                .document(username)
                .set(mapUsername, SetOptions.merge());

        // TODO: Update this so if a user who owns a room rejoins the room through this method, they
        //  will be marked as owner still.
        storeRoomInfoForUser(db, roomName, password, false);

        Intent intent = new Intent(packageContext, MainPanelActivity.class);
        intent.putExtra(KEY_USERNAME, username);
        intent.putExtra(KEY_ROOM_NAME, roomName);
        return intent;
    }

    /**
     * Stores into the user's "joinedRooms" collection the room name of the room being joined, its
     * password, and if the user is the room's owner. This allows for rejoining without inputting a
     * password in the future from the main menu, as well as appropriate actions for disengagement
     * with the room (leaving room for non-owners and deleting the room for owners).
     */
    private static void storeRoomInfoForUser(FirebaseFirestore db, String roomName, String password,
                                             boolean isOwner) {
        Map<String, Object> mapRoomInfo = new HashMap<>();
        mapRoomInfo.put(KEY_ROOM_NAME, roomName);
        mapRoomInfo.put(KEY_PASSWORD, password);
        mapRoomInfo.put(KEY_OWNER, isOwner);

        db.collection("masterUserList")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("joinedRooms")
                .document(roomName)
                .set(mapRoomInfo);
    }
}
