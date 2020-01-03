package com.dommyg.firebasedatabasetestapp;

import androidx.fragment.app.Fragment;

/**
 * This is the first activity created when the app opens. It's fragment is in charge of checking if
 * the user is signed in, and if not, signing them in.
 */
public class SignInActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return SignInFragment.newInstance();
    }
}
