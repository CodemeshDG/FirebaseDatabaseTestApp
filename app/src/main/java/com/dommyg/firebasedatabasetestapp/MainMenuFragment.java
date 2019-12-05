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

public class MainMenuFragment extends Fragment {

    public static MainMenuFragment newInstance() {
        return new MainMenuFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_menu, container, false);

        setUpButtons(v);

        return v;
    }

    private void setUpButtons(View v) {
        Button buttonCreateRoom = v.findViewById(R.id.buttonMainCreateRoom);
        buttonCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = CreateRoomActivity.newIntent(getContext());
                startActivity(intent);
            }
        });

        Button buttonJoinRoom = v.findViewById(R.id.buttonMainJoinRoom);
        buttonJoinRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = JoinRoomActivity.newIntent(getContext());
                startActivity(intent);
            }
        });
    }
}
