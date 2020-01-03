package com.dommyg.firebasedatabasetestapp;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

/**
 * Once signed in, the user will see this activity's fragment. If the user does not have a username,
 * it will help them set one. Once with a username, a user can create or join a room from here.
 */
public class MainMenuActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return MainMenuFragment.newInstance();
    }

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, MainMenuActivity.class);
    }
}
