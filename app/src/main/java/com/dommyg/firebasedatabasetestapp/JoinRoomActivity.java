package com.dommyg.firebasedatabasetestapp;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

public class JoinRoomActivity extends SingleFragmentActivity {
    private static final String KEY_USERNAME = "username";

    @Override
    protected Fragment createFragment() {
        String username = getIntent().getStringExtra(KEY_USERNAME);
        return JoinRoomFragment.newInstance(username);
    }

    public static Intent newIntent(Context packageContext, String username) {
        Intent intent = new Intent(packageContext, JoinRoomActivity.class);
        intent.putExtra(username, KEY_USERNAME);
        return intent;
    }
}
