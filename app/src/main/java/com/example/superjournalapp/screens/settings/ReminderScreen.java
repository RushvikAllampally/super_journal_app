package com.example.superjournalapp.screens.settings;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.example.superjournalapp.R;
import com.example.superjournalapp.constants.ApplicationConstants;
import com.example.superjournalapp.dto.NotificationData;
import com.example.superjournalapp.utils.JournalUtils;
import com.example.superjournalapp.utils.NotificationReceiver;
import com.google.gson.Gson;

import java.util.Calendar;

public class ReminderScreen extends AppCompatActivity {

    private ImageView gratitudeTimeIcon;
    private ImageView reflectTimeIcon;
    private ImageView bulletTimeIcon;
    private ImageView dreamTimeIcon;
    private ImageView quoteTimeIcon;
    private ImageView affirmationTimeIcon;

    private TextView gratitudeNotificationTime;
    private TextView ReflectiveNotificationTime;
    private TextView BulletNotificationTime;
    private TextView DreamNotificationTime;
    private TextView QuoteNotificationTime;
    private TextView AffirmationNotificationTime;

    private SwitchCompat gratitudeSwitchCombat;
    private SwitchCompat reflectiveSwitchCombat;
    private SwitchCompat bulletSwitchCombat;
    private SwitchCompat dreamSwitchCombat;
    private SwitchCompat quoteSwitchCombat;
    private SwitchCompat affirmationSwitchCombat;

    // Function to retrieve notification data
    public static NotificationData getNotificationData(SharedPreferences sharedPreferences, String key) {
        String dataJson = sharedPreferences.getString(key, null);
        if (dataJson != null) {
            return new Gson().fromJson(dataJson, NotificationData.class);
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_screen);

        gratitudeTimeIcon = findViewById(R.id.gratitude_time_icon);
        reflectTimeIcon = findViewById(R.id.reflective_time_icon);
        bulletTimeIcon = findViewById(R.id.bullet_time_icon);
        dreamTimeIcon = findViewById(R.id.dream_time_icon);
        quoteTimeIcon = findViewById(R.id.quote_time_icon);
        affirmationTimeIcon = findViewById(R.id.affirmation_time_icon);

        gratitudeNotificationTime = findViewById(R.id.gratitude_notification_time);
        ReflectiveNotificationTime = findViewById(R.id.reflective_notification_time);
        BulletNotificationTime = findViewById(R.id.bullet_notification_time);
        DreamNotificationTime = findViewById(R.id.dream_notification_time);

        QuoteNotificationTime = findViewById(R.id.quote_time);
        AffirmationNotificationTime = findViewById(R.id.affirmation_time);

        gratitudeNotificationTime.setText(getNotificationTime(ApplicationConstants.Gratitude_NOTIFICATION_TYPE));
        ReflectiveNotificationTime.setText(getNotificationTime(ApplicationConstants.REFLECTIVE_NOTIFICATION_TYPE));
        BulletNotificationTime.setText(getNotificationTime(ApplicationConstants.BULLET_NOTIFICATION_TYPE));
        DreamNotificationTime.setText(getNotificationTime(ApplicationConstants.DREAM_NOTIFICATION_TYPE));

        QuoteNotificationTime.setText(getNotificationTime(ApplicationConstants.QUOTE_NOTIFICATION_TYPE));
        AffirmationNotificationTime.setText(getNotificationTime(ApplicationConstants.AFFIRMATION_NOTIFICATION_TYPE));

        gratitudeSwitchCombat = findViewById(R.id.gratitude_notification_switch);
        reflectiveSwitchCombat = findViewById(R.id.reflective_notification_switch);
        bulletSwitchCombat = findViewById(R.id.bullet_notification_switch);
        dreamSwitchCombat = findViewById(R.id.dream_notification_switch);
        quoteSwitchCombat = findViewById(R.id.quote_notification_switch);
        affirmationSwitchCombat = findViewById(R.id.affirmation_notification_switch);

        gratitudeSwitchCombat.setChecked(getNotificationStatus(ApplicationConstants.Gratitude_NOTIFICATION_TYPE));
        reflectiveSwitchCombat.setChecked(getNotificationStatus(ApplicationConstants.REFLECTIVE_NOTIFICATION_TYPE));
        bulletSwitchCombat.setChecked(getNotificationStatus(ApplicationConstants.BULLET_NOTIFICATION_TYPE));
        dreamSwitchCombat.setChecked(getNotificationStatus(ApplicationConstants.DREAM_NOTIFICATION_TYPE));
        quoteSwitchCombat.setChecked(getNotificationStatus(ApplicationConstants.QUOTE_NOTIFICATION_TYPE));
        affirmationSwitchCombat.setChecked(getNotificationStatus(ApplicationConstants.AFFIRMATION_NOTIFICATION_TYPE));


//        gratitudeTimeIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                TimePickerDialog dialog = new TimePickerDialog(ReminderScreen.this, new TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
//                        setNotificationTime(ApplicationConstants.Gratitude_NOTIFICATION_TYPE, hours, minutes);
//                        scheduleNotification(ApplicationConstants.Gratitude_NOTIFICATION_TYPE, hours, minutes);
//                    }
//                }, 15, 0, true);
//                dialog.show();
//            }
//
//        });

        // Set the common OnClickListener for both buttons
        View.OnClickListener notificationOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String notificationType = (String) view.getTag();
                System.out.println("notificationType : " + notificationType);
                TimePickerDialog dialog = new TimePickerDialog(ReminderScreen.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
                        setNotificationTime(notificationType, hours, minutes);
                        scheduleNotification(notificationType, hours, minutes);
                        setNotificationTimeOnUpdate(notificationType, hours, minutes);
                    }
                }, 12, 0, true);
                dialog.show();
            }
        };

        gratitudeTimeIcon.setOnClickListener(notificationOnClickListener);
        reflectTimeIcon.setOnClickListener(notificationOnClickListener);
        bulletTimeIcon.setOnClickListener(notificationOnClickListener);
        dreamTimeIcon.setOnClickListener(notificationOnClickListener);
        quoteTimeIcon.setOnClickListener(notificationOnClickListener);
        affirmationTimeIcon.setOnClickListener(notificationOnClickListener);

        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                JournalUtils.askForNotificationPermission(ReminderScreen.this, ReminderScreen.this);

                int buildVersion = Build.VERSION.SDK_INT;
                int apiVersion = Build.VERSION_CODES.TIRAMISU;

                if (buildVersion < apiVersion || (buildVersion >= apiVersion && (ContextCompat.checkSelfPermission(ReminderScreen.this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED))) {
                    setNotificationEnabled(compoundButton.getTag().toString(), b);
                }else{
                    compoundButton.setChecked(false);
                }
            }
        };



        gratitudeSwitchCombat.setOnCheckedChangeListener(checkedChangeListener);
        reflectiveSwitchCombat.setOnCheckedChangeListener(checkedChangeListener);
        bulletSwitchCombat.setOnCheckedChangeListener(checkedChangeListener);
        dreamSwitchCombat.setOnCheckedChangeListener(checkedChangeListener);
        quoteSwitchCombat.setOnCheckedChangeListener(checkedChangeListener);
        affirmationSwitchCombat.setOnCheckedChangeListener(checkedChangeListener);

    }


    public void setNotificationTimeOnUpdate(String notificationType, int hour, int minute) {

        String time;
        if (minute < 10) {
            time = hour + ":0" + minute;
        } else {
            time = hour + ":" + minute;
        }
        switch (notificationType) {
            case "gratitude":
                gratitudeNotificationTime.setText(time);
                break;
            case "reflective":
                ReflectiveNotificationTime.setText(time);
                break;
            case "bullet":
                BulletNotificationTime.setText(time);
                break;
            case "dream":
                DreamNotificationTime.setText(time);
                break;
            case "quote":
                QuoteNotificationTime.setText(time);
                break;
            case "affirmation":
                AffirmationNotificationTime.setText(time);
                break;
            default:
                break;
        }

    }


    public void setNotificationTime(String notificationType, int hour, int minute) {

        SharedPreferences sharedPreferences = getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String dataJson = sharedPreferences.getString(notificationType, null);

        if (dataJson == null) {
            // Example: Store data for the "Dream Journal"
            NotificationData notificationData = new NotificationData(true, hour, minute);
            String notificationDataInJson = new Gson().toJson(notificationData);
            editor.putString(notificationType, notificationDataInJson);
        } else {
            NotificationData notificationData = new Gson().fromJson(dataJson, NotificationData.class);
            notificationData.setHour(hour);
            notificationData.setMinute(minute);

            String notificationDataInJson = new Gson().toJson(notificationData);
            editor.putString(notificationType, notificationDataInJson);
        }
        editor.apply();

    }

    public String getNotificationTime(String notificationType) {

        SharedPreferences sharedPreferences = getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        String dataJson = sharedPreferences.getString(notificationType, null);

        if (dataJson == null) {
            return "";
        }
        NotificationData notificationData = new Gson().fromJson(dataJson, NotificationData.class);
        int hour = notificationData.getHour();
        int minute = notificationData.getMinute();
        String time = "";
        if (minute < 10) {
            time = hour + ":0" + minute;
        } else {
            time = hour + ":" + minute;
        }

        return time;
    }

    public boolean getNotificationStatus(String notificationType) {

        SharedPreferences sharedPreferences = getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        String dataJson = sharedPreferences.getString(notificationType, null);

        if (dataJson == null) {
            return false;
        }
        NotificationData notificationData = new Gson().fromJson(dataJson, NotificationData.class);

        return notificationData.isEnabled();
    }

    public void setNotificationEnabled(String notificationType, boolean isNotificationEnabled) {

        SharedPreferences sharedPreferences = getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String dataJson = sharedPreferences.getString(notificationType, null);

        if (dataJson == null) {
            // Example: Store data for the "Dream Journal"
            NotificationData notificationData = new NotificationData(false, 20, 00);
            String notificationDataInJson = new Gson().toJson(notificationData);
            editor.putString(notificationType, notificationDataInJson);
        } else {
            NotificationData notificationData = new Gson().fromJson(dataJson, NotificationData.class);
            notificationData.setEnabled(isNotificationEnabled);
            String notificationDataInJson = new Gson().toJson(notificationData);
            editor.putString(notificationType, notificationDataInJson);
        }
        editor.apply();
    }


    // Function to schedule a single notification
    private void scheduleNotification(String notificationType, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 00);

        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra(ApplicationConstants.NOTIFICATION_TYPE, notificationType);

        System.out.println(" in scheduleNotification fn notificationType :" + notificationType);

        //notification channel
        String channelID = ApplicationConstants.MY_APP_NAME;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelID);

            System.out.println("creating channel");

            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                notificationChannel = new NotificationChannel(channelID, "Journal Notifications", importance);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        //end channel creation

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), ApplicationConstants.NOTIFICATION_TYPE_REQUEST_CODE.get(notificationType), intent, PendingIntent.FLAG_IMMUTABLE);
        System.out.println("setting the receiver");
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

//        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);


//        if (Build.VERSION.SDK_INT >= 23) {
//            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//        } else {
//            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//        }
    }

}