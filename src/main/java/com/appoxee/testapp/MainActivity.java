package com.appoxee.testapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.appoxee.Appoxee;
import com.appoxee.DeviceInfo;
import com.appoxee.RequestStatus;
import com.appoxee.internal.inapp.model.APXInboxMessage;
import com.appoxee.internal.inapp.model.InAppMessage;
import com.appoxee.internal.inapp.model.InAppCallback;
import com.appoxee.internal.inapp.model.InAppInboxCallback;
import com.appoxee.internal.inapp.model.InAppMessageDismissalCallback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import static com.appoxee.Appoxee.removeBadgeNumber;

public class MainActivity extends Activity implements Appoxee.OnInitCompletedListener {
    //This is a test commit
    private Switch pushEnabledSwitch;
    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1 << 3;
    private LinearLayout mMainLayout;
    private TextView mTextView;
    Appoxee appoxee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        appoxee = Appoxee.instance();

        Appoxee.instance().addInitListener(this);
        InAppCallback inAppCallback = new InAppCallback();
        inAppCallback.addInAppMessageReceivedCallback(new InAppCallback.onInAppEventReceived() {
            @Override
            public void onInAppEvent(String eventName, String eventValue) {
                Log.d("  eventName = ", eventName);
                Log.d("  eventValue = ", eventValue);
                Toast.makeText(MainActivity.this, "KEY = " + eventName + "VALUE = " + eventValue, Toast.LENGTH_LONG).show();
            }
        });

        InAppInboxCallback inAppInboxCallback = new InAppInboxCallback();
        inAppInboxCallback.addInAppInboxMessagesReceivedCallback(new InAppInboxCallback.onInAppInboxMessagesReceived() {
            @Override
            public void onInAppInboxMessages(List<APXInboxMessage> richMessages) {
                Log.d("messages", "messages = " + richMessages.get(0).getContent());
                Bundle bundle = new Bundle();
                bundle.putSerializable("inboxMessages", (ArrayList<APXInboxMessage>) richMessages);
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

                Log.v("MainActivity", "onInAppMessageDismissalCallback");
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
                appoxee.addTag("tag" + aliasCounter);
                appoxee.setAttribute("numericAttr" + aliasCounter, aliasCounter);
                appoxee.setAttribute("stringAttr" + aliasCounter, "str" + aliasCounter);
                appoxee.setAttribute("dateAttr" + aliasCounter, Calendar.getInstance().getTime());

                createBuilder("", appoxee.getAlias());
//                Appoxee.instance().setAttribute("custom1", "value1");
            }
        });

        findViewById(R.id.get_alias).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Appoxee appoxee = Appoxee.instance();
                Set<String> tags = appoxee.getTags();
                Log.d("APX", "tags: " + new Gson().toJson(tags));
                createBuilder("", appoxee.getAlias());
            }
        });

        findViewById(R.id.btn_set_alias).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = findViewById(R.id.etxt_set_alias);
                appoxee.setAlias(String.valueOf(editText.getText().toString()));
                createBuilder("New alias", "Added new alias");

            }
        });

        findViewById(R.id.second_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "New activity opened", Toast.LENGTH_SHORT).show();
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

        pushEnabledSwitch = (Switch) findViewById(R.id.push_enabled);
        pushEnabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                RequestStatus status = Appoxee.instance().setPushEnabled(isChecked);

            }
        });

        findViewById(R.id.buttonDeviceInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Appoxee.instance().getDeviceInfoDMC();
                String s = new StringBuilder("Device model: ")
                        .append(appoxee.getDeviceInfo().deviceModel)
                        .append("\n")
                        .append("App version: ")
                        .append(appoxee.getDeviceInfo().appVersion)
                        .append("\n")
                        .append("OS version: ")
                        .append(appoxee.getDeviceInfo().osVersion).toString();
                createBuilder("", s);
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

                Appoxee.instance().triggerDMCCallInApp(MainActivity.this, "app_welcome");
            }
        });

        //TODO: add function for test
        findViewById(R.id.btn_get_tags).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<String> tags = appoxee.getTags();
                StringBuilder s = new StringBuilder("");
                for (String tag : tags) {
                    s.append("\n")
                            .append(tag);
                }
                createBuilder("All tags", s.toString());
            }
        });

        findViewById(R.id.btn_set_tag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText tag = findViewById(R.id.etxt_set_tag);
                appoxee.addTag(tag.getText().toString());
                createBuilder("Set tag", "Setted new tag");
                tag.setText("s");
            }
        });

        findViewById(R.id.btn_remove_tag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText tag = findViewById(R.id.etxt_remove_tag);
                appoxee.removeTag(tag.getText().toString());
                createBuilder("Remote tag", "Remoted tag");
                tag.setText("");
            }
        });

        findViewById(R.id.btn_set_attribute).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText attribute = findViewById(R.id.etxt_set_attribute);
                appoxee.setAttribute(attribute.getText().toString(), attribute.getText().toString());
                createBuilder("Set attribute", "Added new attribute");
                attribute.setText("");
            }
        });

        findViewById(R.id.btn_get_attribute).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText attribute = findViewById(R.id.etxt_get_attribute);
                String s = appoxee.getAttributeStringValue(attribute.getText().toString());
                createBuilder("Get attribute", s);
                attribute.setText("");
            }
        });

        findViewById(R.id.btn_remove_attribute).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText attribute = findViewById(R.id.etxt_remove_attribute);
                appoxee.removeAttribute(attribute.getText().toString());
                createBuilder("Remove attribute", "Removed attribute");
                attribute.setText("");
            }
        });

        findViewById(R.id.btn_remove_badge_number).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeBadgeNumber(MainActivity.this);
                createBuilder("Remove badge", "All badges deleted");
            }
        });

        findViewById(R.id.btn_orientation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogScreenOrientation();
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

    void dialogScreenOrientation() {

        String[] screen_orientation = getResources().getStringArray(R.array.screen_orientation);
        final AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setTitle("Select a screen orientation");
        alt_bld.setItems(screen_orientation, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: {
                        appoxee.setOrientation(getApplication(), ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        break;
                    }

                    case 1: {
                        appoxee.setOrientation(getApplication(), ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        break;
                    }

                    case 2: {
                        appoxee.setOrientation(getApplication(), ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                        break;
                    }

                    case 3: {
                        appoxee.setOrientation(getApplication(), ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                        break;
                    }

                }

                Toast.makeText(getApplicationContext(), (getResources().getStringArray(R.array.screen_orientation)[which]), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

        });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }

    private void startGeo() {
        if (geoPermissionNotGranted()) {
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

    private void stopGeoFencing() {
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

    private void createBuilder(String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setMessage(message).setTitle(title);
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }
}

