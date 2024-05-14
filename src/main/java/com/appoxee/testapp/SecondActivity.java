package com.appoxee.testapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.appoxee.internal.logger.DevLogger;
import com.appoxee.internal.logger.LoggerFactory;

public class SecondActivity extends AppCompatActivity {

    private final String DEEPLINK_SCHEME = "com.appoxee.test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoggerFactory.getDevLogger().d("SECOND ACTIVITY CREATED!!!");
        setContentView(R.layout.activity_second);


        findViewById(R.id.open_link).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://google.com"));
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getIntent() != null) {
            if (DEEPLINK_SCHEME.equals(getIntent().getAction())) {
                Uri uri = getIntent().getData();
            }
        }
    }

}
