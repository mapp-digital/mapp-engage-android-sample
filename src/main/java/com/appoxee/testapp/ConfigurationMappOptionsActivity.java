package com.appoxee.testapp;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.appoxee.AppoxeeOptions;
import com.pixplicity.easyprefs.library.Prefs;

/**
 * Created by Aleksandra Vujadinovic on 11/05/18.
 */
public class ConfigurationMappOptionsActivity extends AppCompatActivity {

    private Context appContext;
    private AppoxeeOptions appoxeeOptions;
    private EditText etxtSetSdkKey;
    private EditText etxtSetGoogleProjectId;
    private EditText etxtSetCepUrl;
    private EditText etxtSetAppId;
    private EditText etxtSetTenantId;
    private Button btnConfigureAppoxeeOptions;
    private ImageButton btnRefresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_mapp_options);
        init();

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    private void init() {
        appoxeeOptions = ((AppoxeeTestApp) getApplication()).getAppoxeeOptions();

        etxtSetSdkKey = findViewById(R.id.etxt_set_sdk_key);
        etxtSetGoogleProjectId = findViewById(R.id.etxt_set_google_project_id);
        etxtSetCepUrl = findViewById(R.id.etxt_set_cep_url);
        etxtSetAppId = findViewById(R.id.etxt_set_app_id);
        etxtSetTenantId = findViewById(R.id.etxt_set_tenant_id);
        btnConfigureAppoxeeOptions = findViewById(R.id.btn_configure_appoxee_options);
        btnRefresh = findViewById(R.id.btn_refresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });


    }

    private void reset() {
        String sdkKey = etxtSetSdkKey.getText().toString();
        String googleProjectId = etxtSetGoogleProjectId.getText().toString();
        String cepUrl = etxtSetCepUrl.getText().toString();
        String appId = etxtSetAppId.getText().toString();
        String tenantId = etxtSetTenantId.getText().toString();


        if (sdkKey.equals("") || googleProjectId.equals("") || cepUrl.equals("") || appId.equals("") || tenantId.equals("")) {
            Toast.makeText(ConfigurationMappOptionsActivity.this, "Please fill all field", Toast.LENGTH_SHORT).show();
            return;
        }

        Prefs.putString("SDK_KEY", sdkKey);
        Prefs.putString("GOOGLE_PROJECT_ID", googleProjectId);
        Prefs.putString("CEP_URL", cepUrl);
        Prefs.putString("APP_ID", appId);
        Prefs.putString("TENANT_ID", tenantId);

        appoxeeOptions.sdkKey = sdkKey;
        appoxeeOptions.googleProjectId = googleProjectId;
        appoxeeOptions.cepURL = cepUrl;
        appoxeeOptions.appID = appId;
        appoxeeOptions.tenantID = tenantId;

        etxtSetSdkKey.setText("");
        etxtSetGoogleProjectId.setText("");
        etxtSetCepUrl.setText("");
        etxtSetAppId.setText("");
        etxtSetTenantId.setText("");
    }

    public void getConfiguration(View view) {

        etxtSetSdkKey.setText(Prefs.getString("SDK_KEY", ""));
        etxtSetGoogleProjectId.setText(Prefs.getString("GOOGLE_PROJECT_ID", ""));
        etxtSetCepUrl.setText(Prefs.getString("CEP_URL", ""));
        etxtSetAppId.setText(Prefs.getString("APP_ID", ""));
        etxtSetTenantId.setText(Prefs.getString("TENANT_ID", ""));

    }

    public void deleteAllFields(View view) {

        etxtSetSdkKey.setText("");
        etxtSetGoogleProjectId.setText("");
        etxtSetCepUrl.setText("");
        etxtSetAppId.setText("");
        etxtSetTenantId.setText("");

    }

    public void refreshConfiguration(View view) {

        Prefs.putString("SDK_KEY", getResources().getString(R.string.hint_sdk_key));
        Prefs.putString("GOOGLE_PROJECT_ID", getResources().getString(R.string.hint_google_project_id));
        Prefs.putString("CEP_URL", getResources().getString(R.string.hint_cep_url));
        Prefs.putString("APP_ID", getResources().getString(R.string.hint_app_id));
        Prefs.putString("TENANT_ID", getResources().getString(R.string.hint_tenant_id));

        appoxeeOptions.sdkKey = getResources().getString(R.string.hint_sdk_key);
        appoxeeOptions.googleProjectId = getResources().getString(R.string.hint_google_project_id);
        appoxeeOptions.cepURL = getResources().getString(R.string.hint_cep_url);
        appoxeeOptions.appID = getResources().getString(R.string.hint_app_id);
        appoxeeOptions.tenantID = getResources().getString(R.string.hint_tenant_id);

        String str = "sdkKey: " + appoxeeOptions.sdkKey +
                "\n" +
                "googleProjectId: " + appoxeeOptions.googleProjectId +
                "\n" +
                "cepURL: " + appoxeeOptions.cepURL +
                "\n" +
                "appID: " + appoxeeOptions.appID +
                "\n" +
                "tenantID: " + appoxeeOptions.tenantID;

        Toast.makeText(this, str, Toast.LENGTH_LONG).show();

    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
