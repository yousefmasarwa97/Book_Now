package com.myapp.booknow.mvvm.viewmodel.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import com.myapp.booknow.R;
import com.myapp.booknow.mvvm.viewmodel.business.B_Login;
import com.myapp.booknow.mvvm.model.VerifyOTP;

public class C_Login extends AppCompatActivity {


    private  com.hbb20.CountryCodePicker countryCodePicker;
    private com.google.android.material.textfield.TextInputEditText phoneEditText;
    private Button sendCodeButton,goToBusinessLoginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.c_login);

        ///----test----////
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = "";
        if (currentUser != null) {
            // User is signed in
            uid = currentUser.getUid();
        }
        Log.d("loggedUserCheck","the logged in user is ::::: -> " +uid);

        countryCodePicker = findViewById(R.id.countryCodeHolder);
        phoneEditText = findViewById(R.id.phone_edit_text);
        sendCodeButton = findViewById(R.id.login_business_button);
        goToBusinessLoginButton = findViewById(R.id.login_as_business);



        // When clicking on "CONTINUE" :
        sendCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String plusSign = "+";
                String country_code = countryCodePicker.getDefaultCountryCode();
                Log.d("PHONE NUMBER CHECK !!","The Country code picker is (in string) :: " + country_code);// For checking the country code
                String typed_phone_number = phoneEditText.getText().toString();
                Log.d("PHONE NUMBER CHECK !!","The tyed phone number (without country code) is (in string) :: " + typed_phone_number);// For checking the typed number by the user
                String phoneNumber = plusSign + country_code + typed_phone_number;
                Log.d("PHONE NUMBER CHECK !!","The full phone number (including country code) is (in string) :: " + phoneNumber);// For checking the full number

                // Checking if the number is valid -> go to OTP verification page
                if (isValidPhoneNumber(phoneNumber)) {
                    Log.d("CHECK isValidPhoneNumber","is the number valid ? :: "+isValidPhoneNumber(phoneNumber));
                    Intent intent = new Intent(C_Login.this, VerifyOTP.class);// Go to the OTP verification to let the user insert the sent code
                    intent.putExtra("phoneNumber", phoneNumber); // Get the phone number as Extra ,, to send OTP to it
                    startActivity(intent);
                }
                else {// else (if the number is invalid) :
                    // Show error message: invalid phone number
                    Toast.makeText(C_Login.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // When clicking on "LOGIN AS A BUSINESS" :
        goToBusinessLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(C_Login.this, B_Login.class);// Go to the business login page
                startActivity(intent);
            }
        });
    }




    // Used to check if the given string is a valid phone number,,, uses phonenumbers library
    // Inside your class
    private boolean isValidPhoneNumber(String phoneNumber) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            return phoneNumberUtil.isValidNumber(phoneNumberUtil.parse(phoneNumber, null));
        } catch (NumberParseException e) {
            e.printStackTrace();
            return false; // Return false if an exception occurs during parsing
        }
    }


}