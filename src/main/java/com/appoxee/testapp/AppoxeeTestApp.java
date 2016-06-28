package com.appoxee.testapp;

import android.app.Application;
import android.util.Log;

import com.appoxee.Appoxee;
import com.appoxee.AppoxeeOptions;
import com.appoxee.DeviceInfo;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

public class AppoxeeTestApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        long start = System.currentTimeMillis();
        AppoxeeOptions opt = new AppoxeeOptions();
        opt.sdkKey = "56c42a1c2b76c0.63627710";
        Appoxee.engage(this, opt);
        long end = System.currentTimeMillis();
        DeviceInfo info = Appoxee.instance().getDeviceInfo();
        long total = end - start;
        Log.i("APX", "Start Service took " + total + " ms on main thread");
        Log.d("APX", "info (before init finished): " + info);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String token = null;
                try {
                    InstanceID instanceID = InstanceID.getInstance(AppoxeeTestApp.this);//.getToken("firebasetest1", "GCM");
                    token = instanceID.getToken("fir-test1-e10ee",
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i("APX", "token: " + token);
            }
        }).start();


    }

}
