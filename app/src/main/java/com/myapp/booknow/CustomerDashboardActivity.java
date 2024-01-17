package com.myapp.booknow;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

/**
 * Dashboard (Main page) for a customer user.
 */
public class CustomerDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BusinessAdapter adapter;
    private DBHelper dbHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        recyclerView = findViewById(R.id.businessRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        dbHelper = new DBHelper();
        fetchBusinesses();

    }

    private void fetchBusinesses(){
        dbHelper.viewBusinesses(new OnSuccessListener<List<User>>() {
            @Override
            public void onSuccess(List<User> businesses) {
                adapter = new BusinessAdapter(businesses);
                recyclerView.setAdapter(adapter);
            }
        });
    }



}
