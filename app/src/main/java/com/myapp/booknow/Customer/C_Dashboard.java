package com.myapp.booknow.Customer;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

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
import com.myapp.booknow.business.SearchResultsListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class C_Dashboard extends AppCompatActivity {

    //Attributes :
    //--------Data Base----------//
    private DBHelper dbHelper;


    //-----Search Box-------//
    private EditText searchText;

    //-----Search results recyclerView------//
    private RecyclerView searchResultsRecyclerView;
    private SearchResultsListAdapter searchAdapter;


    //-------Businesses Design--------//
    private RecyclerView businessesRecycler; // A recyclerView to view businesses list
    private List<User> businesses;// list of businesses (objects) to show
    private BusinessAdapter business_adapter; // adapter to adapt the business objects to the view


    //-------Appointments Design---------//
    private RecyclerView appointmentsRecycler; // A recyclerView to view the upcoming appointments

    private List<Appointment> appointmentList; // List of appointments objects
    private CustomerAdapter customerAdapter; // adapter for appointments

    private TextView viewAll;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // Removing the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.c_dashboard);

        dbHelper = new DBHelper();

        //
        searchText = findViewById(R.id.search_text);
        viewAll = findViewById(R.id.view_all);

        businessesRecycler = findViewById(R.id.featured_recycler);
        appointmentsRecycler = findViewById(R.id.appointments_recycler);

        // Search results recycler :

        searchResultsRecyclerView = findViewById(R.id.search_results_recycler);
        searchAdapter = new SearchResultsListAdapter(new ArrayList<>());
        searchResultsRecyclerView.setAdapter(searchAdapter);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        //--------------------------------------------------------------------------------------//

        // Listener for the search bar :
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // not used here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // filtering the data based on user's input
                List<User> filtered_businesses = filterData(s.toString());// list of filtered businesses based on user input
                searchAdapter.setBusinesses(filtered_businesses);
                if(s.toString().isEmpty()){
                    searchResultsRecyclerView.setVisibility(View.GONE);
                }
               // searchAdapter.notifyDataSetChanged();// Notify adapter that the data set has changed
                else{
                    searchResultsRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().isEmpty()) {
                    searchText.clearFocus();
                }else{
                    searchText.requestFocus();
                }
            }
        });


        // Listener for "view all"
        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),viewUpcomingAppointments.class);
                startActivity(intent);
            }
        });



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


    // Method to filter data based on the user's input
    private List<User> filterData(String userInput){
        // Filtering logic :
        // 1)Iterate through the original list of businesses
        // 2)Check if the business name contains the user input
        // 3)If it matches add the business to the filtered list
        // 4)Return the filtered list

        List<User> filteredList = new ArrayList<>();

        String query = userInput.toLowerCase(Locale.getDefault());

        Log.d("FilterData", "User input: " + query);

        for(User business : businesses){
            if(business.getName().toLowerCase(Locale.getDefault()).contains(query)){
                filteredList.add(business);
            }
        }

        Log.d("FilterData", "Filtered list size: " + filteredList.size());
        return filteredList;
    }


}




