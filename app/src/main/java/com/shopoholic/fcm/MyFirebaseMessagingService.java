package com.shopoholic.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.shopoholic.R;
import com.shopoholic.activities.HomeActivity;
import com.shopoholic.firebasechat.models.NotificationBean;
import com.shopoholic.firebasechat.utils.FirebaseConstants;
import com.shopoholic.utils.AppSharedPreference;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebasePushService";

    @Override
    public void onNewToken(String refreshedToken) {
        Log.e("NEW_TOKEN", refreshedToken);
        //Displaying token on logcat
        AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.DEVICE_TOKEN, refreshedToken);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.i(TAG, "====================================FCM=====================================");
        Log.i(TAG, "==========================Push Notification Received========================");
        Log.i(TAG, "====================================FCM=====================================");

        showNotification(remoteMessage.getData());

    }

    /**
     * method to show not
     *
     * @param data
     */
    private void showNotification(Map<String, String> data) {
        List<String> list = new ArrayList<>();
        list.addAll(data.values());
        String jsonStr = list.get(0);
        Log.d(TAG, jsonStr);
        if (AppUtils.getInstance().isUserLogin(this)) {
            handelNotification(jsonStr);
        }

    }

    /***
     * method to handel the notification json
     * @param jsonStr
     */
    private void handelNotification(String jsonStr) {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            String types = jsonObject.optString(Constants.IntentConstant.TYPE);
            int type = Integer.parseInt(types == null || types.equals("") ? "0" : types);
            if (type == 10) {
                NotificationBean notification = new Gson().fromJson(jsonStr, NotificationBean.class);
                if (notification != null && notification.getRoomId() != null
                        && !AppSharedPreference.getInstance().getString(this, AppSharedPreference.PREF_KEY.CURRENT_CHAT_ROOM).equals(notification.getRoomId())) {
                    showChatNotificationView(notification.getMessageText(), notification, type);
                }
            }else if (type != 0) {
                com.shopoholic.models.NotificationBean notification = new Gson().fromJson(jsonStr, com.shopoholic.models.NotificationBean.class);
                showNotificationView(notification.getMessage(), notification, type);
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.IntentConstant.NOTIFICATION)
                        .putExtra(Constants.IntentConstant.NOTIFICATION, notification));
                int badge = jsonObject.optInt(Constants.IntentConstant.BADGE);
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
                    ShortcutBadger.applyCount(this, badge);
                }
            }else {
                String title = jsonObject.optString("message");
                String alert = jsonObject.optString("desc");
                TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
                Intent intent = new Intent(this, HomeActivity.class);
                taskStackBuilder.addParentStack(HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constants.IntentConstant.TYPE, type);
                intent.putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.NOTIFICATION);
                taskStackBuilder.addNextIntent(intent);
                displayNotification(type, taskStackBuilder, title, this, alert, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * method to show chat notification
     *
     * @param message
     */

    private void showChatNotificationView(String message, NotificationBean notification, int type) {
        String alert = notification.getSenderName() + (notification.getRoomType().equals(FirebaseConstants.SINGLE) ? "" : "@" + notification.getRoomName())
                + " : " + notification.getMessageText();
        String notificationImage = notification.getImageUrl();
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        Intent intent = new Intent(this, HomeActivity.class);
        taskStackBuilder.addParentStack(HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.IntentConstant.TYPE, type);
        intent.putExtra(Constants.IntentConstant.NOTIFICATION, notification);
        intent.putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.NOTIFICATION);
        taskStackBuilder.addNextIntent(intent);

        displayNotification(type, taskStackBuilder, getString(R.string.app_name), this, alert, notificationImage);

//        }
    }
    /**
     * method to show other notification
     *
     * @param message
     */

    private void showNotificationView(String message, com.shopoholic.models.NotificationBean notification, int type) {
        String alert = message;
        String notificationImage = notification.getImage();
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        Intent intent = new Intent(this, HomeActivity.class);
        taskStackBuilder.addParentStack(HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.IntentConstant.TYPE, type);
        intent.putExtra(Constants.IntentConstant.NOTIFICATION, notification);
        intent.putExtra(Constants.IntentConstant.FROM_CLASS, Constants.AppConstant.NOTIFICATION);
        taskStackBuilder.addNextIntent(intent);

        displayNotification(type, taskStackBuilder, getString(R.string.app_name), this, alert, notificationImage);
    }


    /**
     * method to show notification
     *
     * @param pendingIntentId
     * @param title
     * @param context
     */
    private void displayNotification(int pendingIntentId, TaskStackBuilder taskStackBuilder, String title, Context context, String alert, String notificationImage) {
        Notification notification;
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // create android channel
        NotificationChannel channel;
        String channelId = "channel_" + String.valueOf(pendingIntentId);
        String channelName = "Channel " + channelId;
        String groupId = Constants.AppConstant.NOTIFICATION_CHANNEL_GROUP;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            // Sets whether notifications posted to this channel should display notification lights
            channel.enableLights(true);
            // Sets whether notification posted to this channel should vibrate.
            channel.enableVibration(true);
            // Sets the notification light color for notifications posted to this channel
            channel.setLightColor(Color.GREEN);
            // Sets whether notifications posted to this channel appear on the locks creen or not
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            //creating channel group
            channel.setGroup(groupId);

            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannelGroup(new NotificationChannelGroup(groupId, Constants.AppConstant.NOTIFICATION_CHANNEL_GROUP));
                mNotificationManager.createNotificationChannel(channel);
            }
        }

        PendingIntent resultPendingIntent = null;
        if (taskStackBuilder != null) resultPendingIntent = taskStackBuilder.getPendingIntent(pendingIntentId, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context, channelId);
        }else {
            builder = new Notification.Builder(context);
        }

        builder.setContentTitle(title)
                .setContentText(alert)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_splash_logo))
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH);

        if (resultPendingIntent != null) {
            builder.setContentIntent(resultPendingIntent);

        }

//        if (notificationImage != null && !notificationImage.equals("")) {
//            try {
//                URL url = new URL(notificationImage);
//                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                builder.setStyle(new Notification.BigPictureStyle().bigPicture(image));
//            } catch(Exception e) {
//                e.printStackTrace();
//                builder.setStyle(new Notification.BigTextStyle().bigText(alert));
//            }
//        }else {
            builder.setStyle(new Notification.BigTextStyle().bigText(alert));
//        }
        builder.setDefaults(Notification.DEFAULT_ALL);
        notification = builder.build();


        if (mNotificationManager != null) {
            mNotificationManager.notify(pendingIntentId, notification);
        }
    }
}