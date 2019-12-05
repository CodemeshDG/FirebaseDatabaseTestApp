package com.dommyg.firebasedatabasetestapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainPanelFragment extends Fragment {
    private ArrayList<StatusItem> arrayListStatusItems;
    private RecyclerView recyclerViewStatus;
    private StatusAdapter statusAdapter;
    private RecyclerView.LayoutManager recyclerViewStatusLayoutManager;

    private String roomName;
    private String myUsername;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersReference;
    private ListenerRegistration statusListener;

    public static MainPanelFragment newInstance(String roomName, String myUsername) {
        return new MainPanelFragment(roomName, myUsername);
    }

    public MainPanelFragment(String roomName, String myUsername) {
        this.roomName = roomName;
        this.myUsername = myUsername;
        this.usersReference = db.collection("rooms")
                .document(roomName)
                .collection("users");
        this.arrayListStatusItems = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_panel, container, false);

        setUpElements(v);
        setUpStatusRecyclerView(v);

        return v;
    }

    private void setUpElements(View v) {
        Button buttonUpdateStatus = v.findViewById(R.id.buttonUpdateStatus);
        buttonUpdateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateStatusActivity.newIntent(getContext(), roomName, myUsername));
                startActivity(intent);
            }
        });
    }

    private void setUpStatusRecyclerView(View v) {
        recyclerViewStatus = v.findViewById(R.id.recyclerViewStatus);
        recyclerViewStatus.setNestedScrollingEnabled(false);
        recyclerViewStatus.setHasFixedSize(true);

        recyclerViewStatusLayoutManager = new LinearLayoutManager(getContext());
        statusAdapter = new StatusAdapter(arrayListStatusItems);

        recyclerViewStatus.setLayoutManager(recyclerViewStatusLayoutManager);
        recyclerViewStatus.setAdapter(statusAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        statusListener = usersReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "ERROR: EventListener failure.", Toast.LENGTH_SHORT).show();
                    return;
                }

                refreshStatuses();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        statusListener.remove();
    }

    private void refreshStatuses() {
        usersReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        while (arrayListStatusItems.size() != 0) {
                            arrayListStatusItems.remove(0);
                        }
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (!documentSnapshot.contains(UpdateStatusFragment.KEY_FEELING)) {
                                arrayListStatusItems.add(
                                        new StatusItem(documentSnapshot.getString(
                                                MainPanelActivity.KEY_USERNAME)));
                            } else {
                                arrayListStatusItems.add(
                                        new StatusItem(documentSnapshot.getString(
                                                MainPanelActivity.KEY_USERNAME),
                                        Integer.valueOf(
                                                documentSnapshot.getString(
                                                        UpdateStatusFragment.KEY_FEELING)),
                                        documentSnapshot.getString(UpdateStatusFragment.KEY_LOCATION),
                                        documentSnapshot.getBoolean(UpdateStatusFragment.KEY_BUSY)));
                            }
                        }
                        statusAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "ERROR: Cannot refresh statuses.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
