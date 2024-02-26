package com.myapp.booknow.Customer;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.myapp.booknow.R;
import com.myapp.booknow.Utils.Appointment;
import com.myapp.booknow.Utils.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class viewCategories extends AppCompatActivity {

    private RecyclerView categoriesRecycler;// A recyclerView to view the upcoming appointments
    private List<Category> categories; // List of appointments objects
    private CategoryAdapter2 categoryAdapter; // adapter for appointments


    private ImageView backIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.view_all_categories);



        categoriesRecycler = findViewById(R.id.categories_recycler);

        backIcon = findViewById(R.id.view_categories_back_icon);



        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




        categoriesRecycler(); //fetches appointments


    }


    private void categoriesRecycler() {
        categoriesRecycler.setHasFixedSize(true);
        categoriesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        categories = new ArrayList<>();

        // This time is not like previous lists, we set it manually instead of fetching from DB.
        categories.add(new Category("Health",R.drawable.category_health, Color.parseColor("#D8F3DC")));
        categories.add(new Category("Beauty",R.drawable.category_beauty, Color.parseColor("#F5F5DC")));
        categories.add(new Category("Barbershops",R.drawable.category_barbershops,Color.parseColor("#333333"),Color.parseColor("#FFFFFF")));
        categories.add(new Category("Home Services",R.drawable.category_home,Color.parseColor("#ffff7a")));
        categories.add(new Category("Legal\n and\n Financial",R.drawable.category_legal_financial,Color.parseColor("#fcfcfd")));
        categories.add(new Category("Fitness\n and\n Recreation",R.drawable.category_fitness_2,Color.parseColor("#CCE5FF")));
        categories.add(new Category("Education & Learning", R.drawable.category_education,Color.parseColor("#FFD1DC")));
        categories.add(new Category("Pet Care Services", R.drawable.category_pets_service,Color.parseColor("#D2B48C")));
        categories.add(new Category("Shopping\nand\nRetail",R.drawable.category_shopping,Color.parseColor("#FFFFE0")));
        categories.add(new Category("Travelinging\nand\nHospitality",R.drawable.category_traveling,Color.parseColor("#87CEEB")));
        categories.add(new Category("Entertainment\nand\nEvents",R.drawable.category_enertainment_2,Color.parseColor("#ffa600")));

        categoryAdapter = new CategoryAdapter2(categories);

        categoriesRecycler.setAdapter(categoryAdapter);
    }
}
