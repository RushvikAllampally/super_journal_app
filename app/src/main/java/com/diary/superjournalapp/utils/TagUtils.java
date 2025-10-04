package com.diary.superjournalapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.diary.superjournalapp.constants.ApplicationConstants;
import com.diary.superjournalapp.database.DatabaseHelper;
import com.diary.superjournalapp.entity.Journal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class for managing journal tags
 */
public class TagUtils {

    private static final String TAGS_PREF_KEY = "journal_tags";
    private static final String TAG_SEPARATOR = ",";

    /**
     * Add tags to a journal
     * 
     * @param context The context
     * @param journalId The journal ID
     * @param newTags Comma-separated list of tags
     */
    public static void addTagsToJournal(Context context, long journalId, String newTags) {
        if (newTags == null || newTags.trim().isEmpty()) {
            return;
        }
        
        DatabaseHelper databaseHelper = DatabaseHelper.getDb(context);
        Journal journal = databaseHelper.journalDao().getMainJournalById(journalId);
        
        if (journal != null) {
            String existingTags = journal.getTags();
            String updatedTags;
            
            if (existingTags == null || existingTags.trim().isEmpty()) {
                updatedTags = newTags.trim();
            } else {
                // Merge existing and new tags, removing duplicates
                Set<String> tagSet = new HashSet<>();
                tagSet.addAll(Arrays.asList(existingTags.split(TAG_SEPARATOR)));
                tagSet.addAll(Arrays.asList(newTags.split(TAG_SEPARATOR)));
                
                StringBuilder sb = new StringBuilder();
                for (String tag : tagSet) {
                    if (tag != null && !tag.trim().isEmpty()) {
                        if (sb.length() > 0) {
                            sb.append(TAG_SEPARATOR);
                        }
                        sb.append(tag.trim());
                    }
                }
                
                updatedTags = sb.toString();
            }
            
            // Update the journal
            databaseHelper.journalDao().updateJournalTags(journalId, updatedTags);
            
            // Update the saved tags list
            saveTagToPreferences(context, newTags);
        }
    }
    
    /**
     * Remove a tag from a journal
     * 
     * @param context The context
     * @param journalId The journal ID
     * @param tagToRemove The tag to remove
     */
    public static void removeTagFromJournal(Context context, long journalId, String tagToRemove) {
        if (tagToRemove == null || tagToRemove.trim().isEmpty()) {
            return;
        }
        
        DatabaseHelper databaseHelper = DatabaseHelper.getDb(context);
        Journal journal = databaseHelper.journalDao().getMainJournalById(journalId);
        
        if (journal != null) {
            String existingTags = journal.getTags();
            
            if (existingTags != null && !existingTags.trim().isEmpty()) {
                // Remove the tag
                Set<String> tagSet = new HashSet<>(Arrays.asList(existingTags.split(TAG_SEPARATOR)));
                tagSet.remove(tagToRemove.trim());
                
                StringBuilder sb = new StringBuilder();
                for (String tag : tagSet) {
                    if (tag != null && !tag.trim().isEmpty()) {
                        if (sb.length() > 0) {
                            sb.append(TAG_SEPARATOR);
                        }
                        sb.append(tag.trim());
                    }
                }
                
                // Update the journal
                databaseHelper.journalDao().updateJournalTags(journalId, sb.toString());
            }
        }
    }
    
    /**
     * Get all tags for a journal
     * 
     * @param journal The journal
     * @return List of tags
     */
    public static List<String> getTagsForJournal(Journal journal) {
        if (journal == null || journal.getTags() == null || journal.getTags().trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        String[] tags = journal.getTags().split(TAG_SEPARATOR);
        List<String> tagList = new ArrayList<>();
        
        for (String tag : tags) {
            if (tag != null && !tag.trim().isEmpty()) {
                tagList.add(tag.trim());
            }
        }
        
        return tagList;
    }
    
    /**
     * Get all tags used in the app
     * 
     * @param context The context
     * @return Set of all tags
     */
    public static Set<String> getAllTags(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);
        return preferences.getStringSet(TAGS_PREF_KEY, new HashSet<>());
    }
    
    /**
     * Save a tag to the preferences
     * 
     * @param context The context
     * @param tagsString Comma-separated list of tags
     */
    public static void saveTagToPreferences(Context context, String tagsString) {
        if (tagsString == null || tagsString.trim().isEmpty()) {
            return;
        }
        
        SharedPreferences preferences = context.getSharedPreferences(
                ApplicationConstants.MY_APP_NAME, Context.MODE_PRIVATE);
        
        Set<String> existingTags = preferences.getStringSet(TAGS_PREF_KEY, new HashSet<>());
        Set<String> updatedTags = new HashSet<>(existingTags);
        
        String[] newTags = tagsString.split(TAG_SEPARATOR);
        for (String tag : newTags) {
            if (tag != null && !tag.trim().isEmpty()) {
                updatedTags.add(tag.trim());
            }
        }
        
        preferences.edit().putStringSet(TAGS_PREF_KEY, updatedTags).apply();
    }
}
