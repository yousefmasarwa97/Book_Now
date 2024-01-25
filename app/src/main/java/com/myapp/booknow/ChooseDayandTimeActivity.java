package com.myapp.booknow;

import android.os.Bundle;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class ChooseDayandTimeActivity extends AppCompatActivity {

    private CalendarView calendarView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_day_time);

        calendarView = findViewById(R.id.calendarView);

        // Set the minimum available date to today's date
        calendarView.setMinDate(System.currentTimeMillis() - 1000);

        // Calculate the date two months from now
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 12); // Add 12 months to the current date

        // Set the maximum date to two months from now
        calendarView.setMaxDate(calendar.getTimeInMillis());


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Handling the selected day
                // Will fetch working hours + service details for the selected day


            }
        });
    }
}
