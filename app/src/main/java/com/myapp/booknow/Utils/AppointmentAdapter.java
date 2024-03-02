package com.myapp.booknow.Utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myapp.booknow.R;
import com.myapp.booknow.business.BusinessSpecialOffers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.myapp.booknow.R;
import com.myapp.booknow.Utils.ShowBusinessActivity;
import com.myapp.booknow.Utils.User;
import com.myapp.booknow.business.BusinessSpecialOffers;

import java.util.List;

/**
 * Adapts and binds list of appointments to a recyclerView
 * each element's design is as the design of service_item.xml (same design for service item and appointment item to be shown in a recyclerView)
 */
public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>{
    private FirebaseAuth mAuth;
    private List<Appointment> appointmentList;


    public AppointmentAdapter(List<Appointment> list){
        this.appointmentList = list;
    }


    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.upcoming_appointments_card_design, parent, false);//service_item = appointment_item
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {


        Appointment appointmentItem = appointmentList.get(position);





        //holder.appointmentNameTextView.setText(appointmentItem.getAppointmentId());//change to information about appointment

        holder.profilename.setText(appointmentItem.getCustomername());
//        holder.day.setText(appointmentItem.getDate().toString());
//
//        holder.time.setText(appointmentItem.getStartTime().toString() + "-" + appointmentItem.getEndTime().toString());

        //----------------------------//
        //----------------------------//

        Glide.with(holder.itemView)
                .load(appointmentItem.getImageURL())
                .placeholder(R.drawable.business_icon)
                .error(R.drawable.ic_menu_gallery)//should change !!
                .into(holder.profileimage);
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
        public TextView profilename,day,time; // and other views
        public TextView appointmentDate;
        public TextView status;
        public ImageView profileimage;

        public AppointmentViewHolder(View itemView) {
            super(itemView);
            //actions on service item = actions on appointment item
            profilename = itemView.findViewById(R.id.appointment_business_title); // can replace with actual view ID
            profileimage = itemView.findViewById(R.id.appointment_business_image);
            day = itemView.findViewById(R.id.appointment_business_day);
            time = itemView.findViewById(R.id.appointment_business_time);

//            appointmentNameTextView = itemView.findViewById(R.id.serviceNameTextView);
//            editAppointmentButton = itemView.findViewById(R.id.editServiceButton);
//            deleteAppointmentButton = itemView.findViewById(R.id.deleteServiceButton);
        }

    }
    public void setAppointmentList(List<Appointment> appointmentlist) {
        this.appointmentList = appointmentlist;
    }

}
