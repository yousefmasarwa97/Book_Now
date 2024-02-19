package com.myapp.booknow.Customer;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.myapp.booknow.FirestoreCallback;
import com.myapp.booknow.R;
import com.myapp.booknow.Utils.Appointment;
import com.myapp.booknow.Utils.DBHelper;
import com.myapp.booknow.Utils.User;
import com.myapp.booknow.business.BusinessAdapter;

import java.util.ArrayList;
import java.util.List;

public class C_Dashboard extends AppCompatActivity {

    //Attributes :
    //--------Data Base----------//
    private DBHelper dbHelper;


    //-------Businesses Design--------//
    private RecyclerView businessesRecycler; // A recyclerView to view businesses list
    private List<User> businesses;// list of businesses (objects) to show
    private BusinessAdapter business_adapter; // adapter to adapt the business objects to the view


    //-------Appointments Design---------//
    private RecyclerView appointmentsRecycler; // A recyclerView to view the upcoming appointments

    private List<Appointment> appointmentList; // List of appointments objects
    private CustomerAdapter customerAdapter; // adapter for appointments



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // Removing the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.c_dashboard);

        dbHelper = new DBHelper();

        // Hooks
        businessesRecycler = findViewById(R.id.featured_recycler);
        appointmentsRecycler = findViewById(R.id.appointments_recycler);


        businessesRecycler(); //fetches businesses
        appointmentsRecycler(); //fetches appointments

    }



    private void businessesRecycler() {

        businessesRecycler.setHasFixedSize(true);
        businessesRecycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        businesses = new ArrayList<>();

        dbHelper.fetchBusinesses(new FirestoreCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> result) {
                for(User business : result){
                    Log.d("Check businesses (RecyclerView) ",business.toString()+" ");

                    businesses.clear();
                    businesses.addAll(result);
                    business_adapter.notifyDataSetChanged();
                    Log.d("Check businesses (RecyclerView) size : ",""+result.size());

                }
            }

            @Override
            public void onFailure(Exception e) {//error fetching businesses
                Log.d("Checking businesses (RecyclerView) ",e.getMessage());
            }
        });

        business_adapter = new BusinessAdapter(businesses);

        businessesRecycler.setAdapter(business_adapter);



    }


    private void appointmentsRecycler() {
        appointmentsRecycler.setHasFixedSize(true);
        appointmentsRecycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

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
                customerAdapter.notifyDataSetChanged();
                Log.d("Check appointments (RecyclerView) size :",""+result.size());
            }

            @Override
            public void onFailure(Exception e) {//error fetching appointments
                Log.d("Check appointments (RecyclerView) ",e.getMessage());

            }
        });

        //setting the appointments adapter to the recycle and binding it with the list of appointments
        customerAdapter = new CustomerAdapter(appointmentList);
        appointmentsRecycler.setAdapter(customerAdapter);



    }


}




