package com.appoxee.testapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;

import androidx.fragment.app.FragmentActivity;

import com.appoxee.Appoxee;

class Util {

    static String capitalize(String inputWord) {
        String[] words = inputWord.toLowerCase().split("_");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];

            if (i > 0 && word.length() > 0) {
                builder.append("_");
            }

            String cap = word.substring(0, 1).toUpperCase() + word.substring(1);
            builder.append(cap);
        }
        return builder.toString();
    }

    public static void restartApp(Context context){
        Context ctx= context.getApplicationContext();
        PackageManager pm=ctx.getPackageManager();
        Intent intent=pm.getLaunchIntentForPackage(ctx.getPackageName());
        Intent mainIntent=Intent.makeRestartActivityTask(intent.getComponent());
        //ctx.startActivity(mainIntent);

        Runtime.getRuntime().exit(0);
    }
}
