package com.appoxee.testapp;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.appoxee.push.PushData;
import com.appoxee.push.PushDataReceiver;

/**
 * Created by alexeykrichun on 11/07/16.
 */
public class MyPushBroadcastReceiver extends PushDataReceiver {
    @Override
    public void onPushReceived(PushData pushData) {
        Log.d("Engage", "Push received " + pushData);
        super.onPushReceived(pushData);
    }

    @Override
    public void onPushOpened(PushData pushData) {
        Log.d("APX", "Push opened " + pushData);
        //  super.onPushReceived(pushData);
    }

    @Override
    public void onPushDismissed(PushData pushData) {
        Log.d("APX", "Push dismissed " + pushData);
        //   super.onPushReceived(pushData);
    }


    @Override
    public void onSilentPush(PushData pushData) {
        Log.d("APX", "Push Silent" + pushData);
        if ("youtube_music".equals(pushData.silentType)) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(pushData.silentData));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.context.startActivity(i);
        }
    }

    @Override
    public void onButtonClick(PushData pushData, String buttonAction, int buttonPosition) {
        Log.d("APX", "Button clicked: " + pushData);
    }
}
