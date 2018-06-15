package com.appoxee.testapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class DeepLinkActivity extends Activity {

    private final String APX_LAUNCH_DEEPLINK_ACTION = "com.appoxee.VIEW_DEEPLINK";

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
            if (APX_LAUNCH_DEEPLINK_ACTION.equals(getIntent().getAction())) {
                uri = getIntent().getData();
            }

            openDeepLink(uri);
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
        String messageId = uri.getQueryParameter("message_id");

        if(uri != null && uri.toString() != null) {
            tv.setText("DEEPLINK ACTIVITY URI  = " + link + "\n MessageId = " + messageId );
        } else {
            tv.setText("DEEPLINK ACTIVITY URI is Null" );
        }

    }
}
