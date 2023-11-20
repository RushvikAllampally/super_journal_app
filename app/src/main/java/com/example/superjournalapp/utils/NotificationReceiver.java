package com.example.superjournalapp.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.superjournalapp.MainActivity;
import com.example.superjournalapp.R;
import com.example.superjournalapp.constants.ApplicationConstants;
import com.example.superjournalapp.dto.NotificationData;
import com.example.superjournalapp.screens.settings.ReminderScreen;

public class NotificationReceiver extends BroadcastReceiver {

    public static String getNotificationTitle(String notificationType) {

        switch (notificationType) {

            case "gratitude":
                return "gratitude journal title";
            case "dream":
                return "dream journal title";
            case "reflective":
                return "reflective journal title";
            case "bullet":
                return "bullet journal title";
            case "affirmation":
                return "affirmation dude";
            case "quote":
                return "quote dude";
            default:
                return "";
        }

    }

    public static String getNotificationDescription(String notificationType) {

        switch (notificationType) {
            case "gratitude":
                return "write gratitude journal dude";
            case "dream":
                return "write dream journal dude";
            case "reflective":
                return "write reflective journal dude";
            case "bullet":
                return "write bullet journal dude";
            case "affirmation":
                return "see affirmation dude";
            case "quote":
                return "see quote dude";
            default:
                return "";
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("received the notification ");
        String notificationType = intent.getStringExtra(ApplicationConstants.NOTIFICATION_TYPE);

//         Check if the notification is enabled
        SharedPreferences sharedPreferences = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        NotificationData notificationData = ReminderScreen.getNotificationData(sharedPreferences, notificationType);

        System.out.println("Notification data is : " + notificationData);
        System.out.println("Notification type in receiver " + notificationType);

        if (notificationData != null && notificationData.isEnabled()) {
            // Show the notification
            showNotification(context, notificationType);
        }
    }

    private void showNotification(Context context, String notificationType) {
        // Create and show the notification here, similar to your original code
        String channelID = ApplicationConstants.MY_APP_NAME;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID);

        builder.setSmallIcon(R.drawable.notifications_none_24).setContentTitle(getNotificationTitle(notificationType)).setContentText(getNotificationDescription(notificationType))
                .setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_HIGH);

        Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("data", "dhvdjb");

        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), ApplicationConstants.NOTIFICATION_TYPE_REQUEST_CODE.get(notificationType), intent, PendingIntent.FLAG_IMMUTABLE);

        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelID);

            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                notificationChannel = new NotificationChannel(channelID, "Journal Notifications", importance);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        notificationManager.notify(ApplicationConstants.NOTIFICATION_TYPE_REQUEST_CODE.get(notificationType), builder.build());
    }

}
