package com.kvest.wearabletest.ui.acticity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.kvest.wearabletest.R;

/**
 * Created by roman on 12/16/15.
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        init();
    }

    private void init() {
        findViewById(R.id.notifications_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotificationsActivity();
            }
        });
        findViewById(R.id.data_items_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataItemsTestActivity.start(MainActivity.this);
            }
        });
        findViewById(R.id.messages_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessagesTestActivity.start(MainActivity.this);
            }
        });
    }

    private void showNotificationsActivity() {
        NotificationsActivity.start(this);
    }
}
