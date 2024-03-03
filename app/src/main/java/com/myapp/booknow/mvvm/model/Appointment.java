package com.myapp.booknow.mvvm.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {

    private String appointmentId;
    private String businessName;
    private String businessId;
    private String serviceId;
    private String providerId;
    private String customerName;
    private String customerId;
    @PropertyName("imageURL")
    private String imageURL;
    @Exclude
    private LocalDate date; // For the date of the appointment
   @Exclude
    private LocalTime startTime; // For the start time of the appointment
    @Exclude
    private LocalTime endTime;   // For the end time of the appointment

    // Use Timestamp for Firestore, but manage as LocalDate and LocalTime in your code
    private Timestamp dateStamp; // For Firestore
    private Timestamp startTimeStamp; // For Firestore
    private Timestamp endTimeStamp; // For Firestore

    String status; // "Completed","Cancelled","waiting"

    // Constructors:
    public Appointment(){

    }

    public Appointment (String businessId , String  serviceId , String customerId){
        this.businessId = businessId;
        this.customerId = customerId;
        this.serviceId = serviceId;
    }

    public Appointment(String appointmentId, String businessId , String  serviceId , String customerId){
        this.appointmentId = appointmentId;
        this.businessId = businessId;
        this.customerId = customerId;
        this.serviceId = serviceId;
    }



    // Getters & Setters:

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }
    public String getCustomername() {
        return customerName;
    }
    public void setCustomername(String customername) {
        this.customerName = customername;
    }
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }





    //-----------------//


//    public Timestamp getDateStamp() {
//        return dateStamp;
//    }
//
//    public void setDateStamp(Timestamp dateStamp) {
//        this.dateStamp = dateStamp;
//        // Optionally, update the localDate field here if you are keeping it
//        // this.localDate = convertToLocalDate(dateStamp);
//    }
//
//    public Timestamp getStartTimeStamp() {
//        return startTimeStamp;
//    }
//
//    public void setStartTimeStamp(Timestamp startTimeStamp) {
//        this.startTimeStamp = startTimeStamp;
//        // Optionally, update the localStartTime field here if you are keeping it
//        // this.localStartTime = convertToLocalTime(startTimeStamp);
//    }
//
//    public Timestamp getEndTimeStamp() {
//        return endTimeStamp;
//    }
//
//    public void setEndTimeStamp(Timestamp endTimeStamp) {
//        this.endTimeStamp = endTimeStamp;
//        // Optionally, update the localEndTime field here if you are keeping it
//        // this.localEndTime = convertToLocalTime(endTimeStamp);
//    }



    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentId='" + appointmentId + '\'' +
                ", businessId='" + businessId + '\'' +
                ", serviceId='" + serviceId + '\'' +
                ", providerId='" + providerId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", customername='" + customerName + '\'' +
                ", customerimage='" + imageURL + '\'' +
                ", date=" + (date != null ? date.toString() : "null") +
                ", startTime=" + (startTime != null ? startTime.toString() : "null") +
                ", endTime=" + (endTime != null ? endTime.toString() : "null") +
                ", dateStamp=" + (dateStamp != null ? dateStamp.toString() : "null") +
                ", startTimeStamp=" + (startTimeStamp != null ? startTimeStamp.toString() : "null") +
                ", endTimeStamp=" + (endTimeStamp != null ? endTimeStamp.toString() : "null") +
                ", status='" + status + '\'' +
                '}';
    }






}

