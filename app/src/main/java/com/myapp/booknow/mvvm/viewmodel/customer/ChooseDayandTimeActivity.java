package com.myapp.booknow.mvvm.viewmodel.customer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.myapp.booknow.mvvm.model.DBHelper;
import com.myapp.booknow.R;
import com.myapp.booknow.Utils.ServiceAvailabilityCallback;
import com.myapp.booknow.Utils.TimeUtils;

import java.time.LocalDate;
import java.util.Calendar;

public class ChooseDayandTimeActivity extends AppCompatActivity {


    private String businessId , serviceId;

    private CalendarView calendarView;
    private Button nextButton;

    private LocalDate selectedDate;//added
    private TextView textViewSelectedDate;//added

    private TextView textViewOpenTime, textViewCloseTime;//added
    private LinearLayout layoutHours;//added

    private ImageView backButton;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_choose_day_time);

        businessId = getIntent().getStringExtra("businessId");
        serviceId = getIntent().getStringExtra("serviceId");

        calendarView = findViewById(R.id.calendarViewSelectDateToBook);
        textViewSelectedDate = findViewById(R.id.textViewSelectedDate);
        textViewOpenTime = findViewById(R.id.textViewOpenTime);
        textViewCloseTime = findViewById(R.id.textViewCloseTime);
        layoutHours = findViewById(R.id.layoutHours);
        nextButton = findViewById(R.id.buttonGotoTimeSlotsSelection);
        backButton = findViewById(R.id.date_back_icon);

        // Set the minimum available date to today's date
        calendarView.setMinDate(System.currentTimeMillis() - 1000);
        // Calculate the date two months from now
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 12); // Add 12 months to the current date

        // Set the maximum date to two months from now
        calendarView.setMaxDate(calendar.getTimeInMillis());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Handling the selected day
                // Will fetch working hours + service details for the selected day
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                textViewSelectedDate.setText("Selected Date: " + selectedDate);
                layoutHours.setVisibility(View.VISIBLE);

                DBHelper dbHelper = new DBHelper();

                dbHelper.isServiceAvailable(businessId, serviceId, selectedDate, new ServiceAvailabilityCallback() {
                    @Override
                    public void onResult(boolean isAvailable) {
                        if (isAvailable) {
                            // Handle service available
                            Log.d("ServiceCheck", "Service is available.");
                            nextButton.setEnabled(true);
                        } else {
                            // Handle service not available
                            Log.d("ServiceCheck", "Service is not available.");
                            Toast.makeText(ChooseDayandTimeActivity.this,"Service is not available on this day , service" +
                                    "id =  "+ serviceId , Toast.LENGTH_SHORT).show();
                            if(nextButton.isEnabled()){
                                nextButton.setEnabled(false);
                            }
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        // Handle error
                        Log.e("ServiceCheck", "Error checking service availability: " + e.getMessage());
                        Toast.makeText(ChooseDayandTimeActivity.this,"Service is not available on this day", Toast.LENGTH_SHORT).show();
                    }
                });


                // Fetch and display the current regular hours for the selected date

                dbHelper.fetchWorkingHours(businessId, selectedDate, workingHours -> {
                    // Display the fetched hours on the screen
                    String openTimeFormatted = TimeUtils.formatTimestamp(workingHours.getOpenTime());
                    String closeTimeFormatted = TimeUtils.formatTimestamp(workingHours.getCloseTime());

                    Log.d("ChooseDate","the date is :::: " + selectedDate.toString());
                    Log.d("ChooseDate","the day is :::: " + selectedDate.getDayOfWeek().toString());
                    Log.d("ChooseDate","the hours are :::: " + openTimeFormatted +"and " + closeTimeFormatted);

                    textViewOpenTime.setText("Open Time: " + openTimeFormatted);
                    textViewCloseTime.setText("Close Time: " + closeTimeFormatted);

                }, e -> {
                    // Handle any errors
                    Toast.makeText(ChooseDayandTimeActivity.this, "Error fetching hours", Toast.LENGTH_SHORT).show();
                    Log.d("ChooseDate","the day is :::: " + selectedDate.getDayOfWeek().toString());
                });

            }
        });



        // Handling the next button (choose time slot)
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseDayandTimeActivity.this, TimeSlotSelectionActivity.class);
                intent.putExtra("businessId",businessId);
                intent.putExtra("serviceId",serviceId);
                intent.putExtra("selectedDate",selectedDate);


                startActivity(intent);
            }
        });
    }
}
