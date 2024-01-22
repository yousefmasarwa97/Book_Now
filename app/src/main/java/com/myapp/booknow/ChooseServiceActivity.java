package com.myapp.booknow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class ChooseServiceActivity extends AppCompatActivity {

    private Spinner serviceSpinner;//Drop down list of services
    private DBHelper dbHelper;
    private String businessId;//to get the business ID from the pre. page

    private Button nextButton;//button to go to the next page


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_service);

        serviceSpinner = findViewById(R.id.serviceSpinner);
        nextButton = findViewById(R.id.Next_button_choose_service);

       // String id = "example"; // must change to the business id given from the last page (didn't do it yet , must be implemented using putExtra)

        dbHelper = new DBHelper();

        // Retrieve the business ID passed from the previous activity
        businessId = getIntent().getStringExtra("businessId");

        fetchAndDisplayServices();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseServiceActivity.this,ChooseDayandTimeActivity.class);

                startActivity(intent);
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


}
