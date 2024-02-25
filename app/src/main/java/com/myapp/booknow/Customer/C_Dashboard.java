package com.myapp.booknow.Customer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
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

public class C_Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final float END_SCALE = 0.7f;// For sliding animation (navigation view)
    public static final int NAV_HOME_ID = R.id.nav_home;

    //Drawer menu :
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView phoneNumber_nav;
    private ImageView menuIcon;

    private LinearLayout contentView;// The whole page content (except the navigation view)

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



    @SuppressLint("MissingInflatedId")
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

        // For the menu
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);

        // For the content :
        contentView = findViewById(R.id.content_view);

        // To access phone number and name TextViews in the menu_header.xml :
        View headerView = navigationView.getHeaderView(0);
        phoneNumber_nav = headerView.findViewById(R.id.user_phone);

        setPhoneNumberView();// Should make the function also set the name


        navigationDrawer();

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



    private void setPhoneNumberView() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser curr_user = mAuth.getCurrentUser();
        String c_id = null;
        if (curr_user != null) {
            c_id = curr_user.getUid();
        }

        dbHelper.getCustomerPhoneNumber(c_id, new FirestoreCallback<String>() {
            @Override
            public void onSuccess(String result) {
                phoneNumber_nav.setText(result);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

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



    //--------Navigation Drawer functions--------//

    private void navigationDrawer(){


        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);


        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerLayout.isDrawerVisible(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                else{
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        animateNavigationDrawer();

    }


    // Navigation animation
    private void animateNavigationDrawer() {
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                    final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                    final float offsetScale = 1 - diffScaledOffset;
                    contentView.setScaleX(offsetScale);
                    contentView.setScaleY(offsetScale);

                    final float xOffset = drawerView.getWidth() * slideOffset;
                    final float xoffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                    final float xTranslation = xOffset - xoffsetDiff;
                    contentView.setTranslationX(xTranslation);
            }
            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }
            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
            }
            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerVisible(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }

    }


    // Called when the activity is resumed after being paused or stopped
    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.nav_home);// to ensure that the drawer is set ti "Home"
    }

    // Handles all the onClick events of our navigation items
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Handling navigation view item clicks

        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            if (!isCurrentPage(C_Dashboard.class)) {
                startActivity(new Intent(this, C_Dashboard.class));
            }
        }
        else if(itemId == R.id.my_upcoming_appointments){
            if (!isCurrentPage(viewUpcomingAppointments.class)) {
                startActivity(new Intent(this, viewUpcomingAppointments.class));
            }
        }
        else if(itemId == R.id.log_out){
            showLogoutConfirmationDialog();
        }
        else if(itemId == R.id.my_previous_appointments){
            if(!isCurrentPage(viewAppointmentsHistory.class)){
                startActivity(new Intent(this, viewAppointmentsHistory.class));
            }
        }



        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    // Method to check if the given activity is the current page
    private boolean isCurrentPage(Class<? extends Activity> activityClass) {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            try {
                return Class.forName(topActivity.getClassName()).equals(activityClass);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // Called when clicking on "LOGOUT" in the drawer,
    // if "Cancel" is clicked : continue ,, if "Yes" is clicked : sign out the current user
    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logoutUser();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    // Signs out the current user, and redirects to the Customer Login page
    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, C_Login.class));
        finish();
    }



}




