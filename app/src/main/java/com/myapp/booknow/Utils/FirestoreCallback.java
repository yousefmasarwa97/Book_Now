package com.myapp.booknow.Utils;

public interface FirestoreCallback<T> {
    void onSuccess(T result);
    void onFailure(Exception e);
}
