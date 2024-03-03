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
import com.myapp.booknow.R;
import com.myapp.booknow.mvvm.model.Appointment;
import com.myapp.booknow.mvvm.model.DBHelper;

import java.util.List;

public class bussines_UpcomingAppointmentsAdapter extends RecyclerView.Adapter<bussines_UpcomingAppointmentsAdapter.appoitmentViewHolder> {



    /**
     * Adapter class to adapt and bind list of customer's appointments to a recycler view
     */


    private DBHelper dbHelper;

    private List<Appointment> appointmentList;

    public bussines_UpcomingAppointmentsAdapter(List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public bussines_UpcomingAppointmentsAdapter.appoitmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.upcoming_appointments_card_design_2, parent, false);
        return new bussines_UpcomingAppointmentsAdapter.appoitmentViewHolder(view);
    }

//    @Override
//    public void onBindViewHolder(@NonNull appoitmentViewHolder holder, int position) {
//        Appointment appointment = appointmentList.get(position);//the appointment object
//
//        //----------setting the values to be shown on the components for each appointment object in the recyclerview-------//
//
//        holder.customerTitle.setText(appointment.getCustomername());
//
//        holder.day.setText(appointment.getDate().toString());
//
//        holder.time.setText(appointment.getStartTime().toString() + "-" + appointment.getEndTime().toString());
//
//        String businessId = appointment.getBusinessId();
//
//        //now we want the image URL that is connected with the business id that is associated with the appointment
//        //so we need to call data base
//
//        dbHelper = new DBHelper();
//
//
//        //getting the business log (image URL) and binding it with the imageView in case of call success, and logging the error in case of failure
//        dbHelper.getBusinessiamgeURL(businessId, new FirestoreCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//                Glide.with(holder.itemView)
//                        .load(result)
//                        .placeholder(R.drawable.business_icon)
//                        .error(R.drawable.ic_menu_gallery)//should change !!
//                        .into(holder.customerLogo);
//            }
//
//            @Override
//            public void onFailure(Exception e) {
//                Log.e("Appointments adapter image URL",e.getMessage());
//            }
//        });
//
//
//
//
//
//
//
//
//
//
//        //------------Buttons to cancel the appointment----------------//
//
//
//
//
//        // Handle delete service
//        holder.cancelAppointment.setOnClickListener(v -> {
//            String appointmentId = appointment.getAppointmentId(); // should change to getAppointmentID !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//            Log.d("appointmentIDCHECK---","appointmentId variable holds the value : "+appointmentId);
//            DBHelper dbHelper = new DBHelper();
//            dbHelper.cancelAppointment(appointmentId,
//                    aVoid -> {
//                        // Success handling. Perhaps refresh the list of services
//                        Toast.makeText(v.getContext(), "Appointment canceled successfully", Toast.LENGTH_SHORT).show();
//                        appointmentList.remove(appointment);
//                        notifyDataSetChanged();
//                    },
//                    e -> {
//                        // Failure handling
//                        Toast.makeText(v.getContext(), "Error canceling the appointment", Toast.LENGTH_SHORT).show();
//                    });
//        });
//
//    }

        @Override
        public void onBindViewHolder(@NonNull bussines_UpcomingAppointmentsAdapter.appoitmentViewHolder holder, int position) {

            Appointment appointment = appointmentList.get(position);//the appointment object

            //----------setting the values to be shown on the components for each appointment object in the recyclerview-------//

            holder.customerTitle.setText(appointment.getCustomername());

//            holder.day.setText(appointment.getDate().toString());
//
//            holder.time.setText(appointment.getStartTime().toString() + "-" + appointment.getEndTime().toString());

            String customerIdId = appointment.getCustomerId();

            //now we want the image URL that is connected with the business id that is associated with the appointment
            //so we need to call data base

            dbHelper = new DBHelper();


            //getting the business log (image URL) and binding it with the imageView in case of call success, and logging the error in case of failure
            dbHelper.getBusinessiamgeURL(customerIdId, new FirestoreCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    Glide.with(holder.itemView)
                            .load(result)
                            .placeholder(R.drawable.business_icon)
                            .error(R.drawable.ic_menu_gallery)//should change !!
                            .into(holder.customerLogo);
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
        TextView customerTitle,day,time;
        ImageView customerLogo, cancelAppointment;


        public appoitmentViewHolder(View itemView) {
            super(itemView);

            customerLogo = itemView.findViewById(R.id.appointment_business_image);
            customerTitle = itemView.findViewById(R.id.appointment_business_title);
            day = itemView.findViewById(R.id.appointment_business_day);
            time = itemView.findViewById(R.id.appointment_business_time);
            cancelAppointment = itemView.findViewById(R.id.cancel_appointment);


        }
    }
}