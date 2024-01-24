package com.myapp.booknow;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * Dashboard (Main page) for a customer user.
 */
public class BusinessDashboardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView textViewWelcome;

    private Button Woking_hours_redirect_btn;

    private Button Editing_services_redirect_btn;
    private Button Editing_providers_redirect_btn;

    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_dashboard);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        textViewWelcome = findViewById(R.id.textViewWelcome);
        Woking_hours_redirect_btn = findViewById(R.id.Woking_hours_redirecting);
        Editing_services_redirect_btn = findViewById(R.id.editing_services_redirecting);
        Editing_providers_redirect_btn = findViewById(R.id.editing_providers_redirecting);

//        if (currentUser != null && currentUser.getEmail() != null){
//            String welcomemsg = "Hello, " + currentUser.getEmail();
//            textViewWelcome.setText(welcomemsg);
//        }
//        else{
//            textViewWelcome.setText("Hello, User");
//        }


        Woking_hours_redirect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusinessDashboardActivity.this,BusinessWorkingHoursActivity_2.class);
                startActivity(intent);
            }
        });

        Editing_services_redirect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusinessDashboardActivity.this,BusinessServicesManagementActivity.class);
                startActivity(intent);
            }
        });


        Editing_providers_redirect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusinessDashboardActivity.this,ServiceProviderSetNameAndServicesActivity.class);
                startActivity(intent);
            }
        });

    }
}
