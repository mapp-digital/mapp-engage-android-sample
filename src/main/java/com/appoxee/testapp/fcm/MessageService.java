package com.appoxee.testapp.fcm;

import androidx.annotation.NonNull;

import com.appoxee.Appoxee;
import com.appoxee.internal.logger.Logger;
import com.appoxee.internal.logger.LoggerFactory;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessageService extends FirebaseMessagingService {

    private final Logger devLogger= LoggerFactory.getDevLogger();

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        devLogger.d("Message received: ", remoteMessage);
        super.onMessageReceived(remoteMessage);
        Appoxee.instance().setRemoteMessage(remoteMessage);
    }

    @Override
    public void onNewToken(String s) {
        devLogger.d("Token created: ", s);
        super.onNewToken(s);
        Appoxee.instance().setToken(s);
    }
}
