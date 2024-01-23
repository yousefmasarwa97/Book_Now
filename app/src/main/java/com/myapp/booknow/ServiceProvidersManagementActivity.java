package com.myapp.booknow;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ServiceProvidersManagementActivity extends AppCompatActivity {





    private EditText providerNameEditText;
    private TextView tvDay , tvService;
    private Button addProvider_btn;

    boolean[] selectedDay;

    ArrayList<Integer> dayList = new ArrayList<>();
    String[] dayArray = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    String[] serviceArray;
    boolean[] selectedServices;
    private ArrayList<Integer> serviceList;
    private Map<String, String> serviceNameToIdMap = new HashMap<>();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_providers_management);

        providerNameEditText = findViewById(R.id.providerNameEditText);
        tvDay = findViewById(R.id.select_available_days);
        tvService = findViewById(R.id.select_available_services);
        addProvider_btn = findViewById(R.id.addUpdateProviderButton);



        String providerName =  providerNameEditText.getText().toString();
        serviceArray = null; // Initialize as null
        serviceList = new ArrayList<>();


        selectedDay = new boolean[dayArray.length];

        //for selecting available days :
        tvDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //init alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ServiceProvidersManagementActivity.this);

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


        //-----------------------------//




        fetchServicesAndPopulateArray();

        addProvider_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add the provider to the providers collection in database
                //fetch the providers and show them in the activity's recycleView
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
            Toast.makeText(ServiceProvidersManagementActivity.this,"error", Toast.LENGTH_SHORT).show();
            Log.e("ServiceProvidersManagement","SomeHow the business ID is null");
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



                    selectedServices = new boolean[serviceArray.length]; // Initialize selectedServices here

                    setupServiceClickListener(); // Setup click listener for tvService

                }, e -> {
                    // Handle error
                });
    }


    private void setupServiceClickListener() {

        tvService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serviceArray == null || serviceArray.length == 0) {//services are not yet loaded (list of services is empty)
                    Toast.makeText(ServiceProvidersManagementActivity.this,"error", Toast.LENGTH_SHORT).show();
                    Log.e("ServiceProvidersManagement","SomeHow the business ID is null");
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(ServiceProvidersManagementActivity.this);
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
                        for (int j = 0; j < serviceList.size(); j++) {
                            stringBuilder.append(serviceArray[serviceList.get(j)]);
                            if (j != serviceList.size() - 1) {
                                stringBuilder.append(", ");
                            }
                        }
                        tvService.setText(stringBuilder.toString());
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
                        tvService.setText("");
                    }
                });

                builder.show();
            }
        });


    }





}
