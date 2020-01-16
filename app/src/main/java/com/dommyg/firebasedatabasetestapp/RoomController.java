package com.dommyg.firebasedatabasetestapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

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

class RoomController {
    private static final String TAG = "RoomController";

    private static final String KEY_INPUT_PASSWORD = "inputPassword";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROOM_NAME = "roomName";
    static final String KEY_PASSWORD = "password";
    private static final String KEY_OWNER = "owner";
    private static final String KEY_IS_OWNER = "isOwner";

    private static final int CODE_CREATE = 1;
    private static final int CODE_JOIN = 2;
    private static final int CODE_LEAVE = 3;
    private static final int CODE_DELETE = 4;
    private static final int CODE_CHANGE_PASSWORD = 5;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference roomsReference = db.collection("rooms");
    private final CollectionReference userReference = db.collection("masterUserList");

    private final String uid = FirebaseAuth.getInstance().getUid();

    private final String username;
    private final String password;
    private final String roomName;

    private final Context context;

    RoomController(String username, String password, String roomName, Context context) {
        this.username = username;
        this.password = password;
        this.roomName = roomName;
        this.context = context;
    }

    void createRoom() {
        setRoomNameInMasterList(CODE_CREATE);
    }

    /**
     * Processes a request to join a room.
     */
    void joinRoom() {
        setInputPassword(CODE_JOIN);
    }

    /**
     * Processes a request to leave a room.
     */
    void leaveRoom() {
        setInputPassword(CODE_LEAVE);
    }

    /**
     * Sets the "inputPassword" field for the user so that they may read and write to certain documents
     * in the room.
     */
    private void setInputPassword(int command) {
        Map<String, String> mapInputPassword = new HashMap<>();
        mapInputPassword.put(KEY_INPUT_PASSWORD, password);

        userReference.document(Objects.requireNonNull(FirebaseAuth
                .getInstance()
                .getUid()))
                .set(mapInputPassword, SetOptions.merge())
                .addOnSuccessListener(new InputPasswordOnSuccessListener(command))
                .addOnFailureListener(new ActionFailureListener(command));
    }

    private void setRoomNameInMasterList(int command) {
        Map<String, String> mapRoom = new HashMap<>();
        mapRoom.put(KEY_ROOM_NAME, roomName);

        db.collection("masterRoomList")
                .document(roomName)
                .set(mapRoom)
                .addOnSuccessListener(new WriteToMasterListOnSuccessListener(command, mapRoom))
                .addOnFailureListener(new ActionFailureListener(command));
    }

    /**
     * Stores into the user's "joinedRooms" collection the room name of the room being joined, its
     * password, and if the user is the room's owner. This allows for rejoining without inputting a
     * password in the future from the main menu, as well as appropriate actions for disengagement
     * with the room (leaving room for non-owners and deleting the room for owners).
     */
    private void setRoomInfoInUser(int command, String roomName, String password,
                                   boolean isOwner) {
        Map<String, Object> mapRoomInfo = new HashMap<>();
        mapRoomInfo.put(KEY_ROOM_NAME, roomName);
        mapRoomInfo.put(KEY_PASSWORD, password);
        mapRoomInfo.put(KEY_IS_OWNER, isOwner);

        db.collection("masterUserList")
                .document(uid)
                .collection("joinedRooms")
                .document(roomName)
                .set(mapRoomInfo)
                .addOnSuccessListener(new WriteToRoomInfoInUserOnSuccessListener())
                .addOnFailureListener(new ActionFailureListener(command));
    }

    /**
     * Listens for success on modifying the user's "inputPassword" field, and then takes appropriate
     * action based upon the user's request (joining, leaving, or deleting room).
     */
    private class InputPasswordOnSuccessListener implements OnSuccessListener<Void> {
        private int command;

        InputPasswordOnSuccessListener(int command) {
            this.command = command;
        }

        @Override
        public void onSuccess(Void aVoid) {
            switch (command) {
                case CODE_JOIN:
                    // Password was able to be stored; search for room.
                    roomsReference.document(roomName).get()
                            .addOnCompleteListener(new RoomJoinOnCompleteListener());
                    break;

                case CODE_LEAVE:
                    // Password was able to be stored; delete user from room.
                    roomsReference.document(roomName)
                            .collection("users")
                            .document(username)
                            .delete()
                            .addOnSuccessListener(new RoomLeaveOnSuccessListener())
                            .addOnFailureListener(new ActionFailureListener(command));
                    break;

                case CODE_DELETE:
                    // TODO: Add delete room functionality for room owners.
            }
        }
    }

    private class WriteToMasterListOnSuccessListener implements OnSuccessListener<Void> {
        private int command;
        private Map<String, String> mapRoom;

        WriteToMasterListOnSuccessListener(int command, Map<String, String> mapRoom) {
            this.command = command;
            this.mapRoom = mapRoom;
        }

        @Override
        public void onSuccess(Void aVoid) {
            mapRoom.put(KEY_PASSWORD, password);
            mapRoom.put(KEY_OWNER, username);

            db.collection("rooms")
                    .document(roomName)
                    .set(mapRoom)
                    .addOnSuccessListener(new WriteToRoomsOnSuccessListener(command))
                    .addOnFailureListener(new ActionFailureListener(command));
        }
    }

    private class WriteToRoomsOnSuccessListener implements OnSuccessListener<Void> {
        private int command;

        WriteToRoomsOnSuccessListener(int command) {
            this.command = command;
        }

        @Override
        public void onSuccess(Void aVoid) {
            Map<String, String> mapUsername = new HashMap<>();
            mapUsername.put(KEY_USERNAME, username);

            db.collection("rooms")
                    .document(roomName)
                    .collection("users")
                    .document(username)
                    .set(mapUsername)
                    .addOnSuccessListener(new WriteToRoomsUsernameOnSuccessListener(command))
                    .addOnFailureListener(new ActionFailureListener(command));
        }
    }

    private class WriteToRoomsUsernameOnSuccessListener implements OnSuccessListener<Void> {
        private int command;

        WriteToRoomsUsernameOnSuccessListener(int command) {
            this.command = command;
        }

        @Override
        public void onSuccess(Void aVoid) {
            setRoomInfoInUser(command, roomName, password, true);
        }
    }

    private class WriteToRoomInfoInUserOnSuccessListener implements OnSuccessListener<Void> {

        @Override
        public void onSuccess(Void aVoid) {
            Intent intent = MainPanelActivity.newIntentForCreateRoom(context, username, roomName);
            context.startActivity(intent);
        }
    }

    /**
     * Listens for completion of searching for a room in the database, and then attempts to join it
     * if it exists.
     */
    private class RoomJoinOnCompleteListener implements OnCompleteListener<DocumentSnapshot> {

        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            // TODO: See if more accurate errors can be reported based upon current database security
            //  rules. Use onSuccessListener instead?
            if (task.isSuccessful()) {
                // Was able to search for room.
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    // Room document exists.
                    if (password.equals(document.getString(MainPanelActivity.KEY_PASSWORD))) {
                        // Password is correct; start the MainPanelActivity.
                        Intent intent = MainPanelActivity.newIntentForJoinRoom(context,
                                username,
                                roomName,
                                password);
                        context.startActivity(intent);
                    } else {
                        // User entered the wrong password for the room.
                        Toast.makeText(context, "ERROR: Incorrect password.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // User entered a room name which does not exist.
                    Toast.makeText(context, "ERROR: No such room exists.",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                // Could not search for rooms for some reason.
                Toast.makeText(context, "ERROR: Could not search for room.",
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onComplete: " + task.getException());
            }
        }
    }

    /**
     * Listens for success of deleting oneself from a room, and then deletes the room from the
     * user's "joinedRooms" collection.
     */
    private class RoomLeaveOnSuccessListener implements OnSuccessListener<Void> {

        @Override
        public void onSuccess(Void aVoid) {
            userReference.document(uid)
                    .collection("joinedRooms")
                    .document(roomName)
                    .delete();
        }
    }

    /**
     * Listens for action failures in the RoomController class and provides the appropriate Toast
     * message for the user.
     */
    private class ActionFailureListener implements OnFailureListener {
        private String failureMessage;

        ActionFailureListener(int errorCode) {
            switch (errorCode) {
                case CODE_CREATE:
                    failureMessage = "Error creating room.";
                    break;

                case CODE_JOIN:
                    failureMessage = "Error searching for room.";
                    break;

                case CODE_LEAVE:
                    failureMessage = "Error trying to leave room.";
                    break;

                case CODE_DELETE:
                    failureMessage = "Error trying to delete room.";
                    break;

                case CODE_CHANGE_PASSWORD:
                    failureMessage = "Error accessing database.";
            }
        }

        @Override
        public void onFailure(@NonNull Exception e) {
            Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onFailure: " + e);
        }
    }
}



