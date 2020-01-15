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

    private static final int KEY_JOIN = 1;
    private static final int KEY_LEAVE = 2;
    private static final int KEY_DELETE = 3;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference roomsReference = db.collection("rooms");
    private CollectionReference userReference = db.collection("masterUserList");

    private String username;
    private String password;
    private String roomName;

    private final Context context;

    RoomController(String username, String password, String roomName, Context context) {
        this.username = username;
        this.password = password;
        this.roomName = roomName;
        this.context = context;
    }

    /**
     * Processes a request to join a room.
     */
    void joinRoom() {
        setInputPassword(KEY_JOIN);
    }

    /**
     * Processes a request to leave a room.
     */
    void leaveRoom() {
        setInputPassword(KEY_LEAVE);
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
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Password was not able to be stored.
                        Toast.makeText(context, "Error accessing database.",
                                Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: " + e);
                    }
                });
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
                case KEY_JOIN:
                    // Password was able to be stored; search for room.
                    roomsReference.document(roomName).get()
                            .addOnCompleteListener(new RoomJoinOnCompleteListener());
                    break;

                case KEY_LEAVE:
                    // Password was able to be stored; delete user from room.
                    roomsReference.document(roomName)
                            .collection("users")
                            .document(username)
                            .delete()
                            .addOnSuccessListener(new RoomLeaveOnSuccessListener())
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // User was not able to be deleted from room.
                                    Toast.makeText(context, "Error trying to leave room.",
                                            Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "onFailure: " + e);
                                }
                            });
                    break;

                case KEY_DELETE:
                    // TODO: Add delete room functionality for room owners.
                    break;
            }
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
            userReference.document(FirebaseAuth.getInstance().getUid())
                    .collection("joinedRooms")
                    .document(roomName)
                    .delete();
        }
    }
}



