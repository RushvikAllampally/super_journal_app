package com.diary.superjournalapp.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.diary.superjournalapp.utils.ThemeUtils;

/**
 * Base activity class that handles theme application
 * All activities that need theme support should extend this class
 */
public abstract class ThemedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Apply theme before setting content view
        applyTheme();
        super.onCreate(savedInstanceState);
    }

    /**
     * Apply the current theme based on saved preferences
     */
    protected void applyTheme() {
        // This is not needed for recreation since theme is already applied via Application class
        // But it's good to have for consistency when creating new activities
        int themeMode = ThemeUtils.getThemeMode(this);
        ThemeUtils.setThemeMode(this, themeMode);
    }
}
