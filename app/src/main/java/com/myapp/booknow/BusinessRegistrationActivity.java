package com.myapp.booknow;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Handles the process of signing up new businesses.
 */
public class BusinessRegistrationActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;

    private Button buttonRegister;

    private TextView textViewGoBackToLogin;

    private ProgressBar progressBar;

    FirebaseAuth fAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_registration);

        // Initialize the EditText fields
          editTextEmail = findViewById(R.id.editTextEmail);
          editTextPassword = findViewById(R.id.editTextPassword);
          editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
          buttonRegister = findViewById(R.id.buttonRegister);

          textViewGoBackToLogin = findViewById(R.id.textViewGoBackToLogin);

        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar2);

//        if(fAuth.getCurrentUser() != null){
//            startActivity(new Intent(getApplicationContext(),BusinessDashboardActivity.class));
//            finish();
//        }

          textViewGoBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to BusinessLoginActivity
//                Intent intent = new Intent(BusinessRegistrationActivity.this, BusinessActivity.class);
//                startActivity(intent);
                finish();// if clicked "back to login", finish this activity.
            }
        });



        buttonRegister.setOnClickListener(new View.OnClickListener() {
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
                
                
                // Registering the user in firebase
                
                fAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if(task.isSuccessful()){
                                    Toast.makeText(BusinessRegistrationActivity.this, "User created.", Toast.LENGTH_SHORT).show();
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
                                                            Toast.makeText(BusinessRegistrationActivity.this, "Verification link sent to email : " + email, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                    //redirect to waiting page (until user clicks on verification link sent by email!)
                                    Intent intent = new Intent(BusinessRegistrationActivity.this, BusinessEmailVerificationActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(BusinessRegistrationActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });



            }
        });
    }
}
