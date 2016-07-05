package com.appoxee.testapp;

import android.app.Application;
import android.util.Log;

import com.appoxee.Appoxee;
import com.appoxee.AppoxeeOptions;
import com.appoxee.DeviceInfo;

public class AppoxeeTestApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        long start = System.currentTimeMillis();
        AppoxeeOptions opt = new AppoxeeOptions();

        opt.sdkKey = "5725ceaec41069.29878975";
//        opt.sdkKey = "576fc13f3d23e3.06780385";

        opt.googleProjectId = "94866074595";

        Appoxee.engage(this, opt);
        long end = System.currentTimeMillis();
        DeviceInfo info = Appoxee.instance().getDeviceInfo();
        long total = end - start;
        Log.i("APX", "Start Service took " + total + " ms on main thread");
        Log.d("APX", "info (before init finished): " + info);

    }

}
