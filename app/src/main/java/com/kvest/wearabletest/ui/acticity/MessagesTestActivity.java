package com.kvest.wearabletest.ui.acticity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.kvest.wearabletest.R;

/**
 * Created by roman on 12/25/15.
 */
public class MessagesTestActivity extends AppCompatActivity {
    private static final String TAG = "DataItemsTestActivity";
    private static final String MESSAGE_PATH = "/message";
    private static final String MESSAGE_CONFIRMATION_PATH = "/message/confirmation";
    private static final String MESSAGE_KEY = "message";
    private static final String DATE_KEY = "data";

    private GoogleApiClient googleApiClient;

    private EditText message;
    private ListView sentMessagesList;

    public static void start(Context context) {
        Intent intent = new Intent(context, MessagesTestActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages_test_activity);

        init();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
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

    private void init() {
        message = (EditText) findViewById(R.id.message);
        sentMessagesList = (ListView) findViewById(R.id.sent_messages);

        message.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    onSendMessage();
                    return true;
                }
                return false;
            }
        });
    }

    private void onSendMessage() {
        if (!TextUtils.isEmpty(message.getText())) {
            sendMessage(message.getText().toString());

            message.setText("");
        }
    }

    private void sendMessage(final String message) {
        if (googleApiClient.isConnected()) {
            Wearable.NodeApi.getConnectedNodes(googleApiClient).setResultCallback(
                    new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                        @Override
                        public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                            Bundle data = new Bundle();
                            data.putString(MESSAGE_KEY, message);
                            data.putLong(DATE_KEY, System.currentTimeMillis());
                            byte[] dataRow = Utils.bundle2Bytes(data);

                            for (Node node : nodes.getNodes()) {
                                Wearable.MessageApi.sendMessage(googleApiClient, node.getId(), MESSAGE_PATH, dataRow);
                            }
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

        googleApiClient.disconnect();
    }
}
