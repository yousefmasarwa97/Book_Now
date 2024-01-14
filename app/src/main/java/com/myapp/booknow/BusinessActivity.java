package com.myapp.booknow;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BusinessActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button signInButton;
    private TextView registerTextView;

    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //UI elements:
        editTextEmail = findViewById(R.id.email_text_box);
        editTextPassword = findViewById(R.id.password);
        signInButton = findViewById(R.id.Sign_in); //
        registerTextView = findViewById(R.id.Register);
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusinessActivity.this,BusinessRegistrationActivity.class);
                startActivity(intent);
            }
        });

        signInButton.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(BusinessActivity.this, "Email and Password cannot be empty.", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(BusinessActivity.this, "Please verify your email.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // If sign in fails, display a message to the user
                            Toast.makeText(BusinessActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

    }
}