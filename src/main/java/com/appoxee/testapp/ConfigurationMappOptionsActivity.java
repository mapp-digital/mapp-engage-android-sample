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
    private String server;
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
        server = appoxeeOptions.server.toString();

        chooseServer = findViewById(R.id.server_options);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.choose_server_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseServer.setAdapter(adapter);
        chooseServer.setOnItemSelectedListener(onItemSelectedListener);

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
        String server = this.server;

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

        appoxeeOptions.sdkKey = sdkKey;
        appoxeeOptions.googleProjectId = googleProjectId;
        appoxeeOptions.cepURL = cepUrl;
        appoxeeOptions.appID = appId;
        appoxeeOptions.tenantID = tenantId;
        setServer(true);

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

        appoxeeOptions.sdkKey = sdkKeyConf;
        appoxeeOptions.googleProjectId = googleProjectIdConf;
        appoxeeOptions.cepURL = cepUrlConf;
        appoxeeOptions.appID = appIdConf;
        appoxeeOptions.tenantID = tenantIdConf;

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
        server = appoxeeOptions.server.toString();
        setServer(false);
    }

    private void deleteField() {

        textSetSdkKey.setText("");
        textSetGoogleProjectId.setText("");
        textSetCepUrl.setText("");
        textSetAppId.setText("");
        textSetTenantId.setText("");
        chooseServer.setSelection(0);
        server = appoxeeOptions.server.toString();

    }

    private void setServer(boolean shouldWriteAppoxeeOtionsFlag) {
        if (server.equals("L3")) {
            if (shouldWriteAppoxeeOtionsFlag)
                appoxeeOptions.server = AppoxeeOptions.Server.L3;
            chooseServer.setSelection(1);

        }
        if (server.equals("EMC")){
            if(shouldWriteAppoxeeOtionsFlag)
                appoxeeOptions.server = AppoxeeOptions.Server.EMC;
            chooseServer.setSelection(2);

        }
        if (server.equals("EMC_US")){
            if(shouldWriteAppoxeeOtionsFlag)
                appoxeeOptions.server = AppoxeeOptions.Server.EMC_US;
            chooseServer.setSelection(3);

        }
        if (server.equals("CROC")){
            if(shouldWriteAppoxeeOtionsFlag)
                appoxeeOptions.server = AppoxeeOptions.Server.CROC;
            chooseServer.setSelection(4);

        }
        if (server.equals("TEST")){
            if (shouldWriteAppoxeeOtionsFlag)
                appoxeeOptions.server = AppoxeeOptions.Server.TEST;
            chooseServer.setSelection(5);
        }
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (parent.getItemAtPosition(position).equals("L3")){
                server = "L3";
            }
            if (parent.getItemAtPosition(position).equals("EMC")){
                server = "EMC";
            }
            if (parent.getItemAtPosition(position).equals("EMC_US")){
                server = "EMC_US";
            }
            if (parent.getItemAtPosition(position).equals("CROC")){
                server = "CROC";
            }
            if (parent.getItemAtPosition(position).equals("TEST")){
                server = "TEST";
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

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
                "server: " + appoxeeOptions.server.toString();

        Toast.makeText(ConfigurationMappOptionsActivity.this, str, Toast.LENGTH_LONG).show();

    }
}
