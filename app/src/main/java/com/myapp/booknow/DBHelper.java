package com.myapp.booknow;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 This class will handle all the database interactions.
 In the relevant activities, an instance of this class will be created
 the appropriate method will be called.
 */
public class DBHelper {
    private FirebaseFirestore db;

    public DBHelper(){
        this.db = FirebaseFirestore.getInstance();
    }

    public void addBusiness(User business){
        db.collection("Users").document(business.getId())
                .set(business)
                .addOnSuccessListener(unused -> Log.d("DBHelper","Business successfuly added!"))
                .addOnFailureListener(e -> Log.d("DBHelper","Error adding business", e));
    }


    public void viewBusinesses(OnSuccessListener<List<User>> onSuccessListener){
        //The OnSuccessListener is an interface provided by Firebase. It defines a callback method, onSuccess,
        // which is executed when the  Firestore query successfully completes.
        // This method receives the list of businesses as its parameter.



        db.collection("Users").whereEqualTo("type","Business")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> businessList = new ArrayList<>();
                    //Once the query is complete, Firestore returns a 'querySnapshot' object
                    //which contains all the documents that match the query criteria.
                    //after that each document will be converted to a 'User' object, and will be added to the list
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user.getType().equals("Business")) {
                            businessList.add(user);
                        }
                    }
                    onSuccessListener.onSuccess(businessList);

                }).addOnFailureListener(e -> Log.d("DBHelper","Error fetching businesses",e));
    }



    public void addCustomer(String userId, String phoneNumber) {
        db.collection("Users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        // The user does not exist, create a new user
                        User newUser = new User();
                        newUser.setId(userId);
                        newUser.setPhone(phoneNumber);
                        newUser.setType("Customer");

                        db.collection("Users").document(userId)
                                .set(newUser)
                                .addOnSuccessListener(unused -> Log.d("DBHelper", "Customer successfully added!"))
                                .addOnFailureListener(e -> Log.d("DBHelper", "Error adding customer", e));
                    } else {
                        // User already exists, you might want to update the user data or do nothing
                        Log.d("DBHelper", "Customer already exists.");
                    }
                })
                .addOnFailureListener(e -> Log.d("DBHelper", "Error checking customer", e));
    }






}
