package com.appoxee.testapp;

import android.util.Log;

import com.appoxee.push.PushData;
import com.appoxee.push.PushDataReceiver;

/**
 * Created by alexeykrichun on 11/07/16.
 */
public class MyPushBroadcastReceiver extends PushDataReceiver {
    @Override
    public void onPushReceived(PushData pushData) {
        Log.d("APX", "Push received " + pushData);
    }

    @Override
    public void onPushOpened(PushData pushData) {
        Log.d("APX", "Push opened " + pushData);
    }

    @Override
    public void onPushDismissed(PushData pushData) {
        Log.d("APX", "Push dismissed " + pushData);
    }
}
