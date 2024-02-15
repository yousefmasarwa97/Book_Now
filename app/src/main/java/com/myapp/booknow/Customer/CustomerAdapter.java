package com.myapp.booknow.Customer;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myapp.booknow.Utils.Appointment;
import com.myapp.booknow.Utils.DBHelper;
import com.myapp.booknow.R;
import com.myapp.booknow.Utils.TimeSlotSelectionActivity;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.appoitmentViewHolder> {

    private List<Appointment> appointmentList;

    public CustomerAdapter(List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public appoitmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_adapter_view, parent, false);
        return new appoitmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull appoitmentViewHolder holder, int position) {

        Appointment appointment = appointmentList.get(position);
        holder.appNameTextView.setText(appointment.getBusinessName()+" \n"
                +appointment.getDate().toString() + "  "+ appointment.getStartTime().toString() + "-" + appointment.getEndTime().toString());
        //holder.appointmentDateTextView.setText(appointment.getDate().toString());
        // Set other appointment details to the holder as needed

        holder.editappButton.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(v.getContext(), TimeSlotSelectionActivity.class);
                intent.putExtra("business name", appointment.getBusinessName());
                v.getContext().startActivity(intent);
            }catch (Exception e){
               // Log.e("ServiceAdapter", "Error starting EditServiceActivity", e);
            }

        });

        // Handle delete service
        holder.deleteappButton.setOnClickListener(v -> {
            String serviceId = appointment.getServiceId();
            DBHelper dbHelper = new DBHelper();
            dbHelper.deleteappoitment(serviceId,
                    aVoid -> {
                        // Success handling. Perhaps refresh the list of services
                        Toast.makeText(v.getContext(), "appoitment deleted successfully", Toast.LENGTH_SHORT).show();
                        //((BusinessServicesManagementActivity) v.getContext()).fetchServices();
                    },
                    e -> {
                        // Failure handling
                        Toast.makeText(v.getContext(), "Error deleting service", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class appoitmentViewHolder extends RecyclerView.ViewHolder {
        TextView appNameTextView;
        ImageView editappButton, deleteappButton;

        public appoitmentViewHolder(View itemView) {
            super(itemView);
            appNameTextView = itemView.findViewById(R.id.appoitmentNameTextView);
            editappButton = itemView.findViewById(R.id.editappButton);
            deleteappButton = itemView.findViewById(R.id.deleteapp);
        }
    }
}