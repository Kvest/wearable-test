package com.kvest.wearabletest.ui.acticity;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.widget.Toast;

import com.kvest.wearabletest.R;

/**
 * Created by roman on 12/16/15.
 */
public class NotificationsActivity extends AppCompatActivity {
    // Key for the string that's delivered in the action's intent
    private static final String EXTRA_VOICE_REPLY = "extra_voice_reply";

    public static void start(Context context) {
        Intent intent = new Intent(context, NotificationsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications_activity);

        init();

        Intent intent = getIntent();
        if (intent != null) {
            String response = getVoiceReplyText(intent);
            if (response != null) {
                Toast.makeText(this, "response=" + response, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void init() {
        findViewById(R.id.send_usual_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUsualNotification();
            }
        });
        findViewById(R.id.send_notification_with_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotificationWithAction();
            }
        });
        findViewById(R.id.send_notification_wearable_only).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotificationWearableOnly();
            }
        });
        findViewById(R.id.send_notification_with_big_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotificationWithBigText();
            }
        });
        findViewById(R.id.send_notification_with_vice_input).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotificationWithVoiceInput();
            }
        });
        findViewById(R.id.send_notification_with_pages).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotificationWithPages();
            }
        });
    }

    private void sendNotificationWithPages() {
        int notificationId = 006;

        Intent viewIntent = new Intent(this, MainActivity.class);
        PendingIntent viewPendingIntent = PendingIntent.getActivity(this, 0, viewIntent, 0);

        // Create builder for the main notification
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Page 1")
                        .setContentText("Short message")
                        .setContentIntent(viewPendingIntent);

        // Create a big text style for the second page
        NotificationCompat.BigTextStyle secondPageStyle = new NotificationCompat.BigTextStyle();
        secondPageStyle.setBigContentTitle("Page 2");
        secondPageStyle.bigText("\n\nAny long text for the notification.\n\n BigTextStyle allows to show this long text\n" +
                "\n" +
                "Any long text for the notification.\n" +
                "\n" +
                " BigTextStyle allows to show this long text");

        // Create second page notification
        Notification secondPageNotification =
                new NotificationCompat.Builder(this)
                        .setStyle(secondPageStyle)
                        .build();

        // Extend the notification builder with the second page
        Notification notification = notificationBuilder
                .extend(new NotificationCompat.WearableExtender().addPage(secondPageNotification))
                .build();

        // Issue the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, notification);
    }

    private void sendNotificationWithVoiceInput() {
        int notificationId = 005;

        String replyLabel = getResources().getString(R.string.reply_label);
        String[] replyChoices = getResources().getStringArray(R.array.reply_choices);

        RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_VOICE_REPLY)
                .setLabel(replyLabel)
                .setChoices(replyChoices)
                .build();

        // Create an intent for the reply action
        Intent replyIntent = new Intent(this, NotificationsActivity.class);
        replyIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent replyPendingIntent = PendingIntent.getActivity(this, 0, replyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the reply action and add the remote input
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(android.R.drawable.ic_input_add, "Action label", replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();

        // Build the notification and add the action via WearableExtender
        Notification notification =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Notification with voice input")
                        .setContentText("Notification with voice input test")
                        .extend(new WearableExtender().addAction(action))
                        .build();

        // Issue the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, notification);
    }

    private void sendNotificationWithBigText() {
        int notificationId = 004;

        NotificationCompat.BigTextStyle bigStyle = new NotificationCompat.BigTextStyle();
        bigStyle.bigText("\n\nAny long text for the notification.\n\n BigTextStyle allows to show this long text\n" +
                "\n" +
                "Any long text for the notification.\n" +
                "\n" +
                " BigTextStyle allows to show this long text");

        // Build intent for notification content
        Intent viewIntent = new Intent(this, MainActivity.class);
        PendingIntent viewPendingIntent = PendingIntent.getActivity(this, 0, viewIntent, 0);

        // Create the action for the wearable
        Intent mapIntent = new Intent(Intent.ACTION_VIEW);
        Uri geoUri = Uri.parse("geo:0,0?q=Odessa, Ukraine");
        mapIntent.setData(geoUri);
        PendingIntent mapPendingIntent = PendingIntent.getActivity(this, 0, mapIntent, 0);

        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(android.R.drawable.ic_dialog_map,
                        getString(R.string.map), mapPendingIntent)
                        .build();


        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Wearable-only action")
                        .setContentText("Wearable-only action test")
                        .setAutoCancel(true)
                        .setContentIntent(viewPendingIntent)
                        .extend(new WearableExtender().addAction(action))
                        .setStyle(bigStyle);

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    private void sendNotificationWearableOnly() {
        int notificationId = 003;
        // Build intent for notification content
        Intent viewIntent = new Intent(this, MainActivity.class);
        PendingIntent viewPendingIntent = PendingIntent.getActivity(this, 0, viewIntent, 0);

        // Create the action for the wearable
        Intent mapIntent = new Intent(Intent.ACTION_VIEW);
        Uri geoUri = Uri.parse("geo:0,0?q=Odessa, Ukraine");
        mapIntent.setData(geoUri);
        PendingIntent mapPendingIntent = PendingIntent.getActivity(this, 0, mapIntent, 0);

        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(android.R.drawable.ic_dialog_map,
                        getString(R.string.map), mapPendingIntent)
                        .build();

        WearableExtender extender = new WearableExtender()
                .addAction(action)
                .setHintHideIcon(true)
                .setBackground(BitmapFactory.decodeResource(getResources(), R.drawable.notification_bg));

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Wearable-only action")
                        .setContentText("Wearable-only action test")
                        .setAutoCancel(true)
                        .setContentIntent(viewPendingIntent)
                        .extend(extender);

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    private void sendNotificationWithAction() {
        int notificationId = 002;
        // Build intent for notification content
        Intent viewIntent = new Intent(this, MainActivity.class);
        PendingIntent viewPendingIntent = PendingIntent.getActivity(this, 0, viewIntent, 0);

        Intent mapIntent = new Intent(Intent.ACTION_VIEW);
        Uri geoUri = Uri.parse("geo:0,0?q=Odessa, Ukraine");
        mapIntent.setData(geoUri);
        PendingIntent mapPendingIntent = PendingIntent.getActivity(this, 0, mapIntent, 0);


        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Notification with action")
                        .setContentText("Notification with action test")
                        .setAutoCancel(true)
                        .setContentIntent(viewPendingIntent)
                        .addAction(android.R.drawable.ic_dialog_map, getString(R.string.map), mapPendingIntent);

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    private void sendUsualNotification() {
        int notificationId = 001;
        // Build intent for notification content
        Intent viewIntent = new Intent(this, MainActivity.class);
        PendingIntent viewPendingIntent = PendingIntent.getActivity(this, 0, viewIntent, 0);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Usual notification")
                        .setContentText("Usual notification test")
                        .setAutoCancel(true)
                        .setContentIntent(viewPendingIntent);

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null) {
            String response = getVoiceReplyText(intent);
            if (response != null) {
                Toast.makeText(this, "response=" + response, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getVoiceReplyText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(EXTRA_VOICE_REPLY).toString();
        }
        return null;
    }
}
