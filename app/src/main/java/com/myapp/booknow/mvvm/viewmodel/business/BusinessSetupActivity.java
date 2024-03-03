package com.myapp.booknow.mvvm.viewmodel.business;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.myapp.booknow.R;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * Responsible for handling the business setup page (first page after registration).
 */
public class BusinessSetupActivity extends AppCompatActivity {

    private EditText businessNameEditText;
    private EditText businessDescriptionEditText;
    // Other UI elements for services, working hours, etc.

    private String userId; // User ID of the business


    private ImageView businessImageView;
    private ImageView backButton;

    private TextView tvAddPhoto;

    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_business_setup);

        businessNameEditText = findViewById(R.id.businessName);
        businessDescriptionEditText = findViewById(R.id.businessDescription);
        // Initialize other UI elements

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        businessImageView = findViewById(R.id.businessImageView);
        tvAddPhoto = findViewById(R.id.add_photo_text);
        backButton=findViewById(R.id.setup_back_icon);

        tvAddPhoto.setClickable(true);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvAddPhoto.setOnClickListener(view -> {
            // Open gallery to select an image
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });


        Button submitButton = findViewById(R.id.submitBusinessInfoButton);
        submitButton.setOnClickListener(view -> submitBusinessInfo());
    }

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String BUSINESS_IMAGES_PATH = "business_images/";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get selected image URI
            Uri imageUri = data.getData();
            // Set selected image to ImageView
            businessImageView.setImageURI(imageUri);
            // Upload image to Firebase Storage
            uploadImageToStorage(imageUri);
        }
    }

    private void uploadImageToStorage(Uri imageUri) {
        // Get a reference to the Firebase Storage location
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        // Generate a unique name for the image
        String imageName = UUID.randomUUID().toString();

        // Create a reference to the image file in Firebase Storage
        StorageReference imageRef = storageRef.child(BUSINESS_IMAGES_PATH + imageName);

        // Upload image to Firebase Storage
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully
                    // Get the download URL of the uploaded image
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        imageUrl = uri.toString();
                        // Update the business data with the imageURL
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle image upload failure
                });
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
        Log.d("imageURL",""+imageUrl);//for testing (checking if the url is null)
        businessData.put("imageURL", imageUrl); // Set the image URL
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
