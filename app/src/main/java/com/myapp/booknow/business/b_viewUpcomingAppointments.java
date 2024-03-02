package com.myapp.booknow.business;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.myapp.booknow.Customer.UpcomingAppointmentsAdapter;
import com.myapp.booknow.FirestoreCallback;
import com.myapp.booknow.R;
import com.myapp.booknow.Utils.Appointment;
import com.myapp.booknow.Utils.AppointmentAdapter;
import com.myapp.booknow.Utils.DBHelper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class b_viewUpcomingAppointments extends AppCompatActivity {

    private DBHelper dbHelper;
    private RecyclerView upcomingAppointmentsRecyler;
    private List<Appointment> appointmentList;
    private bussines_UpcomingAppointmentsAdapter bussinesUpcomingAppointmentsAdapter;
    private ImageView backIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_bview_upcoming_appointments);

        dbHelper=new DBHelper();

        upcomingAppointmentsRecyler = findViewById(R.id.b_upcoming_appointments_recycler);

        backIcon = findViewById(R.id.b_upcoming_appointments_back_icon);



        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        appointmentsRecycler();


    }

    private void appointmentsRecycler() {

        upcomingAppointmentsRecyler.setHasFixedSize(true);
        upcomingAppointmentsRecyler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

            appointmentList = new ArrayList<>();

            //getting the curr customer id
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser curr_user = mAuth.getCurrentUser();
            String businessId = null;
            if (curr_user != null) {
                businessId = curr_user.getUid();
            }
            String status = "waiting";
            //LocalDateTime today = LocalDate.now().atStartOfDay();


            dbHelper.fetchAppointmentsForDate_business(businessId, status,
                    new FirestoreCallback<List<Appointment>>() {
                        @Override
                        public void onSuccess(List<Appointment> result) {

                            //checking:
                            for (Appointment appointment : result) {
                                Log.d("AppointmentListCheck", "appointment = " + appointment.toString());
                            }

                            appointmentList.clear();
                            appointmentList.addAll(result);
                            bussinesUpcomingAppointmentsAdapter.notifyDataSetChanged();
                            Log.d("appointmentsList", "Number of appointments fetched: " + result.size());
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.d("Check appointments (RecyclerView) ", e.getMessage());
                        }
                    }
            );
        bussinesUpcomingAppointmentsAdapter=new bussines_UpcomingAppointmentsAdapter(appointmentList);
        upcomingAppointmentsRecyler.setAdapter(bussinesUpcomingAppointmentsAdapter);
        }

}






