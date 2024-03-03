package com.myapp.booknow.mvvm.viewmodel.customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.booknow.Utils.FirestoreCallback;
import com.myapp.booknow.R;
import com.myapp.booknow.mvvm.model.DBHelper;
import com.myapp.booknow.mvvm.model.User;
import com.myapp.booknow.mvvm.view.BusinessAdapter2;

import java.util.ArrayList;
import java.util.List;

public class showCategoryBusinesses extends AppCompatActivity {

    private String categoryName;
    private DBHelper dbHelper;

    private RecyclerView businessesRecycler;// A recyclerView to view businesses related to this category
    private List<User> businesses; // List of appointments objects
    private BusinessAdapter2 businessAdapter; // adapter for appointments

    private ImageView backIcon;

    private TextView chosenCategoryTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_show_category_businesses);

        dbHelper = new DBHelper();

        //get the category name from the previous page (from the adapter by clicking on the item (category item in the previous page's list))

        businessesRecycler = findViewById(R.id.category_businesses_recycler);
        backIcon = findViewById(R.id.view_category_businesses_back_icon);
        chosenCategoryTitle = findViewById(R.id.chosen_category_title);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        categoryName = getIntent().getStringExtra("categoryName");
        Log.d("categoryName check"," the variable from the prev page is : "+categoryName);

        chosenCategoryTitle.setText(categoryName);

        if(categoryName != null){
            Log.e("ShowBusinessActivity", "category name is not null and its  : " + categoryName);
            businessesRecycler(); //fetches businesses
        }else{
            Toast.makeText(this, "Error: Category businesses not available.", Toast.LENGTH_LONG).show();
            Log.e("showCategoryBusinesses", "Category name is null or not available");
            Log.e("showCategoryBusinesses", "category name  is : " + categoryName);
            finish();  // Closes the current activity and returns to the previous one
        }




    }

    private void businessesRecycler() {

        businessesRecycler.setHasFixedSize(true);
        businessesRecycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        businesses = new ArrayList<>();

        dbHelper.fetchBusinessesByCategory(categoryName, new FirestoreCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> result) {
                for(User business : result){
                    Log.d("Check businesses (RecyclerView) ",business.toString()+" ");

                    businesses.clear();
                    businesses.addAll(result);
                    businessAdapter.notifyDataSetChanged();
                    Log.d("Check businesses (RecyclerView) size : ",""+result.size());

                }
            }

            @Override
            public void onFailure(Exception e) {//error fetching businesses
                Log.d("Checking businesses (RecyclerView) ",e.getMessage());
            }
        });

        businessAdapter = new BusinessAdapter2(businesses);

        businessesRecycler.setAdapter(businessAdapter);


    }


}