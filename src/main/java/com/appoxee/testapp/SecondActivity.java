package com.appoxee.testapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class SecondActivity extends Activity {

    private final String DEEPLINK_SCHEME = "com.appoxee.test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


        findViewById(R.id.open_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://google.com"));
                startActivity(intent);
            }
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
