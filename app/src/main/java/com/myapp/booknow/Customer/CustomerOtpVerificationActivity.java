package com.myapp.booknow.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.myapp.booknow.Utils.DBHelper;
import com.myapp.booknow.R;

import java.util.concurrent.TimeUnit;

public class CustomerOtpVerificationActivity extends AppCompatActivity {

    private String phoneNumber;
    Long timeoutseconds = 60L;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    EditText otpInput;
    Button nextBtn;
    ProgressBar progressBar;
    TextView resendOtpTextView;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        otpInput = findViewById(R.id.otp);
        nextBtn = findViewById(R.id.next);
        progressBar = findViewById(R.id.OtpProgressBar);
        resendOtpTextView = findViewById(R.id.textView);

        // Retrieve the phone number from the intent
        phoneNumber = getIntent().getStringExtra("phoneNumber");

        setInProgress(true);///Should be deleted !


        sendOtp(phoneNumber,false);

        nextBtn.setOnClickListener(v -> {
            String enteredOtp = otpInput.getText().toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode,enteredOtp);
            signIn(credential);
            setInProgress(true);
        });
    }



    void sendOtp(String phoneNumber, boolean isResend){
        setInProgress(true);
        PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(timeoutseconds, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signIn(phoneAuthCredential);
                        setInProgress(false);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                            Toast.makeText(getApplicationContext(),"OTP verification failed",Toast.LENGTH_LONG)
                                    .show();
                            setInProgress(false);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationCode = s;
                        resendingToken = forceResendingToken;
                        Toast.makeText(getApplicationContext(),"OTP sent successfully",Toast.LENGTH_LONG)
                                .show();
                        setInProgress(false);
                    }
                });

        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        }else{
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    void setInProgress(boolean inProgress){
        if(inProgress){
            Log.d("ProgressBarStatus ::","should be visible now !!!");
            progressBar.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.GONE);
            //
            //also added :
            otpInput.setVisibility(View.GONE);
            resendOtpTextView.setVisibility(View.GONE);
        }
        else{
            Log.d("ProgressBarStatus ::","should be Invisible now !!!");
            progressBar.setVisibility(View.INVISIBLE);
            nextBtn.setVisibility(View.VISIBLE);
            //
            //also added:
            otpInput.setVisibility(View.VISIBLE);
            resendOtpTextView.setVisibility(View.VISIBLE);
        }
    }

    void signIn(PhoneAuthCredential pac){
        //going to next activity
        setInProgress(true);
        mAuth.signInWithCredential(pac).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    //get the user ID from FirebaseAuth
                    String userID = mAuth.getCurrentUser().getUid();

                    //Add the customer to the database
                    DBHelper dbHelper = new DBHelper();
                    dbHelper.addCustomer(userID,phoneNumber);

                    //redirect to the customer dashboard
                    Intent intent = new Intent(CustomerOtpVerificationActivity.this, CustomerDashboardActivity.class);
                    intent.putExtra("phoneNumber", phoneNumber);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"OTP verification failed",Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

}
