package com.dommyg.firebasedatabasetestapp;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

/**
 * Provides the interface for creating a new room.
 */
public class CreateRoomActivity extends SingleFragmentActivity {
    private static final String KEY_USERNAME = "username";

    @Override
    protected Fragment createFragment() {
        String username = getIntent().getStringExtra(KEY_USERNAME);
        return CreateRoomFragment.newInstance(username);
    }

    public static Intent newIntent(Context packageContext, String username) {
        Intent intent = new Intent(packageContext, CreateRoomActivity.class);
        intent.putExtra(KEY_USERNAME, username);
        return intent;
    }
}
