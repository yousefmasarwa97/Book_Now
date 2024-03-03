package com.myapp.booknow.mvvm.view;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myapp.booknow.R;
import com.myapp.booknow.mvvm.model.BusinessService;
import com.myapp.booknow.mvvm.viewmodel.business.BusinessServiceEditActivity;
import com.myapp.booknow.mvvm.viewmodel.business.BusinessServicesManagementActivity;
import com.myapp.booknow.mvvm.model.DBHelper;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {



    private List<BusinessService> serviceList;


    public ServiceAdapter(List<BusinessService> list){
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

        // Handle edit service
        holder.editServiceButton.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(v.getContext(), BusinessServiceEditActivity.class);
                intent.putExtra("serviceId", serviceItem.getServiceId());
                v.getContext().startActivity(intent);
            }catch (Exception e){
                Log.e("ServiceAdapter", "Error starting EditServiceActivity", e);
            }

            });

        // Handle delete service
        holder.deleteServiceButton.setOnClickListener(v -> {
            String serviceId = serviceItem.getServiceId();
            DBHelper dbHelper = new DBHelper();
            dbHelper.deleteBusinessService(serviceId,
                    aVoid -> {
                        // Success handling. Perhaps refresh the list of services
                        Toast.makeText(v.getContext(), "Service deleted successfully", Toast.LENGTH_SHORT).show();
                        ((BusinessServicesManagementActivity) v.getContext()).fetchServices();
                    },
                    e -> {
                        // Failure handling
                        Toast.makeText(v.getContext(), "Error deleting service", Toast.LENGTH_SHORT).show();
                    });
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

