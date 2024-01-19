package com.myapp.booknow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {



    private List<BusinessService> serviceList;


    ServiceAdapter(List<BusinessService> list){
        this.serviceList = list;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.service_item, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        BusinessService serviceItem = serviceList.get(position);
        holder.serviceNameTextView.setText(serviceItem.getName());

        holder.editServiceButton.setOnClickListener(v -> {
            // Handle edit service
        });

        holder.deleteServiceButton.setOnClickListener(v -> {
            // Handle delete service
        });
    }

    @Override
    public int getItemCount() {

        return serviceList.size();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView serviceNameTextView;
        ImageView editServiceButton, deleteServiceButton;

        public ServiceViewHolder(View itemView) {
            super(itemView);
            serviceNameTextView = itemView.findViewById(R.id.serviceNameTextView);
            editServiceButton = itemView.findViewById(R.id.editServiceButton);
            deleteServiceButton = itemView.findViewById(R.id.deleteServiceButton);
        }
    }




}

