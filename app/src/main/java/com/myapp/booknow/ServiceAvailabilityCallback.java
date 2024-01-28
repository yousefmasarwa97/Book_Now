package com.myapp.booknow;

public interface ServiceAvailabilityCallback {
    void onResult(boolean isAvailable);
    void onError(Exception e);
}
