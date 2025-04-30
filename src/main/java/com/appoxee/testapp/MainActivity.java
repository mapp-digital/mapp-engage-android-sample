package com.appoxee.testapp;

import static com.appoxee.Appoxee.removeBadgeNumber;
import static com.appoxee.testapp.BuildConfig.APP_ID;
import static com.appoxee.testapp.BuildConfig.SDK_KEY;
import static com.appoxee.testapp.BuildConfig.SERVER_INDEX;
import static com.appoxee.testapp.BuildConfig.TENANT_ID;
import static com.appoxee.testapp.BuildConfig.VERSION_NAME;
import static com.appoxee.testapp.Constants.KEY_APP_ID;
import static com.appoxee.testapp.Constants.KEY_SDK_KEY;
import static com.appoxee.testapp.Constants.KEY_SERVER_INDEX;
import static com.appoxee.testapp.Constants.KEY_TENANT_ID;
import static com.appoxee.testapp.Util.capitalize;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.appoxee.Appoxee;
import com.appoxee.AppoxeeOptions;
import com.appoxee.GetAliasCallback;
import com.appoxee.RequestStatus;
import com.appoxee.internal.geo.geofencing.GeofenceStatus;
import com.appoxee.internal.inapp.model.APXInboxMessage;
import com.appoxee.internal.inapp.model.InAppCallback;
import com.appoxee.internal.inapp.model.InAppInboxCallback;
import com.appoxee.internal.inapp.model.InAppMessage;
import com.appoxee.internal.inapp.model.InAppMessageDismissalCallback;
import com.appoxee.internal.logger.Logger;
import com.appoxee.internal.logger.LoggerFactory;
import com.appoxee.internal.permission.PermissionHelper;
import com.appoxee.internal.permission.PermissionsManager;
import com.appoxee.internal.util.ResultCallback;
import com.appoxee.push.NotificationMode;
import com.appoxee.testapp.databinding.MainBinding;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity implements Appoxee.OnInitCompletedListener {
    //This is a test commit
    private Appoxee appoxee;
    private AppoxeeOptions options;
    private boolean isInitSpinner = false;

    private Logger devLogger;

    private MainBinding binding;

    private final CompoundButton.OnCheckedChangeListener pushEnabledChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.getId() == R.id.push_enabled) {
                Appoxee.instance().setPushEnabled(isChecked);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        setTitle(getString(R.string.app_name) + " " + VERSION_NAME);
        binding.toolbar.setSubtitle("SDK VERSION: " + com.appoxee.sdk.BuildConfig.VERSION_NAME);

        devLogger = LoggerFactory.getDevLogger();

        appoxee = Appoxee.instance();

        init();
        Appoxee.handleRichPush(this, getIntent());

        Appoxee.instance().requestNotificationsPermission(this, results -> {
            if (results.containsKey(Manifest.permission.POST_NOTIFICATIONS) && results.get(Manifest.permission.POST_NOTIFICATIONS) == PermissionsManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "POST NOTIFICATIONS GRANTED!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Appoxee.handleRichPush(this, intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Appoxee.instance().isReady()) {
            binding.pushEnabled.setOnCheckedChangeListener(null);
            binding.pushEnabled.setChecked(Appoxee.instance().isPushEnabled());
            binding.pushEnabled.setOnCheckedChangeListener(pushEnabledChangeListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private final ResultCallback<String> geofenceCallback = new ResultCallback<>() {
        @Override
        public void onResult(@Nullable String result) {
            devLogger.d(result);
            if (result != null) {
                switch (result) {
                    case GeofenceStatus.GEOFENCE_STARTED_OK:
                        Toast.makeText(MainActivity.this, "Geofence started successfully", Toast.LENGTH_SHORT).show();
                        break;
                    case GeofenceStatus.GEOFENCE_STOPPED_OK:
                        Toast.makeText(MainActivity.this, "Geofence stopped successfully", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    };

    private void init() {
        devLogger.d("init()");

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
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            binding.textView2.setText(token);
            Log.d("token fcm", token);
        });

        binding.btnOpenCustomAttributesSetup.setOnClickListener(v -> {
            Intent intent = new Intent(this, CustomAttributesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        InAppMessageDismissalCallback inAppMessageDismissalCallback = new InAppMessageDismissalCallback();
        inAppMessageDismissalCallback.addOnInAppMessageDismissalCallback(new InAppMessageDismissalCallback.onInAppMessageDismissalCallback() {
            @Override
            public void onInAppMessageDismissalCallback(int templateId, String eventId, boolean isSendStats) {

                Log.v("MainActivity", "onInAppMessageDismissalCallback");
            }

        });

        findViewById(R.id.buttonPushEnabled).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createBuilder("Push enabled", "" + Appoxee.instance().isPushEnabled());
            }
        });

        findViewById(R.id.get_alias).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getAlias = getAlias();
                createBuilder("", getAlias);
            }
        });

        findViewById(R.id.get_deviceId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deviceId = Appoxee.instance().getDeviceInfo().id;
                createBuilder("", deviceId);
            }
        });

        findViewById(R.id.get_device_dmc).setOnClickListener(v -> {
            @SuppressLint("RestrictedApi")
            Map<String, String> map = Appoxee.instance().getDmcDeviceInfo();
            if (map != null) {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    if (Objects.equals("UDIDHashed", entry.getKey()) || Objects.equals("dmcUserId", entry.getKey()))
                        sb.append(entry.getKey()).append(" : ").append(entry.getValue()).append("\n\n");
                }
                createBuilder("Device DMC Data", sb.toString());
            }
        });

        binding.btnSetAlias.setOnClickListener(v -> {
            appoxee.setAlias(binding.etxtSetAlias.getText().toString());
            createBuilder("New alias", "Added alias: " + binding.etxtSetAlias.getText());
            binding.etxtSetAlias.setText("");
        });

        binding.secondActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "New activity opened", Toast.LENGTH_SHORT).show();
            }
        });
        binding.geoFencing.setOnClickListener(v -> startGeo());
        binding.stopGeoFencing.setOnClickListener(v -> stopGeoFencing());

        binding.buttonDeviceInfo.setOnClickListener(v -> {
            Appoxee.instance().getDeviceInfoDMC();
            String s = appoxee.getDeviceInfo().toString();
            createBuilder("", s);
        });

        binding.dmcCallInApp.setOnClickListener(v -> Appoxee.instance().triggerInApp(MainActivity.this, "app_open"));

        binding.inappModalType.setOnClickListener(v -> Appoxee.instance().triggerInApp(MainActivity.this, "app_feedback"));

        binding.inappBannerType.setOnClickListener(v -> Appoxee.instance().triggerInApp(MainActivity.this, "app_discount"));

        binding.inappAppPromo.setOnClickListener(v -> Appoxee.instance().triggerInApp(MainActivity.this, "app_promo"));


        binding.inappInbox.setOnClickListener(v -> fetchInboxMessages());

        binding.inappEvents.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, InAppEventsActivity.class));
        });

        binding.multipleMessages.setOnClickListener(v -> Appoxee.instance().triggerInApp(MainActivity.this, "app_welcome"));

        binding.deleteFcmToken.setOnClickListener(v -> {
            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                            .setTitle("Reset token")
                            .setMessage("Firebase Token reset successfully!")
                            .setPositiveButton("OK", null)
                            .show();
                }
            });
        });

        binding.fcmToken.setOnClickListener(v -> FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("token", token);
            clipboard.setPrimaryClip(clip);
            devLogger.d("FCM TOKEN: ", token);
            createBuilder("Firebase token", token);
        }));

        binding.btnSendPush.setOnClickListener(v -> {
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
            Appoxee.instance().setRemoteMessage(message);
        });

        binding.btnRegisterToken.setOnClickListener(v ->
                FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
                    if (token != null) {
                        Appoxee.instance().setToken(token);
                        createBuilder("FCM Token", token);
                    }
                })
        );

        binding.btnGetTags.setOnClickListener(v -> {
            Set<String> tags = appoxee.getTags();
            StringBuilder s = new StringBuilder("");
            for (String tag : tags) {
                s.append("\n")
                        .append(tag);
            }
            createBuilder("All tags", s.toString());
        });

        binding.btnSetTag.setOnClickListener(v -> {
            String tag = binding.etxtSetTag.getText().toString();
            if (tag.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please, filled field above", Toast.LENGTH_SHORT).show();
            } else {
                appoxee.addTag(tag);
                createBuilder("Set tag", "Setted tag: " + binding.etxtSetTag.getText());
                binding.etxtSetTag.setText("");
            }

        });

        binding.btnRemoveTag.setOnClickListener(v -> {
            Editable data = binding.etxtRemoveTag.getText();
            if (data != null && data.toString().isEmpty()) {
                Toast.makeText(MainActivity.this, "Please, filled field above", Toast.LENGTH_SHORT).show();
            } else {
                RequestStatus status = appoxee.removeTag(binding.etxtRemoveTag.getText().toString());
                createBuilder("Remove tag", "Removed tag: " + binding.etxtRemoveTag.getText());
                binding.etxtRemoveTag.setText("");
            }
        });

        binding.btnRemoveAttribute.setOnClickListener(v -> {
            String s = appoxee.getAttributeStringValue(binding.etxtRemoveAttribute.getText().toString());
            if (s == null || s.equals("")) {
                Toast.makeText(MainActivity.this, "Doesn't exist this attribute", Toast.LENGTH_SHORT).show();
            } else {
                appoxee.removeAttribute(binding.etxtRemoveAttribute.getText().toString());
                createBuilder("Remove attribute", "Removed attribute: " + s);
                binding.etxtRemoveAttribute.setText("");
            }

        });

        binding.getAliasFromServer.setOnClickListener(v -> Appoxee.instance().getAliasFromServer(true, new GetAliasCallback() {
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
        }));

        binding.btnRemoveBadgeNumber.setOnClickListener(v -> {
            removeBadgeNumber(MainActivity.this.getApplicationContext());
            createBuilder("Remove badge", "All badges deleted");
        });

        binding.btnOrientation.setOnClickListener(v -> dialogScreenOrientation());

        binding.btnOpenTestActivity.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ConfigurationMappOptionsActivity.class);
            startActivity(intent);
        });

        binding.btnBackupConfiguration.setOnClickListener(v -> backupConfiguration());

        initSpinnerEvents();

        binding.btnLogoutWithOptout.findViewById(R.id.btn_logout_with_optout).setOnClickListener(view -> Appoxee.instance().logOut(false));

        binding.btnLogout.setOnClickListener(view -> Appoxee.instance().logOut(true));

        binding.btnOpenHighLoadTest.setOnClickListener(v -> {
            Intent intent = new Intent(this, HighLoadActivity.class);
            startActivity(intent);
        });

        Appoxee.instance().addInitListener(this);
    }

    private void fetchInboxMessages() {
        InAppInboxCallback inAppInboxCallback = new InAppInboxCallback();
        inAppInboxCallback.addInAppInboxMessagesReceivedCallback(new InAppInboxCallback.onInAppInboxMessagesReceived() {
            @Override
            public void onInAppInboxMessages(List<APXInboxMessage> richMessages) {
                Log.d("messages", "messages = " + (richMessages.isEmpty() ? 0 : richMessages.get(0).getContent()));
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
        Appoxee.instance().fetchInboxMessages();
    }

    private void backupConfiguration() {
        Prefs.putString(KEY_SDK_KEY, SDK_KEY);
        Prefs.putString(KEY_APP_ID, APP_ID);
        Prefs.putString(KEY_TENANT_ID, TENANT_ID);
        Prefs.putString(KEY_SERVER_INDEX, SERVER_INDEX);

        options.sdkKey = SDK_KEY;
        options.appID = APP_ID;
        options.tenantID = TENANT_ID;
        options.server = AppoxeeOptions.Server.valueOf(SERVER_INDEX);
        options.notificationMode = NotificationMode.BACKGROUND_AND_FOREGROUND;

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

        runOnUiThread(() -> {
            Toast.makeText(this, "OnInitCompleted: " + successful, Toast.LENGTH_SHORT).show();
            if (successful) {
                FirebaseCrashlytics.getInstance().setUserId(Appoxee.instance().getAlias());
                FirebaseCrashlytics.getInstance().setCustomKey("SDK_KEY", SDK_KEY);
                FirebaseCrashlytics.getInstance().setCustomKey("APP_ID", APP_ID);
                FirebaseCrashlytics.getInstance().setCustomKey("TENANT_ID", TENANT_ID);
                FirebaseCrashlytics.getInstance().setCustomKey("SERVER", SERVER_INDEX);
                requestGDPRConsent();
            }
        });
    }

    private void requestGDPRConsent() {
        GdprAgreement agreement = Util.getGdprAgreement(this);
        if (agreement == null || !agreement.isShown()) {
            AtomicBoolean accepted = new AtomicBoolean(false);
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("GDPR Agreement")
                    .setMessage("Do you accept to receive push messages and collect tracking data in order to provide better user experience and reliable offers to You?")
                    .setPositiveButton("Yes", (dialog, position) -> {
                        accepted.set(true);
                    })
                    .setNegativeButton("No", (dialog, position) -> {
                        accepted.set(false);
                    }).setOnDismissListener(dialog -> {
                        Util.setGdprAgreement(this, new GdprAgreement(true, accepted.get()));
                        Appoxee.instance().setPushEnabled(accepted.get());
                        postGdpr(accepted.get());
                    })
                    .show();
        } else {
            postGdpr(Appoxee.instance().isPushEnabled());
        }
    }

    void postGdpr(boolean pushEnabled) {
        runOnUiThread(() -> {
            binding.pushEnabled.setChecked(pushEnabled);
            binding.pushEnabled.setOnCheckedChangeListener(pushEnabledChangeListener);
            Appoxee.instance().triggerInApp(MainActivity.this, "app_open");
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
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);

        Appoxee.instance().requestPermissions(this, permissions, results -> {
            if (!results.containsKey(Manifest.permission.ACCESS_FINE_LOCATION)) return;

            boolean backgroundPermissionGranted = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                backgroundPermissionGranted = this.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
            }
            if ((results.containsKey(Manifest.permission.ACCESS_FINE_LOCATION) && results.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) && backgroundPermissionGranted) {
                Appoxee.instance().startGeoFencing(geofenceCallback);
            } else {
                PermissionHelper.getInstance().openAppSystemSettings(this);
            }
        });
    }

    private void stopGeoFencing() {
        Appoxee.instance().stopGeoFencing(geofenceCallback);
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

    public void removeTag(String tag) {
        appoxee.removeTag(tag);
    }

    private void initSpinnerEvents() {
        binding.spinnerEvents.setPrompt("Choose one option");

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
        binding.spinnerEvents.setAdapter(adapter);

        binding.spinnerEvents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.pushEnabled.setOnCheckedChangeListener(null);
        Appoxee.instance().removeInitListener(this);
    }
}

