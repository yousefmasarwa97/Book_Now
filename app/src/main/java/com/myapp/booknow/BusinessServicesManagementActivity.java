package com.myapp.booknow;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class BusinessServicesManagementActivity extends AppCompatActivity {

    private EditText serviceNameEditText, serviceDescriptionEditText, serviceDurationEditText;

    private Button addServiceButton;
    private RecyclerView servicesRecyclerView;
    private ServiceAdapter serviceAdapter;
    private List<BusinessService> serviceItemList; // This should be populated from Firestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_services_management);

        // Initialize EditTexts and Button
        serviceNameEditText = findViewById(R.id.serviceNameEditText);
        serviceDescriptionEditText = findViewById(R.id.serviceDescriptionEditText);
        serviceDurationEditText = findViewById(R.id.serviceDurationEditText);
        addServiceButton = findViewById(R.id.addServiceButton);

        // Initialize RecyclerView with adapter
        servicesRecyclerView = findViewById(R.id.servicesRecyclerView);
        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        serviceItemList = new ArrayList<>(); // Initialize with empty list or fetch from Firestore
        serviceAdapter = new ServiceAdapter(serviceItemList);
        servicesRecyclerView.setAdapter(serviceAdapter);

        // Set OnClickListener for Add Service Button
        addServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewService();
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
