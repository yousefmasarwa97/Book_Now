package com.myapp.booknow.business;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.myapp.booknow.R;
import com.myapp.booknow.Utils.User;

import java.util.ArrayList;

public class EditSpecialOffers extends AppCompatActivity {

    private EditText offername, offerDescriptionEditText, offerDurationEditText;
    private TextView tvDay;
    boolean[] selectedDay;
    ArrayList<Integer> dayList = new ArrayList<>();
    String[] dayArray = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    private Button saveEditButton;

    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_offer);


        offername = findViewById(R.id.offerNameEditText);
        offerDescriptionEditText = findViewById(R.id.editofferdescription);
        offerDurationEditText = findViewById(R.id.special_offers_duration);
        saveEditButton = findViewById(R.id.addoffer);
//        tvDay=findViewById(R.id.selectDay);


        //init the selected daya array
        //  selectedDay = new boolean[dayArray.length];
//
//        tvDay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //init alert dialog
//                AlertDialog.Builder builder = new AlertDialog.Builder(EditSpecialOffers.this);
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

//        saveEditButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                updateOfferInfo();
//                finish();
//            }
//        });
    }

    private String updateOfferInfo() {
        String name = offername.getText().toString().trim();
        String description = offerDescriptionEditText.getText().toString().trim();
        String durationString = offerDurationEditText.getText().toString().trim();


        // Convert dayList (ArrayList<Integer>) to List<String>
//        List<String> selectedDays = new ArrayList<>();
//        for (Integer dayIndex : dayList) {
//            selectedDays.add(dayArray[dayIndex]);
//        }


//        if(name.isEmpty() || durationString.isEmpty()) {
//            Toast.makeText(this, "Name and duration are required.", Toast.LENGTH_SHORT).show();
//            return;
//        }

        int duration = Integer.parseInt(durationString);

        FirebaseAuth mAuth = null;



        mAuth = FirebaseAuth.getInstance();
        FirebaseUser curr_user = mAuth.getCurrentUser();

        String businessId = null;
        if (curr_user != null) {
            businessId = curr_user.getUid();
        }
//        for (User user: Firebase.class.){
//            if (businessId==user.getId()){
//                return user.getSpecialoffer();
//            }
//        }
        String offerid = getIntent().getStringExtra("OfferId");


        //Should get the service ID from the previous activity ('putExtra' .. from BusinessServiceManagementActivity)
        BusinessSpecialOffers newbusinessSpecialOffers = new BusinessSpecialOffers(businessId, description, duration);
        //  newbusinessSpecialOffers.setWorkingDays(selectedDays);//add the list of available days to the service object
//        updateFirestoreDocument(newbusinessSpecialOffers);

        ///get back to the previous page, handled in the Onclick function for the button "change"

        return name;
    }


//    private void updateFirestoreDocument(BusinessSpecialOffers businessSpecialOffers) {
//        DBHelper dbHelper = new DBHelper();
//        dbHelper.updateSpecialOffers(businessSpecialOffers,
//                aVoid -> {
//                    // Success handling. Perhaps refresh the list of services
//                    Toast.makeText(BusinessSpecialOffers.this, "Service edited successfully", Toast.LENGTH_SHORT).show();
//                },
//                e -> {
//                    // Failure handling
//                    Toast.makeText(BusinessSpecialOffers.this, "Failed to edit service info", Toast.LENGTH_SHORT).show();
//                });
//    }

}
