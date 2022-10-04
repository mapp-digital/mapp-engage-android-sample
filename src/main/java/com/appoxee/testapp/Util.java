package com.appoxee.testapp;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import androidx.fragment.app.FragmentActivity;

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
/*        new Handler().postDelayed(() -> {
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }, 500);*/
/*        if (context instanceof FragmentActivity) {
            ((FragmentActivity)context).finish();
        }*/
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
