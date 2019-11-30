package com.dommyg.firebasedatabasetestapp;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

public class CreateRoomActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return CreateRoomFragment.newInstance();
    }

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, CreateRoomActivity.class);
    }
}
