package com.myapp.booknow.Utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myapp.booknow.R;

import java.util.List;

/**
 * Adapts and binds list of appointments to a recyclerView
 * each element's design is as the design of service_item.xml (same design for service item and appointment item to be shown in a recyclerView)
 */
public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>{

    private List<Appointment> appointmentList;


    public AppointmentAdapter(List<Appointment> list){
        this.appointmentList = list;
    }


    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.service_item, parent, false);//service_item = appointment_item
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointmentItem = appointmentList.get(position);
        holder.appointmentNameTextView.setText(appointmentItem.getAppointmentId());//change to information about appointment

        // Handle edit appointment
       // holder.editAppointmentButton.setOnClickListener();

        // Handle delete appointment
       // holder.deleteAppointmentButton.setOnClickListener();
    }

    @Override
    public int getItemCount() {

        return appointmentList.size();
    }







    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView appointmentNameTextView;
        ImageView editAppointmentButton, deleteAppointmentButton;

        public AppointmentViewHolder(View itemView) {
            super(itemView);
            //actions on service item = actions on appointment item
            appointmentNameTextView = itemView.findViewById(R.id.serviceNameTextView);
            editAppointmentButton = itemView.findViewById(R.id.editServiceButton);
            deleteAppointmentButton = itemView.findViewById(R.id.deleteServiceButton);
        }
    }

}
