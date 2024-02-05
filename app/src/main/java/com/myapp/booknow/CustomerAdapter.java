package com.myapp.booknow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        holder.appNameTextView.setText(appointment.getAppointmentId()+" "+appointment.getDate().toString());
        //holder.appointmentDateTextView.setText(appointment.getDate().toString());
        // Set other appointment details to the holder as needed
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