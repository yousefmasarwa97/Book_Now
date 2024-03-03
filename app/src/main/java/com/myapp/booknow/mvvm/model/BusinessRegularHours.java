package com.myapp.booknow.mvvm.model;

import com.google.firebase.Timestamp;

/**
 * Class for saving a business schedule for the week.
 */
public class BusinessRegularHours {

    private String businessId;
    private String day;
    private String openTime;//for example "08:00" (String)
    private String closeTime;//for example "15:00" (String)

    BusinessRegularHours(){
        //Required for firestore
    }

    public BusinessRegularHours(String businessId, String day, String openTime, String closeTime) {
        this.businessId = businessId;
        this.day = day;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

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

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }
}
