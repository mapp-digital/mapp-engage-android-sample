package com.appoxee.testapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.appoxee.Appoxee;
import com.appoxee.DeviceInfo;
import com.appoxee.internal.inapp.model.APXInboxMessage;
import com.appoxee.internal.inapp.model.InAppMessage;
import com.appoxee.internal.inapp.model.InAppCallback;
import com.appoxee.internal.inapp.model.InAppInboxCallback;
import com.appoxee.internal.inapp.model.InAppMessageDismissalCallback;
import com.appoxee.internal.inapp.model.InAppStatistics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

public class MainActivity extends Activity implements Appoxee.OnInitCompletedListener {
//This is a test commit
    private Switch pushEnabledSwitch;
    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1 << 3;
    private LinearLayout mMainLayout;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Appoxee.instance().addInitListener(this);
        InAppCallback inAppCallback =  new InAppCallback();
        inAppCallback.addInAppMessageReceivedCallback(new InAppCallback.onInAppEventReceived() {
            @Override
            public void onInAppEvent(String eventName, String eventValue) {
                Log.d("  eventName = ", eventName);
                Log.d("  eventValue = ", eventValue);
                Toast.makeText(MainActivity.this, "KEY = " +eventName + "VALUE = " + eventValue, Toast.LENGTH_LONG).show();
            }
        });

        InAppInboxCallback inAppInboxCallback = new InAppInboxCallback();
        inAppInboxCallback.addInAppInboxMessagesReceivedCallback(new InAppInboxCallback.onInAppInboxMessagesReceived() {
            @Override
            public void onInAppInboxMessages(List<APXInboxMessage> richMessages) {
                Log.d("messages","messages = " +richMessages.get(0).getContent());
                Bundle bundle = new Bundle();
                bundle.putSerializable("inboxMessages", (ArrayList<APXInboxMessage>)richMessages);
                Intent intent = new Intent(MainActivity.this, InboxActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onInAppInboxMessage(APXInboxMessage message) {

            }
        });

        InAppMessageDismissalCallback inAppMessageDismissalCallback = new InAppMessageDismissalCallback();
        inAppMessageDismissalCallback.addOnInAppMessageDismissalCallback(new InAppMessageDismissalCallback.onInAppMessageDismissalCallback() {
            @Override
            public void onInAppMessageDismissalCallback(int templateId, String eventId, boolean isSendStats) {

                Log.v("MainActivity","onInAppMessageDismissalCallback");
            }

        });

        mMainLayout = (LinearLayout) findViewById(R.id.parentLayout);
        mTextView = (TextView) findViewById(R.id.dummyText);
        //mMainLayout.setVisibility(View.GONE);//Make it visible to see other controls what Appoxee has in stock
        findViewById(R.id.device_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("test", MODE_PRIVATE);
                int aliasCounter = sp.getInt("aliasCounter", 0);
                aliasCounter++;
                sp.edit().putInt("aliasCounter", aliasCounter).apply();
                DeviceInfo info = Appoxee.instance().getDeviceInfo();
                Log.d("APX", "info: (click)" + new Gson().toJson(info));
                Appoxee appoxee = Appoxee.instance();
                appoxee.setAlias("sdk4.alias-" + aliasCounter);
                appoxee.addTag("tag"+aliasCounter);
                appoxee.setAttribute("numericAttr" + aliasCounter, aliasCounter);
                appoxee.setAttribute("stringAttr" + aliasCounter, "str" + aliasCounter);
                appoxee.setAttribute("dateAttr" + aliasCounter, Calendar.getInstance().getTime());

//                Appoxee.instance().setAttribute("custom1", "value1");
            }
        });

        findViewById(R.id.get_alias).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Appoxee appoxee = Appoxee.instance();
                Set<String> tags = appoxee.getTags();
                Log.d("APX", "tags: " + new Gson().toJson(tags));

            }
        });

        findViewById(R.id.second_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.geo_fencing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              startGeo();
            }
        });
        findViewById(R.id.stop_geo_fencing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopGeoFencing();
            }
        });

        pushEnabledSwitch = (Switch)findViewById(R.id.push_enabled);
        pushEnabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Appoxee.instance().setPushEnabled(isChecked);
            }
        });

        findViewById(R.id.buttonDeviceInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Appoxee.instance().getDeviceInfoDMC();
            }
        });

        findViewById(R.id.dmcCallInApp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Appoxee.instance().triggerDMCCallInApp(MainActivity.this, "app_open");
            }
        });

        findViewById(R.id.inappModalType).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Appoxee.instance().triggerDMCCallInApp(MainActivity.this, "app_feedback");
            }
        });

        findViewById(R.id.inappBannerType).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Appoxee.instance().triggerDMCCallInApp(MainActivity.this, "app_discount");
            }
        });

        findViewById(R.id.inappAppPromo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Appoxee.instance().triggerDMCCallInApp(MainActivity.this, "app_promo");
            }
        });


        findViewById(R.id.inappInbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Appoxee.instance().fetchInboxMessages(MainActivity.this);
            }
        });

        findViewById(R.id.multipleMessages).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Appoxee.instance().triggerDMCCallInApp(MainActivity.this,  "app_welcome");
            }
        });



    }


    @Override
    public void onInitCompleted(boolean successful, Exception failReason) {
        Log.i("APX", "init completed listener - MainActivity");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pushEnabledSwitch.setChecked(Appoxee.instance().isPushEnabled());
                Appoxee.instance().triggerDMCCallInApp(MainActivity.this, "app_open");
                mTextView.setText("App is initialized, Please wait while we display messages...");
            }
        });
    }


    private void startGeo() {
        if (geoPermissionNotGranted()){
           Appoxee.instance().startGeoFencing();
        } else {
            askForGeoPermission();
        }
    }
    private boolean geoPermissionNotGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    private void askForGeoPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_ACCESS_FINE_LOCATION);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (MY_PERMISSIONS_ACCESS_FINE_LOCATION == requestCode) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Appoxee.instance().startGeoFencing();
            } else {

                Log.w("MianActivity", "Geo permission not granted");
            }
        } else {
            Log.w("Main Activity", "some other permission requested? (not geo)");
        }
    }

    private void stopGeoFencing(){
        Appoxee.instance().stopGeoFencing();
    }

    private String getJsonString(List<InAppMessage> inbox) {
        // Before converting to GSON check value of id
        Gson gson = null;
        if (inbox != null && !inbox.isEmpty()) {
            gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
        } else {
            gson = new Gson();
        }
        return gson.toJson(inbox);
    }
}
