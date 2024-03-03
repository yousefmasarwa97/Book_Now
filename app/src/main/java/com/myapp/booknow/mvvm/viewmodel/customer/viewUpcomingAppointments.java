package com.myapp.booknow.mvvm.viewmodel.customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.myapp.booknow.mvvm.view.UpcomingAppointmentsAdapter;
import com.myapp.booknow.Utils.FirestoreCallback;
import com.myapp.booknow.R;
import com.myapp.booknow.mvvm.model.Appointment;
import com.myapp.booknow.mvvm.model.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class viewUpcomingAppointments extends AppCompatActivity {

    private DBHelper dbHelper;
    private RecyclerView upcomingAppointmentsRecyler;// A recyclerView to view the upcoming appointments
    private List<Appointment> appointmentList; // List of appointments objects
    private UpcomingAppointmentsAdapter upcomingAdapter; // adapter for appointments


    private ImageView backIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_view_upcoming_appointments);

        dbHelper = new DBHelper();

        upcomingAppointmentsRecyler = findViewById(R.id.upcoming_appointments_recycler);

        backIcon = findViewById(R.id.upcoming_appointments_back_icon);



        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




        appointmentsRecycler(); //fetches appointments


    }


    private void appointmentsRecycler() {
        upcomingAppointmentsRecyler.setHasFixedSize(true);
        upcomingAppointmentsRecyler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        appointmentList = new ArrayList<>();

        //getting the curr customer id
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser curr_user = mAuth.getCurrentUser();
        String customer_id = null;
        if (curr_user != null) {
            customer_id = curr_user.getUid();
        }


        dbHelper.fetchUpcomingAppointmentsForCustomer(customer_id, new FirestoreCallback<List<Appointment>>() {
            @Override
            public void onSuccess(List<Appointment> result) {
                for(Appointment appointment : result){
                    Log.d("Check appointments (RecyclerView) ",appointment.toString()+" ");
                }
                appointmentList.clear();
                appointmentList.addAll(result);
                upcomingAdapter.notifyDataSetChanged();
                Log.d("Check appointments (RecyclerView) size :",""+result.size());
            }

            @Override
            public void onFailure(Exception e) {//error fetching appointments
                Log.d("Check appointments (RecyclerView) ",e.getMessage());

            }
        });

        //setting the appointments adapter to the recycle and binding it with the list of appointments
        upcomingAdapter = new UpcomingAppointmentsAdapter(appointmentList);
        upcomingAppointmentsRecyler.setAdapter(upcomingAdapter);



    }

}