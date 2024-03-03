package com.myapp.booknow.mvvm.viewmodel.business;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.myapp.booknow.R;
import com.myapp.booknow.mvvm.model.DBHelper;
import com.myapp.booknow.mvvm.model.ServiceProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServiceProviderSetWorkingDaysActivity extends AppCompatActivity {

    private TextView selectDaysTextView;
    private Button confirmButton;
    private String providerName;
    private String[] serviceIds;;
    List<String> combinedAvailableDays = new ArrayList<>();
    String selected_services;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_workingdays_select);

        selectDaysTextView = findViewById(R.id.selectDaysTextView);
        confirmButton = findViewById(R.id.confirmButton);


        // Get data from the previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            providerName = extras.getString("PROVIDER_NAME");
            String serviceIdsConcatenated = extras.getString("SELECTED_SERVICES_IDS");
            serviceIds = serviceIdsConcatenated.split(", ");
            selected_services = extras.getString("SELECTED_SERVICES");
        }

        getServicesDays();

        selectDaysTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDaySelectionDialog();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if at least one day is selected
                String selectedDaysText = selectDaysTextView.getText().toString().trim();
                if (selectedDaysText.isEmpty() || selectedDaysText.equals("Select Working Days")) {
                    Toast.makeText(ServiceProviderSetWorkingDaysActivity.this, "Please select at least one working day", Toast.LENGTH_SHORT).show();
                    return; // Stop further execution
                }


                //add provider to DB
                //advance to next page(or back to the dashboard ...)
                ServiceProvider provider = new ServiceProvider();

                provider.setName(providerName);

                List<String> selectedServiceNames = new ArrayList<>(Arrays.asList(selected_services.split(", ")));
                provider.setServicesOffered(selectedServiceNames);

                List<String> selectedServiceIds = new ArrayList<>(Arrays.asList(serviceIds));
                provider.setServicesOfferedIds(selectedServiceIds);

                List<String> availableDays = getSelectedDayNames();
                provider.setAvailableDays(availableDays);
                Log.d("ServiceProviderSetWorkingDays","the list of available days is : "+Arrays.toString(combinedAvailableDays.toArray()));
                //set businessId
                String businessId = getCurrentBusinessId();
                provider.setBusinessId(businessId);

                DBHelper dbHelper = new DBHelper();
                dbHelper.addServiceProvider(provider,  aVoid -> {
                            // Success handling. Perhaps refresh the list of services
                            Toast.makeText(ServiceProviderSetWorkingDaysActivity.this, "Provider added successfully", Toast.LENGTH_SHORT).show();
                            // fetchProvidersAndSetupAdapter();  // fetching the providers
                        },
                        e -> {
                            // Failure handling
                            Toast.makeText(ServiceProviderSetWorkingDaysActivity.this, "Failed to add provider", Toast.LENGTH_SHORT).show();
                        });


                //add the provider (id) to the list of the providers of the chosen service (ServiceId).
                Log.d("checkingProviderId",provider.getProviderId());//checking if the the provider's id is correct.
                dbHelper.addProviderToServices(provider.getProviderId(),serviceIds);

                //finish
                Intent intent = new Intent(ServiceProviderSetWorkingDaysActivity.this, BusinessDashboardActivity.class);
                startActivity(intent);
            }
        });


    }

    private void showDaySelectionDialog() {
//        final String[] daysArray = selectedServices;////!!!!!!!!!!
//        boolean[] selectedDays = new boolean[selectedServices.length];///!!!!!!!
        final String[] daysArray = combinedAvailableDays.toArray(new String[0]);
        boolean[] selectedDays = new boolean[daysArray.length];
        ArrayList<Integer> dayList = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(ServiceProviderSetWorkingDaysActivity.this);
        builder.setTitle("Select Working Days");
        builder.setMultiChoiceItems(daysArray, selectedDays, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    dayList.add(which);
                } else {
                    dayList.remove(Integer.valueOf(which));
                }
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < dayList.size(); i++) {
                    stringBuilder.append(daysArray[dayList.get(i)]);
                    if (i != dayList.size() - 1) {
                        stringBuilder.append(", ");
                    }
                }
                selectDaysTextView.setText(stringBuilder.toString());
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }


    //gets the working days for the given service
    private void getServicesDays() {

        DBHelper dbHelper = new DBHelper();
        String businessId = getCurrentBusinessId(); // Replace with actual business ID
        for (String serviceId : serviceIds) {
            dbHelper.getAvailableDaysForService(serviceId, businessId,
                    this::updateAvailableDays,
                    e -> Toast.makeText(this, "Error fetching days for service: " + serviceId, Toast.LENGTH_SHORT).show());

        }
    }

    private List<String> getSelectedDayNames() {
        String selectedDaysString = selectDaysTextView.getText().toString();
        if (selectedDaysString.isEmpty() || selectedDaysString.equals("Select Working Days")) {
            return new ArrayList<>(); // Return an empty list if no days are selected
        }
        return new ArrayList<>(Arrays.asList(selectedDaysString.split(", ")));
    }


    private void updateAvailableDays(List<String> availableDays) {
        Set<String> uniqueDays = new HashSet<>(combinedAvailableDays);
        uniqueDays.addAll(availableDays);
        combinedAvailableDays = new ArrayList<>(uniqueDays);
    }


    private String getCurrentBusinessId() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return (currentUser != null) ? currentUser.getUid() : null;
    }
}
