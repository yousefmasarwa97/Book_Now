package com.myapp.booknow;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


/**
 * Responsible for handling the business setup page (first page after registration).
 */
public class BusinessSetupActivity extends AppCompatActivity {

    private EditText businessNameEditText;
    private EditText businessDescriptionEditText;
    // Other UI elements for services, working hours, etc.

    private String userId; // User ID of the business

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_setup);

        businessNameEditText = findViewById(R.id.businessName);
        businessDescriptionEditText = findViewById(R.id.businessDescription);
        // Initialize other UI elements

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Button submitButton = findViewById(R.id.submitBusinessInfoButton);
        submitButton.setOnClickListener(view -> submitBusinessInfo());
    }

    private void submitBusinessInfo() {
        String name = businessNameEditText.getText().toString();
        String description = businessDescriptionEditText.getText().toString();
        // Get values of other fields

        if (TextUtils.isEmpty(name)) {
            businessNameEditText.setError("Name is required");
            return;
        }

        // Construct a business object or a Map to update Firestore
        Map<String, Object> businessData = new HashMap<>();
        businessData.put("name", name);
        businessData.put("description", description);
        // Add other fields

        // Update Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(userId)
                .update(businessData)
                .addOnSuccessListener(aVoid -> {
                    // Set setupCompleted to true
                    db.collection("Users").document(userId)
                            .update("setupCompleted", true)
                            .addOnSuccessListener(aVoid1 -> {
                                // Redirect to dashboard
                                Intent intent = new Intent(BusinessSetupActivity.this, BusinessDashboardActivity.class);
                                startActivity(intent);
                                finish();
                            });
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }
}
