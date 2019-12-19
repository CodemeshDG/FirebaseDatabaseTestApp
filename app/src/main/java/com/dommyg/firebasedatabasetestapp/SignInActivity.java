package com.dommyg.firebasedatabasetestapp;

import androidx.fragment.app.Fragment;

public class SignInActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return SignInFragment.newInstance();
    }
}
