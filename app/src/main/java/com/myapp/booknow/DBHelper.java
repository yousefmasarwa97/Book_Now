package com.myapp.booknow;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 This class handles all the interactions with the database (Cloud Firestore database).
 In the relevant activities, an instance of this class will be created.
 the appropriate method will be called.
 */
public class DBHelper {
    private FirebaseFirestore db;

    public DBHelper(){
        this.db = FirebaseFirestore.getInstance();
    }

//    public void addBusiness(User business){
//        db.collection("Users").document(business.getId())
//                .set(business)
//                .addOnSuccessListener(unused -> Log.d("DBHelper","Business successfuly added!"))
//                .addOnFailureListener(e -> Log.d("DBHelper","Error adding business", e));
//    }




    /**
     * Adds a business to the database (Users collection), the business ID is as the given object ID.
     * @param business object that represent the business to be added with the appropriate fields.
     */
    public void addBusiness(User business){
        Map<String, Object> businessData = business.toMap();
        businessData.put("setupCompleted", false); // Add setupCompleted field

        db.collection("Users").document(business.getId())
                .set(businessData)
                .addOnSuccessListener(unused -> Log.d("DBHelper","Business successfully added!"))
                .addOnFailureListener(e -> Log.d("DBHelper","Error adding business", e));
    }



    /**
     * Executes a query to get the businesses from database.
     * @param onSuccessListener
     */
    public void viewBusinesses(OnSuccessListener<List<User>> onSuccessListener){
        //The OnSuccessListener is an interface provided by Firebase. It defines a callback method, onSuccess,
        // which is executed when the  Firestore query successfully completes.
        // This method receives the list of businesses as its parameter.



        db.collection("Users").whereEqualTo("type","Business")
                .whereEqualTo("setupCompleted",true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> businessList = new ArrayList<>();
                    //Once the query is complete, Firestore returns a 'querySnapshot' object
                    //which contains all the documents that match the query criteria.
                    //after that each document will be converted to a 'User' object, and will be added to the list
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user.getType().equals("Business")) {
                            user.setId(documentSnapshot.getId());
                            businessList.add(user);
                        }
                    }
                    onSuccessListener.onSuccess(businessList);

                }).addOnFailureListener(e -> Log.d("DBHelper","Error fetching businesses",e));
    }


    /**
     * Adds a customer with userId , and phoneNumber to Users collection.
     * @param userId
     * @param phoneNumber
     */
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


    /**
     * Sets a real schedule (TimeStamps) for business with 'businessId'.
     * @param businessId the ID of the business
     * @param hours a hashmap of the day-hours of the day (key-value as String-BusinessHours)
     */
    public void setBusinessHours(String businessId, Map<String, BusinessHours> hours) {
        //The BusinessHours class comes to mirror the structure 'BusinessHours' collection in Firestore.
        for (String day : hours.keySet()) {//for each day (for each key)
            BusinessHours dayHours = hours.get(day);//take each day's hours (value)
            db.collection("BusinessHours").document(businessId + "_" + day)//document name in DB
                    .set(dayHours)//add to DB
                    .addOnSuccessListener(unused -> Log.d("DBHelper", "Business hours updated for " + day))
                    .addOnFailureListener(e -> Log.d("DBHelper", "Error updating business hours", e));
        }
    }


    /**
     * Sets the schedule for a business (daily working hours) in the database (RegularHours collection)
     * as given with the hashmap.
     * Associates the days & hours with 'businessId', all the days & hours are represented in database
     * as one document, with a field for each day, the field contains openTime + closeTime.
     * @param businessId id of the business
     * @param regular_hours hashmap {"day" : {id,openTime,closeTime,//day(we can remove)//}}
     */
    public void setBusinessRegularHours(String businessId, Map<String, BusinessRegularHours> regular_hours){
//        for(String day : regular_hours.keySet()){
//            BusinessRegularHours regularDayHours = regular_hours.get(day);
//            db.collection("BusinessRegularHours").document(businessId + "_" + day)
//                    .set(regular_hours)//add to DB
//                    .addOnSuccessListener(unused-> Log.d("DBHelper", "Business hours updated for " + day))
//                    .addOnFailureListener(e -> Log.d("DBHelper", "Error updating business hours", e));
//        }

        //--------------The previous approach was adding each day as a document--------------//


        //in this approach we add the business itself (the businessID) as a document
        //and give it the fields in this way : {"day" : {businessID , day , openTime , closeTime}}
        //this approach is more efficient in terms of reading/writing and more organized.
        //(We use it because these hours are "fixed")
        db.collection("BusinessRegularHours").document(businessId)// Search/Create a document with the userID name
                .set(regular_hours)//add to DB (all days to the same document)
                .addOnSuccessListener(unused-> Log.d("DBHelper", "Business schedule updated"))//Success
                .addOnFailureListener(e -> Log.d("DBHelper", "Error updating business schedule", e));//Fail
    }




    //-------------------------Services--------------------------//


    /**
     * Adds a service to database (BusinessServices collection).
     * @param service
     * @param onSuccessListener
     * @param onFailureListener
     */
    public void addBusinessService(BusinessService service, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
//        db.collection("BusinessServices").document(service.getServiceId())
//                .set(service)
//                .addOnSuccessListener(onSuccessListener)
//                .addOnFailureListener(onFailureListener);

        String documentId = (service.getServiceId() == null || service.getServiceId().isEmpty())
                ? db.collection("BusinessServices").document().getId()
                : service.getServiceId();

        service.setServiceId(documentId); // Set the generated ID back to the service object

        db.collection("BusinessServices").document(documentId)
                .set(service)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }


    /**
     * Fetches/refreshes the services, and gets them from databse.
     * @param businessId id of the business associated with services we want to fetch.
     * @param onSuccessListener
     * @param onFailureListener
     */
    public void fetchBusinessServices(String businessId, OnSuccessListener<List<BusinessService>> onSuccessListener, OnFailureListener onFailureListener) {
        db.collection("BusinessServices")
                .whereEqualTo("businessId", businessId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<BusinessService> services = new ArrayList<>();
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                        BusinessService service = snapshot.toObject(BusinessService.class);
                        services.add(service);
                    }
                    onSuccessListener.onSuccess(services);
                })
                .addOnFailureListener(onFailureListener);
    }



    /**
     * Handles any change/update in a service information (Name/Description/Duration).
     * @param service a BusinessService object.
     * The Id should be associated with the service object itself.
     */
    public void updateBusinessService (BusinessService service ,OnSuccessListener  onSuccessListener, OnFailureListener onFailureListener){
        db.collection("BusinessServices").document(service.getServiceId())
                .set(service)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }



    /**
     * Deletes the service with id : 'serviceId' from database.
     * @param serviceId the id of the service.
     * @param onSuccessListener
     * @param onFailureListener
     */
    public void deleteBusinessService(String serviceId, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        db.collection("BusinessServices").document(serviceId)
                .delete()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }



    public void fetchBusinessInfo(String businessId, OnSuccessListener<User> onSuccessListener, OnFailureListener onFailureListener) {
        db.collection("Users").document(businessId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User business = documentSnapshot.toObject(User.class);
                        onSuccessListener.onSuccess(business);
                    } else {
                        onFailureListener.onFailure(new Exception("Business not found"));
                    }
                })
                .addOnFailureListener(onFailureListener);
    }


    public void fetchBusinessRegularHours(String businessId, OnSuccessListener<Map<String, String>> onSuccessListener, OnFailureListener onFailureListener) {
        db.collection("BusinessRegularHours").document(businessId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, String> businessHours = new HashMap<>();
                        documentSnapshot.getData().forEach((key, value) -> businessHours.put(key,  value.toString()));
                        onSuccessListener.onSuccess(businessHours);
                    } else {
                        onFailureListener.onFailure(new Exception("Business hours not found"));
                    }
                })
                .addOnFailureListener(onFailureListener);
    }










}
