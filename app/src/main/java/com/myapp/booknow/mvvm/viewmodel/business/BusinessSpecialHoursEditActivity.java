package com.myapp.booknow.mvvm.viewmodel.business;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.myapp.booknow.mvvm.model.DBHelper;
import com.myapp.booknow.R;
import com.myapp.booknow.Utils.TimeUtils;

import java.time.LocalDate;
import java.util.Locale;

public class BusinessSpecialHoursEditActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView textViewSelectedDate;
    private TextView textViewOpenTime, textViewCloseTime;
    private Button editHoursButton;
    private LinearLayout layoutHours;

    private LocalDate selectedDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_business_special_hours_edit);

        calendarView = findViewById(R.id.calendarViewSpecialHours);
        textViewSelectedDate = findViewById(R.id.textViewSelectedDate);
        textViewOpenTime = findViewById(R.id.textViewOpenTime);
        textViewCloseTime = findViewById(R.id.textViewCloseTime);
        editHoursButton = findViewById(R.id.buttonEditHours);
        layoutHours = findViewById(R.id.layoutHours);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String businessId = mAuth.getCurrentUser().getUid();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                textViewSelectedDate.setText("Selected Date: " + selectedDate);
                layoutHours.setVisibility(View.VISIBLE);

                editHoursButton.setEnabled(true);
                // Fetch and display the current regular hours for the selected date

                DBHelper dbHelper = new DBHelper();
                dbHelper.fetchWorkingHours(businessId, selectedDate, workingHours -> {
                    // Display the fetched hours on the screen
                    String openTimeFormatted = TimeUtils.formatTimestamp(workingHours.getOpenTime());
                    String closeTimeFormatted = TimeUtils.formatTimestamp(workingHours.getCloseTime());

                    Log.d("Special","the date is :::: " + selectedDate.toString());
                    Log.d("Special","the day is :::: " + selectedDate.getDayOfWeek().toString());
                    Log.d("Special","the hours are :::: " + openTimeFormatted +"and " + closeTimeFormatted);

                    textViewOpenTime.setText("Open Time: " + openTimeFormatted);
                    textViewCloseTime.setText("Close Time: " + closeTimeFormatted);

                }, e -> {
                    // Handle any errors
                    Toast.makeText(BusinessSpecialHoursEditActivity.this, "Error fetching hours", Toast.LENGTH_SHORT).show();
                    Log.d("Special","the day is :::: " + selectedDate.getDayOfWeek().toString());
                });


                // Allow the business to change these hours
                // You will need to implement the logic to fetch and update hours!!
            }
        });

        editHoursButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditHoursDialog(selectedDate,businessId);
            }
        });
    }



    public void showEditHoursDialog(LocalDate selectedDate, String businessId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_special_hours, null);
        builder.setView(dialogView);


        //------------------------------------OPENING & Closing----------------------------------------//
        NumberPicker numberPickerOpenHour = dialogView.findViewById(R.id.numberPickerOpenHour);
        NumberPicker numberPickerOpenMinute = dialogView.findViewById(R.id.numberPickerOpenMinute);
        NumberPicker numberPickerCloseHour = dialogView.findViewById(R.id.numberPickerCloseHour);
        NumberPicker numberPickerCloseMinute = dialogView.findViewById(R.id.numberPickerCloseMinute);
        // Configure hours picker
        numberPickerOpenHour.setMinValue(0);
        numberPickerOpenHour.setMaxValue(23);
        numberPickerOpenHour.setValue(9); // Default value or fetch from existing data

        // Configure close hours picker
        numberPickerCloseHour.setMinValue(0);
        numberPickerCloseHour.setMaxValue(23);
        numberPickerCloseHour.setValue(17); // Set default or existing value

        // Configure minutes picker
        numberPickerOpenMinute.setMinValue(0);
        numberPickerOpenMinute.setMaxValue(59);
        numberPickerOpenMinute.setValue(0); // Default value
        numberPickerOpenMinute.setFormatter(value -> String.format(Locale.getDefault(), "%02d", value));

        // Configure close minutes picker
        numberPickerCloseMinute.setMinValue(0);
        numberPickerCloseMinute.setMaxValue(59);
        numberPickerCloseMinute.setValue(0); // Set default or existing value
        numberPickerCloseMinute.setFormatter(value -> String.format(Locale.getDefault(), "%02d", value));

        //-------------------------------------------------------------------------------------------------------//

        builder.setPositiveButton("OK", (dialog, which) -> {
            int openHour = numberPickerOpenHour.getValue();
            int openMinute = numberPickerOpenMinute.getValue();
            int closeHour = numberPickerCloseHour.getValue();
            int closeMinute = numberPickerCloseMinute.getValue();

            // Construct time strings
            String openTime = String.format(Locale.getDefault(), "%02d:%02d", openHour, openMinute);
            String closeTime = String.format(Locale.getDefault(), "%02d:%02d", closeHour, closeMinute);

            // Save the new hours to Firestore
            saveSpecialHours(selectedDate,businessId,openTime,closeTime);

        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveSpecialHours(LocalDate selectedDate, String businessId, String openTimestr, String closeTimestr){
        DBHelper dbHelper = new DBHelper();
        dbHelper.addOrUpdateSpecialHours(businessId,selectedDate,openTimestr,closeTimestr);
    }
}
