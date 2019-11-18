/*
 * Created by Aleksandra Vujadinovic on 07/11/18 11:36
 * Copyright (c) 2018 MAPP.
 */

package com.appoxee.testapp;

import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.appoxee.Appoxee;
import com.appoxee.AppoxeeOptions;
import com.pixplicity.easyprefs.library.Prefs;

import static com.appoxee.testapp.Constants.*;


public class ConfigurationMappOptionsActivity extends AppCompatActivity {

    private AppoxeeOptions appoxeeOptions;
    private EditText textSetSdkKey;
    private EditText textSetGoogleProjectId;
    private EditText textSetCepUrl;
    private EditText textSetAppId;
    private EditText textSetTenantId;
    private Spinner chooseServer;

    private String sdkKeyConf = BuildConfig.SDK_KEY;
    private String googleProjectIdConf = BuildConfig.GOOGLE_PROJECT_ID;
    private String cepUrlConf = BuildConfig.CEP_URL;
    private String appIdConf = BuildConfig.APP_ID;
    private String tenantIdConf = BuildConfig.TENANT_ID;
    private int serverIndexConf = BuildConfig.SERVER_INDEX;
    private boolean isLocked = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_mapp_options);

        appoxeeOptions = ((AppoxeeTestApp) getApplication()).getAppoxeeOptions();

        textSetSdkKey = findViewById(R.id.etxt_set_sdk_key);
        textSetGoogleProjectId = findViewById(R.id.etxt_set_google_project_id);
        textSetCepUrl = findViewById(R.id.etxt_set_cep_url);
        textSetAppId = findViewById(R.id.etxt_set_app_id);
        textSetTenantId = findViewById(R.id.etxt_set_tenant_id);

        chooseServer = findViewById(R.id.server_options);
        ArrayAdapter<Enum> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, AppoxeeOptions.Server.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseServer.setAdapter(adapter);
        chooseServer.setPrompt("Choose Server Option");

        textSetGoogleProjectId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(isLocked){
                    if (v.isFocused()) {
                        v.setEnabled(false);
                        Toast.makeText(ConfigurationMappOptionsActivity.this, "This field can’t be changed", Toast.LENGTH_LONG).show();
                    } else {
                        v.setEnabled(true);
                    }
                }

            }
        });
        setConfiguration();

        //hidden keyboard
        hideKeyboard();
    }

    private void hideKeyboard(){
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    public void setConfiguration(View view) {
        String sdkKey = textSetSdkKey.getText().toString();
        String googleProjectId = textSetGoogleProjectId.getText().toString();
        String cepUrl = textSetCepUrl.getText().toString();
        String appId = textSetAppId.getText().toString();
        String tenantId = textSetTenantId.getText().toString();
        int serverIndex = chooseServer.getSelectedItemPosition();

        if (sdkKey.equals("")) {
            sdkKey = sdkKeyConf;
        }
        if (googleProjectId.equals("")) {
            googleProjectId = googleProjectIdConf;
        }
        if (cepUrl.equals("")) {
            cepUrl = cepUrlConf;
        }
        if (appId.equals("")) {
            appId = appIdConf;
        }
        if (tenantId.equals("")) {
            tenantId = tenantIdConf;
        }



        Prefs.putString(KEY_SDK_KEY, sdkKey);
        Prefs.putString(KEY_GOOGLE_PROJECT_ID, googleProjectId);
        Prefs.putString(KEY_CEP_URL, cepUrl);
        Prefs.putString(KEY_APP_ID, appId);
        Prefs.putString(KEY_TENANT_ID, tenantId);
        Prefs.putInt(KEY_SERVER_INDEX, serverIndex);

        appoxeeOptions.sdkKey = sdkKey;
        appoxeeOptions.googleProjectId = googleProjectId;
        appoxeeOptions.cepURL = cepUrl;
        appoxeeOptions.appID = appId;
        appoxeeOptions.tenantID = tenantId;
        appoxeeOptions.server = AppoxeeOptions.Server.values()[serverIndex];

        Appoxee.instance().setDeviceRegistrationState(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Appoxee.engage(getApplication(), appoxeeOptions);
            }
        }, 1000);

        showMessage();
        deleteField();
    }

    public void refreshConfiguration(View view) {

        Prefs.putString(KEY_SDK_KEY, sdkKeyConf);
        Prefs.putString(KEY_GOOGLE_PROJECT_ID, googleProjectIdConf);
        Prefs.putString(KEY_CEP_URL, cepUrlConf);
        Prefs.putString(KEY_APP_ID, appIdConf);
        Prefs.putString(KEY_TENANT_ID, tenantIdConf);
        Prefs.putInt(KEY_SERVER_INDEX, serverIndexConf);

        appoxeeOptions.sdkKey = sdkKeyConf;
        appoxeeOptions.googleProjectId = googleProjectIdConf;
        appoxeeOptions.cepURL = cepUrlConf;
        appoxeeOptions.appID = appIdConf;
        appoxeeOptions.tenantID = tenantIdConf;
        appoxeeOptions.server = AppoxeeOptions.Server.values()[serverIndexConf];
        showMessage();

    }

    public void deleteAllFields(View view) {
        deleteField();
    }


    public void getConfiguration(View view) {
        setConfiguration();
    }

    private void setConfiguration() {
        textSetSdkKey.setText(Prefs.getString(KEY_SDK_KEY, sdkKeyConf));
        textSetGoogleProjectId.setText(Prefs.getString(KEY_GOOGLE_PROJECT_ID, googleProjectIdConf));
        textSetCepUrl.setText(Prefs.getString(KEY_CEP_URL, cepUrlConf));
        textSetAppId.setText(Prefs.getString(KEY_APP_ID, appIdConf));
        textSetTenantId.setText(Prefs.getString(KEY_TENANT_ID, tenantIdConf));
        chooseServer.setSelection(Prefs.getInt(KEY_SERVER_INDEX, serverIndexConf));
    }

    private void deleteField() {

        textSetSdkKey.setText("");
        textSetGoogleProjectId.setText("");
        textSetCepUrl.setText("");
        textSetAppId.setText("");
        textSetTenantId.setText("");
    }

    private void showMessage() {

        String str = "sdkKey: " + appoxeeOptions.sdkKey +
                "\n" +
                "googleProjectId: " + appoxeeOptions.googleProjectId +
                "\n" +
                "cepURL: " + appoxeeOptions.cepURL +
                "\n" +
                "appID: " + appoxeeOptions.appID +
                "\n" +
                "tenantID: " + appoxeeOptions.tenantID +
                "\n" +
                "serverIndexConf: " + appoxeeOptions.server.toString();

        Toast.makeText(ConfigurationMappOptionsActivity.this, str, Toast.LENGTH_LONG).show();

    }
}
