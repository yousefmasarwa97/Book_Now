package com.myapp.booknow.mvvm.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.myapp.booknow.mvvm.viewmodel.customer.C_Login;
import com.myapp.booknow.R;

/**
 * The very first screen in the app, appears for less than a second.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, C_Login.class);
                startActivity(intent);
                finish();
            }
        }, 800); //  = > 0.5 seconds
    }
}
