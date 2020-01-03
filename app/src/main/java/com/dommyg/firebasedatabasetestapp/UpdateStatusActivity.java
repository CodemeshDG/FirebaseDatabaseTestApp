package com.dommyg.firebasedatabasetestapp;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

/**
 * Provides the interface for updating the user's status.
 */
public class UpdateStatusActivity extends SingleFragmentActivity {
    private static final String KEY_ROOM_NAME = "room_name";
    private static final String KEY_USERNAME = "username";

    @Override
    protected Fragment createFragment() {
        String roomName = getIntent().getStringExtra(KEY_ROOM_NAME);
        String username = getIntent().getStringExtra(KEY_USERNAME);
        return UpdateStatusFragment.newInstance(roomName, username);
    }

    public static Intent newIntent(Context packageContext, String roomName, String username) {
        Intent intent = new Intent(packageContext, UpdateStatusActivity.class);
        intent.putExtra(KEY_ROOM_NAME, roomName);
        intent.putExtra(KEY_USERNAME, username);
        return intent;
    }
}