package com.myapp.booknow;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_business);

        tvBusinessName = findViewById(R.id.tvBusinessName);
        tvBusinessDescription = findViewById(R.id.tvBusinessDescription);
        tvBusinessServices = findViewById(R.id.tvBusinessServices);
        tvBusinessHours = findViewById(R.id.tvBusinessHours);


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
    }

    private void fetchBusinessDetails(String businessId){

        DBHelper dbHelper = new DBHelper();
        fetchServices(businessId , dbHelper);
        fetchHours(businessId , dbHelper);
        fetchBusinessInfo(businessId , dbHelper);


    }


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

//    private void fetchHours(String businessId , DBHelper dbHelper){
//        dbHelper.fetchBusinessRegularHours(businessId, businessHours -> {
//            StringBuilder hoursBuilder = new StringBuilder();
//            for (Map.Entry<String, String> entry : businessHours.entrySet()) {
//                hoursBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
//            }
//            tvBusinessHours.setText(hoursBuilder.toString());
//        }, e -> {
//            Log.e("ShowBusiness", "Error fetching business hours", e);
//        });
//    }

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

    private int getDayOrder(String day) {
        List<String> daysOfWeek = Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");
        return daysOfWeek.indexOf(day);
    }

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
