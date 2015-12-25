package com.kvest.wear;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.WearableListView;

/**
 * Created by roman on 12/25/15.
 */
public class MessagesTestActivity extends Activity {

    private WearableListView messagesListView;
    private MessagesAdapter messagesAdapter;

    private MessageReceiver messageReceiver = new MessageReceiver();

    public static void start(Context context) {
        Intent intent = new Intent(context, MessagesTestActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages_test_activity);

        init();
    }

    private void init() {
        messagesListView = (WearableListView)findViewById(R.id.messages);
        messagesAdapter = new MessagesAdapter(this);
        messagesListView.setAdapter(messagesAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter("com.kvest.wear.action.NEW_MESSAGE"));
    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
    }

    private void onNewMessage(String message, long date) {
        messagesAdapter.addMessage(message, date);
    }

    private class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            long date = intent.getLongExtra("date", -1);

            onNewMessage(message, date);
        }
    }
}
