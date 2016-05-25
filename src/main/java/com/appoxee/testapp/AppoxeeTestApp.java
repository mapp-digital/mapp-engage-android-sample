package com.appoxee.testapp;

import android.app.Application;

import com.appoxee.Appoxee;

public class AppoxeeTestApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Appoxee.engage(this);
    }
}
