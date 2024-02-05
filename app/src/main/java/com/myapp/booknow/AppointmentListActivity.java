package com.myapp.booknow;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AppointmentListActivity extends AppCompatActivity {

    private RecyclerView appointmentsRecycleView;
    private List<Appointment> appointmentItemList;
    private AppointmentAdapter appointmentAdapter;

    private String businessId;
    private LocalDate selectedDate;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_list);



        appointmentsRecycleView = findViewById(R.id.appointmentsRecyclerView);
        appointmentsRecycleView.setLayoutManager(new LinearLayoutManager(this));
        appointmentItemList = new ArrayList<>(); // Initialize with empty list or fetch from Firestore
        appointmentAdapter = new AppointmentAdapter(appointmentItemList);
        appointmentsRecycleView.setAdapter(appointmentAdapter);


         businessId = getIntent().getStringExtra("businessId"); // businessId from last activity. (current business ID).
         selectedDate = (LocalDate) getIntent().getSerializableExtra("selectedDate");// Get the wanted date from the last activity.

         Log.d("appointmentsList","the business ID = " + businessId + "  and the selected date is : " + selectedDate );

        fetchAppointments();

    }


    public void fetchAppointments() {
        // Fetch appointments from Firestore and update the appointmentItemList and appointmentAdapter
        // Implement the logic to fetch appointments associated with the business
        DBHelper dbHelper = new DBHelper();

        String status = "waiting";//can be changed

        dbHelper.fetchAppointmentsForDate(businessId, selectedDate, status,
                new FirestoreCallback<List<Appointment>>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(List<Appointment> result) {

                        //checking:
                        for(Appointment appointment : result){
                            Log.d("AppointmentListCheck","appointment = "+ appointment.toString());
                        }

                        appointmentItemList.clear();
                        appointmentItemList.addAll(result);
                        appointmentAdapter.notifyDataSetChanged();
                        Log.d("appointmentsList", "Number of appointments fetched: " + result.size() );
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("AppointmentListActivity", "Error fetching appointments: " + e.getMessage());
                        Toast.makeText(AppointmentListActivity.this, "Error fetching appointments", Toast.LENGTH_SHORT).show();

                    }
                }
               );

    }








}


