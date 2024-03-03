package com.myapp.booknow.mvvm.viewmodel.business;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.myapp.booknow.R;

public class B_Login extends AppCompatActivity {

    private com.google.android.material.textfield.TextInputEditText editTextEmail, editTextPassword;
    private Button signInButton,forgetPasswordButton;
    ImageView backButton;
    private TextView registerTextView;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_business_login);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //UI elements:
        editTextEmail = findViewById(R.id.email_edit_text);
        editTextPassword = findViewById(R.id.password_edit_text);
        signInButton = findViewById(R.id.login_business_button);
        forgetPasswordButton = findViewById(R.id.forget_pass_button);
        registerTextView = findViewById(R.id.create_new_business_account);
        backButton = findViewById(R.id.business_login_back_button);


        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(B_Login.this, B_Register.class);
                startActivity(intent);
            }
        });


        signInButton.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(B_Login.this, "Email and Password cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null && user.isEmailVerified()) {
                                // Redirect to Business Dashboard
                                startActivity(new Intent(getApplicationContext(), BusinessDashboardActivity.class));
                                finish();
                            } else {
                                Toast.makeText(B_Login.this, "Please verify your email.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // If sign in fails, display a message to the user
                            Toast.makeText(B_Login.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}