package com.myapp.booknow;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BusinessServiceEditActivity extends AppCompatActivity {

    private EditText serviceNameEditText, serviceDescriptionEditText, serviceDurationEditText;

    private Button saveEditButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_business_service);


        serviceNameEditText=findViewById(R.id.serviceNameEditText_edit);
        serviceDescriptionEditText=findViewById(R.id.serviceDescriptionEditText_edit);
        serviceDurationEditText=findViewById(R.id.serviceDurationEditText_edit);
        saveEditButton=findViewById(R.id.saveeButton_edit);

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
