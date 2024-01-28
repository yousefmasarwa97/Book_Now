package com.myapp.booknow;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

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
        Timestamp openTime = Utils.convertToTimestamp(day, openTimeStr);
        Timestamp closeTime = Utils.convertToTimestamp(day, closeTimeStr);

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
