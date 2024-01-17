package com.myapp.booknow;

import com.google.firebase.firestore.PropertyName;

import java.util.UUID;

/**
 * Represents a user in the application, encapsulating attributes
 * for both customer and business types. This class includes common fields
 * like name, email, and phone, and allows for additional business-specific
 * details where applicable.
 */
public class User {

    @PropertyName("userID")
    private String id;
    private String type; // "Customer" or "Business"
    private String name;
    private String email;
    private String phone;



    // Business-only fields (null for customers)
    //private String address;
    private String businessHours;
    private String address;
    //default constructor for Firebase
    public User(){

    }

    // constructor for all users (general)
    public User(String type, String name, String email, String phone) throws Exception {
        this.id= UUID.randomUUID().toString();//giving a unique id for the user
        this.type = type;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = "";
        this.businessHours = "";
    }


    // Specific constructor for Business users
    public User(String name, String email, String phone, String address, String businessHours) {
        this.id = UUID.randomUUID().toString(); // Generates a unique identifier
        this.type = "Business"; // Sets type to Business for this constructor
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.businessHours = businessHours;
    }

    @PropertyName("userID")
    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getBusinessHours() {
        return businessHours;
    }


    public void setId(String id) {
        this.id = id;
    }


    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBusinessHours(String businessHours) {
        this.businessHours = businessHours;
    }
}

