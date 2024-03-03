package com.myapp.booknow.mvvm.viewmodel.business;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.myapp.booknow.R;

import java.time.LocalDate;

public class AppointmentDateSelectionActivity extends AppCompatActivity{
    private CalendarView calendarView;
    private TextView textViewSelectedDate;
    private LocalDate selectedDate;
    private Button seeAppointmentsButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_appointments_date);

        calendarView = findViewById(R.id.calendarAppointments);
        textViewSelectedDate = findViewById(R.id.textViewSelectedDate);
        seeAppointmentsButton = findViewById(R.id.see_appointments_btn);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String businessId = mAuth.getCurrentUser().getUid();



        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                textViewSelectedDate.setText("Selected Date: " + selectedDate);

                seeAppointmentsButton.setEnabled(true);


            }
        });

            seeAppointmentsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AppointmentDateSelectionActivity.this, AppointmentListActivity.class);

                    intent.putExtra("businessId",businessId);
                    intent.putExtra("selectedDate",selectedDate);

                    startActivity(intent);

               }
            });

    }
}
