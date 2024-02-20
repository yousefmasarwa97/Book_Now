package com.myapp.booknow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.myapp.booknow.Customer.C_Login;
import com.myapp.booknow.Customer.CustomerActivity;
import com.myapp.booknow.business.B_Login;
import com.myapp.booknow.business.BusinessActivity;

/**
 * The main screen of the app.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button customerButton = findViewById(R.id.customer_button);
        Button businessButton = findViewById(R.id.business_button);

        customerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, C_Login.class);
                startActivity(intent);
            }
        });

        businessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, B_Login.class);
                startActivity(intent);
            }
        });
    }
}