package com.appoxee.testapp;

import android.app.Application;
import android.util.Log;

import com.appoxee.Appoxee;
import com.appoxee.AppoxeeOptions;
import com.appoxee.DeviceInfo;
import com.appoxee.push.CustomXmlLayoutNotificationCreator;

public class AppoxeeTestApp extends Application {

    private Appoxee.OnInitCompletedListener initFinishedListener = new Appoxee.OnInitCompletedListener() {
        @Override
        public void onInitCompleted(boolean successful, Exception failReason) {
            Log.i("APX", "init completed listener - Application class");
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        long start = System.currentTimeMillis();
        AppoxeeOptions opt = new AppoxeeOptions();

        opt.sdkKey = BuildConfig.SDK_KEY;
//        opt.sdkKey = "576fc13f3d23e3.06780385";

        opt.googleProjectId = "651820799870";

        CustomXmlLayoutNotificationCreator.Builder builder = new CustomXmlLayoutNotificationCreator.Builder(this);
        builder.setLayoutResource(R.layout.custom_notification_layout)
                .setIconResourceId(R.id.appoxee_default_push_icon)
                .setTextResourceId(R.id.appoxee_default_push_message)
                .setTitleResourceId(R.id.appoxee_default_push_subject)
                .setTimeResourceId(R.id.appoxee_default_push_hour);

        opt.customNotificationCreator = new CustomXmlLayoutNotificationCreator(builder);

        Appoxee.engage(this, opt);
        Appoxee.instance().addInitListener(initFinishedListener);
        long end = System.currentTimeMillis();
        Log.i("APX", "Start Service took " + (end-start) + " ms on main thread");
        Appoxee.instance().setAlias("alias0");
        DeviceInfo info = Appoxee.instance().getDeviceInfo();
        Log.d("APX", "info (before init finished): " + info);
    }
}
