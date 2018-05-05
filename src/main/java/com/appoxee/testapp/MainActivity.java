package com.appoxee.testapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.appoxee.Appoxee;
import com.appoxee.DeviceInfo;
import com.appoxee.internal.inapp.model.APXInboxMessage;
import com.appoxee.internal.inapp.model.InAppMessage;
import com.appoxee.internal.inapp.model.InAppCallback;
import com.appoxee.internal.inapp.model.InAppInboxCallback;
import com.appoxee.internal.inapp.model.InAppStatistics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

public class MainActivity extends Activity implements Appoxee.OnInitCompletedListener {

    private Switch pushEnabledSwitch;
    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1 << 3;
    private EditText mTenantIdTV, mAppIdTV, mUserIdTV, mDeviceIdTV, mEventName, mJamieUrl, mAlias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mTenantIdTV = (EditText) findViewById(R.id.tenantId);
        mAppIdTV = (EditText) findViewById(R.id.appId);
        mUserIdTV = (EditText) findViewById(R.id.userId);
        mDeviceIdTV = (EditText) findViewById(R.id.deviceid);
        mEventName = (EditText) findViewById(R.id.event_name);
        mJamieUrl = (EditText) findViewById(R.id.jamieUrlVal);
        mAlias = (EditText) findViewById(R.id.aliasVal) ;
        setDefaultText();
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

        findViewById(R.id.fetchInbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Appoxee.instance().fetchInboxMessages(MainActivity.this,
                        mTenantIdTV.getText().toString().trim(),
                        mUserIdTV.getText().toString().trim(),
                        mDeviceIdTV.getText().toString().trim(),
                        mAppIdTV.getText().toString().trim(),
                        mJamieUrl.getText().toString().trim(), mAlias.getText().toString().trim());
            }
        });

        findViewById(R.id.dmcCallInApp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               callInappMessages(MainActivity.this,
                        mTenantIdTV.getText().toString().trim(),
                        mUserIdTV.getText().toString().trim(),
                        mDeviceIdTV.getText().toString().trim(),
                        mAppIdTV.getText().toString().trim(),"app_open");
            }
        });

      /*  findViewById(R.id.inappModalType).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callInappMessages(MainActivity.this,
                        mTenantIdTV.getText().toString().trim(),
                        mUserIdTV.getText().toString().trim(),
                        mDeviceIdTV.getText().toString().trim(),
                        mAppIdTV.getText().toString().trim(),"app_feedback");
            }
        });*/

        /*findViewById(R.id.inappBannerType).setOnClickListener(new View.OnClickListener() {
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
        });*/


    }

    private void callInappMessages(final Context ctx, String tenantId, String userId,
                              String deviceId, String appId, final String event) {
        Appoxee.instance().triggerDMCCallInApp(MainActivity.this,
                mTenantIdTV.getText().toString().trim(),
                mUserIdTV.getText().toString().trim(),
                mDeviceIdTV.getText().toString().trim(),
                mAppIdTV.getText().toString().trim(),
                mEventName.getText().toString().trim(),
                mJamieUrl.getText().toString().trim(), mAlias.getText().toString().trim());
    }


    @Override
    public void onInitCompleted(boolean successful, Exception failReason) {
        Log.i("APX", "init completed listener - MainActivity");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pushEnabledSwitch.setChecked(Appoxee.instance().isPushEnabled());
                //Test Call only for the Testing
                Appoxee.instance().getDeviceInfoDMC();

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
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    private void askForGeoPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {

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

    private void setDefaultText() {
       /* mTenantIdTV.setText("42");
        mDeviceIdTV.setText("02AC264E264EE92B248781008B7571CE979E2AB0724020F59E63336ECCD97B2D");
        mUserIdTV.setText("8900000003");
        mAppIdTV.setText("123456");
        mEventName.setText("app_open");
        mJamieUrl.setText(BuildConfig.CEP_URL);
        mAlias.setText("AUTO_106322_E119BB23A55C49005F1DE9BA030042C52EB8752F2FC2FD8BF9658E1C2B3FDF9F");*/
        mTenantIdTV.setText("55");
        mDeviceIdTV.setText("02AC264E264EE92B248781008B7571CE979E2AB0724020F59E63336ECCD97B2D");
        mUserIdTV.setText("4");
        mAppIdTV.setText("262251");
        mEventName.setText("app_open");
        mJamieUrl.setText(BuildConfig.CEP_URL);
        mAlias.setText("AUTO_106322_E119BB23A55C49005F1DE9BA030042C52EB8752F2FC2FD8BF9658E1C2B3FDF9F");
    }
}
