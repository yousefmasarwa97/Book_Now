package com.myapp.booknow.mvvm.model;

import com.google.firebase.firestore.PropertyName;

import java.util.List;

public class BusinessService {
    @PropertyName("serviceId")
    private String serviceId;
   @PropertyName("businessId")
    private String businessId;
    @PropertyName("name")
    private String name;
    @PropertyName("description\n")
    private String description;
    @PropertyName("duration")
    private int duration;
    @PropertyName("workingDays")
    private List<String> workingDays;//stores the days the service is available at the business.

    private List<String> providers;//stores the providers ids (providers that give this service)

    public BusinessService(){

    }
    public BusinessService(String id ,String businessId, String name , String description, int duration){
        this.serviceId = id;
        this.businessId = businessId;
        this.name = name;
        this.description = description;
        this.duration = duration;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(List<String> workingDays) {
        this.workingDays = workingDays;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public List<String> getProviders() {
        return providers;
    }

    public void setProviders(List<String> providers) {
        this.providers = providers;
    }
}
