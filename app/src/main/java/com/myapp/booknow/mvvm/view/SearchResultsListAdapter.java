package com.myapp.booknow.mvvm.view;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.myapp.booknow.R;
import com.myapp.booknow.mvvm.viewmodel.customer.ShowBusinessActivity;
import com.myapp.booknow.mvvm.model.User;

import java.util.List;

/**
 * Adapter for RecyclerView to display business information.
 * This adapter handles the layout and binding of business data
 * to the views defined in the ViewHolder.
 */
public class SearchResultsListAdapter extends RecyclerView.Adapter<SearchResultsListAdapter.searchViewHolder> {

    private List<User> businessList;

    public SearchResultsListAdapter(List<User> businessList) {
        this.businessList = businessList;
    }

    @Override
    public searchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.business_item, parent, false);
        return new searchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(searchViewHolder holder, int position) {

        // business object (list[position])
        User business = businessList.get(position);

        // set the name
        holder.businessName.setText(business.getName());

        // set the image
        Glide.with(holder.itemView)
                .load(business.getImageURL())
                .placeholder(R.drawable.business_icon)
                .error(R.drawable.ic_menu_gallery)//should change !!
                .into(holder.businessLogo);


        //we can set other attributes !


        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.d("CHECKING IF BUSINESS INFO ARE CORRECT !!!! :: ","Name = " + business.getName() + " and imageURL= " + business.getImageURL());
                Intent intent = new Intent(v.getContext(), ShowBusinessActivity.class);
                intent.putExtra("businessId",business.getId());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return businessList.size();
    }

    public static class searchViewHolder extends RecyclerView.ViewHolder {
        public TextView businessName; // and other views
        public ImageView businessLogo;
        public searchViewHolder(View view) {
            super(view);
            businessName = view.findViewById(R.id.businessName); // can replace with actual view ID
            businessLogo = view.findViewById(R.id.businessLogo);
            // Initialize other views
        }
    }



    public void setBusinesses(List<User> businesses) {
        this.businessList = businesses;
        notifyDataSetChanged();
    }


}
