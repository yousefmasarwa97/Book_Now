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

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> categories;

    public CategoryAdapter(List<Category> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_card_view, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.categoryNameTextView.setText(category.getName());
        holder.categoryLogoImageView.setImageResource(category.getLogoResourceId());
        holder.itemView.setBackgroundColor(category.getBackgroundColor()); // Set background color
        holder.categoryNameTextView.setTextColor(category.getNameTextColor());

        // Handling clicking the element itself
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
