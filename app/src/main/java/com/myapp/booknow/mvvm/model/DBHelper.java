package com.myapp.booknow.mvvm.model;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Transaction;
import com.myapp.booknow.Utils.FirestoreCallback;
import com.myapp.booknow.Utils.ServiceAvailabilityCallback;
import com.myapp.booknow.Utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
//    public void fetchAppointmentsForDate_business(String businessId, String status, FirestoreCallback<List<Appointment>> callback) {
//
//        List<Appointment> appointments = new ArrayList<>();//List of appointments
//        LocalDate S=LocalDate.now().atStartOfDay().toLocalDate();
//
//        // Correct conversion
////        Timestamp startTimestamp = Utils.localDateToTimestamp(S);
////        Timestamp endTimestamp = Utils.localDateToTimestamp(S.plusDays(7));
//
//        db.collection("Appointments")
//                .whereEqualTo("businessId", businessId)
//                .whereEqualTo("status", status)
//                //.whereGreaterThanOrEqualTo("date", startTimestamp)
//                //.whereLessThan("date", endTimestamp)
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                        //Appointment appointment = documentSnapshot.toObject(Appointment.class);/////cehck if not NULL !!!!!
//                        Appointment appointment = new Appointment();
//                        if (documentSnapshot.exists()) {
//
////
////                            // Directly using Timestamp from Firestore
////                            Timestamp DATE = documentSnapshot.getTimestamp("date");
////                            Timestamp START = documentSnapshot.getTimestamp("startTime");
////                            Timestamp END = documentSnapshot.getTimestamp("endTime");
////
////                            LocalDate local_date = Utils.timestampToLocalDate(DATE);
////                            // LocalTime local_start = Utils.timestampToLocalTime(START);
////                            //LocalTime local_end = Utils.timestampToLocalTime(END);
////
////
////                            // If they don't, we'll need to adjust them accordingly
////                            appointment.setDate(local_date); // Adjust if your setDate expects a different type
////                            // appointment.setStartTime(local_start); // Adjust if your setStartTime expects a different type
////                            //appointment.setEndTime(local_end); // Adjust if your setEndTime expects a different type
//
//                            // Set other fields as necessary
//                            appointment.setAppointmentId(documentSnapshot.getId());
//                            appointment.setBusinessId(documentSnapshot.getString("businessId"));
//                            appointment.setServiceId(documentSnapshot.getString("serviceId"));
//                            appointment.setProviderId(documentSnapshot.getString("providerId"));
//                            appointment.setCustomername(documentSnapshot.getString("customerName"));
//
//                            appointment.setCustomerId(documentSnapshot.getString("customerId"));
//                            appointment.setStatus(documentSnapshot.getString("status"));
//
//                            // Now, 'appointment' is populated with the data from the documentSnapshot
//                        }
//
//
//                        appointments.add(appointment);
//                    }
//                    if (callback != null) {
//                        callback.onSuccess(appointments);
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    if (callback != null) {
//                        callback.onFailure(e);
//                    }
//                });
//    }


    //--------------------------------------------------------//
    public void fetchAppointmentsForDate_business(String businessId, String status, FirestoreCallback<List<Appointment>> callback) {

        List<Appointment> appointments = new ArrayList<>();//List of appointments
        LocalDate S=LocalDate.now().atStartOfDay().toLocalDate();

        // Correct conversion
        Timestamp startTimestamp = TimeUtils.localDateToTimestamp(S);
//        Timestamp endTimestamp = Utils.localDateToTimestamp(S.plusDays(7));

        db.collection("Appointments")
                .whereEqualTo("businessId", businessId)
                .whereEqualTo("status", status)
                .whereGreaterThanOrEqualTo("date", startTimestamp)
                .orderBy("date", Query.Direction.ASCENDING)
                //.whereLessThan("date", endTimestamp)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Appointment appointment = new Appointment();
                        if (documentSnapshot.exists()) {

                            appointment.setAppointmentId(documentSnapshot.getId());
                            appointment.setBusinessId(documentSnapshot.getString("businessId"));
                            appointment.setServiceId(documentSnapshot.getString("serviceId"));
                            appointment.setProviderId(documentSnapshot.getString("providerId"));
                            appointment.setCustomername(documentSnapshot.getString("customerName"));
                            appointment.setCustomerId(documentSnapshot.getString("customerId"));
                            appointment.setStatus(documentSnapshot.getString("status"));

                            // Retrieve imageURL and customerName from the "Users" collection
                            String customerId = appointment.getCustomerId();
                            db.collection("Users")
                                    .document(customerId)
                                    .get()
                                    .addOnSuccessListener(userDocumentSnapshot -> {
                                        if (userDocumentSnapshot.exists()) {
                                            String imageURL = userDocumentSnapshot.getString("imageURL");
                                            String customerName = userDocumentSnapshot.getString("name");

                                            // Set the retrieved values to the appointment object
                                            appointment.setImageURL(imageURL);
                                            appointment.setCustomername(customerName);
                                        }

                                        // Now, 'appointment' is populated with the data from the documentSnapshot
                                        appointments.add(appointment);

                                        if (callback != null) {
                                            callback.onSuccess(appointments);
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        if (callback != null) {
                                            callback.onFailure(e);
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                });

    }
    //--------------------------------------------------------//

    public void updateSpecialOffers (BusinessSpecialOffers specialOffers, OnSuccessListener  onSuccessListener, OnFailureListener onFailureListener){
        db.collection("SpecialOffers").document(specialOffers.getOfferId())
                .set(specialOffers)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
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

                            // Update imageURL field with the appropriate photo URL
                            String imageURL = documentSnapshot.getString("imageURL");
                            user.setImageURL(imageURL);

                            businessList.add(user);
                        }
                    }
                    onSuccessListener.onSuccess(businessList);

                }).addOnFailureListener(e -> Log.d("DBHelper","Error fetching businesses",e));
    }
    public void updateappointment_for_customer(String customerid, String customerNme, String C_image, FirestoreCallback<List<Appointment>> onSuccessListener){
        //The OnSuccessListener is an interface provided by Firebase. It defines a callback method, onSuccess,
        // which is executed when the  Firestore query successfully completes.
        // This method receives the list of businesses as its parameter.



        db.collection("Appointments").whereEqualTo("customerId\n",customerid)
//                .whereEqualTo("setupCompleted",true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Appointment> businessList = new ArrayList<>();
                    //Once the query is complete, Firestore returns a 'querySnapshot' object
                    //which contains all the documents that match the query criteria.
                    //after that each document will be converted to a 'User' object, and will be added to the list
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Appointment user = documentSnapshot.toObject(Appointment.class);
                        if (user.getCustomerId().equals(customerid)) {
                            user.setCustomername(customerNme);
                            user.setImageURL(C_image);

                            // Update imageURL field with the appropriate photo URL
                            String imageURL = documentSnapshot.getString("imageURL");
                            user.setImageURL(C_image);

                            businessList.add(user);
                        }
                    }
                    onSuccessListener.onSuccess(businessList);

                }).addOnFailureListener(e -> Log.d("DBHelper","Error fetching businesses",e));
    }

    /**
     * Executes a query to get the businesses from the database.
     * @param callback FirestoreCallback interface to handle success or failure.
     */
    public void fetchBusinesses(FirestoreCallback<List<User>> callback) {
        db.collection("Users").whereEqualTo("type", "Business")
                .whereEqualTo("setupCompleted", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<User> businessList = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            User user = documentSnapshot.toObject(User.class);
                            if ("Business".equals(user.getType())) {
                                user.setId(documentSnapshot.getId());

                                // Update imageURL field with the appropriate photo URL
                                String imageURL = documentSnapshot.getString("imageURL");
                                user.setImageURL(imageURL);
                                //set the description
                                String desc = documentSnapshot.getString("description");
                                user.setDescription(desc);

                                businessList.add(user);
                            }
                        }
                        callback.onSuccess(businessList);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }


    public void fetchBusinessesByCategory(String categoryName, FirestoreCallback<List<User>> callback){
        db.collection("Users").whereEqualTo("type","Business")
                .whereEqualTo("category",categoryName)
                .get()
                .addOnCompleteListener(task->{
                    if(task.isSuccessful()){
                        List<User> businessList = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            User user = documentSnapshot.toObject(User.class);
                            if ("Business".equals(user.getType())){
                                // set the values : name , imageURL, description, status
                                user.setId(documentSnapshot.getId());
                                String name = documentSnapshot.getString("name");
                                user.setName(name);
                                String imageURL = documentSnapshot.getString("imageURL");
                                user.setImageURL(imageURL);
                                String desc = documentSnapshot.getString("description");
                                user.setDescription(desc);

                                businessList.add(user);

                            }

                        }
                        callback.onSuccess(businessList);
                    }
                    else {
                        callback.onFailure(task.getException());
                    }
                });
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
     * Fetches/refreshes the services, and gets them from database.
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
    public void deleteappoitment(String serviceId, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        db.collection("Appointments").document(serviceId)
                .delete()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void cancelAppointment(String appointmentId, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        db.collection("Appointments").document(appointmentId)
                .delete()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void getCustomerPhoneNumber(String customerId, FirestoreCallback<String> callback) {
        db.collection("Users").document(customerId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String phoneNumber = documentSnapshot.getString("phone");
                        callback.onSuccess(phoneNumber);
                    } else {
                        callback.onFailure(new Exception("Customer document not found"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }



    /**
     * Fetches business's (with businessId) info.
     * @param businessId
     * @param onSuccessListener
     * @param onFailureListener
     */
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


    /**
     * Fetches business's (with businessId) regular hours (scheduled hours).
     * @param businessId
     * @param onSuccessListener
     * @param onFailureListener
     */
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


    public void addServiceProvider(ServiceProvider provider, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        String documentId = (provider.getProviderId() == null || provider.getProviderId().isEmpty())
                ? db.collection("ServiceProviders").document().getId()
                : provider.getProviderId();

        provider.setProviderId(documentId); //

        db.collection("ServiceProviders").document(documentId)
                .set(provider)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    /**
     * Fetches all the providers for the given business.
     * @param businessId
     * @param onSuccessListener
     * @param onFailureListener
     */
    public void fetchServiceProviders(String businessId, OnSuccessListener<List<ServiceProvider>> onSuccessListener, OnFailureListener onFailureListener) {
        db.collection("ServiceProviders")
                .whereEqualTo("businessId", businessId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ServiceProvider> providers = new ArrayList<>();
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                        ServiceProvider provider = snapshot.toObject(ServiceProvider.class);
                        providers.add(provider);
                    }
                    onSuccessListener.onSuccess(providers);
                })
                .addOnFailureListener(onFailureListener);
    }

    public void getAvailableDaysForService(String serviceId , String businessId,OnSuccessListener<List<String>> onSuccessListener, OnFailureListener onFailureListener){
        db.collection("BusinessServices")
                .whereEqualTo("serviceId",serviceId)
                .whereEqualTo("businessId",businessId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots  -> {
                    if (!queryDocumentSnapshots.isEmpty()){
                        DocumentSnapshot serviceDoc = queryDocumentSnapshots.getDocuments().get(0);
                        List<String> available_days_for_service = (List<String>) serviceDoc.get("workingDays");
                        onSuccessListener.onSuccess(available_days_for_service);
                    }else{//if there is no such service found
                        onSuccessListener.onSuccess(Collections.emptyList());
                    }


                }).addOnFailureListener(onFailureListener);
    }


    /**
     * Gets the working hours for business (businessId), on a specific day.
     * @param businessId
     * @param selectedDate
     * @param onSuccessListener
     * @param onFailureListener
     */

    public void fetchWorkingHours(String businessId, LocalDate selectedDate, OnSuccessListener<WorkingHours> onSuccessListener, OnFailureListener onFailureListener) {

        Log.d("DBHelper", "fetchWorkingHours called with businessId: " + businessId + ", selectedDate: " + selectedDate);



        // Convert LocalDate to LocalDateTime at the start and end of the day
        LocalDateTime startOfDay = selectedDate.atStartOfDay();
        LocalDateTime endOfDay = selectedDate.plusDays(1).atStartOfDay();

        Log.d("DBHelper", "Before conversion - Start of day: " + startOfDay.toString() + ", End of day: " + endOfDay.toString());

        // Convert LocalDateTime to java.util.Date
        Date dateStart = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
        Date dateEnd = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());


        // Convert java.util.Date to java.sql.Timestamp
        Timestamp dayStart = new Timestamp(dateStart);
        Timestamp dayEnd = new Timestamp(dateEnd);

        Log.d("DBHelper", "After conversion - Timestamp dayStart: " + dayStart.toString() + ", Timestamp dayEnd: " + dayEnd.toString());



        Log.d("DBHelper", "Querying Firestore - Start: " + dayStart.toString() + ", End: " + dayEnd.toString());

        // Check in BusinessSpecialHours collection first
        db.collection("BusinessSpecialHours")
                .whereEqualTo("businessId", businessId)
                .whereGreaterThanOrEqualTo("openTime", dayStart)
                .whereLessThan("openTime", dayEnd)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Found special hours
                       // WorkingHours hours = queryDocumentSnapshots.getDocuments().get(0).toObject(WorkingHours.class);
                        WorkingHours hours = queryDocumentSnapshots.getDocuments().get(0).toObject(WorkingHours.class);
                        String openTimeString = hours.formatTimestamp(hours.getOpenTime());
                        String closeTimeString = hours.formatTimestamp(hours.getCloseTime());
                        Log.d("DBHelper", "Special hours found: " + hours.toString());
                        onSuccessListener.onSuccess(hours);
                    } else {
                        // If not found, check in BusinessRegularHours
                        fetchRegularHours(businessId, selectedDate, onSuccessListener, onFailureListener);
                    }
                })
                .addOnFailureListener(e -> {
                    // Log error
                    Log.d("DBHelper", "Error fetching hours", e);

                    onFailureListener.onFailure(e);
                });
    }


    public void fetchRegularHours(String businessId, LocalDate selectedDate, OnSuccessListener<WorkingHours> successListener, OnFailureListener failureListener) {

        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String formattedDayOfWeek = dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1).toLowerCase();

        db.collection("BusinessRegularHours")
                .document(businessId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            Map<String, Object> data = document.getData();
                            if (data != null && data.containsKey(formattedDayOfWeek)) {
                                Map<String, String> hoursData = (Map<String, String>) data.get(formattedDayOfWeek);
                                try {
                                    // Assume openTime and closeTime are in HH:mm format
                                    String openTimeStr = hoursData.get("openTime");//Correct !!!
                                    String closeTimeStr = hoursData.get("closeTime");//Correct !!!
                                    Log.d("open time and close time in regular days : ",""+openTimeStr+"  "+closeTimeStr);//Correct Log
                                    // Convert String times to Timestamps
                                    WorkingHours workingHours = convertStringHoursToTimestamp(openTimeStr, closeTimeStr, selectedDate);//can create a new instance !!!
                                    successListener.onSuccess(workingHours);
                                } catch (Exception e) {
                                    failureListener.onFailure(e);
                                }
                            } else {
                                failureListener.onFailure(new Exception("No regular hours for " + dayOfWeek));
                            }
                        } else {
                            failureListener.onFailure(new Exception("Document does not exist"));
                        }
                    } else {
                        failureListener.onFailure(task.getException());
                    }
                });
    }


private WorkingHours convertStringHoursToTimestamp(String openTimeStr, String closeTimeStr, LocalDate selectedDate) {
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    LocalTime openTime = LocalTime.parse(openTimeStr, timeFormatter);
    LocalTime closeTime = LocalTime.parse(closeTimeStr, timeFormatter);

    LocalDateTime openDateTime = LocalDateTime.of(selectedDate, openTime);
    LocalDateTime closeDateTime = LocalDateTime.of(selectedDate, closeTime);

    // Convert LocalDateTime to Instant (assuming system's default time zone)
    ZoneId zoneId = ZoneId.systemDefault(); // Adjust the time zone as necessary
    Instant openInstant = openDateTime.atZone(zoneId).toInstant();
    Instant closeInstant = closeDateTime.atZone(zoneId).toInstant();

    // Create Firebase Timestamps
    Timestamp openTimestamp = new Timestamp(openInstant.getEpochSecond(), openInstant.getNano());
    Timestamp closeTimestamp = new Timestamp(closeInstant.getEpochSecond(), closeInstant.getNano());

    return new WorkingHours(openTimestamp, closeTimestamp);
}


    public void addOrUpdateSpecialHours(String businessId, LocalDate day, String openTimeStr, String closeTimeStr) {
        Timestamp openTime = TimeUtils.convertToTimestamp(day, openTimeStr);
        Timestamp closeTime = TimeUtils.convertToTimestamp(day, closeTimeStr);

        String documentId = businessId + "_" + day.toString(); // Unique ID using businessId and day

        Map<String, Object> specialHoursData = new HashMap<>();
        specialHoursData.put("businessId", businessId);
        specialHoursData.put("day", day.toString());
        specialHoursData.put("openTime", openTime);
        specialHoursData.put("closeTime", closeTime);

        db.collection("BusinessSpecialHours").document(documentId)
                .set(specialHoursData)
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                    System.out.println("Special hours updated successfully");
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    System.err.println("Error updating special hours: " + e.getMessage());
                });
    }


    /**
     * Checks if a service is available on a given date.
     * @param businessId The ID of the business offering the service.
     * @param serviceId The ID of the service to check availability for.
     * @param selectedDate The date for which to check service availability.
     * @return true if the service is available on the selected date, false otherwise.
     */
    public void isServiceAvailable(String businessId, String serviceId, LocalDate selectedDate, ServiceAvailabilityCallback callback) {
        Log.d("DBHelper","before calling the db , this is the passed serviceId : " +serviceId);
        db.collection("BusinessServices")
                .document(serviceId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d("DBHelper","the document existis and this is the id :  "+documentSnapshot.getId());
                        Map<String, Object> data = documentSnapshot.getData();
                        if (data != null) {
                            BusinessService service = new BusinessService();

                            // Assuming 'name' and 'workingDays' are the fields in your Firestore document
                            service.setName((String) data.get("name"));
                            service.setWorkingDays((List<String>) data.get("workingDays"));

                            Log.d("DBHelper","these are the days that are in workingDays array of the service");
                            for(String day : service.getWorkingDays()){
                                Log.d("DBHelper", day);
                            }

                            // Convert selectedDate to the day of week and check if it's in workingDays
                            String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH).toUpperCase(Locale.ROOT);
                            Log.d("DBHelper","this is  the string that converted date to string : "+ dayOfWeek);
                            String formattedDay = dayOfWeek.substring(0, 1).toUpperCase() + dayOfWeek.substring(1).toLowerCase();
                            if (service.getWorkingDays() != null && service.getWorkingDays().contains(formattedDay)) {
                                callback.onResult(true); // The service is available on this day
                            } else {
                                callback.onResult(false); // The service is not available on this day
                            }
                        } else {
                            Log.d("ServiceCheck", "Document data is null.");
                            callback.onResult(false); // Document data is null, treat as not available
                        }
                    } else {
                        Log.d("ServiceCheck", "Document does not exist.");
                        callback.onResult(false); // Document does not exist, treat as not available
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onError(e); // Handle the error
                    Log.e("ServiceCheck", "Error fetching document: " + e.getMessage());
                });
    }



    /**
     * Fetches the serviceId for a given service name and businessId.
     *
     * @param businessId The ID of the business offering the service.
     * @param serviceName The name of the service.
     * @param onSuccessListener Listener for successful retrieval of serviceId.
     * @param onFailureListener Listener for handling errors.
     */
    public void fetchServiceIdByName(String businessId, String serviceName, OnSuccessListener<String> onSuccessListener, OnFailureListener onFailureListener) {
        db.collection("BusinessServices")
                .whereEqualTo("businessId", businessId)
                .whereEqualTo("name", serviceName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Assuming that service names are unique within a business
                            String serviceId = documentSnapshot.getId(); // Get the document ID, which is the serviceId
                            onSuccessListener.onSuccess(serviceId);
                            return; // Break after the first match
                        }
                    } else {
                        // Handle the case where no services are found matching the criteria
                        onSuccessListener.onSuccess(null);
                    }
                })
                .addOnFailureListener(onFailureListener);
    }

    // Pseudocode outline


//    public void fetchProvidersForService(String serviceId, final FirestoreCallback<List<ServiceProvider>> callback) {
//
//        List<ServiceProvider> providers = new ArrayList<>();
//
//        db.collection("ServiceProviders")
//                .whereArrayContains("servicesOfferedIds", serviceId)
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                        ServiceProvider provider = documentSnapshot.toObject(ServiceProvider.class);
//                        providers.add(provider);
//                    }
//                    callback.onSuccess(providers);
//                })
//                .addOnFailureListener(e -> {
//                    callback.onFailure(e);
//                });
//    }



    //find already booked appointments for the given business and service.
    public void fetchAppointmentsForDateAndService(String businessId, String serviceId, LocalDate selectedDate, String status, FirestoreCallback<List<Appointment>> callback) {

        List<Appointment> appointments = new ArrayList<>();//List of appointments

        // Correct conversion
        Timestamp startTimestamp = TimeUtils.localDateToTimestamp(selectedDate);
        Timestamp endTimestamp = TimeUtils.localDateToTimestamp(selectedDate.plusDays(1));

        db.collection("Appointments")
                .whereEqualTo("businessId", businessId)
                .whereEqualTo("serviceId", serviceId)
                .whereEqualTo("status", status)
                .whereGreaterThanOrEqualTo("date", startTimestamp)
                .whereLessThan("date", endTimestamp)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        //Appointment appointment = documentSnapshot.toObject(Appointment.class);/////cehck if not NULL !!!!!
                        Appointment appointment = new Appointment();
                        if (documentSnapshot.exists()) {


                            // Directly using Timestamp from Firestore
                            Timestamp DATE = documentSnapshot.getTimestamp("date");
                            Timestamp START = documentSnapshot.getTimestamp("startTime");
                            Timestamp END = documentSnapshot.getTimestamp("endTime");

                            LocalDate local_date = TimeUtils.timestampToLocalDate(DATE);
                           //LocalTime local_start = Utils.timestampToLocalTime(START);
                            //LocalTime local_end = Utils.timestampToLocalTime(END);


                            // If they don't, we'll need to adjust them accordingly
                            appointment.setDate(local_date); // Adjust if your setDate expects a different type
                           // appointment.setStartTime(local_start); // Adjust if your setStartTime expects a different type
                            //appointment.setEndTime(local_end); // Adjust if your setEndTime expects a different type

                            // Set other fields as necessary
                            appointment.setAppointmentId(documentSnapshot.getId());
                            appointment.setBusinessId(documentSnapshot.getString("businessId"));
                            appointment.setServiceId(documentSnapshot.getString("serviceId"));
                            appointment.setProviderId(documentSnapshot.getString("providerId"));
                            appointment.setCustomerId(documentSnapshot.getString("customerId"));
                            appointment.setStatus(documentSnapshot.getString("status"));

                            // Now, 'appointment' is populated with the data from the documentSnapshot
                        }


                        appointments.add(appointment);
                    }
                    if (callback != null) {
                        callback.onSuccess(appointments);
                    }
                })
                .addOnFailureListener(e -> {
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                });
    }


    //fetch all the appointments for the given business.
    public void fetchAppointmentsForDate(String businessId, LocalDate selectedDate, String status, FirestoreCallback<List<Appointment>> callback) {

        List<Appointment> appointments = new ArrayList<>();//List of appointments

        // Correct conversion
        Timestamp startTimestamp = TimeUtils.localDateToTimestamp(selectedDate);
        Timestamp endTimestamp = TimeUtils.localDateToTimestamp(selectedDate.plusDays(1));

        db.collection("Appointments")
                .whereEqualTo("businessId", businessId)
                .whereEqualTo("status", status)
                .whereGreaterThanOrEqualTo("date", startTimestamp)
                .whereLessThan("date", endTimestamp)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        //Appointment appointment = documentSnapshot.toObject(Appointment.class);/////cehck if not NULL !!!!!
                        Appointment appointment = new Appointment();
                        if (documentSnapshot.exists()) {


                            // Directly using Timestamp from Firestore
                            Timestamp DATE = documentSnapshot.getTimestamp("date");
                            Timestamp START = documentSnapshot.getTimestamp("startTime");
                            Timestamp END = documentSnapshot.getTimestamp("endTime");

                            LocalDate local_date = TimeUtils.timestampToLocalDate(DATE);
                            // LocalTime local_start = Utils.timestampToLocalTime(START);
                            //LocalTime local_end = Utils.timestampToLocalTime(END);


                            // If they don't, we'll need to adjust them accordingly
                            appointment.setDate(local_date); // Adjust if your setDate expects a different type
                            // appointment.setStartTime(local_start); // Adjust if your setStartTime expects a different type
                            //appointment.setEndTime(local_end); // Adjust if your setEndTime expects a different type

                            // Set other fields as necessary
                            appointment.setAppointmentId(documentSnapshot.getId());
                            appointment.setBusinessId(documentSnapshot.getString("businessId"));
                            appointment.setServiceId(documentSnapshot.getString("serviceId"));
                            appointment.setProviderId(documentSnapshot.getString("providerId"));
                            appointment.setCustomerId(documentSnapshot.getString("customerId"));
                            appointment.setStatus(documentSnapshot.getString("status"));

                            // Now, 'appointment' is populated with the data from the documentSnapshot
                        }


                        appointments.add(appointment);
                    }
                    if (callback != null) {
                        callback.onSuccess(appointments);
                    }
                })
                .addOnFailureListener(e -> {
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                });
    }


    /**
     * Adds the given provider (providerId) to the list of providers for the services in the given array (serviceIds).
     * @param providerId
     * @param serviceIds
     */
    public void addProviderToServices(String providerId, String[] serviceIds) {
        for (String serviceId : serviceIds) {
            DocumentReference serviceRef = db.collection("BusinessServices").document(serviceId);

            // Use Firestore transactions to read the current list of providers and update it
            db.runTransaction((Transaction.Function<Void>) transaction -> {
                        DocumentSnapshot serviceSnapshot = transaction.get(serviceRef);
                        List<String> currentProviders = (List<String>) serviceSnapshot.get("providers");

                        if (currentProviders == null) {
                            currentProviders = new ArrayList<>();
                        }

                        // Add the provider's ID if it's not already in the list
                        if (!currentProviders.contains(providerId)) {
                            currentProviders.add(providerId);
                            transaction.update(serviceRef, "providers", currentProviders);
                        }

                        return null; // Transaction must return null if void
                    }).addOnSuccessListener(aVoid -> Log.d("DBHelper", "Provider added to service successfully"))
                    .addOnFailureListener(e -> Log.e("DBHelper", "Error adding provider to service", e));
        }
    }


    // Function to fetch the duration of a service
    public void getServiceDuration(String serviceId, OnSuccessListener<Integer> onSuccessListener, OnFailureListener onFailureListener) {
        db.collection("BusinessServices").document(serviceId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Long durationLong = task.getResult().getLong("duration");
                if (durationLong != null) {
                    // Convert Long to Integer
                    int duration = durationLong.intValue();
                    onSuccessListener.onSuccess(duration);
                } else {
                    // Invoke the onFailureListener if the duration field is missing
                    onFailureListener.onFailure(new Exception("Duration field is missing for serviceId: " + serviceId));
                }
            } else {
                // Invoke the onFailureListener if the document doesn't exist or the query failed
                onFailureListener.onFailure(task.getException() != null ? task.getException() : new Exception("Failed to fetch service duration for serviceId: " + serviceId));
            }
        });
    }

    public void fetchProvidersForService(String serviceId, FirestoreCallback<List<String>> callback) {
        db.collection("ServiceProviders")
                .whereArrayContains("servicesOfferedIds", serviceId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> providerIds = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        providerIds.add(document.getId());
                    }
                    callback.onSuccess(providerIds);
                })
                .addOnFailureListener(e -> callback.onFailure(e));
    }

    public void fetchAppointmentsForDateAndProviders(LocalDate chosenDate, List<String> providerIds, FirestoreCallback<List<Appointment>> callback) {
        // Start of the day at 00:00
        LocalDateTime startOfDay = LocalDateTime.of(chosenDate, LocalTime.MIN);
        // End of the day just before midnight
        LocalDateTime endOfDay = LocalDateTime.of(chosenDate, LocalTime.MAX);

        Log.d("TimeCheck","startOfdayLocaltime = "+startOfDay);
        Log.d("TimeCheck","endOfdayLocalTime = "+endOfDay);

        Timestamp startOfDayStamp = TimeUtils.localDateTimeToTimestamp(startOfDay);
        Timestamp endOfDayStamp = TimeUtils.localDateTimeToTimestamp(endOfDay);

        Log.d("TimeCheck","startOfdayStamp = "+startOfDayStamp);
        Log.d("TimeCheck","endOfdayStamp = "+endOfDayStamp);

                db.collection("Appointments")
                        .whereIn("providerId", providerIds)
                        .whereGreaterThanOrEqualTo("startTime", startOfDayStamp)
                        .whereLessThanOrEqualTo("startTime", endOfDayStamp)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            List<Appointment> appointments = new ArrayList<>();
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Appointment appointment = new Appointment();

                                // Directly setting properties using setters
                                appointment.setAppointmentId(document.getId());
                                appointment.setBusinessId(document.getString("businessId"));
                                appointment.setServiceId(document.getString("serviceId"));
                                appointment.setProviderId(document.getString("providerId"));
                                appointment.setCustomerId(document.getString("customerId"));
                                appointment.setStatus(document.getString("status"));

                                appointment.setDate(chosenDate);
                                // For Timestamp fields, convert them to LocalDate and LocalTime if needed
                                Timestamp startTimeStamp = document.getTimestamp("startTime");
                                Timestamp endTimeStamp = document.getTimestamp("endTime");
                                if (startTimeStamp != null) {
                                    // Convert Timestamp to LocalTime and set
                                    LocalTime startTime = startTimeStamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
                                    appointment.setStartTime(startTime);
                                }
                                if (endTimeStamp != null) {
                                    // Convert Timestamp to LocalTime and set
                                    LocalTime endTime = endTimeStamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
                                    appointment.setEndTime(endTime);
                                }

                                // Add the constructed Appointment object to the list
                                appointments.add(appointment);
                            }
                            callback.onSuccess(appointments);
                        })
                        .addOnFailureListener(e -> callback.onFailure(e));
    }


    public List<String> generateAvailableTimeSlots(LocalTime openTime, LocalTime closeTime, int serviceDuration, List<Appointment> existingAppointments) {
        List<String> availableTimeSlots = new ArrayList<>();
        LocalTime currentTime = openTime;

        while (currentTime.plusMinutes(serviceDuration).isBefore(closeTime) || currentTime.plusMinutes(serviceDuration).equals(closeTime)) {
            boolean isSlotAvailable = true;

            // Check against each existing appointment
            for (Appointment appointment : existingAppointments) {
                LocalTime appointmentStart = appointment.getStartTime();
                LocalTime appointmentEnd = appointment.getEndTime();

                // Check if the current slot overlaps with this appointment
                if (!(currentTime.isAfter(appointmentEnd) || currentTime.plusMinutes(serviceDuration).isBefore(appointmentStart))) {
                    isSlotAvailable = false;
                    break; // This time slot is not available, no need to check further
                }
            }

            if (isSlotAvailable) {
                // If the slot does not overlap with any appointments, add it to the list
                String slot = currentTime.toString() + " - " + currentTime.plusMinutes(serviceDuration).toString();
                availableTimeSlots.add(slot);
            }

            // Move to the next slot
            currentTime = currentTime.plusMinutes(serviceDuration);
        }

        return availableTimeSlots;
    }

    public void fetchAppointmentsAndGenerateSlots(LocalDate chosenDate, List<String> providerIds, LocalTime openTime, LocalTime closeTime, int serviceDuration) {
        fetchAppointmentsForDateAndProviders(chosenDate, providerIds, new FirestoreCallback<List<Appointment>>() {
            @Override
            public void onSuccess(List<Appointment> existingAppointments) {
                // Now that you have the existing appointments, generate available time slots
                List<String> availableTimeSlots = generateAvailableTimeSlots(openTime, closeTime, serviceDuration, existingAppointments);

                // Logging the available time slots
                for (String slot : availableTimeSlots) {
                    Log.d("Available Time Slot", slot);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("Error fetching appointments", e.getMessage());
            }
        });
    }
//    public void bookappointmentforcustomer(String UrImg, String customerName,String businessId){
//        db.collection("Appointments").document(businessId)
//
//    }



        public void bookOrUpdateAppointment(String customerName,String UrImg,String businessId, String customerId, String serviceId, LocalDate selectedDate, String timeSlot, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
            // Assuming Appointment is a class you've defined to model your appointments
            String[] times = timeSlot.split(" - ");
            if (times.length != 2) return; // Basic validation

            Timestamp startTimeStamp = TimeUtils.convertToTimestamp(selectedDate, times[0]);
            Timestamp endTimeStamp = TimeUtils.convertToTimestamp(selectedDate, times[1]);
            Timestamp dateStamp = TimeUtils.localDateToTimestamp(selectedDate);//converting date to 'TimeStamp' to use in DB.



            Map<String, Object> appointment = new HashMap<>();


            appointment.put("CustomerName",customerName);
            appointment.put("customer image",UrImg);
            appointment.put("businessId", businessId);
            appointment.put("customerId", customerId);
            appointment.put("serviceId", serviceId);
            appointment.put("date", dateStamp); // Adjust as necessary for your date handling
            appointment.put("startTime", startTimeStamp);
            appointment.put("endTime", endTimeStamp);
            appointment.put("status", "waiting");
            getBusinessName(businessId, new FirestoreCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    appointment.put("businessName",result);

                    String documentId = db.collection("Appointments").document().getId(); // For new appointments
                    // String documentId = existingAppointmentId; // For updates, use the existing appointment ID

                    db.collection("Appointments").document(documentId)
                            .set(appointment)
                            .addOnSuccessListener(onSuccessListener)
                            .addOnFailureListener(onFailureListener);
                }

                @Override
                public void onFailure(Exception e) {
                    Log.d("name adding DBH","Faile to add business name");
                }
            });


            // Use an appropriate document ID strategy: for a new appointment, generate a new ID; for updates, use the existing appointment's ID
//            String documentId = db.collection("Appointments").document().getId(); // For new appointments
//            // String documentId = existingAppointmentId; // For updates, use the existing appointment ID
//
//            db.collection("Appointments").document(documentId)
//                    .set(appointment)
//                    .addOnSuccessListener(onSuccessListener)
//                    .addOnFailureListener(onFailureListener);
        }




//
//    public void fetchUpcomingAppointmentsForCustomer(String customerId, FirestoreCallback<List<Appointment>> callback) {
//        List<Appointment> appointments = new ArrayList<>();
//        LocalDateTime now = LocalDateTime.now();
//
//        db.collection("Appointments")
//                .whereEqualTo("customerId", customerId)
//                // Assuming 'date' field in Firestore is stored as Timestamp
//                .whereGreaterThanOrEqualTo("date", now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
//                .orderBy("date", Query.Direction.ASCENDING) // Ensure appointments are ordered by date
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                        Appointment appointment = documentSnapshot.toObject(Appointment.class);
//                        appointments.add(appointment);
//                    }
//                    callback.onSuccess(appointments);
//                })
//                .addOnFailureListener(e -> callback.onFailure(e));
//    }




//    public void fetchUpcomingAppointmentsForCustomer(String customerId, FirestoreCallback<List<Appointment>> callback) {
//        List<Appointment> appointments = new ArrayList<>();
//        LocalDateTime now = LocalDateTime.now();
//        LocalDate ld = now.toLocalDate();
//        Timestamp nowTimestamp = Utils.localDateToTimestamp(ld);
//
//        db.collection("Appointments")
//                .whereEqualTo("customerId", customerId)
//                .whereEqualTo("status", "waiting")
//                .whereGreaterThanOrEqualTo("date", nowTimestamp)
//                .orderBy("date", Query.Direction.ASCENDING) //order by date
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                        Appointment appointment = new Appointment();
//
//                        appointment.setAppointmentId(documentSnapshot.getId());
//                        appointment.setBusinessId(documentSnapshot.getString("businessId"));
//                        appointment.setServiceId(documentSnapshot.getString("serviceId"));
//                        appointment.setProviderId(documentSnapshot.getString("providerId"));
//                        appointment.setCustomerId(documentSnapshot.getString("customerId"));
//                        appointment.setStatus(documentSnapshot.getString("status"));
//
//
//
//
//                        Timestamp dateTimestamp = documentSnapshot.getTimestamp("date");
//                        Timestamp startTimeStamp = documentSnapshot.getTimestamp("startTime");
//                        Timestamp endTimeStamp = documentSnapshot.getTimestamp("endTime");
//
//                        if (dateTimestamp != null) {
//                            appointment.setDate(dateTimestamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
//                        }
//                        if (startTimeStamp != null) {
//                            appointment.setStartTime(startTimeStamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime());
//                        }
//                        if (endTimeStamp != null) {
//                            appointment.setEndTime(endTimeStamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime());
//                        }
//
//                        String businessId = documentSnapshot.getString("businessId");
//                        getBusinessName(businessId, new FirestoreCallback<String>() {
//                            @Override
//                            public void onSuccess(String result) {
//                                appointment.setBusinessName(result);
//                            }
//
//                            @Override
//                            public void onFailure(Exception e) {
//                                Log.d("name adding DBH","Faile to add business name");
//                            }
//                        });
//
//
//                        appointments.add(appointment);
//                    }
//                    callback.onSuccess(appointments);
//                })
//                .addOnFailureListener(e -> callback.onFailure(e));
//    }
public void fetchUpcomingAppointmentsForCustomer(String customerId, FirestoreCallback<List<Appointment>> callback) {
    List<Appointment> appointments = new ArrayList<>();
    LocalDateTime now = LocalDateTime.now();
    LocalDate ld = now.toLocalDate();
    Timestamp nowTimestamp = TimeUtils.localDateToTimestamp(ld);

    db.collection("Appointments")
            .whereEqualTo("customerId", customerId)
            .whereEqualTo("status", "waiting")
            .whereGreaterThanOrEqualTo("date", nowTimestamp)
            .orderBy("date", Query.Direction.ASCENDING) //order by date
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                final int[] pendingBusinessNameFetches = {queryDocumentSnapshots.size()};

                if (queryDocumentSnapshots.isEmpty()) {
                    callback.onSuccess(appointments); // No appointments to process
                    return;
                }

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Appointment appointment = new Appointment();
                    // Set all known fields here...

                        appointment.setAppointmentId(documentSnapshot.getId());
                        appointment.setBusinessId(documentSnapshot.getString("businessId"));
                        appointment.setServiceId(documentSnapshot.getString("serviceId"));
                        appointment.setProviderId(documentSnapshot.getString("providerId"));
                        appointment.setCustomerId(documentSnapshot.getString("customerId"));
                        appointment.setStatus(documentSnapshot.getString("status"));




                        Timestamp dateTimestamp = documentSnapshot.getTimestamp("date");
                        Timestamp startTimeStamp = documentSnapshot.getTimestamp("startTime");
                        Timestamp endTimeStamp = documentSnapshot.getTimestamp("endTime");

                        if (dateTimestamp != null) {
                            appointment.setDate(dateTimestamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                        }
                        if (startTimeStamp != null) {
                            appointment.setStartTime(startTimeStamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime());
                        }
                        if (endTimeStamp != null) {
                            appointment.setEndTime(endTimeStamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime());
                        }


                    String businessId = documentSnapshot.getString("businessId");

                    // Fetch business name for each appointment
                    getBusinessName(businessId, new FirestoreCallback<String>() {
                        @Override
                        public void onSuccess(String businessName) {
                            appointment.setBusinessName(businessName);
                            appointments.add(appointment);

                            // Decrement the counter and check if all fetches are done
                            pendingBusinessNameFetches[0]--;
                            if (pendingBusinessNameFetches[0] == 0) {
                                callback.onSuccess(appointments);
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.d("DBHelper", "Failed to add business name for businessId: " + businessId);
                            // Handle the failure or consider decrementing the counter and checking as well
                            pendingBusinessNameFetches[0]--;
                            if (pendingBusinessNameFetches[0] == 0) {
                                callback.onSuccess(appointments); // or consider callback.onFailure(e);
                            }
                        }
                    });
                }
            })
            .addOnFailureListener(callback::onFailure);
}

    public void fetchAppointmentsHistoryForCustomer(String customerId, FirestoreCallback<List<Appointment>> callback) {
        List<Appointment> appointments = new ArrayList<>();

        db.collection("Appointments")
                .whereEqualTo("customerId", customerId)
                .whereIn("status", Arrays.asList("completed", "cancelled"))
                .orderBy("date", Query.Direction.DESCENDING) // Order by date descending for history
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        callback.onSuccess(appointments); // No appointments to process
                        return;
                    }

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Appointment appointment = new Appointment();
                        // Set all known fields here...
                        appointment.setAppointmentId(documentSnapshot.getId());
                        appointment.setBusinessId(documentSnapshot.getString("businessId"));
                        appointment.setServiceId(documentSnapshot.getString("serviceId"));
                        appointment.setProviderId(documentSnapshot.getString("providerId"));
                        appointment.setCustomerId(documentSnapshot.getString("customerId"));
                        appointment.setStatus(documentSnapshot.getString("status"));

                        Timestamp dateTimestamp = documentSnapshot.getTimestamp("date");
                        Timestamp startTimeStamp = documentSnapshot.getTimestamp("startTime");
                        Timestamp endTimeStamp = documentSnapshot.getTimestamp("endTime");

                        if (dateTimestamp != null) {
                            appointment.setDate(dateTimestamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                        }
                        if (startTimeStamp != null) {
                            appointment.setStartTime(startTimeStamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime());
                        }
                        if (endTimeStamp != null) {
                            appointment.setEndTime(endTimeStamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime());
                        }

                        // Fetch business name for each appointment
                        String businessId = documentSnapshot.getString("businessId");
                        getBusinessName(businessId, new FirestoreCallback<String>() {
                            @Override
                            public void onSuccess(String businessName) {
                                appointment.setBusinessName(businessName);
                                appointments.add(appointment);
                                callback.onSuccess(appointments);
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.d("DBHelper", "Failed to add business name for businessId: " + businessId);
                                // Handle the failure or consider callback.onFailure(e);
                                callback.onFailure(e);
                            }
                        });
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }




    public void getBusinessName(String businessId, FirestoreCallback<String> callback) {
        db.collection("Users").document(businessId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                if (task.getResult().exists() && task.getResult().contains("name")) {
                    String businessName = task.getResult().getString("name");
                    if (businessName != null) {
                        // Call onSuccess with the business name
                        callback.onSuccess(businessName);
                    } else {
                        // The name field is missing or null, trigger onFailure
                        callback.onFailure(new Exception("Business name field is missing or null."));
                    }
                } else {
                    // Document does not exist or missing 'name' field
                    callback.onFailure(new Exception("Document does not exist or is missing 'name' field."));
                }
            } else {
                // Fetching the document failed
                callback.onFailure(task.getException() != null ? task.getException() : new Exception("Failed to fetch document."));
            }
        });
    }


    public void getBusinessiamgeURL(String businessId, FirestoreCallback<String> callback) {
        db.collection("Users").document(businessId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                if (task.getResult().exists() && task.getResult().contains("imageURL")) {
                    String businessiamgeURL = task.getResult().getString("imageURL");
                    if (businessiamgeURL != null) {
                        // Call onSuccess with the business name
                        callback.onSuccess(businessiamgeURL);
                    } else {
                        // The name field is missing or null, trigger onFailure
                        callback.onFailure(new Exception("Business imageURL field is missing or null."));
                    }
                } else {
                    // Document does not exist or missing 'name' field
                    callback.onFailure(new Exception("Document does not exist or is missing 'imageURL' field."));
                }
            } else {
                // Fetching the document failed
                callback.onFailure(task.getException() != null ? task.getException() : new Exception("Failed to fetch document."));
            }
        });
    }

    /**
     *
     * //---------------------------------------------SPECIAL OFFERS----------------------------------------------------//
     *
     */
    public void addBusinessoffer(BusinessSpecialOffers offers, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
//        db.collection("BusinessServices").document(service.getServiceId())
//                .set(service)
//                .addOnSuccessListener(onSuccessListener)
//                .addOnFailureListener(onFailureListener);

        String documentId = (offers.getOfferId() == null || offers.getOfferId().isEmpty())
                ? db.collection("BusinessOFFERS").document().getId()
                : offers.getOfferId();

        offers.setofferId(documentId); // Set the generated ID back to the service object

        db.collection("BusinessOFFERS").document(documentId)
                .set(offers)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }


    /**
     * Fetches/refreshes the services, and gets them from database.
     * @param businessId id of the business associated with services we want to fetch.
     * @param onSuccessListener
     * @param onFailureListener
     */
    public void fetchBusinessoffer(String businessId, OnSuccessListener<List<BusinessSpecialOffers>> onSuccessListener, OnFailureListener onFailureListener) {
        db.collection("BusinessOFFERS")
                .whereEqualTo("businessId", businessId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<BusinessSpecialOffers> offers = new ArrayList<>();
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                        BusinessSpecialOffers offer = snapshot.toObject(BusinessSpecialOffers.class);
                        offers.add(offer);
                    }
                    onSuccessListener.onSuccess(offers);
                })
                .addOnFailureListener(onFailureListener);
    }


    /**
     * Handles any change/update in a service information (Name/Description/Duration).
     * @param offer a BusinessService object.
     * The Id should be associated with the service object itself.
     */
    public void updateBusinessoffer (BusinessSpecialOffers offer ,OnSuccessListener  onSuccessListener, OnFailureListener onFailureListener){
        db.collection("BusinessOFFERS").document(offer.getOfferId())
                .set(offer)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }


    /**
     * Deletes the service with id : 'serviceId' from database.
     * @param offerID the id of the service.
     * @param onSuccessListener
     * @param onFailureListener
     */
    public void deleteBusinessoffer(String offerID, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        db.collection("BusinessOFFERS").document(offerID)
                .delete()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }


    public void update_name_and_image(String customerid,String name,String image, FirestoreCallback<List<Appointment>> callback) {

        List<Appointment> appointments = new ArrayList<>();//List of appointments
        LocalDate S=LocalDate.now().atStartOfDay().toLocalDate();

        // Correct conversion
//        Timestamp startTimestamp = Utils.localDateToTimestamp(S);
//        Timestamp endTimestamp = Utils.localDateToTimestamp(S.plusDays(7));

        db.collection("Appointments")
                .whereEqualTo("customerId", customerid)
                //.whereGreaterThanOrEqualTo("date", startTimestamp)
                //.whereLessThan("date", endTimestamp)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        //Appointment appointment = documentSnapshot.toObject(Appointment.class);/////cehck if not NULL !!!!!
                        Appointment appointment = new Appointment();
                        if (documentSnapshot.exists()) {

//
//                            // Directly using Timestamp from Firestore
//                            Timestamp DATE = documentSnapshot.getTimestamp("date");
//                            Timestamp START = documentSnapshot.getTimestamp("startTime");
//                            Timestamp END = documentSnapshot.getTimestamp("endTime");
//
//                            LocalDate local_date = Utils.timestampToLocalDate(DATE);
//                            // LocalTime local_start = Utils.timestampToLocalTime(START);
//                            //LocalTime local_end = Utils.timestampToLocalTime(END);
//
//
//                            // If they don't, we'll need to adjust them accordingly
//                            appointment.setDate(local_date); // Adjust if your setDate expects a different type
//                            // appointment.setStartTime(local_start); // Adjust if your setStartTime expects a different type
//                            //appointment.setEndTime(local_end); // Adjust if your setEndTime expects a different type

                            // Set other fields as necessary
                            appointment.setAppointmentId(documentSnapshot.getId());
                            appointment.setBusinessId(documentSnapshot.getString("businessId"));
                            appointment.setServiceId(documentSnapshot.getString("serviceId"));
                            appointment.setProviderId(documentSnapshot.getString("providerId"));
                            appointment.setCustomername(documentSnapshot.getString("customerName"));
                            appointment.setCustomername(name);
                            appointment.setImageURL(image);

                            appointment.setCustomerId(documentSnapshot.getString("customerId"));
                            appointment.setStatus(documentSnapshot.getString("status"));

                            // Now, 'appointment' is populated with the data from the documentSnapshot
                        }


                        appointments.add(appointment);
                    }
                    if (callback != null) {
                        callback.onSuccess(appointments);
                    }
                })
                .addOnFailureListener(e -> {
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                });
    }
    public void update_name_and_image(String customerid,String name,String image){
        db.collection("Appointments")
                .whereEqualTo("customerId", customerid) // Use the unique identifier to locate the document
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String documentId = document.getId(); // Get the document ID
                            // Now you can update the document as shown above
                            db.collection("Appointments").document(documentId)
                                    .update("CustomerName", name)
                                    .addOnSuccessListener(aVoid -> Log.d("Update", "Document successfully updated"))
                                    .addOnFailureListener(e -> Log.w("Update", "Error updating document", e));
                        }
                    } else {
                        Log.d("Query", "Error getting documents: ", task.getException());
         }
});
    }




    //----------------------------------------------------------------------------------------------------------------//


    /**
     * static class to represent working hours for a business (only open time and close time without a business ID).
     * Used in multiple activities in the app to show these hours on the screen.
     */

    public static class WorkingHours {
        public Timestamp openTime;
        public Timestamp closeTime;

        public WorkingHours(){
            //required for Firestore
        }

        public WorkingHours(Timestamp openTime, Timestamp closeTime) {
            this.openTime = openTime;
            this.closeTime = closeTime;
        }


        // Getters and setters

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


        // Method to convert Timestamp to a readable String format, if needed
        public String formatTimestamp(Timestamp timestamp) {
            if (timestamp != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = timestamp.toDate(); // Convert Timestamp to java.util.Date
                return sdf.format(date); // Format Date to String
            }
            return null;
        }
    }

    //------------------------------------------------------------------------------------------------------------------//











}
