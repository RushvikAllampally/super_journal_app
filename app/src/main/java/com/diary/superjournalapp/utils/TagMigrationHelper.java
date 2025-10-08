package com.diary.superjournalapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.diary.superjournalapp.constants.ApplicationConstants;
import com.diary.superjournalapp.database.DatabaseHelper;
import com.diary.superjournalapp.entity.Journal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Helper class for migrating tags from the old system to the new one
 */
public class TagMigrationHelper {

    private static final String TAG = "TagMigrationHelper";
    private static final String MIGRATION_COMPLETE_KEY = "tag_migration_complete";
    private static final String TAGS_PREF_KEY = "journal_tags";
    
    private Context context;
    private TagManager tagManager;
    private DatabaseHelper databaseHelper;
    
    /**
     * Constructor
     * @param context Application context
     */
    public TagMigrationHelper(Context context) {
        this.context = context;
        this.tagManager = new TagManager(context);
        this.databaseHelper = DatabaseHelper.getDb(context);
    }
    
    /**
     * Check if migration has been completed
     * @return True if migration is complete, false otherwise
     */
    public boolean isMigrationComplete() {
        SharedPreferences preferences = context.getSharedPreferences(
                ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(MIGRATION_COMPLETE_KEY, false);
    }
    
    /**
     * Migrate tags from the old system to the new one
     * This should be called once during app startup after updating to the new version
     */
    public void migrateTagsIfNeeded() {
        if (isMigrationComplete()) {
            return;
        }
        
        Log.i(TAG, "Starting tag migration...");
        
        try {
            // Migrate journal tags
            migrateJournalTags();
            
            // Clean up old SharedPreferences tags
            cleanUpOldPreferences();
            
            // Mark migration as complete
            markMigrationComplete();
            
            Log.i(TAG, "Tag migration completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error during tag migration", e);
        }
    }
    
    /**
     * Migrate tags from all journals
     */
    private void migrateJournalTags() {
        List<Journal> allJournals = databaseHelper.journalDao().getAllJournal();
        
        int migratedCount = 0;
        for (Journal journal : allJournals) {
            String oldTags = journal.getTags();
            if (oldTags != null && !oldTags.trim().isEmpty()) {
                tagManager.addTagsToJournal(journal.getJournalId(), oldTags);
                migratedCount++;
            }
        }
        
        Log.i(TAG, "Migrated tags from " + migratedCount + " journals");
    }
    
    /**
     * Clean up old SharedPreferences tags
     * We keep them for reference but no longer use them
     */
    private void cleanUpOldPreferences() {
        SharedPreferences preferences = context.getSharedPreferences(
                ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);
        
        // We don't remove the old tags, just in case we need to reference them
        // But we'll indicate that they're no longer in use
        Set<String> existingTags = preferences.getStringSet(TAGS_PREF_KEY, new HashSet<>());
        if (!existingTags.isEmpty()) {
            Log.i(TAG, "Keeping " + existingTags.size() + " tags in SharedPreferences for reference");
        }
    }
    
    /**
     * Mark migration as complete
     */
    private void markMigrationComplete() {
        SharedPreferences preferences = context.getSharedPreferences(
                ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(MIGRATION_COMPLETE_KEY, true).apply();
    }
}
