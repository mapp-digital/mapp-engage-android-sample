package com.appoxee.testapp;

import static com.appoxee.testapp.Constants.KEY_APP_ID;
import static com.appoxee.testapp.Constants.KEY_SDK_KEY;
import static com.appoxee.testapp.Constants.KEY_SERVER_INDEX;
import static com.appoxee.testapp.Constants.KEY_TENANT_ID;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.appoxee.Appoxee;
import com.appoxee.AppoxeeOptions;
import com.appoxee.internal.util.SharedPreferenceUtil;
import com.pixplicity.easyprefs.library.Prefs;


public class ConfigurationMappOptionsActivity extends AppCompatActivity {

    private AppoxeeOptions appoxeeOptions;
    private EditText textSetSdkKey;
    private EditText textSetAppId;
    private EditText textSetTenantId;
    private Spinner chooseServer;

    private String sdkKeyConf = BuildConfig.SDK_KEY;
    private String appIdConf = BuildConfig.APP_ID;
    private String tenantIdConf = BuildConfig.TENANT_ID;
    private String serverIndexConf = BuildConfig.SERVER_INDEX;
    private boolean isLocked = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_mapp_options);

        appoxeeOptions = ((AppoxeeTestApp) getApplication()).getAppoxeeOptions();

        textSetSdkKey = findViewById(R.id.etxt_set_sdk_key);
        textSetAppId = findViewById(R.id.etxt_set_app_id);
        textSetTenantId = findViewById(R.id.etxt_set_tenant_id);

        chooseServer = findViewById(R.id.server_options);

        ArrayAdapter<Enum> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, AppoxeeOptions.Server.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseServer.setAdapter(adapter);
        chooseServer.setPrompt("Choose Server Option");

        setConfiguration();

        findViewById(R.id.btn_configure_appoxee_options).setOnClickListener(v -> {
            setNewConfiguration();
        });

    }


    public void setNewConfiguration() {
        Appoxee.instance().resetRegistration();

        // save new settings
        String sdkKey = textSetSdkKey.getText().toString();
        String appId = textSetAppId.getText().toString();
        String tenantId = textSetTenantId.getText().toString();
        int serverIndex = chooseServer.getSelectedItemPosition();

        if (sdkKey.isEmpty()) {
            sdkKey = sdkKeyConf;
        }

        if (appId.isEmpty()) {
            appId = appIdConf;
        }
        if (tenantId.isEmpty()) {
            tenantId = tenantIdConf;
        }

//        appoxeeOptions.sdkKey = sdkKey;
//        appoxeeOptions.appID = appId;
//        appoxeeOptions.tenantID = tenantId;
//        appoxeeOptions.server = AppoxeeOptions.Server.values()[serverIndex];

        Prefs.putString(KEY_SDK_KEY, sdkKey);
        Prefs.putString(KEY_APP_ID, appId);
        Prefs.putString(KEY_TENANT_ID, tenantId);
        AppoxeeOptions.Server serverName = AppoxeeOptions.Server.values()[serverIndex];
        Prefs.putString(KEY_SERVER_INDEX, serverName.name());

        new AlertDialog.Builder(this)
                .setTitle("Info")
                .setMessage("Application will restart for the changes to take effect.")
                .setPositiveButton("OK", (dialog, which) -> {
                    showMessage();

                    deleteField();

                    //SharedPreferenceUtil.getInstance().setEngageOptions(appoxeeOptions);

                    int pid = android.os.Process.myPid();
                    android.os.Process.killProcess(pid);
                    //Util.restartApp(this);
                })
                .show();
    }

    public void refreshConfiguration(View view) {

        Prefs.putString(KEY_SDK_KEY, sdkKeyConf);
        Prefs.putString(KEY_APP_ID, appIdConf);
        Prefs.putString(KEY_TENANT_ID, tenantIdConf);
        Prefs.putString(KEY_SERVER_INDEX, serverIndexConf);

        appoxeeOptions.sdkKey = sdkKeyConf;
        appoxeeOptions.appID = appIdConf;
        appoxeeOptions.tenantID = tenantIdConf;
        appoxeeOptions.server = AppoxeeOptions.Server.valueOf(serverIndexConf);

        showMessage();

        setConfiguration();

    }

    public void deleteAllFields(View view) {
        deleteField();
    }


    public void getConfiguration(View view) {
        setConfiguration();
    }

    private void setConfiguration() {
        textSetSdkKey.setText(Prefs.getString(KEY_SDK_KEY, sdkKeyConf));
        textSetAppId.setText(Prefs.getString(KEY_APP_ID, appIdConf));
        textSetTenantId.setText(Prefs.getString(KEY_TENANT_ID, tenantIdConf));

        int spinner_index = AppoxeeOptions.Server.valueOf(Prefs.getString(KEY_SERVER_INDEX, serverIndexConf)).ordinal();
        chooseServer.setSelection(spinner_index);
    }

    private void deleteField() {
        textSetSdkKey.setText("");
        textSetAppId.setText("");
        textSetTenantId.setText("");
    }

    private void showMessage() {

        String str = "sdkKey: " + appoxeeOptions.sdkKey +
                "\n" +
                "appID: " + appoxeeOptions.appID +
                "\n" +
                "tenantID: " + appoxeeOptions.tenantID +
                "\n" +
                "serverIndexConf: " + appoxeeOptions.server.toString();

        Toast.makeText(ConfigurationMappOptionsActivity.this, str, Toast.LENGTH_LONG).show();

    }
}
