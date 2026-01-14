package com.diary.superjournalapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatDelegate;

import com.diary.superjournalapp.constants.ApplicationConstants;
import com.diary.superjournalapp.constants.ThemeConstants;

/**
 * Utility class for managing app theme (light/dark mode)
 */
public class ThemeUtils {
    // Theme mode constants mapping to AppCompatDelegate modes
    public static final int MODE_SYSTEM = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    public static final int MODE_LIGHT = AppCompatDelegate.MODE_NIGHT_NO;
    public static final int MODE_DARK = AppCompatDelegate.MODE_NIGHT_YES;

    /**
     * Get saved theme preference
     * @param context Application context
     * @return Theme mode (MODE_SYSTEM, MODE_LIGHT, or MODE_DARK)
     */
    public static int getThemeMode(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(ThemeConstants.THEME_PREF_KEY, MODE_SYSTEM);
    }

    /**
     * Set and apply theme mode
     * @param context Application context
     * @param themeMode Theme mode to apply
     */
    public static void setThemeMode(Context context, int themeMode) {
        // Apply theme
        AppCompatDelegate.setDefaultNightMode(themeMode);
        
        // Save preference
        SharedPreferences preferences = context.getSharedPreferences(
                ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(ThemeConstants.THEME_PREF_KEY, themeMode);
        editor.apply();
    }

    /**
     * Check if dark mode is currently active
     * @param context Application context
     * @return true if dark mode is active
     */
    public static boolean isDarkModeActive(Context context) {
        int currentNightMode = context.getResources().getConfiguration().uiMode 
                & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }
    
    /**
     * Apply theme change with activity recreation
     * @param activity Activity to recreate
     */
    public static void applyTheme(Activity activity) {
        activity.recreate();
    }
    
    /**
     * Convert ThemeConstants mode to AppCompatDelegate mode
     * @param themeConstantMode Theme constant mode value
     * @return AppCompatDelegate mode value
     */
    public static int convertToAppCompatMode(int themeConstantMode) {
        switch (themeConstantMode) {
            case ThemeConstants.MODE_LIGHT:
                return MODE_LIGHT;
            case ThemeConstants.MODE_DARK:
                return MODE_DARK;
            case ThemeConstants.MODE_SYSTEM:
            default:
                return MODE_SYSTEM;
        }
    }
}
