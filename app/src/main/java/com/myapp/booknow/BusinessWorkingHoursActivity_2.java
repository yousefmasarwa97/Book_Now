package com.myapp.booknow;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;


/**
 * This class handles the editing of a business schedule (working hours in general)
 */
public class BusinessWorkingHoursActivity_2 extends AppCompatActivity {

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
        setContentView(R.layout.activity_business_working_hours);

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
        Map<String, BusinessRegularHours> hours = new HashMap<>();


        //Getting the currently logged in user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser curr_user = mAuth.getCurrentUser();
        String businessId = null;
        if (curr_user != null) {
            businessId = curr_user.getUid();
        }




        // Add each day hours
        hours.put("Sunday", getBusinessRegularHours(businessId , "Sunday" ,timePickerSundayOpen, timePickerSundayClose));
        hours.put("Monday", getBusinessRegularHours(businessId , "Monday" ,timePickerMondayOpen, timePickerMondayClose));
        hours.put("Tuesday", getBusinessRegularHours(businessId , "Tuesday" ,timePickerTuesdayOpen, timePickerTuesdayClose));
        hours.put("Wednesday", getBusinessRegularHours(businessId , "Wednesday" ,timePickerWednesdayOpen, timePickerWednesdayClose));
        hours.put("Thursday", getBusinessRegularHours(businessId , "Thursday" ,timePickerThursdayOpen, timePickerThursdayClose));
        hours.put("Friday", getBusinessRegularHours(businessId , "Friday" ,timePickerFridayOpen, timePickerFridayClose));
        hours.put("Saturday", getBusinessRegularHours(businessId , "Saturday" ,timePickerSaturdayOpen, timePickerSaturdayClose));

        //-----END----//

        if(businessId !=null){
            DBHelper dbHelper = new DBHelper();
            dbHelper.setBusinessRegularHours(businessId, hours);
        }
        else{//Handling the case where business is null
            Toast.makeText(this, "Error: Business ID not found.", Toast.LENGTH_SHORT).show();
        }







    }




    private BusinessRegularHours getBusinessRegularHours(String businessId, String day, TimePicker openTimePicker, TimePicker closeTimePicker) {
        String openTime = convert_TimePicker_to_String(openTimePicker);
        String closeTime = convert_TimePicker_to_String(closeTimePicker);
        return new BusinessRegularHours( businessId ,  day , openTime, closeTime);
    }


    private String convert_TimePicker_to_String(TimePicker timePicker){
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        String timeString = String.format("%02d:%02d", hour , minute);

        return timeString;
    }
}
