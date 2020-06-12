package com.stoken.stoken.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.stoken.stoken.R;
import com.stoken.stoken.utils.Tools;

public class SplashActivity extends AppCompatActivity {

    int splashTimeOut = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Tools.setSystemBarColor(this, R.color.orange_400);

        // loggedin user data
        SharedPreferences prefs = getSharedPreferences("FCM_PREF", MODE_PRIVATE);
        final String userID = prefs.getString("userID", null);
        final String name = prefs.getString("name", null);

        // check logged in status
        if (userID == null || userID.isEmpty()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashActivity.this, SignInActivity.class);
                    startActivity(i);
                    finish();
                }
            }, splashTimeOut);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }, splashTimeOut);
        }
    }
}
