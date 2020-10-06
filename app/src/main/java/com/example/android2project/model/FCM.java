package com.example.android2project.model;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.NotificationTarget;
import com.example.android2project.R;
import com.example.android2project.view.MainActivity;
import com.example.android2project.view.fragments.ConversationFragment;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Objects;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

public class FCM extends FirebaseMessagingService {

    private NotificationManager mNotificationManager;
    private RemoteViews mRemoteViews;
    private Notification mNotification;

    private final String CHAT_NOTIFICATION = "chat";
    private final String COMMENT_NOTIFICATION = "comment";
    private final String LIKE_NOTIFICATION = "like";

    private final int NOTIFICATION_ID = 1;

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

        final Map<String, String> dataMap = remoteMessage.getData();
        // Do something with Token
        if (dataMap.size() > 0) {
            Log.d(TAG, "Message data payload: " + dataMap);

            if (dataMap.containsKey("type")) {
                switch (Objects.requireNonNull(dataMap.get("type"))) {
                    case CHAT_NOTIFICATION:
                        final String chatId = ConversationFragment.sConversationId;
                        if (chatId != null && !chatId.equals(dataMap.get("chat_id"))) {
                            createChatNotification(dataMap);
                        }
                        break;
                }
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

    private void createChatNotification(final Map<String, String> data) {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        String channelID = null;
        CharSequence channelName = "PetClan_Channel";
        channelID = "pet_clan_channel_id";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelID, channelName,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationChannel.setSound(null, null);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelID);
        builder.setPriority(Notification.PRIORITY_MAX).setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_petclan_logo)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        mRemoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);
        mRemoteViews.setTextViewText(R.id.user_name_tv, data.get("name"));
        mRemoteViews.setTextViewText(R.id.details_tv, data.get("message"));
        NotificationTarget notificationTarget = new NotificationTarget(
                this,
                R.id.notif_user_image,
                mRemoteViews,
                mNotification,
                NOTIFICATION_ID
        );

        RequestOptions options = new RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.ic_default_user_pic)
                .error(R.drawable.ic_default_user_pic);

        Glide.with(this)
                .asBitmap()
                .load(data.get("photo") == null ? R.drawable.ic_default_user_pic : data.get("photo"))
                .apply(options)
                .into(notificationTarget);

        /*Intent playIntent = new Intent(this, MusicService.class);
        playIntent.putExtra("action", "play");
        PendingIntent playPendingIntent = PendingIntent.getService(this,
                0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notif_play_btn, playPendingIntent);*/

        Intent activityIntent = new Intent(this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activityIntent.setAction("open_chat");

        final String email = data.get("email");
        final String userName = data.get("name");
        String firstName = "";
        String lastName = "";
        if (userName != null) {
            firstName = userName.split(" ")[0];
            lastName = userName.split(" ")[1];
        }
        final String photoPath = data.get("photo");
        final String token = data.get("token");
        User recipient = new User(email, firstName, lastName, photoPath, token);

        activityIntent.putExtra("recipient", recipient);
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this,
                4, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(activityPendingIntent);

        builder.setCustomContentView(mRemoteViews);

        mNotification = builder.build();

        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }

    public boolean foregrounded() {
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE);
    }
}
