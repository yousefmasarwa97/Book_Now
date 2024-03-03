package com.myapp.booknow.mvvm.viewmodel.business;

import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.myapp.booknow.R;
import com.myapp.booknow.mvvm.model.BusinessService;
import com.myapp.booknow.mvvm.model.DBHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ServiceProviderSetNameAndServicesActivity extends AppCompatActivity {

    private EditText providerNameEditText;
    private TextView selectServicesTextView;
    private Button nextButton;
    private ImageView backButton;

    private boolean[] selectedServices;
    private String[] serviceArray; // Assuming this is fetched or predefined
    private ArrayList<Integer> serviceList = new ArrayList<>();
    private Map<String, String> serviceNameToIdMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_service_provider_name_and_service);

        providerNameEditText = findViewById(R.id.providerNameEditText);
        selectServicesTextView = findViewById(R.id.selectServicesTextView);
        backButton = findViewById(R.id.back_icon);
        nextButton = findViewById(R.id.nextButton);

        serviceArray = null; // Initialize as null
        serviceList = new ArrayList<>();

        fetchServicesAndPopulateArray();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the provider's name
                String providerName = providerNameEditText.getText().toString();
                if (providerName.isEmpty()) {
                    providerNameEditText.setError("Please enter a provider name");
                    providerNameEditText.requestFocus();
                    return; // Stop further execution
                }

                // Check if at least one service is selected
                if (serviceList.isEmpty()) { // Assuming serviceList holds the indices of selected services
                    Toast.makeText(ServiceProviderSetNameAndServicesActivity.this, "Please select at least one service", Toast.LENGTH_SHORT).show();
                    return; // Stop further execution
                }
                //Go to selectDays for provider activity
                Intent intent = new Intent(ServiceProviderSetNameAndServicesActivity.this, ServiceProviderSetWorkingDaysActivity.class);


                // Get selected services as a comma-separated String
                StringBuilder selectedServicesBuilder = new StringBuilder();
                for (int i = 0; i < serviceList.size(); i++) {
                    selectedServicesBuilder.append(serviceArray[serviceList.get(i)]);
                    if (i != serviceList.size() - 1) {
                        selectedServicesBuilder.append(", "); // delimiter
                    }
                }
                String selectedServices = selectedServicesBuilder.toString();
                List<String> selectedServiceNames = new ArrayList<>(Arrays.asList(selectedServices.split(", ")));


                StringBuilder selectedServicesIds = new StringBuilder();
                int i = 0;
                int size = selectedServiceNames.size();
                for (String serviceName : selectedServiceNames) {
                    String serviceId = serviceNameToIdMap.get(serviceName);
                    selectedServicesIds.append(serviceId);
                    if (i < size - 1) {
                        selectedServicesIds.append(", ");
                    }
                    i++;

                }
                String selectedServicesIDs = selectedServicesIds.toString();


                Log.d("ServiceProviderSetWorkingDays", "the list of services ids is : " + selectedServicesIDs);
                // Put extra data in the intent
                intent.putExtra("PROVIDER_NAME", providerName);
                intent.putExtra("SELECTED_SERVICES", selectedServices);
                intent.putExtra("SELECTED_SERVICES_IDS", selectedServicesIDs);
                // Start the new activity
                startActivity(intent);
            }
        });
    }


    private void fetchServicesAndPopulateArray() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String businessId = null;

        if (currentUser != null) {
            // The user is signed in
            businessId = currentUser.getUid(); // the unique id firebase gives (businessId)
        } else {
            Toast.makeText(ServiceProviderSetNameAndServicesActivity.this, "error", Toast.LENGTH_SHORT).show();
            Log.e("ServiceProvidersManagement", "SomeHow the business ID is null");
            return;
        }


        DBHelper dbHelper = new DBHelper();


        dbHelper.fetchBusinessServices(businessId, businessServices -> {
            serviceArray = new String[businessServices.size()];
            for (int i = 0; i < businessServices.size(); i++) {
                BusinessService service = businessServices.get(i);
                serviceArray[i] = service.getName();
                serviceNameToIdMap.put(service.getName(), service.getServiceId());//mapping service names to ids
            }


            selectedServices = new boolean[serviceArray.length]; // Initialize selectedServices

            setupServiceClickListener(); // Setup click listener for tvService

        }, e -> {

        });
    }

    private void setupServiceClickListener() {

        selectServicesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serviceArray == null || serviceArray.length == 0) {//services are not yet loaded (list of services is empty)
                    Toast.makeText(ServiceProviderSetNameAndServicesActivity.this, "error", Toast.LENGTH_SHORT).show();
                    Log.e("ServiceProvidersManagement", "no services available");
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(ServiceProviderSetNameAndServicesActivity.this);
                builder.setTitle("Select Service");
                builder.setCancelable(false);

                builder.setMultiChoiceItems(serviceArray, selectedServices, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialoginterface, int i, boolean b) {
                        if (b) {
                            serviceList.add(i);
                            Collections.sort(serviceList);
                        } else {
                            serviceList.remove(Integer.valueOf(i));
                        }
                    }
                });

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuilder stringBuilder = new StringBuilder();
                        Set<String> combinedAvailableDays = new HashSet<>(); // To store the intersection of available days
                        for (int j = 0; j < serviceList.size(); j++) {
                            String selectedServiceName = serviceArray[serviceList.get(j)];
                            stringBuilder.append(selectedServiceName);
                            if (j != serviceList.size() - 1) {
                                stringBuilder.append(", ");
                            }


                        }
                        selectServicesTextView.setText(stringBuilder.toString());


                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                builder.setNeutralButton("Clear all", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (int j = 0; j < selectedServices.length; j++) {
                            selectedServices[j] = false;
                        }
                        serviceList.clear();
                        selectServicesTextView.setText("");
                    }
                });

                builder.show();
            }
        });


    }


}
