package com.diary.superjournalapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;

import com.diary.superjournalapp.applock.AppLock;
import com.diary.superjournalapp.constants.ApplicationConstants;
import com.diary.superjournalapp.database.DatabaseHelper;
import com.diary.superjournalapp.entity.Journal;

/**
 * Helper class for journal lock/unlock operations with authentication
 * Use this in journal detail activities (ReflectiveJournal, GratitudeJournal, etc.)
 */
public class JournalLockHelper {
    
    private Context context;
    private PremiumFeatureManager premiumManager;
    private JournalLockManager lockManager;
    
    public interface LockToggleCallback {
        void onLockToggled(boolean isNowLocked);
        void onAuthenticationRequired();
        void onSetupRequired();
    }
    
    public JournalLockHelper(Context context) {
        this.context = context;
        this.premiumManager = PremiumFeatureManager.getInstance(context);
        this.lockManager = JournalLockManager.getInstance(context);
    }
    
    /**
     * Check if app lock is set up
     */
    public boolean isAppLockSetup() {
        return lockManager.isAppPasscodeEnabled();
    }
    
    /**
     * Show dialog to inform user about app lock requirement
     */
    public void showAppLockRequiredDialog(Activity activity) {
        new AlertDialog.Builder(context)
            .setTitle("App Lock Required")
            .setMessage("To use journal locking, please set up App Lock first.\n\n" +
                       "The same password will be used for all locked journals.")
            .setPositiveButton("Go to Settings", (dialog, which) -> {
                Intent intent = new Intent(context, com.diary.superjournalapp.screens.settings.SettingsScreen.class);
                activity.startActivity(intent);
            })
            .setNegativeButton("Cancel", null)
            .setCancelable(true)
            .show();
    }
    
    /**
     * Toggle lock status for a journal with proper checks
     * Call this from lock/unlock button in journal detail screen
     */
    public void toggleJournalLock(Journal journal, Activity activity, 
                                   ActivityResultLauncher<Intent> authLauncher,
                                   LockToggleCallback callback) {
        
        // Check if app lock is set up
        if (!isAppLockSetup()) {
            showAppLockRequiredDialog(activity);
            if (callback != null) callback.onSetupRequired();
            return;
        }
        
        if (journal.isLocked()) {
            // Unlocking - show confirmation dialog first
            showUnlockConfirmationDialog(activity, journal, authLauncher, callback);
        } else {
            // Locking - check premium limits first
            if (!premiumManager.canLockMoreJournals()) {
                showPremiumUpgradeDialog();
                return;
            }
            
            // Lock the journal (no authentication needed to lock)
            lockJournal(journal);
            if (callback != null) callback.onLockToggled(true);
        }
    }
    
    /**
     * Lock a journal in database
     */
    private void lockJournal(Journal journal) {
        journal.setLocked(true);
        DatabaseHelper db = DatabaseHelper.getDb(context);
        db.journalDao().updateLockStatus(journal.getJournalId(), true);
        
        Toast.makeText(context, "Journal locked", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Unlock a journal in database
     */
    private void unlockJournal(Journal journal) {
        journal.setLocked(false);
        DatabaseHelper db = DatabaseHelper.getDb(context);
        db.journalDao().updateLockStatus(journal.getJournalId(), false);
        
        Toast.makeText(context, "Journal unlocked", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Show confirmation dialog before unlocking
     */
    private void showUnlockConfirmationDialog(Activity activity, Journal journal,
                                              ActivityResultLauncher<Intent> launcher,
                                              LockToggleCallback callback) {
        new AlertDialog.Builder(context)
            .setTitle("Remove Lock")
            .setMessage("Are you sure you want to remove the lock from this journal?\n\n" +
                       "After removing the lock:\n" +
                       "• Anyone can open and read this journal\n" +
                       "• You can lock it again anytime\n" +
                       "• You'll need to enter your password to confirm")
            .setPositiveButton("Remove Lock", (dialog, which) -> {
                // Launch authentication for unlock
                launchAuthenticationForUnlock(journal, launcher, callback);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    /**
     * Launch AppLock activity for authentication to permanently unlock journal
     */
    private void launchAuthenticationForUnlock(Journal journal, 
                                               ActivityResultLauncher<Intent> launcher,
                                               LockToggleCallback callback) {
        if (launcher == null) {
            Toast.makeText(context, "Authentication not available", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Intent intent = new Intent(context, AppLock.class);
        intent.putExtra("journal_unlock", true); // This is for permanently unlocking (changing database)
        intent.putExtra("journal_id", journal.getJournalId());
        launcher.launch(intent);
        
        if (callback != null) callback.onAuthenticationRequired();
    }
    
    /**
     * Handle authentication result - call this from onActivityResult
     */
    public void handleAuthenticationResult(boolean success, Journal journal, 
                                          LockToggleCallback callback) {
        if (success) {
            unlockJournal(journal);
            if (callback != null) callback.onLockToggled(false);
        } else {
            Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Check if journal can be accessed (for opening locked journals)
     */
    public boolean canAccessJournal(Journal journal, Activity activity,
                                    ActivityResultLauncher<Intent> authLauncher) {
        if (!journal.isLocked()) {
            return true; // Not locked, can access
        }
        
        // Check if temporarily unlocked
        if (lockManager.isTemporarilyUnlocked(journal.getJournalId())) {
            return true; // Temporarily unlocked, can access
        }
        
        // Need authentication
        if (!isAppLockSetup()) {
            showAppLockRequiredDialog(activity);
            return false;
        }
        
        // Launch authentication
        Intent intent = new Intent(context, AppLock.class);
        intent.putExtra("journal_access", true);
        intent.putExtra("journal_id", journal.getJournalId());
        authLauncher.launch(intent);
        
        return false; // Will be accessible after authentication
    }
    
    /**
     * Handle authentication result for journal access
     */
    public void handleAccessAuthenticationResult(boolean success, long journalId) {
        if (success) {
            lockManager.addTemporaryUnlock(journalId);
            Toast.makeText(context, "Journal unlocked temporarily (10 minutes)", 
                Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Authentication failed. Cannot access journal.", 
                Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Show premium upgrade dialog
     */
    private void showPremiumUpgradeDialog() {
        new AlertDialog.Builder(context)
            .setTitle("Premium Feature")
            .setMessage(premiumManager.getUpgradeMessage())
            .setPositiveButton("Upgrade", (dialog, which) -> {
                // TODO: Launch premium upgrade flow
                Toast.makeText(context, "Premium upgrade coming soon!", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    /**
     * Get lock button text based on current state
     */
    public String getLockButtonText(Journal journal) {
        return journal.isLocked() ? "Unlock Journal" : "Lock Journal";
    }
    
    /**
     * Get lock button icon resource based on current state
     */
    public int getLockButtonIcon(Journal journal) {
        return journal.isLocked() ? 
            android.R.drawable.ic_lock_lock : 
            android.R.drawable.ic_lock_idle_lock;
    }
}
