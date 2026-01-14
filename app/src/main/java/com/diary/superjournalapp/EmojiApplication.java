package com.diary.superjournalapp;

import android.app.Application;

import com.diary.superjournalapp.constants.ThemeConstants;
import com.diary.superjournalapp.utils.ThemeUtils;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

/**
 * Application class for initialization
 */
public class EmojiApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize emoji manager
        EmojiManager.install(new GoogleEmojiProvider());
        
        // Apply saved theme preference
        applyTheme();
    }
    
    /**
     * Apply the saved theme from preferences
     */
    private void applyTheme() {
        int themeMode = ThemeUtils.getThemeMode(this);
        ThemeUtils.setThemeMode(this, themeMode);
    }
}
