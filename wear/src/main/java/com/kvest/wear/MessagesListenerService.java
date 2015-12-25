package com.kvest.wear;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by roman on 12/25/15.
 */
public class MessagesListenerService extends WearableListenerService {
    private static final String MESSAGE_PATH = "/message";
    private static final String MESSAGE_CONFIRMATION_PATH = "/message/confirmation";
    private static final String MESSAGE_KEY = "message";
    private static final String DATE_KEY = "data";

    private GoogleApiClient googleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        googleApiClient.disconnect();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        switch (messageEvent.getPath()) {
            case MESSAGE_PATH:
                onMessage(messageEvent);
                break;
        }
    }

    private void onMessage(MessageEvent messageEvent) {
        //send message to subscribers
        Bundle data = Utils.bytes2Bundle(messageEvent.getData());
        if (data != null) {
            String message = data.getString(MESSAGE_KEY);
            long date = data.getLong(DATE_KEY, -1);

            Intent notification = new Intent("com.kvest.wear.action.NEW_MESSAGE");
            notification.putExtra("message", message);
            notification.putExtra("date", date);
            LocalBroadcastManager.getInstance(this).sendBroadcast(notification);
        }

        //confirm receiving message
        Wearable.MessageApi.sendMessage(googleApiClient, messageEvent.getSourceNodeId(), MESSAGE_CONFIRMATION_PATH, messageEvent.getData());
    }
}
