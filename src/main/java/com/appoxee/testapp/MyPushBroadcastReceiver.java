package com.appoxee.testapp;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.appoxee.internal.push.PushLocalBroadcast;
import com.appoxee.push.PushData;

/**
 * Created by alexeykrichun on 11/07/16.
 */
public class MyPushBroadcastReceiver extends PushLocalBroadcast {
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
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P)
//            {
//                startActivityNotification(pushData,context,23332,"Open It","YouTube");
//            }else {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(pushData.silentData));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.context.startActivity(i);
//            }

        }


    }

//    public static void startActivityNotification(PushData pushData,Context context, int notificationID,
//                                                 String title, String message) {
//
//        NotificationManager mNotificationManager =
//                (NotificationManager)
//                        context.getSystemService(Context.NOTIFICATION_SERVICE);
//        //Create GPSNotification builder
//        NotificationCompat.Builder mBuilder;
//
//        //Initialise ContentIntent
//
//        Intent ContentIntent=new Intent(Intent.ACTION_VIEW, Uri.parse(pushData.silentData));
//        ContentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        PendingIntent ContentPendingIntent = PendingIntent.getActivity(context,
//                0,
//                ContentIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//
//        mBuilder = new NotificationCompat.Builder(context)
//                .setSmallIcon(R.drawable.apx_small_icon)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setAutoCancel(true)
//                .setContentIntent(ContentPendingIntent)
//                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
//                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            NotificationChannel mChannel = new NotificationChannel("Test1233",
//                    "Activity Opening Notification",
//                    NotificationManager.IMPORTANCE_HIGH);
//            mChannel.enableLights(true);
//            mChannel.enableVibration(true);
//            mChannel.setDescription("Open It");
//            mBuilder.setChannelId("Test1233");
//
//            Objects.requireNonNull(mNotificationManager).createNotificationChannel(mChannel);
//        }
//
//        Objects.requireNonNull(mNotificationManager).notify("NOTIFICATION_MESSAGE",notificationID,
//                mBuilder.build());
//    }


    @Override
    public void onButtonClick(PushData pushData, String buttonAction, int buttonPosition) {
        Log.d("APX", "Button clicked: " + pushData);
    }
}
