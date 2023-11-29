package com.example.superjournalapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.superjournalapp.constants.ApplicationConstants;
import com.example.superjournalapp.screens.fragments.HomeFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class JournalUtils {

    public static String getDateFromJavaDate(Date date) {

        // Create a SimpleDateFormat with the desired format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");

        // Format the Date to get the date and month as strings
        String formattedDate = dateFormat.format(date);

        return formattedDate;
    }

    public static String getMonthFromJavaDate(Date date) {

        // Create a SimpleDateFormat with the desired format
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");

        // Format the Date to get the date and month as strings
        String formattedMonth = monthFormat.format(date);

        return getMonthName(formattedMonth);
    }

    public static int getRandomNumber() {
        Random random = new Random();
        return random.nextInt(5); // Generates a random number from 0 to 4 (inclusive)
    }

    public static String getMonthName(String monthNumber) {
        int monthNum = Integer.valueOf(monthNumber);
        switch (monthNum) {
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "Aug";
            case 9:
                return "Sept";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";
            default:
                return "";
        }
    }

    public static int updateStreakOnLoad(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);
        boolean isStreakContinued = checkStreakContinuation(preferences);

        long lastEntryDateMillis = preferences.getLong(ApplicationConstants.LAST_ENTRY_DATE_PREF_KEY, -1);

        long currentDateMillis = System.currentTimeMillis();
        // Compare dates to check if the last entry was on the previous date
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTimeInMillis(currentDateMillis);

        Calendar lastEntryCalendar = Calendar.getInstance();
        lastEntryCalendar.setTimeInMillis(lastEntryDateMillis);

        if (lastEntryDateMillis != -1 && isSameDay(currentCalendar, lastEntryCalendar)) {
            return 1;
        } else if (!isStreakContinued) {

            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(ApplicationConstants.STREAK_PREF_KEY, 0);
            editor.apply();

            saveLastEntryDate(preferences);
            HomeFragment.updateStreakCount(0);

            return 0;
        } else {
            int currentStreak = preferences.getInt(ApplicationConstants.STREAK_PREF_KEY, 0);
            return currentStreak;
        }

    }

    public static void updateStreak(Context context) {

        SharedPreferences preferences = context.getSharedPreferences(ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);
        int currentStreak = preferences.getInt(ApplicationConstants.STREAK_PREF_KEY, 0);

        boolean isStreakContinued = checkStreakContinuation(preferences);

        if (isStreakContinued) {
            currentStreak++;
        } else {
            currentStreak = 1; // Start a new streak
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(ApplicationConstants.STREAK_PREF_KEY, currentStreak);
        editor.apply();

        saveLastEntryDate(preferences);
        HomeFragment.updateStreakCount(currentStreak);
    }

    public static int getCurrentStreak(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);
        int currentStreak = preferences.getInt(ApplicationConstants.STREAK_PREF_KEY, 0);
        return currentStreak;
    }


    private static boolean checkStreakContinuation(SharedPreferences preferences) {
        long currentDateMillis = System.currentTimeMillis();
        long lastEntryDateMillis = preferences.getLong(ApplicationConstants.LAST_ENTRY_DATE_PREF_KEY, -1);

        if (lastEntryDateMillis != -1) {
            // Compare dates to check if the last entry was on the previous date
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTimeInMillis(currentDateMillis);

            Calendar lastEntryCalendar = Calendar.getInstance();
            lastEntryCalendar.setTimeInMillis(lastEntryDateMillis);
            lastEntryCalendar.add(Calendar.DAY_OF_MONTH, 1);

            return isSameDay(currentCalendar, lastEntryCalendar);
        }

        return false;
    }

    private static boolean isSameDay(Calendar calendar1, Calendar calendar2) {
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
                calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
    }

    public static void saveLastEntryDate(SharedPreferences preferences) {
        long currentDateMillis = System.currentTimeMillis();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(ApplicationConstants.LAST_ENTRY_DATE_PREF_KEY, currentDateMillis);
        editor.apply();
    }

    public static void askForNotificationPermission(Context context, Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

    }

}
