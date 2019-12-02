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

import java.util.ArrayList;

public class MainPanelFragment extends Fragment {
    private ArrayList<StatusItem> arrayListStatusItems;
    private String roomName;
    private String myUsername;

    public static MainPanelFragment newInstance(String roomName, String myUsername) {
        return new MainPanelFragment(roomName, myUsername);
    }

    public MainPanelFragment(String roomName, String myUsername) {
        this.roomName = roomName;
        this.myUsername = myUsername;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_panel, container, false);

        setUpElements(v);

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
}
