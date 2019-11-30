package com.dommyg.firebasedatabasetestapp;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

public class JoinRoomActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return JoinRoomFragment.newInstance();
    }

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, JoinRoomActivity.class);
    }
}
