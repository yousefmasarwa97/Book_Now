package com.myapp.booknow;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {

    private String appointmentId;
    private String businessId;
    private String serviceId;
    private String customerId;
    private LocalDate date; // For the date of the appointment
    private LocalTime startTime; // For the start time of the appointment
    private LocalTime endTime;   // For the end time of the appointment

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



}

