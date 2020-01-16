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

public class UpdateStatusFragment extends Fragment {

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_update_status, container, false);

        setUpElements(v);

        return v;
    }

    /**
     * Sets up all the elements for updating the user's status.
     */
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
        buttonSubmitStatus.setOnClickListener(new SubmitOnClickListener(this,
                editTextLocation, checkBoxBusy));

    }

    /**
     * Listens for the submit status button to be pressed and passes the values to be updated.
     */
    private class SubmitOnClickListener implements View.OnClickListener {
        private final UpdateStatusFragment updateStatusFragment;
        private final EditText editTextLocation;
        private final CheckBox checkBoxBusy;

        SubmitOnClickListener(UpdateStatusFragment updateStatusFragment,
                                     EditText editTextLocation, CheckBox checkBoxBusy) {
            this.updateStatusFragment = updateStatusFragment;
            this.editTextLocation = editTextLocation;
            this.checkBoxBusy = checkBoxBusy;
        }

        @Override
        public void onClick(View view) {
            if (selectedFeeling != 0) {
                // User selected a feeling and all data is being saved to the user's document.
                String selectedFeelingString = Integer.toString(selectedFeeling);
                String location = editTextLocation.getText().toString();
                boolean isBusy = checkBoxBusy.isChecked();

                new RoomController(updateStatusFragment, username, roomName,
                        getContext()).updateStatus(selectedFeelingString, location, isBusy);
            } else {
                // User did not select a feeling.
                Toast.makeText(getContext(), "Select a feeling.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}