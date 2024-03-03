package com.myapp.booknow.mvvm.view;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.myapp.booknow.Utils.FirestoreCallback;
import com.myapp.booknow.mvvm.model.Appointment;
import com.myapp.booknow.mvvm.model.DBHelper;
import com.myapp.booknow.R;

import java.util.List;

/**
 * Adapter class to adapt and bind list of customer's appointments to a recycler view
 */
public class AppointmentsHistoryAdapter extends RecyclerView.Adapter<AppointmentsHistoryAdapter.appoitmentViewHolder> {

    private DBHelper dbHelper;

    private List<Appointment> appointmentList;

    public AppointmentsHistoryAdapter(List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public appoitmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.upcoming_appointments_card_design_2, parent, false);
        return new appoitmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull appoitmentViewHolder holder, int position) {

        Appointment appointment = appointmentList.get(position);//the appointment object

        //----------setting the values to be shown on the components for each appointment object in the recyclerview-------//

        holder.businessTitle.setText(appointment.getBusinessName());

        holder.day.setText(appointment.getDate().toString());

        holder.time.setText(appointment.getStartTime().toString() + "-" + appointment.getEndTime().toString());

        String businessId = appointment.getBusinessId();

        //now we want the image URL that is connected with the business id that is associated with the appointment
        //so we need to call data base

        dbHelper = new DBHelper();


        //getting the business log (image URL) and binding it with the imageView in case of call success, and logging the error in case of failure
        dbHelper.getBusinessiamgeURL(businessId, new FirestoreCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Glide.with(holder.itemView)
                        .load(result)
                        .placeholder(R.drawable.business_icon)
                        .error(R.drawable.ic_menu_gallery)//should change !!
                        .into(holder.businessLogo);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Appointments adapter image URL",e.getMessage());
            }
        });


        // setting the status
        String appointmentStatus = appointment.getStatus();
        if (appointmentStatus != null) {
            if (appointmentStatus.equalsIgnoreCase("cancelled")) {
                holder.status.setText(appointmentStatus);
                holder.status.setTextColor(Color.parseColor("#D11A2A"));// appears red
            } else if(appointmentStatus.equalsIgnoreCase("completed")) {
                holder.status.setText(appointmentStatus);
                holder.status.setTextColor(Color.parseColor("#008000"));// appears green
            } else {
                holder.status.setVisibility(View.GONE);
            }
        }


            holder.delete.setVisibility(View.INVISIBLE);

    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class appoitmentViewHolder extends RecyclerView.ViewHolder {
        TextView businessTitle,day,time,status;
        ImageView businessLogo,delete;

        public appoitmentViewHolder(View itemView) {
            super(itemView);

            businessLogo = itemView.findViewById(R.id.appointment_business_image);
            businessTitle = itemView.findViewById(R.id.appointment_business_title);
            day = itemView.findViewById(R.id.appointment_business_day);
            time = itemView.findViewById(R.id.appointment_business_time);
            status = itemView.findViewById(R.id.appointment_status);
            delete = itemView.findViewById(R.id.cancel_appointment);



        }
    }
}