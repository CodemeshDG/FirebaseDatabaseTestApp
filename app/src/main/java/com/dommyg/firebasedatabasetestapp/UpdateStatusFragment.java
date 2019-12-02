package com.dommyg.firebasedatabasetestapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class UpdateStatusFragment extends Fragment {
    private static final String KEY_FEELING = "feeling";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_BUSY = "is_busy";

    private int selectedFeeling;
    private String roomName;
    private String username;

    public static UpdateStatusFragment newInstance(String roomName, String username) {
        return new UpdateStatusFragment(roomName, username);
    }

    public UpdateStatusFragment(String roomName, String username) {
        this.selectedFeeling = 0;
        this.roomName = roomName;
        this.username = username;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_update_status, container, false);

        setUpElements(v);

        return v;
    }

    private void setUpElements(View v) {
        final RadioGroup radioGroupFeeling = v.findViewById(R.id.radioGroupFeeling);
        radioGroupFeeling.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                selectedFeeling = radioGroupFeeling.getCheckedRadioButtonId();
            }
        });

        final EditText editTextLocation = v.findViewById(R.id.editTextLocation);
        final CheckBox checkBoxBusy = v.findViewById(R.id.checkBoxBusy);
        Button buttonSubmitStatus = v.findViewById(R.id.buttonSubmitStatus);
        buttonSubmitStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedFeeling != 0) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    Map<String, Object> mapStatus = new HashMap<>();
                    mapStatus.put(KEY_FEELING, selectedFeeling);

                    if (editTextLocation.length() != 0) {
                        String location = editTextLocation.getText().toString();
                        mapStatus.put(KEY_LOCATION, location);
                    } else {
                        mapStatus.put(KEY_LOCATION, null);
                    }

                    boolean isBusy = checkBoxBusy.isChecked();

                    mapStatus.put(KEY_BUSY, isBusy);

                    db.collection("rooms").document(roomName).collection("users").document(username).set(mapStatus, SetOptions.merge());
                    getActivity().finish();
                } else {
                    Toast.makeText(getContext(), "Select a feeling.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}