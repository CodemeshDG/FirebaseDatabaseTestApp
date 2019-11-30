package com.dommyg.firebasedatabasetestapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class UpdateStatusActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return UpdateStatusFragment.newInstance();
    }
}

class UpdateStatusFragment extends Fragment {

    public static UpdateStatusFragment newInstance() {
        return new UpdateStatusFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_update_status, container, false);

        return v;
    }
}