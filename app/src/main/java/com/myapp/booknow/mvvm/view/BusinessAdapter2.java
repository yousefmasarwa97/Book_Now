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
public class BusinessAdapter2 extends RecyclerView.Adapter<BusinessAdapter2.ViewHolder> {

    private List<User> businessList;

    public BusinessAdapter2(List<User> businessList) {
        this.businessList = businessList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.featured_card_design_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User business = businessList.get(position);
        holder.businessName.setText(business.getName());
        holder.businessDescription.setText(business.getDescription());

        //----------------------------//
        //----------------------------//

        Glide.with(holder.itemView)
                .load(business.getImageURL())
                .placeholder(R.drawable.business_icon)
                .error(R.drawable.ic_menu_gallery)//should change !!
                .into(holder.businessLogo);
        //we can set other attributes !
        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.d("CHECKING IF BUSINESS INFO ARE CORRECT !!!! :: ","Name = " + business.getName() + "and description is = "+ business.getDescription()
                        +" and imageURL= " + business.getImageURL());
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView businessName; // and other views
        public TextView businessDescription;
        public ImageView businessLogo;
        public TextView businessStatus;

        public ViewHolder(View view) {
            super(view);
            businessName = view.findViewById(R.id.featured_title); // can replace with actual view ID
            businessLogo = view.findViewById(R.id.featured_image);
            businessDescription = view.findViewById(R.id.featured_descreption);
            businessStatus = view.findViewById(R.id.business_status);
            // Initialize other views
        }
    }


    public void setBusinesses(List<User> businesses) {
        this.businessList = businesses;
    }


}
