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
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

/**
 * Processes most requests to the database, including reading and writing (and deleting) information.
 * Used to create, join, leave, or delete rooms, along with updating user statuses.
 */
class RoomController {
    private static final String TAG = "RoomController";

    private static final String KEY_INPUT_PASSWORD = "inputPassword";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROOM_NAME = "roomName";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_OWNER = "owner";
    private static final String KEY_IS_OWNER = "isOwner";
    private static final String KEY_FEELING = "feeling";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_BUSY = "isBusy";

    private static final int CODE_CREATE = 1;
    private static final int CODE_JOIN = 2;
    private static final int CODE_LEAVE = 3;
    private static final int CODE_DELETE = 4;
    private static final int CODE_CHANGE_PASSWORD = 5;
    private static final int CODE_UPDATE_STATUS = 6;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference roomsReference = db.collection("rooms");
    private final CollectionReference masterUserReference = db.collection("masterUserList");
    private final CollectionReference masterRoomReference = db.collection("masterRoomList");

    private final String uid = FirebaseAuth.getInstance().getUid();

    private final String username;
    private final String password;
    private final String roomName;
    private final boolean newJoin;

    private final Context context;

    private final UpdateStatusFragment updateStatusFragment;

    RoomController(String username, String password, String roomName, boolean newJoin,
                   Context context) {
        this.username = username;
        this.password = password;
        this.roomName = roomName;
        this.newJoin = newJoin;
        this.context = context;

        this.updateStatusFragment = null;
    }

    RoomController(UpdateStatusFragment updateStatusFragment, String username, String roomName,
                   Context context) {
        this.updateStatusFragment = updateStatusFragment;
        this.username = username;
        this.roomName = roomName;
        this.context = context;

        this.password = null;
        this.newJoin = false;
    }

    /**
     * Processes a request to create a room.
     */
    void createRoom() {
        readMasterRoomList();
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

    void deleteRoom() {
        setInputPassword(CODE_DELETE);
    }

    /**
     * Processes a request to update the user's status.
     */
    void updateStatus(String selectedFeelingString, String location, boolean isBusy) {
        Map<String, Object> mapStatus = new HashMap<>();
        mapStatus.put(KEY_FEELING, selectedFeelingString);

        if (location.length() != 0) {
            mapStatus.put(KEY_LOCATION, location);
        } else {
            mapStatus.put(KEY_LOCATION, null);
        }

        mapStatus.put(KEY_BUSY, isBusy);

        roomsReference.document(roomName)
                .collection("users")
                .document(username)
                .set(mapStatus, SetOptions.merge())
                .addOnSuccessListener(new UpdateStatusOnSuccessListener())
                .addOnFailureListener(new ActionFailureListener(CODE_UPDATE_STATUS));
    }

    /**
     * Sets the "inputPassword" field for the user so that they may read and write to certain documents
     * in the room. This is a required set to join, leave, or delete a room.
     */
    private void setInputPassword(int command) {
        Map<String, String> mapInputPassword = new HashMap<>();
        mapInputPassword.put(KEY_INPUT_PASSWORD, password);

        masterUserReference.document(uid)
                .set(mapInputPassword, SetOptions.merge())
                .addOnSuccessListener(new WriteInputPasswordOnSuccessListener(command))
                .addOnFailureListener(new ActionFailureListener(command));
    }

    /**
     * Reads the "masterRoomList" to verify if a room already exists.
     */
    private void readMasterRoomList() {
        masterRoomReference.document(roomName)
                .get()
                .addOnCompleteListener(new ReadMasterRoomListOnCompleteListener());
    }

    /**
     * Creates and commits a batch write to set up a new room.
     */
    private void batchWriteToCreateRoom() {
        WriteBatch creationBatch = db.batch();

        // Stores the room name in the "masterRoomList" collection for future querying (to prevent
        // someone from making a room with the same name and overwriting this room's data).
        Map<String, String> mapRoom = new HashMap<>();
        mapRoom.put(KEY_ROOM_NAME, roomName);

        creationBatch.set(masterRoomReference.document(roomName), mapRoom);

        // Creates the room document in the "rooms" collection, storing its name, password, and
        // owner, which allows for other users to join with a password and enables the owner to
        // delete the room rather than leave.
        mapRoom.put(KEY_PASSWORD, password);
        mapRoom.put(KEY_OWNER, username);

        creationBatch.set(roomsReference.document(roomName), mapRoom);

        // Creates the "users" collection in the room's document and adds the owner as the first user.
        // This document holds information related to the user's status once the user updates their
        // status.
        Map<String, String> mapUsername = new HashMap<>();
        mapUsername.put(KEY_USERNAME, username);

        creationBatch.set(roomsReference
                        .document(roomName)
                        .collection("users")
                        .document(username),
                mapUsername);

        // Stores into the user's "joinedRooms" collection the room name of the room being joined, its
        // password, and if the user is the room's owner. This allows for rejoining without inputting a
        // password in the future from the main menu, as well as appropriate actions for disengagement
        // with the room (leaving room for non-owners and deleting the room for owners).
        Map<String, Object> mapRoomInfo = new HashMap<>();
        mapRoomInfo.put(KEY_ROOM_NAME, roomName);
        mapRoomInfo.put(KEY_PASSWORD, password);
        mapRoomInfo.put(KEY_IS_OWNER, true);

        creationBatch.set(masterUserReference
                        .document(uid)
                        .collection("joinedRooms")
                        .document(roomName),
                mapRoomInfo);

        creationBatch.commit()
                .addOnSuccessListener(new BatchedWriteCreateRoomOnSuccessListener())
                .addOnFailureListener(new ActionFailureListener(CODE_CREATE));
    }

    /**
     * Listens for completion of searching for a room in the database.
     */
    private class ReadMasterRoomListOnCompleteListener implements OnCompleteListener<DocumentSnapshot> {

        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    Toast.makeText(context, "This room name already exists.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // User entered a room name which does not exist and a password; create the room.
                    batchWriteToCreateRoom();
                }
            }
        }
    }

    /**
     * Listens for success on modifying the user's "inputPassword" field, and then takes appropriate
     * action based upon the user's request (joining, leaving, or deleting room).
     */
    private class WriteInputPasswordOnSuccessListener implements OnSuccessListener<Void> {
        private int command;

        WriteInputPasswordOnSuccessListener(int command) {
            this.command = command;
        }

        @Override
        public void onSuccess(Void aVoid) {
            switch (command) {
                case CODE_JOIN:
                    // Search for room.
                    roomsReference.document(roomName).get()
                            .addOnCompleteListener(new ReadRoomsOnCompleteListener());
                    break;

                case CODE_LEAVE:
                    // Delete user from room.
                    WriteBatch leaveBatch = db.batch();

                    leaveBatch.delete(roomsReference
                            .document(roomName)
                            .collection("users")
                            .document(username));

                    leaveBatch.delete(masterUserReference.document(uid)
                            .collection("joinedRooms")
                            .document(roomName));

                    leaveBatch.commit()
                            .addOnSuccessListener(new BatchWriteLeaveRoomOnSuccessListener())
                            .addOnFailureListener(new ActionFailureListener(command));
                    break;

                case CODE_DELETE:
                    // TODO: Add delete room functionality for room owners.
            }
        }
    }

    /**
     * Listens for completion of searching for a room in the database, and then attempts to join it
     * if it exists.
     */
    private class ReadRoomsOnCompleteListener implements OnCompleteListener<DocumentSnapshot> {

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
                        // Password is correct.
                        if (newJoin) {
                            // User is joining this room for the first time. Stores into the user's
                            // "joinedRooms" collection the room name of the room being joined, its
                            // password, and if the user is the room's owner. This allows for
                            // rejoining without inputting a password in the future from the main
                            // menu, as well as appropriate actions for disengagement with the room
                            // (leaving room for non-owners and deleting the room for owners).
                            Map<String, Object> mapRoomInfo = new HashMap<>();
                            mapRoomInfo.put(KEY_ROOM_NAME, roomName);
                            mapRoomInfo.put(KEY_PASSWORD, password);
                            mapRoomInfo.put(KEY_OWNER, false);

                            masterUserReference.document(uid)
                                    .collection("joinedRooms")
                                    .document(roomName)
                                    .set(mapRoomInfo)
                                    .addOnSuccessListener(new WriteUsersJoinedRoomOnSuccessListener())
                                    .addOnFailureListener(new ActionFailureListener(CODE_JOIN));
                        } else {
                            // User is rejoining this room and does not require his "joinedRooms"
                            // collection to be updated.
                            startMainPanelActivity();
                        }
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
     * Listens for success of the write required to save the room info into the user's "joinedRooms"
     * collection in the database, and then launches the MainPanelActivity to bring the user to the
     * room.
     */
    private class WriteUsersJoinedRoomOnSuccessListener implements OnSuccessListener<Void> {

        @Override
        public void onSuccess(Void aVoid) {
            startMainPanelActivity();
        }
    }

    /**
     * Listens for success of the batched write required to create room documentation in the
     * database, and then launches the MainPanelActivity to bring the user to their new room.
     */
    private class BatchedWriteCreateRoomOnSuccessListener implements OnSuccessListener<Void> {

        @Override
        public void onSuccess(Void aVoid) {
            startMainPanelActivity();
        }
    }

    /**
     * Listens for success of the batched write required to delete the proper documentation in the
     * database for leaving a room, and then shows a successful Toast message.
     */
    private class BatchWriteLeaveRoomOnSuccessListener implements OnSuccessListener<Void> {

        @Override
        public void onSuccess(Void aVoid) {
            Toast.makeText(context, "Left room: " + roomName, Toast.LENGTH_SHORT).show();
        }
    }

    private class UpdateStatusOnSuccessListener implements OnSuccessListener<Void> {

        @Override
        public void onSuccess(Void aVoid) {
            updateStatusFragment.getActivity().finish();
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
                    break;

                case CODE_UPDATE_STATUS:
                    failureMessage = "Error updating status.";
            }
        }

        @Override
        public void onFailure(@NonNull Exception e) {
            Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onFailure: " + e);
        }
    }

    private void startMainPanelActivity() {
        Intent intent = MainPanelActivity.newIntent(context, username, roomName);
        context.startActivity(intent);
    }
}



