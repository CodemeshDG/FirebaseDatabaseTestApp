package com.dommyg.firebasedatabasetestapp;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

public class MainPanelActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return MainPanelFragment.newInstance();
    }

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, MainPanelActivity.class);
    }
}
