package com.example.android2project.model;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.icu.text.UnicodeSetIterator;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.android2project.R;
import com.example.android2project.view.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FCM extends FirebaseMessagingService {

    NotificationManager mNotificationManager;
    RemoteViews mRemoteViews;
    Notification mNotification;
    final int NOTIFICATION_ID = 1;

    private final String TAG = "FCM";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        if (!token.isEmpty()) {
            Log.d(TAG, "onNewToken: ");
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();
        if (data != null) {
            // Do something with Token
            if (remoteMessage.getData().size() > 0) {
                Log.d(TAG, "Message data payload: " + remoteMessage.getData());

                /*if (*//*Check if data needs to be processed by long running job*//*true) {
                    // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                    //scheduleJob();
                } else {
                    // Handle message within 10 seconds
                    //handleNow();
                }*/
            }
        }

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    private void createNotification() {
        /**<-------Initializing notification------->**/
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        String channelID = null;
        CharSequence channelName = "MuSeek_Channel";
        channelID = "oron_music_channel_id";
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel(channelID, channelName,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setSound(null, null);

            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelID);
        builder.setPriority(Notification.PRIORITY_MAX).setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_petclan_logo)
                .setOnlyAlertOnce(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        mRemoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);

        /*Intent playIntent = new Intent(this, MusicService.class);
        playIntent.putExtra("action", "play");
        PendingIntent playPendingIntent = PendingIntent.getService(this,
                0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notif_play_btn, playPendingIntent);*/

        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this,
                4, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(activityPendingIntent);

        builder.setCustomContentView(mRemoteViews);

        mNotification = builder.build();
        mNotification.flags |= Notification.FLAG_NO_CLEAR;

        startForeground(NOTIFICATION_ID, mNotification);
    }
}
