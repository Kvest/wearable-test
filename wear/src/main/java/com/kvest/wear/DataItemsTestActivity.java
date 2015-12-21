package com.kvest.wear;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by roman on 12/18/15.
 */
public class DataItemsTestActivity extends Activity {
    private static final String COUNT_PATH = "/count";
    private static final String COUNT_KEY = "count";

    private int count = 0;
    private TextView countView;

    public static void start(Context context) {
        Intent intent = new Intent(context, DataItemsTestActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_items_test_activity);

        init();
    }

    private void init() {
        countView = (TextView)findViewById(R.id.count);
        findViewById(R.id.increment_count).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //incrementCount();
            }
        });

        updateCount(getCurrentCount());
    }

    private int getCurrentCount() {
        //TODO
        //try to load old value
        return 0;
    }

    private void updateCount(int newCount) {
        count = newCount;

        countView.setText(String.format("Count: %d", count));
    }
}
