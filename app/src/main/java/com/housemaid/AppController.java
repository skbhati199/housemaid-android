package com.housemaid;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class AppController extends Application {
    private static AppController instance;

    public static AppController getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        FirebaseApp.initializeApp(this);
    }
}
