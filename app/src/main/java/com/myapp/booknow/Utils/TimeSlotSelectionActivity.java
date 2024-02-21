package com.myapp.booknow.Utils;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.myapp.booknow.Customer.C_Dashboard;
import com.myapp.booknow.FirestoreCallback;
import com.myapp.booknow.R;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotSelectionActivity extends AppCompatActivity {

    List<String> providerIds;
    private DBHelper dbHelper;
    private TextView titleTextView;
    private ListView timeSlotsListView;
    private  String customerId;
    private String businessName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_slot_selection);

        //Getting the customer Id (can replace it with getExtra and retrieve it from the prev. activity)!!!
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            customerId = currentUser.getUid();
        } else {
            // Handle the case where the user is not logged in
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return; // Optionally return or handle this scenario appropriately
        }

        dbHelper = new DBHelper();
        String businessId = getIntent().getStringExtra("businessId");
        String serviceId = getIntent().getStringExtra("serviceId");
        LocalDate selectedDate = (LocalDate) getIntent().getSerializableExtra("selectedDate");//get the date from the prev. activity



        providerIds = new ArrayList<>();

        titleTextView = findViewById(R.id.titleTextView);
        timeSlotsListView = findViewById(R.id.timeSlotsListView);


        Log.d("TimeSlotsSelectionTest", "the businessId is ::::-> " + businessId);
        Log.d("TimeSlotsSelectionTest", "the serviceId is ::::-> " + serviceId);
        Log.d("TimeSlotsSelectionTest", "the selected date_string is ::::-> " + selectedDate);

        generateAndDisplayTimeSlots(businessId, serviceId, selectedDate);


    }
    private void generateAndDisplayTimeSlots(String businessId, String serviceId, LocalDate selectedDate) {
        // Fetch the service duration first
        dbHelper.getServiceDuration(serviceId, new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer serviceDuration) {
                // Now fetch providers for the service
                dbHelper.fetchProvidersForService(serviceId, new FirestoreCallback<List<String>>() {
                    @Override
                    public void onSuccess(List<String> providerIds) {
                        if (providerIds.isEmpty()) {
                            Log.e("TimeSlotSelection", "No providers found for this service.");
                            return;
                        }
                        // Fetch the working hours next
                        dbHelper.fetchWorkingHours(businessId, selectedDate, new OnSuccessListener<DBHelper.WorkingHours>() {
                            @Override
                            public void onSuccess(DBHelper.WorkingHours workingHours) {
                                LocalTime openTime = Utils.timestampToLocalTime(workingHours.getOpenTime());
                                LocalTime closeTime = Utils.timestampToLocalTime(workingHours.getCloseTime());

                                // Finally, fetch appointments for the date and providers
                                dbHelper.fetchAppointmentsForDateAndProviders(selectedDate, providerIds, new FirestoreCallback<List<Appointment>>() {
                                    @Override
                                    public void onSuccess(List<Appointment> appointments) {
                                        // Generate available time slots
                                        List<String> availableTimeSlots = dbHelper.generateAvailableTimeSlots(openTime, closeTime, serviceDuration, appointments);


                                        // Bind the ListView
                                        ArrayAdapter<String> adapter = new ArrayAdapter<>(TimeSlotSelectionActivity.this, android.R.layout.simple_list_item_1, availableTimeSlots);
                                        timeSlotsListView.setAdapter(adapter);

                                        //item click listener for the ListView (view on the screen)
                                        timeSlotsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                String selectedTimeSlot = availableTimeSlots.get(position);


//                                                // Booking the appointment
//                                                dbHelper.bookOrUpdateAppointment(businessId, customerId, serviceId, selectedDate, selectedTimeSlot,
//                                                        aVoid -> Toast.makeText(TimeSlotSelectionActivity.this, "Appointment booked successfully!", Toast.LENGTH_SHORT).show(),
//                                                        e -> Toast.makeText(TimeSlotSelectionActivity.this, "Failed to book appointment.", Toast.LENGTH_SHORT).show());
//
//                                            }
                                                AlertDialog.Builder builder = new AlertDialog.Builder(TimeSlotSelectionActivity.this);
                                                builder.setTitle("Confirmation");
                                                builder.setMessage("Are you sure you want to book this appointment?");
                                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        // Booking the appointment
                                                        dbHelper.bookOrUpdateAppointment(businessId, customerId, serviceId, selectedDate, selectedTimeSlot,
                                                                aVoid -> {
                                                                    Toast.makeText(TimeSlotSelectionActivity.this, "Appointment booked successfully!", Toast.LENGTH_SHORT).show();

                                                                    // Go back to CustomerDashboardAct
                                                                    Intent intent = new Intent(TimeSlotSelectionActivity.this, C_Dashboard.class);
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                    startActivity(intent);
                                                                    finish(); // Optional, to finish the current activity
                                                                },
                                                                e -> Toast.makeText(TimeSlotSelectionActivity.this, "Failed to book appointment.", Toast.LENGTH_SHORT).show());
                                                    }
                                                });
                                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss(); // Dismiss the dialog if Cancel is clicked
                                                    }
                                                });

                                                AlertDialog dialog = builder.create();
                                                dialog.show();
                                            }
                                        });


                                        // Log the available time slots
                                        runOnUiThread(() -> {
                                            // Update your UI here with the availableTimeSlots
                                            Log.d("AvailableTimeSlots", "Slots: " + availableTimeSlots.toString());
                                        });
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        Log.e("TimeSlotSelection", "Failed to fetch appointments: " + e.getMessage());
                                    }
                                });
                            }

                        }, e -> Log.e("TimeSlotSelection", "Failed to fetch working hours: " + e.getMessage()));
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("TimeSlotSelection", "Failed to fetch providers: " + e.getMessage());
                    }
                });
            }

        },e->{
            Log.e("TimeSlotSelection", "Failed to get service duration: " + e.getMessage());
        });
    }






}


