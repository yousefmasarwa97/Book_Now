package com.myapp.booknow.mvvm.view;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
public class UpcomingAppointmentsAdapter extends RecyclerView.Adapter<UpcomingAppointmentsAdapter.appoitmentViewHolder> {

    private DBHelper dbHelper;

    private List<Appointment> appointmentList;

    public UpcomingAppointmentsAdapter(List<Appointment> appointmentList) {
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










        //------------Buttons to cancel the appointment----------------//




        // Handle delete service
        holder.cancelAppointment.setOnClickListener(v -> {
            String appointmentId = appointment.getAppointmentId(); // should change to getAppointmentID !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            Log.d("appointmentIDCHECK---","appointmentId variable holds the value : "+appointmentId);
            DBHelper dbHelper = new DBHelper();
            dbHelper.cancelAppointment(appointmentId,
                    aVoid -> {
                        // Success handling. Perhaps refresh the list of services
                        Toast.makeText(v.getContext(), "Appointment canceled successfully", Toast.LENGTH_SHORT).show();
                        appointmentList.remove(appointment);
                        notifyDataSetChanged();
                    },
                    e -> {
                        // Failure handling
                        Toast.makeText(v.getContext(), "Error canceling the appointment", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class appoitmentViewHolder extends RecyclerView.ViewHolder {
        TextView businessTitle,day,time;
        ImageView businessLogo, cancelAppointment;


        public appoitmentViewHolder(View itemView) {
            super(itemView);

            businessLogo = itemView.findViewById(R.id.appointment_business_image);
            businessTitle = itemView.findViewById(R.id.appointment_business_title);
            day = itemView.findViewById(R.id.appointment_business_day);
            time = itemView.findViewById(R.id.appointment_business_time);
            cancelAppointment = itemView.findViewById(R.id.cancel_appointment);


        }
    }
}