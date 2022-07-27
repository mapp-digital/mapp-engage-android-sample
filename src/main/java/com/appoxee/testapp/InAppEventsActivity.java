package com.appoxee.testapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.appoxee.Appoxee;
import com.appoxee.internal.inapp.InAppTracker;
import com.appoxee.internal.inapp.model.InAppMessage;
import com.appoxee.internal.inapp.model.InAppStatistics;
import com.appoxee.internal.inapp.nativemodel.Message;
import com.appoxee.internal.ui.UiUtils;

import java.util.List;

public class InAppEventsActivity extends AppCompatActivity {

    private int templateId;
    private String originalEventId;
    private Button btnContentLoadError, btnContentLoadTimeout, btnTimeoutExpiration, btnWebViewLoadError,
            btnSessionInterruption, btnOtherMessageDisplaying;
    private TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app_events);

        btnContentLoadError = findViewById(R.id.btn_content_load_error);
        btnContentLoadTimeout = findViewById(R.id.btn_content_load_timeout);
        btnTimeoutExpiration = findViewById(R.id.btn_timeout_expiration);
        btnWebViewLoadError=findViewById(R.id.btn_webview_load_error);
        btnSessionInterruption=findViewById(R.id.btn_session_interrupted_error);
        btnOtherMessageDisplaying=findViewById(R.id.btn_other_message_displaying);
        tvInfo=findViewById(R.id.tvInfo);

        List<Message> nativeMessages = InAppTracker.getInstance().getNativeMessages();
        List<InAppMessage> inAppMessages = InAppTracker.getInstance().getWebMessages();

        if (nativeMessages != null && !nativeMessages.isEmpty()) {
            templateId = nativeMessages.get(0).getTemplateId();
            originalEventId = nativeMessages.get(0).getEventId();
            tvInfo.setVisibility(View.GONE);
        } else if (inAppMessages != null && !inAppMessages.isEmpty()) {
            templateId = inAppMessages.get(0).getTemplate_id();
            originalEventId = inAppMessages.get(0).getEvent_id();
            tvInfo.setVisibility(View.GONE);
        } else {
            btnContentLoadError.setEnabled(false);
            btnContentLoadTimeout.setEnabled(false);
            btnTimeoutExpiration.setEnabled(false);
            btnWebViewLoadError.setEnabled(false);
            btnSessionInterruption.setEnabled(false);
            btnOtherMessageDisplaying.setEnabled(false);
            tvInfo.setVisibility(View.VISIBLE);
        }

        btnContentLoadError.setOnClickListener(v -> {
            Appoxee.instance().triggerStatistcs(this,
                    UiUtils.getInAppStatisticsRequestObject(templateId,
                            originalEventId,
                            InAppStatistics.INAPP_IA_MESSAGE_NOT_DISPLAYED_KEY,
                            null,
                            InAppStatistics.REASON_CONTENT_LOAD_ERROR,
                            null)
            );
        });

        btnContentLoadTimeout.setOnClickListener(v -> {
            Appoxee.instance().triggerStatistcs(this,
                    UiUtils.getInAppStatisticsRequestObject(templateId,
                            originalEventId,
                            InAppStatistics.INAPP_IA_MESSAGE_NOT_DISPLAYED_KEY,
                            null,
                            InAppStatistics.REASON_CONTENT_LOAD_TIMEOUT,
                            null)
            );
        });

        btnTimeoutExpiration.setOnClickListener(v -> {
            Appoxee.instance().triggerStatistcs(this,
                    UiUtils.getInAppStatisticsRequestObject(templateId,
                            originalEventId,
                            InAppStatistics.INAPP_IA_MESSAGE_NOT_DISPLAYED_KEY,
                            null,
                            InAppStatistics.REASON_TIMEOUT_EXPIRATION,
                            null)
            );
        });

        btnWebViewLoadError.setOnClickListener(v -> {
            Appoxee.instance().triggerStatistcs(this,
                    UiUtils.getInAppStatisticsRequestObject(templateId,
                            originalEventId,
                            InAppStatistics.INAPP_IA_MESSAGE_NOT_DISPLAYED_KEY,
                            null,
                            InAppStatistics.REASON_WEBVIEW_LOAD_ERROR,
                            null)
            );
        });

        btnSessionInterruption.setOnClickListener(v -> {
            Appoxee.instance().triggerStatistcs(this,
                    UiUtils.getInAppStatisticsRequestObject(templateId,
                            originalEventId,
                            InAppStatistics.INAPP_IA_MESSAGE_NOT_DISPLAYED_KEY,
                            null,
                            InAppStatistics.REASON_SESSION_INTERRUPTED_ERROR,
                            null)
            );
        });

        btnOtherMessageDisplaying.setOnClickListener(v -> {
            Appoxee.instance().triggerStatistcs(this,
                    UiUtils.getInAppStatisticsRequestObject(templateId,
                            originalEventId,
                            InAppStatistics.INAPP_IA_MESSAGE_NOT_DISPLAYED_KEY,
                            null,
                            InAppStatistics.REASON_OTHER_MESSAGE_DISPLAYING,
                            null)
            );
        });
    }
}