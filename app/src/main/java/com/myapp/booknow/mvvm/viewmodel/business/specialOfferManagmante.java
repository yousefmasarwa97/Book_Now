package com.myapp.booknow.mvvm.viewmodel.business;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

//import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.myapp.booknow.R;
import com.myapp.booknow.mvvm.model.BusinessSpecialOffers;
import com.myapp.booknow.mvvm.model.DBHelper;

import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class specialOfferManagmante extends AppCompatActivity {


    private EditText offername, offerDescriptionEditText, offerDurationEditText;
    private TextView tvDay;
    boolean[] selectedDay;
    private ImageView offerImageView;
    private String imageUrl;
    ArrayList<Integer> dayList = new ArrayList<>();
    private Button editoffer;
    //    FirebaseAuth fAuth;
    private ImageView backButton;
    String[] dayArray = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String OFFER_IMAGES_PATH = "business_images/";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_special_offer_managmante);

        //fAuth = FirebaseAuth.getInstance();


        offername = findViewById(R.id.offerNameEditText);
        offerDescriptionEditText = findViewById(R.id.editofferdescription);
        offerDurationEditText = findViewById(R.id.special_offers_duration);
        editoffer = findViewById(R.id.addoffer);
        backButton = findViewById(R.id.offer_back_icon);
        offerImageView=findViewById(R.id.offerImageView);
        // tvDay=findViewById(R.id.selectDay);
        TextView tvAddPhoto = findViewById(R.id.add_photo_text);

        tvAddPhoto.setClickable(true);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // init the selected daya array
        selectedDay = new boolean[dayArray.length];

//        tvDay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //init alert dialog
//                AlertDialog.Builder builder = new AlertDialog.Builder(specialOfferManagmante.this);
//
//                //set title
//                builder.setTitle("Select Day");
//
//                //set dialog non cancelable
//                builder.setCancelable(false);
//
//                builder.setMultiChoiceItems(dayArray, selectedDay, new DialogInterface.OnMultiChoiceClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialoginterface, int i, boolean b) {
//                        if (b) {//when checkbox selected
//                            dayList.add(i);
//                            Collections.sort(dayList);
//                        } else {//when checkbox unselected
//                            dayList.remove(i);
//                        }
//
//                    }
//                });
//
//                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        StringBuilder stringBuilder = new StringBuilder();
//                        for (int j = 0; j < dayList.size(); j++) {
//                            //concat array value
//                            stringBuilder.append(dayArray[dayList.get(j)]);
//
//                            if (j != dayList.size() - 1) {
//                                //add comma
//                                stringBuilder.append(", ");
//                            }
//                        }
//                        //set text on text view
//                        tvDay.setText(stringBuilder.toString());
//                    }
//                });
//
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        //dismiss dialog
//                        dialogInterface.dismiss();
//                    }
//                });
//
//                builder.setNeutralButton("Clear all", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        for (int j = 0; j < selectedDay.length; j++) {
//                            selectedDay[j] = false;
//                            dayList.clear();
//                            tvDay.setText("");
//                        }
//                    }
//                });
//
//                //show dialog
//                builder.show();
//            }
//        });

        editoffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser offer = FirebaseAuth.getInstance().getCurrentUser();
                BusinessSpecialOffers newoffer = new BusinessSpecialOffers();
                if (offer != null) {
                    String offerID = offer.getUid();
                    //creating a new User object

                    newoffer.setBusinessId(offerID);
                    newoffer.setofferId(offerID);
                    newoffer.setName(offername.getText().toString());
                    newoffer.setDescription(offerDescriptionEditText.getText().toString());
                    newoffer.setDuration(Integer.parseInt(offerDurationEditText.getText().toString()));
                    newoffer.setImageURL(imageUrl);
                }
                addoffertofirebase(newoffer);
                updateOfferInfo();
                finish();
            }

        });


        tvAddPhoto.setOnClickListener(view -> {
            // Open gallery to select an image
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get selected image URI
            Uri imageUri = data.getData();
            // Set selected image to ImageView
            offerImageView.setImageURI(imageUri);
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
        StorageReference imageRef = storageRef.child(OFFER_IMAGES_PATH + imageName);

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


    private void updateOfferInfo() {
        String name = offername.getText().toString().trim();
        String description = offerDescriptionEditText.getText().toString().trim();
        String durationString = offerDurationEditText.getText().toString().trim();


        // Convert dayList (ArrayList<Integer>) to List<String>
        List<String> selectedDays = new ArrayList<>();
        for (Integer dayIndex : dayList) {
            selectedDays.add(dayArray[dayIndex]);
        }


        if (name.isEmpty() || durationString.isEmpty()) {
            Toast.makeText(this, "Name and duration are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        int duration = Integer.parseInt(durationString);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser curr_user = mAuth.getCurrentUser();
        String businessId = null;
        if (curr_user != null) {
            businessId = curr_user.getUid();
        }
        String offerID = getIntent().getStringExtra("offerID");
        Map<String, Object> offerData = new HashMap<>();
        offerData.put("name", name);
        offerData.put("description", description);
        Log.d("imageURL", "" + imageUrl);//for testing (checking if the url is null)
        offerData.put("imageURL", imageUrl); // Set the image URL
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("BusinessOFFERS").document(offerID)
                .update(offerData)
                .addOnSuccessListener(aVoid -> {
                    // Set setupCompleted to true
                    db.collection("BusinessOFFERS").document(offerID)
                            .update("setupCompleted", true)
                            .addOnSuccessListener(aVoid1 -> {
                                // Redirect to dashboard
                                Intent intent = new Intent(specialOfferManagmante.this, BusinessDashboardActivity.class);
                                startActivity(intent);
                                finish();
                            });
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });


        //Should get the service ID from the previous activity ('putExtra' .. from BusinessServiceManagementActivity)
        BusinessSpecialOffers newoffer = new BusinessSpecialOffers(offerID, businessId, name, description, duration);
        newoffer.setWorkingDays(selectedDays);//add the list of available days to the service object
       // updateFirestoreDocument(newoffer);

        ///get back to the previous page, handled in the Onclick function for the button "change"

    }


    private void updateFirestoreDocument(BusinessSpecialOffers offer) {
        DBHelper dbHelper = new DBHelper();

        dbHelper.updateSpecialOffers(offer,
                aVoid -> {
                    // Success handling. Perhaps refresh the list of services
                    Toast.makeText(specialOfferManagmante.this, "Service edited successfully", Toast.LENGTH_SHORT).show();
                },
                e -> {
                    // Failure handling
                    Toast.makeText(specialOfferManagmante.this, "Failed to edit service info", Toast.LENGTH_SHORT).show();
                });
    }

    private void addoffertofirebase(BusinessSpecialOffers offer) {
        DBHelper dbHelper = new DBHelper();
        dbHelper.addBusinessoffer(offer,
                aVoid -> {
                    // Success handling. Perhaps refresh the list of services
                    Toast.makeText(specialOfferManagmante.this, "Service edited successfully", Toast.LENGTH_SHORT).show();
                },
                e -> {
                    // Failure handling
                    Toast.makeText(specialOfferManagmante.this, "Failed to edit service info", Toast.LENGTH_SHORT).show();
                });

    }

}

