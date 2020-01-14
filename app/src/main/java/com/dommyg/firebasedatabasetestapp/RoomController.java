package com.dommyg.firebasedatabasetestapp;

import android.content.Context;
import android.content.Intent;
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
    private final String KEY_INPUT_PASSWORD = "inputPassword";

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

    void joinRoom() {
        Map<String, String> mapInputPassword = new HashMap<>();
        mapInputPassword.put(KEY_INPUT_PASSWORD, password);

        // The password which the user input must be stored for database security check.
        userReference.document(Objects.requireNonNull(FirebaseAuth
                .getInstance()
                .getUid()))
                .set(mapInputPassword, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Password was able to be stored.
                        roomsReference.document(roomName).get()
                                .addOnCompleteListener(new RoomJoinOnCompleteListener(
                                        password, roomName, context));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Password was not able to be stored.
                Toast.makeText(context, "ERROR: Could not access database.",
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    void leaveRoom() {
        Map<String, String> mapInputPassword = new HashMap<>();
        mapInputPassword.put(KEY_INPUT_PASSWORD, password);

        userReference.document(Objects.requireNonNull(FirebaseAuth
                .getInstance()
                .getUid()))
                .set(mapInputPassword, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                roomsReference.document(roomName)
                        .collection("users")
                        .document(username)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                userReference.document(FirebaseAuth.getInstance().getUid())
                                        .collection("joinedRooms")
                                        .document(roomName)
                                        .delete();
                            }
                        });
            }
        });
    }

    /**
     * Listener for searching for a room in the database.
     */
    private class RoomJoinOnCompleteListener implements OnCompleteListener<DocumentSnapshot> {
        private final String password;
        private final String roomName;
        private final Context context;

        RoomJoinOnCompleteListener(String password, String editTextRoomName, Context context) {
            this.password = password;
            this.roomName = editTextRoomName;
            this.context = context;
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
            }
        }
    }
}


