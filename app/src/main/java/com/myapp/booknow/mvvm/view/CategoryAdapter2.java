package com.myapp.booknow.mvvm.view;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.myapp.booknow.R;
import com.myapp.booknow.mvvm.model.Category;
import com.myapp.booknow.mvvm.viewmodel.customer.showCategoryBusinesses;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

/**
 * Adapter to bind the list of categories to a design list (like RecyclerView)
 * used in "Business Categories"/"view all categories" page
 */

public class CategoryAdapter2 extends RecyclerView.Adapter<CategoryAdapter2.CategoryViewHolder> {
    private List<Category> categories;

    public CategoryAdapter2(List<Category> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_card_view_2, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.categoryNameTextView.setText(category.getName());
        holder.categoryLogoImageView.setImageResource(category.getLogoResourceId());
        holder.itemView.setBackgroundColor(category.getBackgroundColor()); // Set background color
        holder.categoryNameTextView.setTextColor(category.getNameTextColor());


        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), showCategoryBusinesses.class);
                intent.putExtra("categoryName",category.getName());// take the name of the category to the new redirected page
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameTextView;
        ImageView categoryLogoImageView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameTextView = itemView.findViewById(R.id.category_name_text_view);
            categoryLogoImageView = itemView.findViewById(R.id.category_logo_image_view);
        }
    }
}
