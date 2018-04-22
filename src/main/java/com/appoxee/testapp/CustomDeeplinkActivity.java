package com.appoxee.testapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Varun on 4/7/2018.
 */

public class CustomDeeplinkActivity extends Activity {

    private final String APX_LAUNCH_CUSTOM_ACTION = "com.appoxee.VIEW_CUSTOM_LINKS";
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        tv = (TextView) findViewById(R.id.textView);

        findViewById(R.id.open_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://google.com"));
                startActivity(intent);
            }
        });


        Uri uri = null;
        if (getIntent() != null) {
            if (APX_LAUNCH_CUSTOM_ACTION.equals(getIntent().getAction())) {
                uri = getIntent().getData();
                if(uri != null) {
                    openDeepLink(uri);
                }
            }

        }




    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void openDeepLink(Uri uri) {

        String deeplinkValue = uri.getQueryParameter("link");
        String mesageId = uri.getQueryParameter("message_id");

        if(uri != null && uri.toString() != null) {
            tv.setText("DEEPLINK ACTIVITY URI  = " + deeplinkValue + "\nMessageId = " + mesageId );
        } else {
            tv.setText("DEEPLINK ACTIVITY URI is Null" );
        }

    }
}
