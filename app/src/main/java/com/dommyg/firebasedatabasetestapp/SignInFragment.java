package com.dommyg.firebasedatabasetestapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class SignInFragment extends Fragment {
    private static final int RC_SIGN_IN = 100;

    public static SignInFragment newInstance() {
        return new SignInFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sign_in, container, false);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // The user is signed in; finish SignInActivity and start the MainMenuActivity.
            startActivity(MainMenuActivity.newIntent(getContext()));
            Objects.requireNonNull(getActivity()).finish();
        }
        else {
            // The user is not signed in; start AuthUI sign in process.
            startActivityForResult(AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .build(),
                    RC_SIGN_IN);
        }

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // resultCode indicates user has signed in; finish SignInActivity and start the
                // MainMenuActivity.
                startActivity(MainMenuActivity.newIntent(getContext()));
                Objects.requireNonNull(getActivity()).finish();
            } else {
                if (response == null) {
                    // No response from activity.
                    Toast.makeText(getContext(), "ERROR: Unknown error. Please try again.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                    // Activity response indicates no network connection.
                    Toast.makeText(getContext(), "ERROR: No network connection.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
