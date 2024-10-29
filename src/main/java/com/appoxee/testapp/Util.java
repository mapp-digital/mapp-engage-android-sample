package com.appoxee.testapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

class Util {

    private static final String GdprKey = "GdprAgreement";

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

    public static void restartApp(Context context) {
        Context ctx = context.getApplicationContext();
        PackageManager pm = ctx.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(ctx.getPackageName());
        Intent mainIntent = Intent.makeRestartActivityTask(intent.getComponent());
        //ctx.startActivity(mainIntent);

        Runtime.getRuntime().exit(0);
    }

    @Nullable
    public static GdprAgreement getGdprAgreement(Context context) {
        Gson gson = new Gson();
        SharedPreferences prefs = context.getSharedPreferences(GdprKey, Context.MODE_PRIVATE);
        String json = prefs.getString("gdpr", null);
        return json != null ? gson.fromJson(json, GdprAgreement.class) : null;
    }

    public static void setGdprAgreement(Context context, GdprAgreement gdpr) {
        Gson gson = new Gson();
        SharedPreferences prefs = context.getSharedPreferences(GdprKey, Context.MODE_PRIVATE);
        String json = gson.toJson(gdpr);
        prefs.edit()
                .putString("gdpr", json)
                .apply();
    }
}
