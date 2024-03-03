package com.myapp.booknow.mvvm.model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.myapp.booknow.mvvm.viewmodel.customer.C_Dashboard;
import com.myapp.booknow.R;

import java.util.concurrent.TimeUnit;

public class VerifyOTP extends AppCompatActivity {

    private String phoneNumber;

    private Long timeoutseconds = 60L;

    private String verificationCode;

    private TextView shownMessagePhoneNumber;
    private PhoneAuthProvider.ForceResendingToken resendingToken;


    private com.chaos.view.PinView pinView; // The pinView that takes the OTP numbers inserted one by one (by the user)


    private Button nextBtn;

    private ProgressBar progressBar;

    private TextView resendOtpTextView;

    private String entered_otp;

    ImageView exit; // Exit button (image) at the top to go back to the main page

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_verify_otp);


        mAuth = FirebaseAuth.getInstance();



        shownMessagePhoneNumber = findViewById(R.id.otp_phone_number_text_view);
        pinView = findViewById(R.id.pin_view_otp);
        nextBtn = findViewById(R.id.otp_verify_code_button);
        progressBar = findViewById(R.id.otp_progress_bar);
        resendOtpTextView = findViewById(R.id.resend_verification_code);
        exit = findViewById(R.id.exit_otp);

        phoneNumber = getIntent().getStringExtra("phoneNumber");

        shownMessagePhoneNumber.setText("Enter one time password sent to \n "+phoneNumber);



        setInProgress(true);///Should be deleted !


        sendOtp(phoneNumber,false);



        // When clicking "verify code" button :
        nextBtn.setOnClickListener(v -> {
            if(pinView.getText() != null){
                entered_otp = pinView.getText().toString();
            }
            if(entered_otp.length()!=6){
                Toast.makeText(getApplicationContext(),"You should enter 6 digits code, you entered "+entered_otp.length(), Toast.LENGTH_SHORT).show();
            }
            else{
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode,entered_otp);
            signIn(credential);
            setInProgress(true);
            }
        });


        // When clicking "resend OTP" :
        resendOtpTextView.setOnClickListener((v)->{
            sendOtp(phoneNumber,true);
        });

        // When clicking "exit"
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


    // Sends an OTP to the given phone number,, when calling from "resend" button, the second parameter is true, else it's false
    void sendOtp(String phoneNumber, boolean isResend){
        startResendTimer();// Starts a timer for "resend OTP"
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
            nextBtn.setVisibility(View.INVISIBLE);
            //
            //also added :
            pinView.setVisibility(View.INVISIBLE);
            resendOtpTextView.setVisibility(View.INVISIBLE);
            shownMessagePhoneNumber.setVisibility(View.INVISIBLE);
        }
        else{
            Log.d("ProgressBarStatus ::","should be Invisible now !!!");
            progressBar.setVisibility(View.INVISIBLE);
            nextBtn.setVisibility(View.VISIBLE);
            //
            //also added:
            pinView.setVisibility(View.VISIBLE);
            resendOtpTextView.setVisibility(View.VISIBLE);
            shownMessagePhoneNumber.setVisibility(View.VISIBLE);
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
                    dbHelper.addCustomer(userID,phoneNumber);//the function checks if the userID already exists (Bad approach, should change !!, the check should be here)

                    //redirect to the customer dashboard
                    //Intent intent = new Intent(CustomerOtpVerificationActivity.this, CustomerDashboardActivity.class);
                    Intent intent = new Intent(VerifyOTP.this, C_Dashboard.class);
                    intent.putExtra("phoneNumber", phoneNumber);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"OTP verification failed",Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }






    void startResendTimer() {
        resendOtpTextView.setEnabled(false); // user can't click on it
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                timeoutseconds--; // every second, decrease the timeout
                resendOtpTextView.setText("Resend OTP in " + timeoutseconds + " seconds");
                if (timeoutseconds <= 0) {
                    timeoutseconds = 60L;
                    handler.removeCallbacks(this);
                    resendOtpTextView.setEnabled(true);
                    resendOtpTextView.setText("Resend OTP");
                } else {
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.postDelayed(runnable, 1000);
    }



    //-------------------DIDNT WORK-----------------------------//
//    void startResendTimer(){
//        resendOtpTextView.setEnabled(false); // user cant click on it
//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                timeoutseconds--; // every second, decrease the time out
//                resendOtpTextView.setText("Resend OTP in "+ timeoutseconds + "seconds");
//                if(timeoutseconds<=0){
//                    timeoutseconds = 60L;
//                    timer.cancel();
//                   runOnUiThread(() ->{
//                       resendOtpTextView.setEnabled(true);
//                   });
//                }
//
//            }
//        },0,1000);
//    }

}