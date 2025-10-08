package com.diary.superjournalapp.utils;

import android.content.Context;

import com.diary.superjournalapp.entity.Journal;
import com.diary.superjournalapp.entity.Tag;
import com.diary.superjournalapp.repository.TagRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manager class for tag-related operations
 * This is the main entry point for tag functionality in the app
 */
public class TagManager {

    private static final String TAG_SEPARATOR = ",";
    private static final int SUGGESTED_TAGS_LIMIT = 20;

    private TagRepository tagRepository;
    private Context context;

    /**
     * Constructor
     * @param context Application context
     */
    public TagManager(Context context) {
        this.context = context;
        this.tagRepository = new TagRepository(context);
    }

    /**
     * Add a single tag to a journal
     * @param journalId Journal ID
     * @param tag Tag name
     */
    public void addTagToJournal(long journalId, String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            return;
        }
        tagRepository.addTagToJournal(journalId, tag.trim());
    }

    /**
     * Add multiple tags to a journal (comma-separated)
     * @param journalId Journal ID
     * @param tags Comma-separated list of tags
     */
    public void addTagsToJournal(long journalId, String tags) {
        if (tags == null || tags.trim().isEmpty()) {
            return;
        }
        
        List<String> tagList = Arrays.stream(tags.split(TAG_SEPARATOR))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toList());
        
        tagRepository.addTagsToJournal(journalId, tagList);
    }

    /**
     * Remove a tag from a journal
     * @param journalId Journal ID
     * @param tagId Tag ID
     */
    public void removeTagFromJournal(long journalId, long tagId) {
        tagRepository.removeTagFromJournal(journalId, tagId);
    }

    /**
     * Get all tags for a journal
     * @param journalId Journal ID
     * @return List of tags
     */
    public List<Tag> getTagsForJournal(long journalId) {
        return tagRepository.getTagsForJournal(journalId);
    }
    
    /**
     * Get all tags for a journal entity
     * @param journal Journal entity
     * @return List of tags
     */
    public List<Tag> getTagsForJournal(Journal journal) {
        if (journal == null || journal.getJournalId() == 0) {
            return List.of();
        }
        return getTagsForJournal(journal.getJournalId());
    }

    /**
     * Get suggested tags (combination of most used and favorites)
     * @return List of suggested tags
     */
    public List<Tag> getSuggestedTags() {
        return tagRepository.getMostUsedTags(SUGGESTED_TAGS_LIMIT);
    }

    /**
     * Get all tags in the system
     * @return List of all tags
     */
    public List<Tag> getAllTags() {
        return tagRepository.getAllTags();
    }

    /**
     * Set a tag as favorite
     * @param tagId Tag ID
     * @param isFavorite Whether tag should be favorite
     */
    public void setTagFavorite(long tagId, boolean isFavorite) {
        tagRepository.setTagFavorite(tagId, isFavorite);
    }

    /**
     * Search for tags by name
     * @param query Search query
     * @return List of matching tags
     */
    public List<Tag> searchTags(String query) {
        return tagRepository.searchTags(query);
    }
    
    /**
     * Migrate old tags to new system
     * This is a one-time operation when upgrading
     * @param journal Journal with old-style tags
     */
    public void migrateOldTags(Journal journal) {
        if (journal == null || journal.getJournalId() == 0) {
            return;
        }
        
        String oldTags = journal.getTags();
        if (oldTags == null || oldTags.trim().isEmpty()) {
            return;
        }
        
        // Add old tags to new system
        addTagsToJournal(journal.getJournalId(), oldTags);
    }
}
