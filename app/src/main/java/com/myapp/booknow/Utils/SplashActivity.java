package com.myapp.booknow.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.myapp.booknow.MainActivity;
import com.myapp.booknow.R;

/**
 * The very first screen in the app, appears for less than a second.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 800); //  = > 0.5 seconds
    }
}
