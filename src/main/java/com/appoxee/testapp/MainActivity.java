package com.appoxee.testapp;

import static com.appoxee.Appoxee.removeBadgeNumber;
import static com.appoxee.testapp.Constants.KEY_APP_ID;
import static com.appoxee.testapp.Constants.KEY_CEP_URL;
import static com.appoxee.testapp.Constants.KEY_GOOGLE_PROJECT_ID;
import static com.appoxee.testapp.Constants.KEY_SDK_KEY;
import static com.appoxee.testapp.Constants.KEY_TENANT_ID;
import static com.appoxee.testapp.Util.capitalize;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.appoxee.Appoxee;
import com.appoxee.AppoxeeOptions;
import com.appoxee.GetAliasCallback;
import com.appoxee.GetCustomAttributesCallback;
import com.appoxee.RequestStatus;
import com.appoxee.internal.inapp.model.APXInboxMessage;
import com.appoxee.internal.inapp.model.InAppCallback;
import com.appoxee.internal.inapp.model.InAppInboxCallback;
import com.appoxee.internal.inapp.model.InAppMessage;
import com.appoxee.internal.inapp.model.InAppMessageDismissalCallback;
import com.appoxee.internal.logger.Logger;
import com.appoxee.internal.logger.LoggerFactory;
import com.appoxee.internal.permission.GeofencePermissions;
import com.appoxee.internal.permission.GeofencingPermissionsCallback;
import com.appoxee.internal.permission.PermissionsCallback;
import com.appoxee.internal.permission.PermissionsManager;
import com.appoxee.internal.service.AppoxeeServiceAdapter;
import com.appoxee.push.NotificationMode;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends AppCompatActivity implements Appoxee.OnInitCompletedListener {
    //This is a test commit
    private Switch pushEnabledSwitch;
    private Switch deviceRegistrationState;
    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1 << 3;
    private static final int MY_PERMISSIONS_ACCESS_FINE_AND_BACKGROUND_LOCATION = 1 << 4;
    private LinearLayout mMainLayout;
    private TextView mTextView;
    private Appoxee appoxee;
    private EditText set_alias;
    private EditText set_tag;
    private EditText remove_tag;
    private EditText set_attribute_key;
    private EditText set_attribute_value;
    private EditText get_attribute;
    private EditText remove_attribute;
    private EditText get_custom_attributes;
    TextView textView;
    private AppoxeeOptions options;
    private Spinner spinner_events;
    private boolean isInitSpinner = false;
    private boolean runningQOrLater = Build.VERSION.SDK_INT >= 29;

    private Logger devLogger;

    private GeofencePermissions geofencePermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        appoxee = Appoxee.instance();
        set_alias = findViewById(R.id.etxt_set_alias);
        set_tag = findViewById(R.id.etxt_set_tag);
        remove_tag = findViewById(R.id.etxt_remove_tag);
        set_attribute_key = findViewById(R.id.etxt_set_attribute_key);
        set_attribute_value = findViewById(R.id.etxt_set_attribute_value);
        get_attribute = findViewById(R.id.etxt_get_attribute);
        remove_attribute = findViewById(R.id.etxt_remove_attribute);
        get_custom_attributes = findViewById(R.id.etxt_get_custom_attributes);
        spinner_events = findViewById(R.id.spinner_events);
        init();
        Appoxee.handleRichPush(this, getIntent());

        devLogger = LoggerFactory.getDevLogger();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Appoxee.handleRichPush(this, intent);
    }

    private void init() {
        Appoxee.instance().addInitListener(this);

        geofencePermissions = new GeofencePermissions(this, new GeofencingPermissionsCallback() {
            @Override
            public void onGranted() {
                devLogger.d("OnGranted", "startGeoFencing()");
                Appoxee.instance().startGeoFencing();
            }

            @Override
            public void onPermissionsNotGranted(List<String> permissions) {
                if (permissions.contains(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        geofencePermissions.openPermissionSettings();
                        //remove background permission
                        permissions = permissions.stream()
                                .filter(p -> !Manifest.permission.ACCESS_BACKGROUND_LOCATION.equals(p))
                                .collect(Collectors.toList());
                    }
                }
                if (!permissions.isEmpty())
                    geofencePermissions.checkPermissions(permissions);
            }

            @Override
            public void onPermanentlyDeniedPermissions(List<String> permissions) {
                geofencePermissions.openPermissionSettings();
            }
        });

        options = ((AppoxeeTestApp) getApplication()).getAppoxeeOptions();

        InAppCallback inAppCallback = new InAppCallback();

        inAppCallback.addInAppMessageReceivedCallback(new InAppCallback.onInAppEventReceived() {
            @Override
            public void onInAppEvent(String eventName, String eventValue) {
                Log.d("  eventName = ", eventName);
                Log.d("  eventValue = ", eventValue);
                Toast.makeText(MainActivity.this, "KEY = " + eventName + "VALUE = " + eventValue, Toast.LENGTH_LONG).show();
            }
        });
        textView = (TextView) findViewById(R.id.textView2);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String deviceToken = instanceIdResult.getToken();
                textView.setText(deviceToken);
                Log.d("token fcm", deviceToken);
            }
        });

        //  textView.setText(FirebaseInstanceId.getInstance().getToken());
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
//                SharedPreferences sp = getSharedPreferences("test", MODE_PRIVATE);
//                int aliasCounter = sp.getInt("aliasCounter", 0);
//                aliasCounter++;
//                sp.edit().putInt("aliasCounter", aliasCounter).apply();
//                DeviceInfo info = Appoxee.instance().getDeviceInfo();
//                Log.d("APX", "info: (click)" + new Gson().toJson(info));
//                Appoxee appoxee = Appoxee.instance();
////                appoxee.setAlias(getString(R.string.alias_email));
//                appoxee.addTag("tag" + aliasCounter);
//                appoxee.setAttribute("numericAttr" + aliasCounter, aliasCounter);
//                appoxee.setAttribute("stringAttr" + aliasCounter, "str" + aliasCounter);
//                appoxee.setAttribute("dateAttr" + aliasCounter, Calendar.getInstance().getTime());
//
//                createBuilder("", appoxee.getAlias());
//                Appoxee.instance().setAttribute("custom1", "value1");

                Toast.makeText(MainActivity.this, "Push enabled: " + Appoxee.instance().isPushEnabled(), Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.get_alias).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Appoxee appoxee = Appoxee.instance();
//                Set<String> tags = appoxee.getTags();
//                Log.d("APX", "tags: " + new Gson().toJson(tags));

                String getAlias = getAlias();
                createBuilder("", getAlias);

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", getAlias);
                clipboard.setPrimaryClip(clip);
            }
        });

        findViewById(R.id.get_deviceId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deviceId = Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);
                createBuilder("", deviceId);
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", deviceId);
                clipboard.setPrimaryClip(clip);
            }
        });


        findViewById(R.id.btn_set_alias).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                appoxee.setAlias(set_alias.getText().toString());
                createBuilder("New alias", "Added alias: " + set_alias.getText());
                set_alias.setText("");
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
                String s = appoxee.getDeviceInfo().toString();
                createBuilder("", s);
            }
        });

        findViewById(R.id.dmcCallInApp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Appoxee.instance().triggerInApp(MainActivity.this, "app_open");
            }
        });

        findViewById(R.id.inappModalType).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Appoxee.instance().triggerInApp(MainActivity.this, "app_feedback");
            }
        });

        findViewById(R.id.inappBannerType).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Appoxee.instance().triggerInApp(MainActivity.this, "app_discount");
            }
        });

        findViewById(R.id.inappAppPromo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Appoxee.instance().triggerInApp(MainActivity.this, "app_promo");
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

                Appoxee.instance().triggerInApp(MainActivity.this, "app_welcome");
            }
        });

        findViewById(R.id.fcm_token).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", textView.getText().toString());
                clipboard.setPrimaryClip(clip);
                devLogger.d("FCM TOKEN: ", clip.toString());
            }
        });

        findViewById(R.id.btn_send_push).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Bundle[{push_description=Do you recive push notification?,
                google.delivered_priority=normal,
                google.sent_time=1631097227253,
                google.ttl=2419200,
                google.original_priority=normal,
                p=81723,
                from=1028993954364,
                alert=MappTest,
                google.message_id=0:1631097227271591%37fb9af9cccfb49c,
                google.c.sender.id=1028993954364,
                collapse_key=type_a,
                push_title=MappTest}]*/

                Bundle bundle = new Bundle();
                bundle.putString("push_description", "Do you recive push notification?");
                bundle.putString("google.delivered_priority", "normal");
                bundle.putString("google.sent_time", "1631097227253");
                bundle.putString("google.ttl", "2419200");
                bundle.putString("google.original_priority", "normal");
                bundle.putString("p", "81723");
                bundle.putString("from", "1028993954364L");
                bundle.putString("alert", "MappTest");
                bundle.putString("google.message_id", "0:1631097227271591%37fb9af9cccfb49c");
                bundle.putString("google.c.sender.id", "1028993954364");
                bundle.putString("collapse_key", "type_a");
                bundle.putString("push_title", "MappTest");
                RemoteMessage message = new RemoteMessage(bundle);
                AppoxeeServiceAdapter.getInstance().setRemoteMessage(message);
            }
        });

        findViewById(R.id.btn_register_token).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = FirebaseInstanceId.getInstance().getToken();
                if (token != null) {
                    AppoxeeServiceAdapter.getInstance().setToken(token);
                    createBuilder("FCM Token", token);
                }
            }
        });

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

                if (set_tag.getText().length() == 0) {
                    Toast.makeText(MainActivity.this, "Please, filled field above", Toast.LENGTH_SHORT).show();
                } else {
                    appoxee.addTag(set_tag.getText().toString());
                    createBuilder("Set tag", "Setted tag: " + set_tag.getText());
                    set_tag.setText("");
                }

            }
        });

        findViewById(R.id.btn_remove_tag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (remove_tag.getText().length() == 0) {
                    Toast.makeText(MainActivity.this, "Please, filled field above", Toast.LENGTH_SHORT).show();
                } else {
                    RequestStatus status = appoxee.removeTag(remove_tag.getText().toString());
                    createBuilder("Remove tag", "Removed tag: " + remove_tag.getText());
                    remove_tag.setText("");
                }

            }
        });

        findViewById(R.id.btn_set_attribute).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (set_attribute_key.getText().length() == 0 || set_attribute_value.getText().length() == 0) {
                    Toast.makeText(MainActivity.this, "Please, fill key and value field above", Toast.LENGTH_SHORT).show();
                } else {
                    appoxee.setAttribute(set_attribute_key.getText().toString(), set_attribute_value.getText().toString());
                    createBuilder("Set attribute", "Added attribute: " + set_attribute_key.getText() + " - " + set_attribute_value.getText().toString());
                    set_attribute_key.setText("");
                    set_attribute_value.setText("");
                }
            }
        });

        findViewById(R.id.btn_get_attribute).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String s = appoxee.getAttributeStringValue(get_attribute.getText().toString());
                if (s == null || s.equals("")) {
                    Toast.makeText(MainActivity.this, "Doesn't exist this attribute", Toast.LENGTH_SHORT).show();
                } else {
                    createBuilder("Get attribute", "Get attribute: " + s);
                    get_attribute.setText("");
                }

            }
        });

        findViewById(R.id.btn_remove_attribute).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String s = appoxee.getAttributeStringValue(remove_attribute.getText().toString());
                if (s == null || s.equals("")) {
                    Toast.makeText(MainActivity.this, "Doesn't exist this attribute", Toast.LENGTH_SHORT).show();
                } else {
                    appoxee.removeAttribute(remove_attribute.getText().toString());
                    createBuilder("Remove attribute", "Removed attribute: " + s);
                    remove_attribute.setText("");
                }

            }
        });

        findViewById(R.id.get_custom_attributes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (get_custom_attributes.getText().length() == 0) {
                    Toast.makeText(MainActivity.this, "Please, fill text field above", Toast.LENGTH_SHORT).show();
                } else {
                    String getText = get_custom_attributes.getText().toString().replaceAll("\\s", "");
                    String[] customAttributes = getText.split(",");
                    appoxee.getCustomAttributes(true, Arrays.asList(customAttributes), new GetCustomAttributesCallback() {
                        @Override
                        public void onSuccess(Map<String, String> customAttributes) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    Iterator<Map.Entry<String, String>> iterator = customAttributes.entrySet().iterator();
                                    int i = 1;
                                    while (iterator.hasNext()) {
                                        Map.Entry<String, String> entry = iterator.next();
                                        stringBuilder.append('(');
                                        stringBuilder.append(i);
                                        stringBuilder.append(") ");
                                        stringBuilder.append(entry.getKey());
                                        stringBuilder.append(": ");
                                        stringBuilder.append(entry.getValue());
                                        i++;
                                        if (iterator.hasNext())
                                            stringBuilder.append("\n");
                                    }
                                    createBuilder("", stringBuilder.toString());
                                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("label", customAttributes.toString());
                                    clipboard.setPrimaryClip(clip);
                                }
                            });
                        }

                        @Override
                        public void onError(String exception) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    createBuilder("", exception);
                                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("label", exception);
                                    clipboard.setPrimaryClip(clip);
                                }
                            });
                        }
                    });
                }
            }
        });

        findViewById(R.id.get_alias_from_server).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appoxee.getAliasFromServer(true, new GetAliasCallback() {
                    @Override
                    public void onSuccess(String alias) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                createBuilder("", alias);
                                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("label", alias);
                                clipboard.setPrimaryClip(clip);
                            }
                        });
                    }

                    @Override
                    public void onError(String exception) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                createBuilder("", exception);
                                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("label", exception);
                                clipboard.setPrimaryClip(clip);
                            }
                        });
                    }
                });
            }
        });

        findViewById(R.id.btn_remove_badge_number).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeBadgeNumber(MainActivity.this.getApplicationContext());
                createBuilder("Remove badge", "All badges deleted");
            }
        });

        findViewById(R.id.btn_orientation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogScreenOrientation();
            }
        });

        findViewById(R.id.btn_open_test_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ConfigurationMappOptionsActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_backup_configuration).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backupConfiguration();
            }
        });

        initSpinnerEvents();

        findViewById(R.id.btn_logout_with_optout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Appoxee.instance().logOut(getApplication(), false);
            }
        });

        findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Appoxee.instance().logOut(getApplication(), true);
            }
        });
    }


    private void backupConfiguration() {
        Prefs.putString(KEY_SDK_KEY, BuildConfig.SDK_KEY);
        Prefs.putString(KEY_GOOGLE_PROJECT_ID, BuildConfig.GOOGLE_PROJECT_ID);
        Prefs.putString(KEY_CEP_URL, BuildConfig.CEP_URL);
        Prefs.putString(KEY_APP_ID, BuildConfig.APP_ID);
        Prefs.putString(KEY_TENANT_ID, BuildConfig.TENANT_ID);

        options.sdkKey = BuildConfig.SDK_KEY;
        options.googleProjectId = BuildConfig.GOOGLE_PROJECT_ID;
        options.cepURL = BuildConfig.CEP_URL;
        options.appID = BuildConfig.APP_ID;
        options.tenantID = BuildConfig.TENANT_ID;
        options.notificationMode = NotificationMode.BACKGROUND_AND_FOREGROUND;

        Appoxee.instance().setDeviceRegistrationState(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Appoxee.engage(getApplication(), options);
            }
        }, 1000);


        Toast.makeText(this, "Reset configuration", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onInitCompleted(boolean successful, Exception failReason) {

        Log.i("APX", "init completed listener - MainActivity");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pushEnabledSwitch.setChecked(Appoxee.instance().isPushEnabled());
                Appoxee.instance().triggerInApp(MainActivity.this, "app_open");
                restartGeofencing();
                mTextView.setText("App is initialized, Please wait while we display messages...");
            }
        });
    }

    private void restartGeofencing() {
        if (Appoxee.instance().isGeofencingActive()) {
            //startGeo();
        }
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
        List<String> permissions;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
            permissions = new ArrayList<>(Collections.singletonList(Manifest.permission.ACCESS_FINE_LOCATION));
        else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) { // Android Q (29) and higher requires BACKGROUND Location
            permissions = new ArrayList<>(Arrays.asList(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION));
        } else { // for Android R (30) and higher we can't ask both permissions at a same time
            permissions = new ArrayList<>(Collections.singletonList(Manifest.permission.ACCESS_FINE_LOCATION));
        }

        geofencePermissions.requestPermissions();
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


    public String getAlias() {
        return appoxee.getAlias();
    }


    public String getAttribute(String attr) {

        String attribute = appoxee.getAttributeStringValue(attr);
        if (attribute == null || attribute.equals("")) {
            attribute = "";
        } else {
            get_attribute.setText("");
        }

        return attribute;
    }

    public void removeTag(String tag) {
        appoxee.removeTag(tag);
    }

    private void initSpinnerEvents() {
        spinner_events.setPrompt("Choose one option");

        final String[] eventsList = getResources().getStringArray(R.array.event_array);
        ArrayList<String> eventsArrayList = new ArrayList<>();

        eventsArrayList.add("");
        for (String item : eventsList) {
            String s = capitalize(item);
            eventsArrayList.add(s);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, eventsArrayList) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = null;
                if (position == 0) {
                    TextView tv = new TextView(getContext());
                    tv.setHeight(0);
                    tv.setVisibility(View.GONE);
                    v = tv;
                } else {
                    v = super.getDropDownView(position, null, parent);
                }

                parent.setVerticalScrollBarEnabled(false);
                return v;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_events.setAdapter(adapter);

        spinner_events.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isInitSpinner) {
                    Toast.makeText(MainActivity.this, eventsList[position - 1], Toast.LENGTH_LONG).show();
                    Appoxee.instance().triggerInApp(MainActivity.this, eventsList[position - 1]);
                } else {
                    isInitSpinner = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }
}

