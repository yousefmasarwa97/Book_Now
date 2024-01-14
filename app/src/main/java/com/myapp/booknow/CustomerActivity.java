package com.myapp.booknow;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import androidx.annotation.NonNull;

import java.util.concurrent.TimeUnit;


public class CustomerActivity extends AppCompatActivity {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);


        final EditText phoneEditText = findViewById(R.id.username);//text box for phone number
        Button sendCodeButton = findViewById(R.id.login);//send code button

        sendCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneEditText.getText().toString();
                if (isValidPhoneNumber(phoneNumber)) {
                    Intent intent = new Intent(CustomerActivity.this, OtpVerificationActivity.class);
                    intent.putExtra("phoneNumber", phoneNumber);
                    startActivity(intent);
                } else {
                    // Show error message: invalid phone number
                    Toast.makeText(CustomerActivity.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Add your phone number validation logic here (e.g., check length, format)
        if(phoneNumber.length()<10) return false;
        return phoneNumber != null && !phoneNumber.trim().isEmpty(); // Basic check

    }
}