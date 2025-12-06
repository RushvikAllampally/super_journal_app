package com.diary.superjournalapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;

import com.diary.superjournalapp.applock.AppLock;
import com.diary.superjournalapp.constants.ApplicationConstants;

import java.util.HashSet;
import java.util.Set;

/**
 * Manages per-journal locking functionality including authentication and temporary unlocks
 */
public class JournalLockManager {
    
    private static final String PREFS_NAME = "journal_lock_prefs";
    private static final String UNLOCKED_JOURNALS_KEY = "unlocked_journals";
    private static final long AUTO_LOCK_TIMEOUT_MS = 10 * 60 * 1000; // 10 minutes
    
    private static JournalLockManager instance;
    private Context context;
    private Set<Long> temporaryUnlockedJournals;
    private Handler autoLockHandler;
    private SharedPreferences prefs;
    
    /**
     * Interface for handling authentication results
     */
    public interface AuthenticationCallback {
        void onAuthenticationSuccess(long journalId);
        void onAuthenticationFailed(long journalId);
    }
    
    private JournalLockManager(Context context) {
        this.context = context.getApplicationContext();
        this.temporaryUnlockedJournals = new HashSet<>();
        this.autoLockHandler = new Handler(Looper.getMainLooper());
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadUnlockedJournals();
    }
    
    /**
     * Get singleton instance of JournalLockManager
     */
    public static synchronized JournalLockManager getInstance(Context context) {
        if (instance == null) {
            instance = new JournalLockManager(context);
        }
        return instance;
    }
    
    /**
     * Check if a journal is temporarily unlocked
     * NOTE: This feature is disabled - always returns false
     * User must authenticate every time they open a locked journal
     * 
     * @param journalId The journal ID to check
     * @return always false (no temporary unlock)
     */
    public boolean isTemporarilyUnlocked(long journalId) {
        return false; // Disabled - always require authentication
    }
    
    /**
     * Check if app-level passcode is enabled
     * 
     * @return true if passcode is enabled
     */
    public boolean isAppPasscodeEnabled() {
        SharedPreferences appPrefs = context.getSharedPreferences(ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);
        return appPrefs.getBoolean(ApplicationConstants.IS_PASSCODE_ENABLED, false);
    }
    
    /**
     * Authenticate user for journal access
     * 
     * @param activity The calling activity
     * @param journalId The journal ID to unlock
     * @param launcher ActivityResultLauncher for handling the result
     */
    public void authenticateForJournal(Activity activity, long journalId, ActivityResultLauncher<Intent> launcher) {
        if (!isAppPasscodeEnabled()) {
            // No app passcode set, show message and fail authentication
            Toast.makeText(context, "Please set up app passcode first in Settings", Toast.LENGTH_LONG).show();
            return;
        }
        
        Intent intent = new Intent(activity, AppLock.class);
        intent.putExtra("journal_authentication", true);
        intent.putExtra("journal_id", journalId);
        launcher.launch(intent);
    }
    
    /**
     * Handle successful authentication result
     * 
     * @param journalId The journal ID that was unlocked
     */
    public void handleAuthenticationSuccess(long journalId) {
        addTemporaryUnlock(journalId);
        Toast.makeText(context, "Journal unlocked temporarily", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Add journal to temporary unlock cache with auto-lock timer
     * 
     * @param journalId The journal ID to temporarily unlock
     */
    public void addTemporaryUnlock(long journalId) {
        temporaryUnlockedJournals.add(journalId);
        saveUnlockedJournals();
        
        // Set timer to automatically lock after timeout
        autoLockHandler.postDelayed(() -> {
            removeTemporaryUnlock(journalId);
            Toast.makeText(context, "Journal automatically locked due to inactivity", Toast.LENGTH_SHORT).show();
        }, AUTO_LOCK_TIMEOUT_MS);
    }
    
    /**
     * Remove journal from temporary unlock cache
     * 
     * @param journalId The journal ID to lock again
     */
    public void removeTemporaryUnlock(long journalId) {
        temporaryUnlockedJournals.remove(journalId);
        saveUnlockedJournals();
    }
    
    /**
     * Clear all temporary unlocks (called when app goes to background)
     */
    public void clearAllTemporaryUnlocks() {
        temporaryUnlockedJournals.clear();
        saveUnlockedJournals();
        autoLockHandler.removeCallbacksAndMessages(null);
    }
    
    /**
     * Reset auto-lock timer for a journal (extend the unlock period)
     * 
     * @param journalId The journal ID to reset timer for
     */
    public void resetAutoLockTimer(long journalId) {
        if (isTemporarilyUnlocked(journalId)) {
            // Cancel existing timer and set new one
            autoLockHandler.removeCallbacksAndMessages(null);
            autoLockHandler.postDelayed(() -> {
                removeTemporaryUnlock(journalId);
            }, AUTO_LOCK_TIMEOUT_MS);
        }
    }
    
    /**
     * Check if journal should be accessible (either unlocked or temporarily unlocked)
     * 
     * @param isLocked The journal's lock status from database
     * @param journalId The journal ID
     * @return true if journal should be accessible
     */
    public boolean isJournalAccessible(boolean isLocked, long journalId) {
        if (!isLocked) {
            return true; // Not locked, always accessible
        }
        return isTemporarilyUnlocked(journalId); // Locked but temporarily unlocked
    }
    
    /**
     * Save unlocked journals to SharedPreferences
     */
    private void saveUnlockedJournals() {
        Set<String> stringSet = new HashSet<>();
        for (Long journalId : temporaryUnlockedJournals) {
            stringSet.add(String.valueOf(journalId));
        }
        prefs.edit().putStringSet(UNLOCKED_JOURNALS_KEY, stringSet).apply();
    }
    
    /**
     * Load unlocked journals from SharedPreferences
     */
    private void loadUnlockedJournals() {
        Set<String> stringSet = prefs.getStringSet(UNLOCKED_JOURNALS_KEY, new HashSet<>());
        temporaryUnlockedJournals.clear();
        for (String journalIdStr : stringSet) {
            try {
                temporaryUnlockedJournals.add(Long.parseLong(journalIdStr));
            } catch (NumberFormatException e) {
                // Ignore invalid entries
            }
        }
    }
    
    /**
     * Called when app resumes from background
     */
    public void onAppResume() {
        // Could implement logic to selectively clear unlocks or reset timers
        // For now, we'll keep the unlocks but reset timers
        for (Long journalId : new HashSet<>(temporaryUnlockedJournals)) {
            resetAutoLockTimer(journalId);
        }
    }
    
    /**
     * Called when app goes to background
     */
    public void onAppPause() {
        // Clear all temporary unlocks for security
        clearAllTemporaryUnlocks();
    }
}
