package com.appoxee.testapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.appoxee.Appoxee;
import com.appoxee.DeviceInfo;
import com.google.gson.Gson;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findViewById(R.id.device_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceInfo info = Appoxee.instance().getDeviceInfo();
                Log.d("APX", "info: (click)" + new Gson().toJson(info));
                Appoxee.instance().setAlias("sdk4.alias@test.appoxee.com");
                Appoxee.instance().setAttribute("custom1", "value1");
            }
        });
    }


}
