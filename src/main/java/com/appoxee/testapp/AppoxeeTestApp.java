package com.appoxee.testapp;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ActivityInfo;
import android.util.Log;

import com.appoxee.Appoxee;
import com.appoxee.AppoxeeOptions;
import com.appoxee.DeviceInfo;
import com.appoxee.internal.util.SharedPreferenceUtil;
import com.appoxee.push.CustomXmlLayoutNotificationCreator;
import com.appoxee.push.NotificationMode;
import com.crashlytics.android.Crashlytics;
import com.pixplicity.easyprefs.library.Prefs;

//import com.squareup.leakcanary.LeakCanary;

import io.fabric.sdk.android.Fabric;

import static com.appoxee.testapp.Constants.*;

public class AppoxeeTestApp extends Application {

    private AppoxeeOptions opt;

    private Appoxee.OnInitCompletedListener initFinishedListener = new Appoxee.OnInitCompletedListener() {
        @Override
        public void onInitCompleted(boolean successful, Exception failReason) {
            Log.i("APX", "init completed listener - Application class");
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

//        final Fabric fabric = new Fabric.Builder(this)
//                .kits(new Crashlytics())
//                .debuggable(true)
//                .build();
//        Fabric.with(fabric);


        long start = System.currentTimeMillis();

        opt = new AppoxeeOptions();
        opt.sdkKey = Prefs.getString(KEY_SDK_KEY, BuildConfig.SDK_KEY);
        opt.googleProjectId = Prefs.getString(KEY_GOOGLE_PROJECT_ID, BuildConfig.GOOGLE_PROJECT_ID);
        opt.cepURL = Prefs.getString(KEY_CEP_URL, BuildConfig.CEP_URL);
        opt.appID = Prefs.getString(KEY_APP_ID, BuildConfig.APP_ID);
        opt.tenantID = Prefs.getString(KEY_TENANT_ID, BuildConfig.TENANT_ID);
        opt.notificationMode = NotificationMode.BACKGROUND_AND_FOREGROUND;
        opt.server = AppoxeeOptions.Server.EMC;

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
        Log.i("APX", "Start Service took " + (end - start) + " ms on main thread");
        Appoxee.instance().setAlias("alias0");
        Appoxee.setOrientation(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        DeviceInfo info = Appoxee.instance().getDeviceInfo();
        Log.d("APX", "info (before init finished): " + info);
        Appoxee.instance().setReceiver(MyPushBroadcastReceiver.class);
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
//        // Normal app init code...
    }


    public AppoxeeOptions getAppoxeeOptions() {
        return opt;
    }


}
