package com.myapp.booknow;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BusinessWorkingHoursActivity extends AppCompatActivity {

    // TimePickers for each day
    TimePicker timePickerSundayOpen , timePickerSundayClose;
    TimePicker timePickerMondayOpen, timePickerMondayClose;
    TimePicker timePickerTuesdayOpen, timePickerTuesdayClose;
    TimePicker timePickerWednesdayOpen, timePickerWednesdayClose;
    TimePicker timePickerThursdayOpen, timePickerThursdayClose;
    TimePicker timePickerFridayOpen, timePickerFridayClose;
    TimePicker timePickerSaturdayOpen, timePickerSaturdayClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_working_hours_activity);

        // Initializing TimePickers for each day

        timePickerSundayOpen = findViewById(R.id.timePickerSundayOpen);
        timePickerSundayClose = findViewById(R.id.timePickerSundayClose);

        timePickerMondayOpen = findViewById(R.id.timePickerMondayOpen);
        timePickerMondayClose = findViewById(R.id.timePickerMondayClose);

        timePickerTuesdayOpen = findViewById(R.id.timePickerTusedayOpen);
        timePickerTuesdayClose = findViewById(R.id.timePickerTuesdayClose);

        timePickerWednesdayOpen = findViewById(R.id.timePickerWednesdayOpen);
        timePickerWednesdayClose = findViewById(R.id.timePickerWendesdayClose);

        timePickerThursdayOpen = findViewById(R.id.timePickerThursdayOpen);
        timePickerThursdayClose = findViewById(R.id.timePickerThursdayClose);

        timePickerFridayOpen = findViewById(R.id.timePickerFridayOpen);
        timePickerFridayClose = findViewById(R.id.timePickerFridayClose);

        timePickerSaturdayOpen = findViewById(R.id.timePickerSaturdayOpen);
        timePickerSaturdayClose = findViewById(R.id.timePickerSaturdayClose);


        //handling "save" button
        Button btnSaveWorkingHours = findViewById(R.id.btnSaveWorkingHours);
        btnSaveWorkingHours.setOnClickListener(view -> saveWorkingHours());
    }

    private void saveWorkingHours() {
        Map<String, BusinessHours> hours = new HashMap<>();


        //Getting the currently logged in user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser curr_user = mAuth.getCurrentUser();
        String businessId = null;
        if (curr_user != null) {
            businessId = curr_user.getUid();
        }



        // Add each day hours
        hours.put("Sunday", getBusinessHours(businessId , "Sunday" ,timePickerSundayOpen, timePickerSundayClose));
        hours.put("Monday", getBusinessHours(businessId , "Monday" ,timePickerMondayOpen, timePickerMondayClose));
        hours.put("Tuesday", getBusinessHours(businessId , "Tuesday" ,timePickerTuesdayOpen, timePickerTuesdayClose));
        hours.put("Wednesday", getBusinessHours(businessId , "Wednesday" ,timePickerWednesdayOpen, timePickerWednesdayClose));
        hours.put("Thursday", getBusinessHours(businessId , "Thursday" ,timePickerThursdayOpen, timePickerThursdayClose));
        hours.put("Friday", getBusinessHours(businessId , "Friday" ,timePickerFridayOpen, timePickerFridayClose));
        hours.put("Saturday", getBusinessHours(businessId , "Saturday" ,timePickerSaturdayOpen, timePickerSaturdayClose));

        //-----END----//



        if(businessId !=null){
            DBHelper dbHelper = new DBHelper();
            dbHelper.setBusinessHours(businessId, hours);
        }
        else{//Handling the case where business is null
            Toast.makeText(this, "Error: Business ID not found.", Toast.LENGTH_SHORT).show();
        }







    }




    private BusinessHours getBusinessHours(String businessId, String day, TimePicker openTimePicker, TimePicker closeTimePicker) {
        Calendar openCalendar = Calendar.getInstance();
        openCalendar.set(Calendar.HOUR_OF_DAY, openTimePicker.getCurrentHour());
        openCalendar.set(Calendar.MINUTE, openTimePicker.getCurrentMinute());

        Calendar closeCalendar = Calendar.getInstance();
        closeCalendar.set(Calendar.HOUR_OF_DAY, closeTimePicker.getCurrentHour());
        closeCalendar.set(Calendar.MINUTE, closeTimePicker.getCurrentMinute());

        Timestamp openTime = new Timestamp(openCalendar.getTime());
        Timestamp closeTime = new Timestamp(closeCalendar.getTime());

        return new BusinessHours( businessId ,  day , openTime, closeTime);
    }
}
