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
import java.util.Objects;

public class UpdateStatusFragment extends Fragment {
    static final String KEY_FEELING = "feeling";
    static final String KEY_LOCATION = "location";
    static final String KEY_BUSY = "is_busy";

    private int selectedFeeling;
    private String roomName;
    private String username;

    public static UpdateStatusFragment newInstance(String roomName, String username) {
        return new UpdateStatusFragment(roomName, username);
    }

    private UpdateStatusFragment(String roomName, String username) {
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
                switch (radioGroupFeeling.getCheckedRadioButtonId()) {
                    case R.id.radioButtonHappy:
                        selectedFeeling = 1;
                        break;
                    case R.id.radioButtonIndifferent:
                        selectedFeeling = 2;
                        break;
                    case R.id.radioButtonSad:
                        selectedFeeling = 3;
                        break;
                }
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

                    String selectedFeelingString = Integer.toString(selectedFeeling);

                    Map<String, Object> mapStatus = new HashMap<>();
                    mapStatus.put(KEY_FEELING, selectedFeelingString);

                    if (editTextLocation.length() != 0) {
                        String location = editTextLocation.getText().toString();
                        mapStatus.put(KEY_LOCATION, location);
                    } else {
                        mapStatus.put(KEY_LOCATION, null);
                    }

                    boolean isBusy = checkBoxBusy.isChecked();

                    mapStatus.put(KEY_BUSY, isBusy);

                    db.collection("rooms").document(roomName).collection("users").document(username).set(mapStatus, SetOptions.merge());
                    Objects.requireNonNull(getActivity()).finish();
                } else {
                    Toast.makeText(getContext(), "Select a feeling.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}