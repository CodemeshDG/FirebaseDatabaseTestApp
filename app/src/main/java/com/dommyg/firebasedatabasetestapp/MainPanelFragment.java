package com.dommyg.firebasedatabasetestapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MainPanelFragment extends Fragment {
    private StatusAdapter statusAdapter;

    private String roomName;
    private String myUsername;

    private CollectionReference usersReference;

    public static MainPanelFragment newInstance(String roomName, String myUsername) {
        return new MainPanelFragment(roomName, myUsername);
    }

    private MainPanelFragment(String roomName, String myUsername) {
        this.roomName = roomName;
        this.myUsername = myUsername;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // TODO: Do I need to add an onFailureListener here?
        this.usersReference = db.collection("rooms")
                .document(roomName)
                .collection("users");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_panel, container, false);

        setUpElements(v);
        setUpStatusRecyclerView(v);

        return v;
    }

    /**
     * Sets up the update status button at the bottom of the interface.
     */
    private void setUpElements(View v) {
        Button buttonUpdateStatus = v.findViewById(R.id.buttonUpdateStatus);
        buttonUpdateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateStatusActivity.newIntent(getContext(), roomName,
                        myUsername));
                startActivity(intent);
            }
        });
    }

    /**
     * Sets up the recyclerView for the room users' statuses.
     */
    private void setUpStatusRecyclerView(View v) {
        Query query = usersReference;

        FirestoreRecyclerOptions<StatusItem> options = new FirestoreRecyclerOptions.Builder<StatusItem>()
                .setQuery(query, StatusItem.class)
                .build();

                statusAdapter = new StatusAdapter(options);

                RecyclerView recyclerViewStatus = v.findViewById(R.id.recyclerViewStatus);
                recyclerViewStatus.setNestedScrollingEnabled(false);
                recyclerViewStatus.setHasFixedSize(true);

                RecyclerView.LayoutManager recyclerViewStatusLayoutManager = new LinearLayoutManager(
                        getContext());

                recyclerViewStatus.setLayoutManager(recyclerViewStatusLayoutManager);
                recyclerViewStatus.setAdapter(statusAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        statusAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        statusAdapter.stopListening();
    }
}
