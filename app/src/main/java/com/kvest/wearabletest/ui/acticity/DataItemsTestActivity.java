package com.kvest.wearabletest.ui.acticity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
import com.kvest.wearabletest.R;

/**
 * Created by roman on 12/18/15.
 */
public class DataItemsTestActivity extends AppCompatActivity implements DataApi.DataListener {
    private static final String TAG = "DataItemsTestActivity";
    private static final String COUNT_PATH = "/count";
    private static final String COUNT_KEY = "count";

    private int count;
    private TextView countView;
    private GoogleApiClient googleApiClient;

    public static void start(Context context) {
        Intent intent = new Intent(context, DataItemsTestActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_items_test_activity);

        init();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                        Wearable.DataApi.addListener(googleApiClient, DataItemsTestActivity.this);
                        findViewById(R.id.increment_count).setEnabled(true);
                        findViewById(R.id.delete_count).setEnabled(true);

                        restoreCount();
                    }
                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                        // Request access only to the Wearable API
                .addApi(Wearable.API)
                .build();
    }

    private void restoreCount() {
        if (googleApiClient.isConnected()) {
            Wearable.DataApi.getDataItems(googleApiClient)
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

    private void init() {
        countView = (TextView)findViewById(R.id.count);
        findViewById(R.id.increment_count).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementCount();
            }
        });
        findViewById(R.id.delete_count).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCount();
            }
        });

        updateCount(0);
    }

    private void deleteCount() {
        if (googleApiClient.isConnected()) {
            Wearable.DataApi.getDataItems(googleApiClient)
                    .setResultCallback(new ResultCallback<DataItemBuffer>() {
                        @Override
                        public void onResult(DataItemBuffer result) {
                            for (int i = 0; i < result.getCount(); ++i) {
                                if (COUNT_PATH.equals(result.get(i).getUri().getPath())) {
                                    Wearable.DataApi.deleteDataItems(googleApiClient, result.get(i).getUri());
                                }
                            }

                            result.close();
                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        Wearable.DataApi.removeListener(googleApiClient, DataItemsTestActivity.this);
        googleApiClient.disconnect();
    }

    private void incrementCount() {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(COUNT_PATH);
        putDataMapRequest.setUrgent();
        putDataMapRequest.getDataMap().putInt(COUNT_KEY, count++);
        PutDataRequest request = putDataMapRequest.asPutDataRequest();

        Log.d(TAG, "Generating DataItem: " + request);
        if (!googleApiClient.isConnected()) {
            return;
        }
        Wearable.DataApi.putDataItem(googleApiClient, request)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(DataApi.DataItemResult dataItemResult) {
                        if (!dataItemResult.getStatus().isSuccess()) {
                            Log.e(TAG, "ERROR: failed to putDataItem, status code: "
                                    + dataItemResult.getStatus().getStatusCode());
                        }
                    }
                });
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

    private void updateCount(int newCount) {
        count = newCount;

        countView.setText(String.format("Count: %d", count));
    }
}
