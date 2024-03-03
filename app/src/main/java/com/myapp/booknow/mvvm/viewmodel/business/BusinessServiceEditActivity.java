package com.myapp.booknow.mvvm.viewmodel.business;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.myapp.booknow.mvvm.model.BusinessService;
import com.myapp.booknow.mvvm.model.DBHelper;
import com.myapp.booknow.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BusinessServiceEditActivity extends AppCompatActivity {

    private EditText serviceNameEditText, serviceDescriptionEditText, serviceDurationEditText;
    private TextView tvDay;
    boolean[] selectedDay;
    ArrayList<Integer> dayList = new ArrayList<>();
    String[] dayArray = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};

    private Button saveEditButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_business_service);


        serviceNameEditText=findViewById(R.id.serviceNameEditText_edit);
        serviceDescriptionEditText=findViewById(R.id.serviceDescriptionEditText_edit);
        serviceDurationEditText=findViewById(R.id.serviceDurationEditText_edit);
        saveEditButton=findViewById(R.id.saveeButton_edit);
        tvDay=findViewById(R.id.selectDay);


        //init the selected daya array
        selectedDay = new boolean[dayArray.length];

        tvDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //init alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(BusinessServiceEditActivity.this);

                //set title
                builder.setTitle("Select Day");

                //set dialog non cancelable
                builder.setCancelable(false);

                builder.setMultiChoiceItems(dayArray, selectedDay, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialoginterface, int i, boolean b) {
                        if(b){//when checkbox selected
                            dayList.add(i);
                            Collections.sort(dayList);
                        }else{//when checkbox unselected
                            dayList.remove(i);
                        }

                    }
                });

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for(int j=0; j<dayList.size();j++){
                            //concat array value
                            stringBuilder.append(dayArray[dayList.get(j)]);

                            if(j != dayList.size()-1){
                                //add comma
                                stringBuilder.append(", ");
                            }
                        }
                        //set text on text view
                        tvDay.setText(stringBuilder.toString());
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //dismiss dialog
                        dialogInterface.dismiss();
                    }
                });

                builder.setNeutralButton("Clear all", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for(int j=0 ; j<selectedDay.length;j++){
                            selectedDay[j] = false;
                            dayList.clear();
                            tvDay.setText("");
                        }
                    }
                });

                //show dialog
                builder.show();
            }
        });

        saveEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateServiceInfo();
                finish();
            }
        });
    }

    private void updateServiceInfo(){
        String name = serviceNameEditText.getText().toString().trim();
        String description = serviceDescriptionEditText.getText().toString().trim();
        String durationString = serviceDurationEditText.getText().toString().trim();


        // Convert dayList (ArrayList<Integer>) to List<String>
        List<String> selectedDays = new ArrayList<>();
        for (Integer dayIndex : dayList) {
            selectedDays.add(dayArray[dayIndex]);
        }


        if(name.isEmpty() || durationString.isEmpty()) {
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
        String serviceId = getIntent().getStringExtra("serviceId");


        //Should get the service ID from the previous activity ('putExtra' .. from BusinessServiceManagementActivity)
        BusinessService newService = new BusinessService(serviceId , businessId, name, description, duration);
        newService.setWorkingDays(selectedDays);//add the list of available days to the service object
        updateFirestoreDocument(newService);

        ///get back to the previous page, handled in the Onclick function for the button "change"

    }

    private void updateFirestoreDocument(BusinessService service){
        DBHelper dbHelper = new DBHelper();
        dbHelper.updateBusinessService(service,
                aVoid -> {
                    // Success handling. Perhaps refresh the list of services
                    Toast.makeText(BusinessServiceEditActivity.this, "Service edited successfully", Toast.LENGTH_SHORT).show();
                },
                e -> {
                    // Failure handling
                    Toast.makeText(BusinessServiceEditActivity.this, "Failed to edit service info", Toast.LENGTH_SHORT).show();
                });
    }

}
