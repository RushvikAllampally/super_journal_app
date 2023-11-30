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
                return "Reflect and Express Gratitude";
            case "dream":
                return "Capture Your Dreams";
            case "diary":
                return "Journal Your Thoughts";
            case "bullet":
                return "Organize with Bullet Journal";
            case "affirmation":
                return "Receive Positive Affirmation";
            case "quote":
                return "Discover Daily Inspiration";
            case "mood":
                return "Rate Your Mood Today";
            default:
                return "";
        }

    }

    public static String getNotificationDescription(String notificationType) {

        switch (notificationType) {
            case "gratitude":
                return "Take a moment to write about what you're grateful for today.";
            case "dream":
                return "Jot down your dreams and aspirations in your dream journal.";
            case "diary":
                return "Express your thoughts and feelings by writing in your personal diary.";
            case "bullet":
                return "Organize your tasks and thoughts with the bullet journal method.";
            case "affirmation":
                return "Read a positive affirmation to boost your mood and confidence.";
            case "quote":
                return "Explore a motivational quote to inspire and uplift your spirit.";
            case "mood":
                return "Share how you're feeling today and reflect on your mood.";
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

        builder.setSmallIcon(R.drawable.journal_icon).setContentTitle(getNotificationTitle(notificationType)).setContentText(getNotificationDescription(notificationType))
                .setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_MAX);

        Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("notificationType", notificationType);

        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), ApplicationConstants.NOTIFICATION_TYPE_REQUEST_CODE.get(notificationType), intent, PendingIntent.FLAG_IMMUTABLE);

        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelID);

            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(channelID, "Journal Notifications", importance);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        notificationManager.notify(ApplicationConstants.NOTIFICATION_TYPE_REQUEST_CODE.get(notificationType), builder.build());
    }

}
