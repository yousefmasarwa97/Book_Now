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
import com.myapp.booknow.mvvm.view.AppointmentsHistoryAdapter;
import com.myapp.booknow.Utils.FirestoreCallback;
import com.myapp.booknow.R;
import com.myapp.booknow.mvvm.model.Appointment;
import com.myapp.booknow.mvvm.model.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class viewAppointmentsHistory extends AppCompatActivity {

    private DBHelper dbHelper;
    private RecyclerView appointmentsHistoryRecycler;// A recyclerView to view the upcoming appointments
    private List<Appointment> appointmentList; // List of appointments objects
    private AppointmentsHistoryAdapter historyAdapter; // adapter for appointments


    private ImageView backIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_view_appointments_history);


        dbHelper = new DBHelper();

        appointmentsHistoryRecycler = findViewById(R.id.appointments_history_recycler);

        backIcon = findViewById(R.id.appointments_history_back_icon);

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        appointmentsRecycler(); //fetches appointments

    }

    private void appointmentsRecycler() {
        appointmentsHistoryRecycler.setHasFixedSize(true);
        appointmentsHistoryRecycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        appointmentList = new ArrayList<>();

        //getting the curr customer id
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser curr_user = mAuth.getCurrentUser();
        String customer_id = null;
        if (curr_user != null) {
            customer_id = curr_user.getUid();
        }


        dbHelper.fetchAppointmentsHistoryForCustomer(customer_id, new FirestoreCallback<List<Appointment>>() {
            @Override
            public void onSuccess(List<Appointment> result) {
                for(Appointment appointment : result){
                    Log.d("Check appointments (RecyclerView) ",appointment.toString()+" ");
                }
                appointmentList.clear();
                appointmentList.addAll(result);
                historyAdapter.notifyDataSetChanged();
                Log.d("Check appointments (RecyclerView) size :",""+result.size());
            }

            @Override
            public void onFailure(Exception e) {//error fetching appointments
                Log.d("Check appointments (RecyclerView) ",e.getMessage());

            }
        });

        //setting the appointments adapter to the recycle and binding it with the list of appointments
        historyAdapter = new AppointmentsHistoryAdapter(appointmentList);
        appointmentsHistoryRecycler.setAdapter(historyAdapter);



    }
}