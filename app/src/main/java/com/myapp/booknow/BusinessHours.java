package com.myapp.booknow;


import com.google.firebase.Timestamp;


public class BusinessHours {
    private String businessId;
    private String day;
    private Timestamp openTime;
    private Timestamp closeTime;

    public BusinessHours() {
        // Default constructor required for Firestore
    }

    // Constructor with parameters
    public BusinessHours(String businessId, String day, Timestamp openTime, Timestamp closeTime) {
        this.businessId = businessId;
        this.day = day;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    // Getters and setters
    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Timestamp getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Timestamp openTime) {
        this.openTime = openTime;
    }

    public Timestamp getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Timestamp closeTime) {
        this.closeTime = closeTime;
    }
}
