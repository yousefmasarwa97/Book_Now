package com.myapp.booknow;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Represents the Login page for a customer user.
 */
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
                    Intent intent = new Intent(CustomerActivity.this, CustomerOtpVerificationActivity.class);
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