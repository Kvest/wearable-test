package com.kvest.wear;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by roman on 12/18/15.
 */
public class DataItemsTestActivity extends Activity implements DataApi.DataListener,
                                                                GoogleApiClient.ConnectionCallbacks,
                                                                GoogleApiClient.OnConnectionFailedListener {
    private static final String COUNT_PATH = "/count";
    private static final String COUNT_KEY = "count";

    private GoogleApiClient mGoogleApiClient;
    private int count;
    private TextView countView;

    public static void start(Context context) {
        Intent intent = new Intent(context, DataItemsTestActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_items_test_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        init();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private void init() {
        countView = (TextView)findViewById(R.id.count);
        findViewById(R.id.increment_count).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementCount();
            }
        });

        updateCount(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        findViewById(R.id.increment_count).setEnabled(true);

        restoreCount();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d("KVEST_TAG", "onDataChanged(): " + dataEvents);

        for (DataEvent event : dataEvents) {
            String path = event.getDataItem().getUri().getPath();
            if (COUNT_PATH.equals(path)) {
                if (event.getType() == DataEvent.TYPE_CHANGED) {
                    DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                    updateCount(dataMap.getInt(COUNT_KEY));
                } else if (event.getType() == DataEvent.TYPE_DELETED) {
                    updateCount(-1);
                }
            } else {
                Log.d("KVEST_TAG", "Unrecognized path: " + path);
            }
        }
    }

    private void restoreCount() {
        if (mGoogleApiClient.isConnected()) {
            Wearable.DataApi.getDataItems(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<DataItemBuffer>() {
                        @Override
                        public void onResult(DataItemBuffer result) {
                            for (int i = 0; i < result.getCount(); ++i) {
                                if (COUNT_PATH.equals(result.get(i).getUri().getPath())) {
                                    DataMap dataMap = DataMapItem.fromDataItem(result.get(i)).getDataMap();
                                    updateCount(dataMap.getInt(COUNT_KEY));

                                    break;
                                }
                            }

                            result.close();
                        }
                    });
        }
    }

    private void incrementCount() {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(COUNT_PATH);
        putDataMapRequest.getDataMap().putInt(COUNT_KEY, count++);
        PutDataRequest request = putDataMapRequest.asPutDataRequest();

        Log.d("KVEST_TAG", "Generating DataItem: " + request);
        if (!mGoogleApiClient.isConnected()) {
            return;
        }
        Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(DataApi.DataItemResult dataItemResult) {
                        if (!dataItemResult.getStatus().isSuccess()) {
                            Log.e("KVEST_TAG", "ERROR: failed to putDataItem, status code: "
                                    + dataItemResult.getStatus().getStatusCode());
                        }
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    private void updateCount(int newCount) {
        count = newCount;

        countView.setText(String.format("Count: %d", count));
    }
}
