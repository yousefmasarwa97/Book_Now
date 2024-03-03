package com.myapp.booknow.mvvm.viewmodel.customer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.myapp.booknow.mvvm.model.BusinessService;
import com.myapp.booknow.mvvm.model.DBHelper;
import com.myapp.booknow.R;

import java.util.ArrayList;
import java.util.List;

public class ChooseServiceActivity extends AppCompatActivity {

    private Spinner serviceSpinner;//Drop down list of services
    private DBHelper dbHelper;
    private String businessId;//to get the business ID from the pre. page
    private String selectedServiceName,selectedServiceId;
    private Button nextButton;//button to go to the next page

    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_choose_service);

        serviceSpinner = findViewById(R.id.serviceSpinner);
        nextButton = findViewById(R.id.next_button_choose_service);
        backButton = findViewById(R.id.service_back_icon);

       // String id = "example"; // must change to the business id given from the last page (didn't do it yet , must be implemented using putExtra)

        dbHelper = new DBHelper();

        // Retrieve the business ID passed from the previous activity
        businessId = getIntent().getStringExtra("businessId");


        fetchAndDisplayServices();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedServiceName = serviceSpinner.getSelectedItem().toString();
                Log.d("ChooseService","The name of the selected service is : "+selectedServiceName);

                Log.d("ChooseService","The id of the selected service before calling fetch id function is :  "+selectedServiceId);

                getSelectedServiceIdbyNameandProceed();//to get the id of the selected service

                Log.d("ChooseService","The id of the selected service after calling fetch id function is :  "+selectedServiceId);

//                Intent intent = new Intent(ChooseServiceActivity.this,ChooseDayandTimeActivity.class);
//                intent.putExtra("businessId",businessId);
//                intent.putExtra("serviceId",selectedServiceId);
//                startActivity(intent);
            }
        });



    }


    private void fetchAndDisplayServices() {
        dbHelper.fetchBusinessServices(businessId, services -> {
            // Success Listener
            List<String> serviceNames = new ArrayList<>();
            for (BusinessService service : services) {
                serviceNames.add(service.getName()); // Assuming getName() method exists in BusinessService
            }
            Log.d("chooseService","the business id is : "+businessId);//FOR TESTING
            updateSpinner(serviceNames);
        }, e -> {
            Toast.makeText(ChooseServiceActivity.this,"error displaying services" , Toast.LENGTH_SHORT).show();
            Log.e("ChooseService","error fetching/displaying the services and the business id is : "+businessId,e);//FOR TESTING
        });
    }

    private void updateSpinner(List<String> serviceNames) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, serviceNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serviceSpinner.setAdapter(adapter);
    }

//    private void getSelectedServiceIdbyName(){
//        dbHelper.fetchServiceIdByName(businessId, selectedServiceName, new OnSuccessListener<String>() {
//            @Override
//            public void onSuccess(String serviceId) {
//                if (serviceId != null) {
//                    //System.out.println("Found service ID: " + serviceId);
//                    selectedServiceId = serviceId;
//                    Log.d("ChooseService","the service name is : " + selectedServiceName + "  and the service id is : " + selectedServiceId);
//                } else {
//                    Log.d("ChooseService", "Service not found.");
//                }
//            }
//        }, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.e("ChooseService", "Error fetching service ID: " + e.getMessage());
//
//            }
//        });
//    }

    private void getSelectedServiceIdbyNameandProceed(){
        dbHelper.fetchServiceIdByName(businessId, selectedServiceName, new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String serviceId) {
                if (serviceId != null) {
                    selectedServiceId = serviceId;
                    Log.d("ChooseService","the service name is : " + selectedServiceName + " and the service id is : " + selectedServiceId);

                    // Now that we have the serviceId, proceed with creating the intent
                    Intent intent = new Intent(ChooseServiceActivity.this, ChooseDayandTimeActivity.class);
                    intent.putExtra("businessId", businessId);
                    intent.putExtra("serviceId", selectedServiceId);
                    startActivity(intent);
                } else {
                    Log.d("ChooseService", "Service not found.");
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("ChooseService", "Error fetching service ID: " + e.getMessage());
            }
        });
    }



}
