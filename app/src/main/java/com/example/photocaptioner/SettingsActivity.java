package com.example.photocaptioner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.TwitterException;

import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class SettingsActivity extends AppCompatActivity {

    TwitterLoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        loginButton = findViewById(R.id.login_button);

        TextView accountText = findViewById(R.id.loginText);

        // Hide twitter login button if user already logged in
        if (TwitterCore.getInstance().getSessionManager()
                .getActiveSession() != null) {

            loginButton.setVisibility(View.GONE);
            accountText.setVisibility(View.VISIBLE);

            TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();

            String userName = session.getUserName();
            accountText.setText("Logged in as: " + userName);

        } else {

            loginButton.setCallback(new Callback<TwitterSession>() {

                @Override
                public void success(Result<TwitterSession> result) {
                    // Do something with result, which provides a TwitterSession for making API calls
                }

                @Override
                public void failure(TwitterException exception) {
                    // Do something on failure
                }

            });

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    /** logs user out of their current twitter session */
    public void logOutTwitterAccount(View v) {

        // check if there is an active session
        if (TwitterCore.getInstance().getSessionManager()
                .getActiveSession() != null) {

            // clears the active session
            TwitterCore.getInstance().getSessionManager().clearActiveSession();

        }

    }



}
