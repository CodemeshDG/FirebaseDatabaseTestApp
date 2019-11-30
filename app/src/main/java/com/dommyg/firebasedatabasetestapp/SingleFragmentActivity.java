package com.dommyg.firebasedatabasetestapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public abstract class SingleFragmentActivity extends AppCompatActivity {
    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        FragmentManager fragmentManager = getSupportFragmentManager();
        // Find fragment with container view ID of R.id.fragment_container and return it to fragment
        // (if it exists already and was just destroyed by rotation or memory reclamation).
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = createFragment();
            // Create a new fragment transaction, include one add operation in it, and then commit it.
            fragmentManager.beginTransaction()
                    // Passing the container view ID, which tells the FragmentManager where in the
                    // activity's view the fragment's view should appear, and serves as a unique ID
                    // for a fragment in the FragmentManager's list.
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
