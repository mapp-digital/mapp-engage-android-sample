package com.appoxee.testapp;

import static com.appoxee.testapp.Constants.KEY_APP_ID;
import static com.appoxee.testapp.Constants.KEY_GOOGLE_PROJECT_ID;
import static com.appoxee.testapp.Constants.KEY_SDK_KEY;
import static com.appoxee.testapp.Constants.KEY_SERVER_INDEX;
import static com.appoxee.testapp.Constants.KEY_TENANT_ID;

import android.content.ContextWrapper;
import android.content.pm.ActivityInfo;
import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.appoxee.Appoxee;
import com.appoxee.AppoxeeOptions;
import com.appoxee.DeviceInfo;
import com.appoxee.push.NotificationMode;
import com.pixplicity.easyprefs.library.Prefs;

public class AppoxeeTestApp extends MultiDexApplication {

    private AppoxeeOptions opt;

    private final Appoxee.OnInitCompletedListener initFinishedListener = new Appoxee.OnInitCompletedListener() {
        @Override
        public void onInitCompleted(boolean successful, Exception failReason) {
            Log.i("APX", "init completed listener - Application class");
            Appoxee.instance().setPushEnabled(true);
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

        long start = System.currentTimeMillis();

        opt = new AppoxeeOptions();
        opt.sdkKey = Prefs.getString(KEY_SDK_KEY, BuildConfig.SDK_KEY);
        opt.appID = Prefs.getString(KEY_APP_ID, BuildConfig.APP_ID);
        opt.tenantID = Prefs.getString(KEY_TENANT_ID, BuildConfig.TENANT_ID);
        opt.notificationMode = NotificationMode.BACKGROUND_AND_FOREGROUND;
        opt.server = AppoxeeOptions.Server.valueOf(Prefs.getString(KEY_SERVER_INDEX, BuildConfig.SERVER_INDEX));

        Appoxee.engage(this, opt);
        Appoxee.instance().addInitListener(initFinishedListener);
        long end = System.currentTimeMillis();
        Log.i("APX", "Start Service took " + (end - start) + " ms on main thread");
        Appoxee.setOrientation(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Appoxee.instance().setReceiver(MyPushBroadcastReceiver.class);
    }


    public AppoxeeOptions getAppoxeeOptions() {
        return opt;
    }
}
