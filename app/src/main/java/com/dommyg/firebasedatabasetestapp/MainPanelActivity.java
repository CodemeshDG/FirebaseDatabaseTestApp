package com.dommyg.firebasedatabasetestapp;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

/**
 * Provides the interface for reading all room users' statuses and updating ones own.
 */
public class MainPanelActivity extends SingleFragmentActivity {
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROOM_NAME = "roomName";
    static final String KEY_PASSWORD = "password";

    @Override
    protected Fragment createFragment() {
        String username = getIntent().getStringExtra(KEY_USERNAME);
        String roomName = getIntent().getStringExtra(KEY_ROOM_NAME);
        return MainPanelFragment.newInstance(roomName, username);
    }

    public static Intent newIntent(Context packageContext, String username, String roomName) {
        Intent intent = new Intent(packageContext, MainPanelActivity.class);
        intent.putExtra(KEY_USERNAME, username);
        intent.putExtra(KEY_ROOM_NAME, roomName);
        return intent;
    }
}
