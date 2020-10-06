package com.example.android2project.model;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
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
            if (dataMap.containsKey("type")) {
                switch (Objects.requireNonNull(dataMap.get("type"))) {
                    case CHAT_NOTIFICATION:
                        final String chatId = ConversationFragment.sConversationId;
                        if (!(chatId != null && chatId.equals(dataMap.get("chat_id")))) {
                            createChatNotification(dataMap);
                        }
                        break;
                    case COMMENT_NOTIFICATION:
                        createCommentNotification(dataMap);
                        break;
                    case LIKE_NOTIFICATION:
                        createLikeNotification(dataMap);
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
            notificationChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    null);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelID);
        builder.setPriority(Notification.PRIORITY_MAX).setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_petclan_logo)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        mRemoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);

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
                0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(activityPendingIntent);

        builder.setCustomContentView(mRemoteViews);

        mNotification = builder.build();

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

        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }

    private void createLikeNotification(final Map<String, String> data) {
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
            notificationChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    null);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelID);
        builder.setPriority(Notification.PRIORITY_MAX).setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_petclan_logo)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        mRemoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);

        builder.setCustomContentView(mRemoteViews);

        mNotification = builder.build();

        mRemoteViews.setTextViewText(R.id.user_name_tv, data.get("name"));
        mRemoteViews.setTextViewText(R.id.details_tv, "Liked Your Post");
        mRemoteViews.setImageViewResource(R.id.notif_user_image, R.drawable.ic_like);

        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }

    private void createCommentNotification(final Map<String, String> data) {
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
            notificationChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    null);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelID);
        builder.setPriority(Notification.PRIORITY_MAX).setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_petclan_logo)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        mRemoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);

        Intent activityIntent = new Intent(this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activityIntent.setAction("open_comments");

        final String postId = data.get("post_id");
        final String email = data.get("email");
        final String userName = data.get("name");
        String firstName = "";
        String lastName = "";
        if (userName != null) {
            firstName = userName.split(" ")[0];
            lastName = userName.split(" ")[1];
        }
        final String photoPath = data.get("photo");
        final String postContent = data.get("post_content");
        Post post = new Post(email, userName, photoPath, postContent);
        post.setPostId(postId);

        activityIntent.putExtra("post", post);
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this,
                1, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(activityPendingIntent);

        builder.setCustomContentView(mRemoteViews);

        mNotification = builder.build();

        mRemoteViews.setTextViewText(R.id.user_name_tv, data.get("name"));
        mRemoteViews.setTextViewText(R.id.details_tv, data.get("message"));
        mRemoteViews.setImageViewResource(R.id.notif_user_image, R.drawable.ic_comment);

        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }
}
