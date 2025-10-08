package com.diary.superjournalapp.utils;

import android.content.Context;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.diary.superjournalapp.entity.Journal;
import com.diary.superjournalapp.entity.Tag;
import com.diary.superjournalapp.repository.TagRepository;

/**
 * @deprecated This class is deprecated and will be removed in a future release.
 * Please use {@link TagManager} instead.
 */
@Deprecated
public class TagUtils {

    /**
     * @deprecated Use {@link TagManager#addTagsToJournal(long, String)} instead
     */
    @Deprecated
    public static void addTagsToJournal(Context context, long journalId, String newTags) {
        TagManager tagManager = new TagManager(context);
        tagManager.addTagsToJournal(journalId, newTags);
    }
    
    /**
     * @deprecated Use {@link TagManager} and {@link TagRepository#removeTagFromJournal(long, long)} instead
     */
    @Deprecated
    public static void removeTagFromJournal(Context context, long journalId, String tagToRemove) {
        // Forward to new implementation
        TagManager tagManager = new TagManager(context);
        Tag tag = tagManager.searchTags(tagToRemove).stream()
                .filter(t -> t.getName().equals(tagToRemove))
                .findFirst()
                .orElse(null);
                
        if (tag != null) {
            new TagRepository(context).removeTagFromJournal(journalId, tag.getTagId());
        }
    }
    
    /**
     * @deprecated Use {@link TagManager#getTagsForJournal(Journal)} instead
     */
    @Deprecated
    public static List<String> getTagsForJournal(Journal journal) {
        if (journal == null) {
            return Collections.emptyList();
        }
        
        // This is a compatibility method during migration
        // If using the old field, return the old style tags
        if (journal.getTags() != null && !journal.getTags().isEmpty()) {
            // Old style tag parsing
            return parseOldStyleTags(journal.getTags());
        }
        
        // Otherwise try to use the new system
        try {
            TagManager tagManager = new TagManager(null);
            return tagManager.getTagsForJournal(journal).stream()
                    .map(Tag::getName)
                    .toList();
        } catch (Exception e) {
            // If any error occurs, return empty list
            return Collections.emptyList();
        }
    }
    
    /**
     * @deprecated Use {@link TagManager#getAllTags()} instead
     */
    @Deprecated
    public static Set<String> getAllTags(Context context) {
        // Migration of code paths that call this directly is required
        // This will not work properly in the new system
        return Collections.emptySet();
    }
    
    /**
     * @deprecated Use {@link TagManager} instead
     */
    @Deprecated
    public static void saveTagToPreferences(Context context, String tagsString) {
        // This method is no longer needed as tags are now stored in the database
    }
    
    private static List<String> parseOldStyleTags(String tags) {
        if (tags == null || tags.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        return java.util.Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .toList();
    }
}
