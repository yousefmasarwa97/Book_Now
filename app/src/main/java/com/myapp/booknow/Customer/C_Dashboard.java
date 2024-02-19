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
import com.myapp.booknow.FirestoreCallback;
import com.myapp.booknow.R;
import com.myapp.booknow.Utils.Appointment;
import com.myapp.booknow.Utils.DBHelper;
import com.myapp.booknow.Utils.User;
import com.myapp.booknow.business.BusinessAdapter;

import java.util.ArrayList;
import java.util.List;

public class C_Dashboard extends AppCompatActivity {

    private DBHelper dbHelper;
    RecyclerView businessesRecycler; // A recyclerView to view businesses list
    private List<User> businesses;// list of businesses to show
    private BusinessAdapter business_adapter;

    private RecyclerView recyclerView_app; // A recyclerView to view the upcoming appointments

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // Removing the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.c_dashboard);

        dbHelper = new DBHelper();

        // Hooks
        businessesRecycler = findViewById(R.id.featured_recycler);



        businessesRecycler();//fetches businesses

    }

    private void businessesRecycler() {

        businessesRecycler.setHasFixedSize(true);
        businessesRecycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        businesses = new ArrayList<>();

        dbHelper.fetchBusinesses(new FirestoreCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> result) {
                for(User business : result){
                    Log.d("Check businesses (RecyclerView)",business.toString()+" ");

                    businesses.clear();
                    businesses.addAll(result);
                    business_adapter.notifyDataSetChanged();
                    Log.d("Check businesses (RecyclerView) size : ",""+result.size());

                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("Checking businesses (RecyclerView)",e.getMessage());
            }
        });

        business_adapter = new BusinessAdapter(businesses);

        businessesRecycler.setAdapter(business_adapter);

        GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]
        {0xffeff400,0xffaff600});

    }
}
