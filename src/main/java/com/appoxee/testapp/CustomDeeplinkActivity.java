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

        String protocol = uri.getScheme();
        String server = uri.getAuthority();
        String path = uri.getPath();
        String query = uri.getQuery();
        String link = uri.getQueryParameter("link");

        Log.d("Varun","protocol = " +protocol);
        Log.d("Varun","server = " +server);
        Log.d("Varun","path = " +path);
        Log.d("Varun","query = " +query);
        Log.d("Varun","link = " +link);
        if(uri != null && uri.toString() != null) {
            tv.setText("DEEPLINK ACTIVITY URI  = " + link);
        } else {
            tv.setText("DEEPLINK ACTIVITY URI is Null" );
        }

    }
}
