package com.myapp.booknow;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class BusinessDashboardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView textViewWelcome;



    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_dashboard);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        textViewWelcome = findViewById(R.id.textViewWelcome);


        if (currentUser != null && currentUser.getEmail() != null){
            String welcomemsg = "Hello, " + currentUser.getEmail();
            textViewWelcome.setText(welcomemsg);
        }
        else{
            textViewWelcome.setText("Hello, User");
        }
    }
}
