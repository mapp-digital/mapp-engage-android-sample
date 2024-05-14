package com.appoxee.testapp.fcm;

import androidx.annotation.NonNull;

import com.appoxee.Appoxee;
import com.appoxee.internal.logger.Logger;
import com.appoxee.internal.logger.LoggerFactory;
import com.appoxee.push.fcm.MappMessagingService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessageService extends MappMessagingService {

    private final Logger devLogger= LoggerFactory.getDevLogger();

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        devLogger.d("Message received: ", remoteMessage);
        if(remoteMessage.getData().containsKey("p")) {
            // handle Mapp push messages
            super.onMessageReceived(remoteMessage);
        }else{
            // handle your own push messages
        }
    }

    @Override
    public void onNewToken(String s) {
        devLogger.d("Token created: ", s);
        super.onNewToken(s);
        // subscribe on your own service with firebase token
    }
}
