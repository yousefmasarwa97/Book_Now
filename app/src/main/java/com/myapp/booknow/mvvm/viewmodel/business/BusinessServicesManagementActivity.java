package com.myapp.booknow.mvvm.viewmodel.business;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myapp.booknow.mvvm.model.BusinessService;
import com.myapp.booknow.mvvm.model.DBHelper;
import com.myapp.booknow.R;
import com.myapp.booknow.mvvm.view.ServiceAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BusinessServicesManagementActivity extends AppCompatActivity {

    private EditText serviceNameEditText, serviceDescriptionEditText, serviceDurationEditText;

    private Button addServiceButton;
    private RecyclerView servicesRecyclerView;
    private ServiceAdapter serviceAdapter;
    private List<BusinessService> serviceItemList;// This should be populated from Firestore

    private TextView tvDay;
    boolean[] selectedDay;
    private ImageView backButton;
    ArrayList<Integer> dayList = new ArrayList<>();
    String[] dayArray = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_business_services_management);

        // Initialize EditTexts and Button
        serviceNameEditText = findViewById(R.id.serviceNameEditText);
        serviceDescriptionEditText = findViewById(R.id.serviceDescriptionEditText);
        serviceDurationEditText = findViewById(R.id.serviceDurationEditText);
        addServiceButton = findViewById(R.id.addServiceButton);
        backButton = findViewById(R.id.service_back_icon);




        // Initialize RecyclerView with adapter
        servicesRecyclerView = findViewById(R.id.servicesRecyclerView);
        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        serviceItemList = new ArrayList<>(); // Initialize with empty list or fetch from Firestore
        serviceAdapter = new ServiceAdapter(serviceItemList);
        servicesRecyclerView.setAdapter(serviceAdapter);



        tvDay=findViewById(R.id.selectDaysManagment);
        //init the selected daya array
        selectedDay = new boolean[dayArray.length];


        tvDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //init alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(BusinessServicesManagementActivity.this);

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
                            dayList.remove(Integer.valueOf(i));
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



        // Set OnClickListener for Add Service Button
        addServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewService();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Load existing services (You will implement the fetchServices method)
        fetchServices();
    }

    private void addNewService() {
        // Get the values from EditTexts
        String name = serviceNameEditText.getText().toString().trim();
        String description = serviceDescriptionEditText.getText().toString().trim();
        String durationString = serviceDurationEditText.getText().toString().trim();

        // Convert dayList (ArrayList<Integer>) to List<String>
        List<String> selectedDays = new ArrayList<>();
        for (Integer dayIndex : dayList) {
            selectedDays.add(dayArray[dayIndex]);
        }

        // Validate input
        if(name.isEmpty() || durationString.isEmpty()) {
            Toast.makeText(this, "Name and duration are required.", Toast.LENGTH_SHORT).show();
            return;
        }
        int duration = Integer.parseInt(durationString);

        //Get the signed user id
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser curr_user = mAuth.getCurrentUser();
        String businessId = null;
        if (curr_user != null) {
            businessId = curr_user.getUid();
        }

        //generate ID for service
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference newServiceRef = db.collection("BusinessServices").document();
        String serviceId = newServiceRef.getId(); // Firestore generates the ID

        //-----We got the id of a new service so we can use it down here----------//

        // Create a new ServiceItem object
        BusinessService newService = new BusinessService(serviceId , businessId, name, description, duration);
        newService.setWorkingDays(selectedDays); // Set available days

        // Add new service to Firestore (You will implement the addServiceToFirestore method)
        addServiceToFirestore(newService);

        // Clear input fields
        serviceNameEditText.setText("");
        serviceDescriptionEditText.setText("");
        serviceDurationEditText.setText("");
    }

    public void fetchServices() {
        // Fetch services from Firestore and update the serviceItemList and serviceAdapter
        // Implement the logic to fetch services associated with the business
        DBHelper dbHelper = new DBHelper();

        //getting the businessID
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser curr_user = mAuth.getCurrentUser();
        String businessId = null;
        if (curr_user != null) {
            businessId = curr_user.getUid();
        }

        dbHelper.fetchBusinessServices(businessId,
                businessServices -> {
                // Update the RecyclerView with these services
                serviceItemList.clear();
                serviceItemList.addAll(businessServices);
                serviceAdapter.notifyDataSetChanged();
                Log.d("BusinessServices", "Number of services fetched: " + businessServices.size());

                },
                e->{//To handle errors
                    Toast.makeText(this, "Error fetching services", Toast.LENGTH_SHORT).show();
                });

    }


    private void addServiceToFirestore(BusinessService serviceItem) {
        // Implement the logic to add the serviceItem to Firestore
        // After adding, you might want to update the RecyclerView with the new service
        DBHelper dbHelper = new DBHelper();
        dbHelper.addBusinessService(serviceItem,
                aVoid -> {
                    // Success handling. Perhaps refresh the list of services
                    Toast.makeText(BusinessServicesManagementActivity.this, "Service added successfully", Toast.LENGTH_SHORT).show();
                    fetchServices();  // Assuming this method refreshes the list
                },
                e -> {
                    // Failure handling
                    Toast.makeText(BusinessServicesManagementActivity.this, "Failed to add service", Toast.LENGTH_SHORT).show();
                });
    }


    /**
     * Handles the case when the user goes to another activity and resumes with this activity.
     */
    @Override
    protected void onResume() {
        super.onResume();

        //refresh the list of services every time activity resumes
        fetchServices();
    }
}
