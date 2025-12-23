package com.diary.superjournalapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.diary.superjournalapp.database.DatabaseHelper;

/**
 * Manages premium feature access and subscription limits
 */
public class PremiumFeatureManager {
    
    private static final String PREFS_NAME = "premium_prefs";
    private static final String IS_PREMIUM_KEY = "is_premium_user";
    
    // Lock feature is PREMIUM ONLY - no free locks
    private static final int FREE_LOCK_LIMIT = 0; // Free users cannot lock journals
    
    // FOR TESTING ONLY - Set to true to bypass premium checks
    // IMPORTANT: Set to false before production release!
    private static final boolean ENABLE_LOCK_FOR_TESTING = true;
    
    // NEW: Export feature testing flag
    // IMPORTANT: Set to false before production release!
    private static final boolean ENABLE_EXPORT_FOR_TESTING = true;
    
    // NEW: Backup feature testing flag
    // IMPORTANT: Set to false before production release!
    private static final boolean ENABLE_BACKUP_FOR_TESTING = true;
    
    private static PremiumFeatureManager instance;
    private Context context;
    private SharedPreferences prefs;
    
    private PremiumFeatureManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * Get singleton instance of PremiumFeatureManager
     */
    public static synchronized PremiumFeatureManager getInstance(Context context) {
        if (instance == null) {
            instance = new PremiumFeatureManager(context);
        }
        return instance;
    }
    
    /**
     * Check if user has premium subscription
     * 
     * @return true if user is premium subscriber
     */
    public boolean isPremiumUser() {
        // TODO: Integrate with actual subscription system (Google Play Billing, etc.)
        return prefs.getBoolean(IS_PREMIUM_KEY, false);
    }
    
    /**
     * Set premium subscription status (for testing or after successful purchase)
     * 
     * @param isPremium true if user should have premium access
     */
    public void setPremiumStatus(boolean isPremium) {
        prefs.edit().putBoolean(IS_PREMIUM_KEY, isPremium).apply();
    }
    
    /**
     * Get the current count of locked journals
     * 
     * @return number of currently locked journals
     */
    public int getLockedJournalsCount() {
        DatabaseHelper db = DatabaseHelper.getDb(context);
        return db.journalDao().getLockedJournals().size();
    }
    
    /**
     * Check if user can lock more journals
     * Lock feature is PREMIUM ONLY (no free locks)
     * 
     * @return true if user can lock another journal
     */
    public boolean canLockMoreJournals() {
        // FOR TESTING: Bypass premium check
        if (ENABLE_LOCK_FOR_TESTING) {
            return true;
        }
        
        // PRODUCTION: Only premium users can lock journals
        return isPremiumUser();
    }
    
    /**
     * Get the maximum number of journals a user can lock
     * 
     * @return maximum lock limit for current user
     */
    public int getMaxLockLimit() {
        return isPremiumUser() ? Integer.MAX_VALUE : FREE_LOCK_LIMIT;
    }
    
    /**
     * Get remaining lock slots for free users
     * 
     * @return number of journals user can still lock
     */
    public int getRemainingLockSlots() {
        if (isPremiumUser()) {
            return Integer.MAX_VALUE;
        }
        
        return Math.max(0, FREE_LOCK_LIMIT - getLockedJournalsCount());
    }
    
    /**
     * Check if user can use the per-journal lock feature
     * 
     * @return true if feature is available
     */
    public boolean canUseLockFeature() {
        return isPremiumUser() || getLockedJournalsCount() < FREE_LOCK_LIMIT;
    }
    
    /**
     * Get feature description for current user
     * 
     * @return description of lock feature limits
     */
    public String getLockFeatureDescription() {
        if (ENABLE_LOCK_FOR_TESTING) {
            return "Testing Mode: Lock feature enabled";
        }
        
        if (isPremiumUser()) {
            return "Premium: Unlimited journal locks";
        }
        
        return "Premium Feature: Upgrade to lock journals";
    }
    
    /**
     * Check if a specific premium feature is available
     * 
     * @param feature The feature to check
     * @return true if feature is available
     */
    public boolean isFeatureAvailable(PremiumFeature feature) {
        switch (feature) {
            case PER_JOURNAL_LOCKING:
                return canUseLockFeature();
            case UNLIMITED_LOCKS:
                return isPremiumUser();
            case EXPORT_LOCKED_JOURNALS:
                return isPremiumUser();
            // NEW: Export features
            case EXPORT_PDF:
            case EXPORT_TXT:
            case BATCH_EXPORT:
                return canExportJournals();
            // NEW: Backup features
            case BACKUP_TO_CLOUD:
            case RESTORE_FROM_BACKUP:
            case AUTO_BACKUP:
                return canUseBackupFeature();
            default:
                return isPremiumUser();
        }
    }
    
    /**
     * Enum for different premium features
     */
    public enum PremiumFeature {
        PER_JOURNAL_LOCKING,
        UNLIMITED_LOCKS,
        EXPORT_LOCKED_JOURNALS,
        // NEW: Export features
        EXPORT_PDF,
        EXPORT_TXT,
        BATCH_EXPORT,
        // NEW: Backup features
        BACKUP_TO_CLOUD,
        RESTORE_FROM_BACKUP,
        AUTO_BACKUP,
        // Future features
        CLOUD_SYNC,
        ADVANCED_THEMES,
        PREMIUM_TEMPLATES
    }
    
    /**
     * Get upgrade message for when limits are reached
     * 
     * @return user-friendly upgrade message
     */
    public String getUpgradeMessage() {
        return "Journal locking is a Premium feature. " +
               "Upgrade to Premium to lock your private journals and access more exclusive features!";
    }
    
    /**
     * NEW: Check if user can export journals
     * Export is a PREMIUM feature
     * 
     * @return true if user can export journals
     */
    public boolean canExportJournals() {
        // FOR TESTING: Bypass premium check
        if (ENABLE_EXPORT_FOR_TESTING) {
            return true;
        }
        
        // PRODUCTION: Only premium users can export
        return isPremiumUser();
    }
    
    /**
     * NEW: Get export feature description for current user
     * 
     * @return description of export feature limits
     */
    public String getExportFeatureDescription() {
        if (ENABLE_EXPORT_FOR_TESTING) {
            return "Testing Mode: Export feature enabled";
        }
        
        if (isPremiumUser()) {
            return "Premium: Unlimited journal exports";
        }
        
        return "Premium Feature: Upgrade to export journals";
    }
    
    /**
     * NEW: Get upgrade message specifically for export feature
     * 
     * @return user-friendly upgrade message for export
     */
    public String getExportUpgradeMessage() {
        return "Journal export is a Premium feature.\n\n" +
               "Upgrade to DiaryVerse Premium to:\n" +
               "✨ Export journals to beautiful PDFs\n" +
               "📝 Create plain text backups\n" +
               "📚 Batch export multiple journals\n" +
               "🎨 Customize export formatting\n" +
               "💾 Share your memories easily";
    }
    
    /**
     * NEW: Check if user can use backup features
     * Backup is a PREMIUM feature
     * 
     * @return true if user can use backup features
     */
    public boolean canUseBackupFeature() {
        // FOR TESTING: Bypass premium check
        if (ENABLE_BACKUP_FOR_TESTING) {
            return true;
        }
        
        // PRODUCTION: Only premium users can use backup
        return isPremiumUser();
    }
    
    /**
     * NEW: Get backup feature description for current user
     * 
     * @return description of backup feature limits
     */
    public String getBackupFeatureDescription() {
        if (ENABLE_BACKUP_FOR_TESTING) {
            return "Testing Mode: Backup feature enabled";
        }
        
        if (isPremiumUser()) {
            return "Premium: Unlimited backups & restores";
        }
        
        return "Premium Feature: Upgrade to backup journals";
    }
    
    /**
     * NEW: Get upgrade message specifically for backup feature
     * 
     * @return user-friendly upgrade message for backup
     */
    public String getBackupUpgradeMessage() {
        return "Backup & Restore is a Premium feature.\n\n" +
               "Upgrade to DiaryVerse Premium to:\n" +
               "☁️ Secure cloud backups to Google Drive\n" +
               "🔒 Military-grade encryption (AES-256)\n" +
               "🔄 Automatic daily backups\n" +
               "📱 Restore on any device\n" +
               "🛡️ Never lose your memories again!";
    }
    
    /**
     * Show premium upgrade dialog for the backup feature
     * 
     * @param context The context to show dialog from
     */
    public void showPremiumUpgradeDialog(Context context) {
        // TODO: Implement actual premium upgrade dialog
        // For now, this is a placeholder method that can be expanded
        // to integrate with Google Play Billing or other subscription system
    }
}
