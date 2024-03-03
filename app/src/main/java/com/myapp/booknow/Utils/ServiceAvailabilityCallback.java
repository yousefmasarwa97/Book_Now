package com.myapp.booknow.Utils;

public interface ServiceAvailabilityCallback {
    void onResult(boolean isAvailable);
    void onError(Exception e);
}
