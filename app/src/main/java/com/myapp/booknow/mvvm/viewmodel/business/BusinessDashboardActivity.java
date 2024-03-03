package com.myapp.booknow.mvvm.viewmodel.business;

import static android.app.PendingIntent.getActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.LocalDateTime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.myapp.booknow.mvvm.viewmodel.customer.C_Login;
import com.myapp.booknow.Utils.FirestoreCallback;
import com.myapp.booknow.mvvm.model.Appointment;
import com.myapp.booknow.mvvm.model.BusinessSpecialOffers;
import com.myapp.booknow.mvvm.view.AppointmentAdapter;
import com.myapp.booknow.R;
import com.myapp.booknow.mvvm.model.DBHelper;
import com.myapp.booknow.mvvm.view.SpecialOfferAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Dashboard (Main page) for a customer user.
 */
public class BusinessDashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final float END_SCALE = 0.7f;// For sliding animation (navigation view)
    private FirebaseAuth mAuth;
    private RecyclerView appointmentsRecycler_B;
    private RecyclerView specialOffers_recycle;
    // private List<BusinessSpecialOffers> specialOffers;
    private AppointmentAdapter appointmentAdapter;
    //    private offersAdapter offersAdapter;
    private List<Appointment> appointmentList;
    private List<BusinessSpecialOffers> specialOffers ;
    private DBHelper dbHelper;
    public ImageView menu_button;
    private TextView textViewWelcome;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private static final String TAG = "HotSpotsFragment";
    private MapView mapView;
    private GoogleMap map;
    private SpecialOfferAdapter offerAdapter;
    private TextView appointments_viewAll;


    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_business_dashboard);
        dbHelper = new DBHelper();

        appointmentsRecycler_B = findViewById(R.id.appointments_recycler);
        specialOffers_recycle = findViewById(R.id.offer_recycler);

        appointmentList = new ArrayList<>();
        appointmentAdapter = new AppointmentAdapter(appointmentList);
        appointmentAdapter = new AppointmentAdapter(appointmentList);
        appointmentsRecycler_B.setAdapter(appointmentAdapter);
        menu_button = findViewById(R.id.menu_button);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        appointments_viewAll=findViewById(R.id.view_all_bt);


        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        //navigationDrawer();


        // Apply OnClickListener  to imageView to
        // switch from one activity to another
        menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });


        appointments_viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), b_viewUpcomingAppointments.class);
                startActivity(intent);
            }
        });







        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        specialOffers_recycle();
        appointmentsRecycler_B();
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }


    // Called when the activity is resumed after being paused or stopped
    @Override
    protected void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.nav_home);// to ensure that the drawer is set ti "Home"
    }





    private void specialOffers_recycle() {
        specialOffers_recycle.setHasFixedSize(true);
        specialOffers_recycle.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser curr_user = mAuth.getCurrentUser();
        String businessId = null;
        if (curr_user != null) {
            businessId = curr_user.getUid();
        }

        specialOffers = new ArrayList<BusinessSpecialOffers>();

        dbHelper.fetchBusinessoffer(businessId,
                new OnSuccessListener<List<BusinessSpecialOffers>>() {
                    @Override
                    public void onSuccess(List<BusinessSpecialOffers> result) {
                        for (BusinessSpecialOffers businessoffer : result) {
                            Log.d("AppointmentListCheck", "appointment = " + businessoffer.toString());
                        }

                        specialOffers.clear();
                        specialOffers.addAll(result);
                        offerAdapter.notifyDataSetChanged();
                        Log.d("appointmentsList", "Number of appointments fetched: " + result.size());
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                    }
                });
        offerAdapter=new SpecialOfferAdapter(specialOffers);
        specialOffers_recycle.setAdapter(offerAdapter);
    }


    private void appointmentsRecycler_B() {
        appointmentsRecycler_B.setHasFixedSize(true);
        appointmentsRecycler_B.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        appointmentList = new ArrayList<>();

        //getting the curr customer id
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser curr_user = mAuth.getCurrentUser();
        String businessId = null;
        if (curr_user != null) {
            businessId = curr_user.getUid();
        }
        String status = "waiting";
        LocalDateTime today = LocalDate.now().atStartOfDay();


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
                        appointmentAdapter.notifyDataSetChanged();
                        Log.d("appointmentsList", "Number of appointments fetched: " + result.size());
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.d("Check appointments (RecyclerView) ", e.getMessage());
                    }
                }
        );
        appointmentAdapter=new AppointmentAdapter(appointmentList);
        appointmentsRecycler_B.setAdapter(appointmentAdapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(Menu.NONE, R.id.working_hours, 0, "show setting");

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int item_id = (item.getItemId());


        if (item_id == R.id.working_hours) {


            Intent show = new Intent(this, BusinessRegularHoursEditActivity.class);
            startActivity(show);
            return true;


        } else if (item_id == R.id.edit_service) {

            Intent intent = new Intent(BusinessDashboardActivity.this, BusinessServicesManagementActivity.class);
            startActivity(intent);


        } else if (item_id == R.id.service_provider) {

            Intent intent = new Intent(BusinessDashboardActivity.this, ServiceProviderSetNameAndServicesActivity.class);
            startActivity(intent);


        } else if (item_id == R.id.specific_dates) {

            Intent intent = new Intent(BusinessDashboardActivity.this, BusinessSpecialHoursEditActivity.class);
            startActivity(intent);


        } else if (item_id == R.id.log_out) {
//            Intent edit_specific_dates = new Intent(BusinessDashboardActivity.this, C_Login.class);
//            startActivity(edit_specific_dates);
            showLogoutConfirmationDialog();
        }else if (item_id == R.id.special_offers) {
            Intent edit_offer= new Intent(BusinessDashboardActivity.this, specialOfferManagmante.class);
            startActivity(edit_offer);

        }
        else if (item_id == R.id.account) {
            Intent account= new Intent(BusinessDashboardActivity.this, BusinessSetupActivity.class);
            startActivity(account);

        }


        drawerLayout.closeDrawer(GravityCompat.START);
        return true;


}

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