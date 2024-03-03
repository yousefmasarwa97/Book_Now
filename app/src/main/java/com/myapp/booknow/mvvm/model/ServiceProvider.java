package com.myapp.booknow.mvvm.model;

/**
 * This class represents a service provider.
 * A service provider can serve specific services (has a list of services) and has a schedule (available days).
 */
import java.util.List;

public class ServiceProvider {
    private String providerId;
    private String name;
    private List<String> servicesOffered; // List of service names
    private List<String> servicesOfferedIds;//List of service IDS
    private List<String> availableDays; // Days of the week they are available
    private String businessId;
    // Constructor
    public ServiceProvider(){
        
    }
    public ServiceProvider(String providerId, String name, List<String> servicesOffered, List<String> availableDays, String businessId) {
        this.providerId = providerId;
        this.name = name;
        this.servicesOffered = servicesOffered;
        this.availableDays = availableDays;
        this.businessId = businessId;
    }

    // Getters and Setters
    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getServicesOffered() {
        return servicesOffered;
    }

    public void setServicesOffered(List<String> servicesOffered) {
        this.servicesOffered = servicesOffered;
    }

    public List<String> getServicesOfferedIds() {
        return servicesOfferedIds;
    }

    public void setServicesOfferedIds(List<String> servicesOfferedIds) {
        this.servicesOfferedIds = servicesOfferedIds;
    }

    public List<String> getAvailableDays() {
        return availableDays;
    }

    public void setAvailableDays(List<String> availableDays) {
        this.availableDays = availableDays;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }



}
