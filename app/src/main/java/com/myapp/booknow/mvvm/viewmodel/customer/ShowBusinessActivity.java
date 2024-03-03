package com.myapp.booknow.mvvm.viewmodel.customer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.myapp.booknow.Utils.FirestoreCallback;
import com.myapp.booknow.R;
import com.myapp.booknow.mvvm.model.BusinessService;
import com.myapp.booknow.mvvm.model.DBHelper;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * Shows a business details when clicking on the business name/logo in the customer dashboard.
 */
public class ShowBusinessActivity extends AppCompatActivity {

    private TextView tvBusinessName, tvBusinessDescription, tvBusinessServices, tvBusinessHours;
    private Button bookAppointmentButton;
    private ImageView businessLogo,backButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.business_information);

        tvBusinessName = findViewById(R.id.business_name);
        tvBusinessDescription = findViewById(R.id.description_content);
        tvBusinessServices = findViewById(R.id.services_content);
        tvBusinessHours = findViewById(R.id.working_hours_content);
        bookAppointmentButton = findViewById(R.id.booking_appointment_btn);
        businessLogo = findViewById(R.id.business_image);
        backButton = findViewById(R.id.business_info_back_icon);

        String businessId = getIntent().getStringExtra("businessId");
        if(businessId != null){
            Log.e("ShowBusinessActivity", "business id is not null and its  : " + businessId);
            fetchBusinessDetails(businessId);//show business info on the page
        }else{
            Toast.makeText(this, "Error: Business details not available.", Toast.LENGTH_LONG).show();
            Log.e("ShowBusinessActivity", "Business ID is null or not available");
            Log.e("ShowBusinessActivity", "business id is : " + businessId);
            finish();  // Closes the current activity and returns to the previous one
        }

        bookAppointmentButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowBusinessActivity.this, ChooseServiceActivity.class);
                intent.putExtra("businessId",businessId);
                Log.d("ShowBusiness","the putExtra (businessId from ShowbusinessActicity) is:  " + businessId);//FOR TESTING
                startActivity(intent);
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(ShowBusinessActivity.this, C_Dashboard.class);
//                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Fetches all the business's data by calling other helping functions.
     * The purpose of this function is to show the business's details on the screen.
     * @param businessId
     */
    private void fetchBusinessDetails(String businessId){

        DBHelper dbHelper = new DBHelper();
        fetchServices(businessId , dbHelper);
        fetchHours(businessId , dbHelper);
        fetchBusinessInfo(businessId , dbHelper);
        fetchBusinessImage(businessId,dbHelper);

    }

    private void fetchBusinessImage(String businessId, DBHelper dbHelper) {
        dbHelper.getBusinessiamgeURL(businessId, new FirestoreCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Glide.with(getApplicationContext())
                        .load(result)
                        .into(businessLogo);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }


    /**
     * Fetches the services the business gives.
     * @param businessId
     * @param dbHelper
     */
    private void fetchServices(String businessId , DBHelper dbHelper){
        dbHelper.fetchBusinessServices(businessId, businessServices -> {
            StringBuilder servicesBuilder = new StringBuilder();
            for (BusinessService service : businessServices) {
                servicesBuilder.append(service.getName()).append("\n");
            }
            tvBusinessServices.setText(servicesBuilder.toString());
        }, e -> {
            // Handle errors
            Log.e("ShowBusiness", "Error fetching services", e);
        });
    }


    /**
     * Fetches the business's weekly working hours.
     * @param businessId
     * @param dbHelper
     */
    private void fetchHours(String businessId, DBHelper dbHelper) {
        dbHelper.fetchBusinessRegularHours(businessId, businessHours -> {
            Map<String, String> sortedHours = new TreeMap<>(Comparator.comparingInt(this::getDayOrder)); // Sorts by day order
            sortedHours.putAll(businessHours);

            StringBuilder hoursBuilder = new StringBuilder();
            for (Map.Entry<String, String> entry : sortedHours.entrySet()) {
                String dayHours = entry.getValue().replaceAll("[{}]", ""); // Remove braces if present
                String[] dayHourParts = dayHours.split(", ");
                String openTime = "", closeTime = "";
                for (String part : dayHourParts) {
                    if (part.startsWith("openTime")) openTime = part.split("=")[1];
                    if (part.startsWith("closeTime")) closeTime = part.split("=")[1];
                }
                hoursBuilder.append(entry.getKey()).append(": ").append(openTime).append(" - ").append(closeTime).append("\n");
            }
            tvBusinessHours.setText(hoursBuilder.toString());
        }, e -> {
            Log.e("ShowBusiness", "Error fetching business hours", e);
        });
    }

    //Helping function to show the days in order (Sunday, Monday, ...,Saturday)
    private int getDayOrder(String day) {
        List<String> daysOfWeek = Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");
        return daysOfWeek.indexOf(day);
    }


    /**
     * Fetches name + description of the business.
     * @param businessId
     * @param dbHelper
     */
    private void fetchBusinessInfo(String businessId , DBHelper dbHelper){
        dbHelper.fetchBusinessInfo(businessId, businessInfo -> {
            tvBusinessName.setText(businessInfo.getName());
            tvBusinessDescription.setText(businessInfo.getDescription());
            // Handle other business details if necessary
        }, e -> {
            Log.e("ShowBusiness", "Error fetching business info", e);
        });
    }


}
