package com.myapp.booknow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * Adapter for RecyclerView to display business information.
 * This adapter handles the layout and binding of business data
 * to the views defined in the ViewHolder.
 */
public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.ViewHolder> {

    private List<User> businessList;

    public BusinessAdapter(List<User> businessList) {
        this.businessList = businessList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.business_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User business = businessList.get(position);
        holder.businessName.setText(business.getName());
        // Set other attributes if necessary
    }

    @Override
    public int getItemCount() {
        return businessList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView businessName; // and other views

        public ViewHolder(View view) {
            super(view);
            businessName = view.findViewById(R.id.businessName); // can replace with actual view ID
            // Initialize other views
        }
    }


    public void setBusinesses(List<User> businesses) {
        this.businessList = businesses;
    }


}
