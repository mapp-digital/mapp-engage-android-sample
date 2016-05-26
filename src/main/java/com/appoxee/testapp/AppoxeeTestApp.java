package com.appoxee.testapp;

import android.app.Application;

import com.appoxee.Appoxee;
import com.appoxee.AppoxeeOptions;

public class AppoxeeTestApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppoxeeOptions opt = new AppoxeeOptions();
        opt.sdkKey = "574597825dcf97.14767101";
        Appoxee.engage(this, opt);
    }
}
