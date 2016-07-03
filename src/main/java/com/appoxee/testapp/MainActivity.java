package com.appoxee.testapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.appoxee.Appoxee;
import com.appoxee.DeviceInfo;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Set;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
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

        Switch pushEnabledSwitch = (Switch)findViewById(R.id.push_enabled);
        //todo check push enabled in Appoxee init callback
//        pushEnabledSwitch.setChecked(Appoxee.instance().isPushEnabled());
        pushEnabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Appoxee.instance().setPushEnabled(isChecked);
            }
        });
    }


}
