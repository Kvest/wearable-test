package com.kvest.wear;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    private Button start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                start = (Button) stub.findViewById(R.id.start);
                start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UniversalLayoutActivity.start(MainActivity.this);
                    }
                });
                findViewById(R.id.data_items_test).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DataItemsTestActivity.start(MainActivity.this);
                    }
                });
            }
        });

        //print out app info
        Log.d("WEAR_TAG", "BuildConfig.APPLICATION_ID=" + BuildConfig.APPLICATION_ID);
        Log.d("WEAR_TAG", "VERSION.RELEASE=" + Build.VERSION.RELEASE);
        Log.d("WEAR_TAG", "VERSION.SDK_INT=" + Build.VERSION.SDK_INT);
    }
}
