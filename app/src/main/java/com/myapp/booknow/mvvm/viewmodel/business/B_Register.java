package com.myapp.booknow.mvvm.viewmodel.business;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.myapp.booknow.R;
import com.myapp.booknow.mvvm.model.DBHelper;
import com.myapp.booknow.mvvm.model.User;

public class B_Register extends AppCompatActivity {


    private com.google.android.material.textfield.TextInputEditText editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button registerButton;

    private ImageView backToLogin;

    private ProgressBar progressBar;

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_business_register);


        editTextEmail = findViewById(R.id.business_register_email_edit_text);
        editTextPassword = findViewById(R.id.business_register_password_edit_text) ;
        editTextConfirmPassword = findViewById(R.id.password_confirmation_text);
        registerButton = findViewById(R.id.register_business_button);
        backToLogin = findViewById(R.id.business_register_back_button);
        progressBar = findViewById(R.id.register_business_progress_bar);

        fAuth = FirebaseAuth.getInstance();


        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();// if clicked "back", finish this activity.
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String password_confirm = editTextConfirmPassword.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    editTextEmail.setError("Email is required.");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    editTextPassword.setError("Password is required.");
                    return;
                }
                if(TextUtils.isEmpty(password_confirm)){
                    editTextConfirmPassword.setError("Password confirmation is required.");
                    return;
                }
                if(password.length() < 6){
                    editTextPassword.setError("Password should be >= 6 characters.");
                    return;
                }
                if( ! password_confirm.equals(password) ){
                    editTextConfirmPassword.setError("The passwords are different.");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // for testing:
                Log.d("CheckingNull","email is : " + email + " and the password is : " + password +
                        " and the confirmed password is : " + password_confirm);


                // Registering the user in firebase
                fAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if(task.isSuccessful()){
                                    Toast.makeText(B_Register.this, "User created.", Toast.LENGTH_SHORT).show();
                                    //startActivity(new Intent(getApplicationContext(),BusinessDashboardActivity.class));
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                    if (user != null) {
                                        String userID = user.getUid();
                                        //creating a new User object
                                        User newUser = new User();
                                        newUser.setId(userID);
                                        newUser.setEmail(user.getEmail());
                                        newUser.setType("Business");
                                        newUser.setName("");//the names is not available yet (didn't setup the business)

                                        DBHelper dbHelper = new DBHelper();
                                        dbHelper.addBusiness(newUser);

                                        user.sendEmailVerification()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            // Email sent, inform the user
                                                            Toast.makeText(B_Register.this, "Verification link sent to email : " + email, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                    // Redirect to waiting page (until user clicks on verification link sent by email!)
                                    Intent intent = new Intent(B_Register.this, BusinessEmailVerificationActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(B_Register.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });



            }
        });

    }
}