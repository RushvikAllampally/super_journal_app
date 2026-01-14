package com.diary.superjournalapp.repository;

import android.content.Context;

import com.diary.superjournalapp.database.DatabaseHelper;
import com.diary.superjournalapp.entity.Journal;
import com.diary.superjournalapp.entity.JournalTag;
import com.diary.superjournalapp.entity.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for tag operations, providing a clean API between data sources and UI
 */
public class TagRepository {
    
    private DatabaseHelper database;

    public TagRepository(Context context) {
        database = DatabaseHelper.getDb(context);
    }
    
    /**
     * Create a new tag or get existing one
     * @param name Tag name
     * @return Tag ID
     */
    public long createTag(String name) {
        if (name == null || name.trim().isEmpty()) {
            return -1;
        }
        
        // Check if tag already exists
        Tag existingTag = database.tagDao().getTagByName(name.trim());
        if (existingTag != null) {
            // Increment usage count of existing tag
            database.tagDao().incrementTagUsageCount(existingTag.getTagId());
            return existingTag.getTagId();
        }
        
        // Create new tag
        Tag newTag = new Tag(name.trim());
        return database.tagDao().insertTag(newTag);
    }
    
    /**
     * Add a tag to a journal
     * @param journalId Journal ID
     * @param tagName Tag name
     */
    public void addTagToJournal(long journalId, String tagName) {
        if (tagName == null || tagName.trim().isEmpty()) {
            return;
        }
        
        long tagId = createTag(tagName.trim());
        if (tagId == -1) {
            return;
        }
        
        // Check if journal already has this tag
        if (database.tagDao().hasTag(journalId, tagId) > 0) {
            return;
        }
        
        // Add tag to journal
        JournalTag journalTag = new JournalTag(journalId, tagId);
        database.tagDao().insertJournalTag(journalTag);
    }
    
    /**
     * Add multiple tags to a journal
     * @param journalId Journal ID
     * @param tagNames List of tag names
     */
    public void addTagsToJournal(long journalId, List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return;
        }
        
        for (String tagName : tagNames) {
            addTagToJournal(journalId, tagName);
        }
    }
    
    /**
     * Remove a tag from a journal
     * @param journalId Journal ID
     * @param tagId Tag ID
     */
    public void removeTagFromJournal(long journalId, long tagId) {
        JournalTag journalTag = new JournalTag(journalId, tagId);
        database.tagDao().deleteJournalTag(journalTag);
    }
    
    /**
     * Remove all tags from a journal
     * @param journalId Journal ID
     */
    public void removeAllTagsFromJournal(long journalId) {
        database.tagDao().deleteAllTagsForJournal(journalId);
    }
    
    /**
     * Get all tags for a journal
     * @param journalId Journal ID
     * @return List of tags
     */
    public List<Tag> getTagsForJournal(long journalId) {
        return database.tagDao().getTagsForJournal(journalId);
    }
    
    /**
     * Get all journals with a specific tag
     * @param tagId Tag ID
     * @return List of journals
     */
    public List<Journal> getJournalsWithTag(long tagId) {
        return database.tagDao().getJournalsWithTag(tagId);
    }
    
    /**
     * Get all journals with a specific tag name
     * @param tagName Tag name
     * @return List of journals
     */
    public List<Journal> getJournalsWithTagName(String tagName) {
        Tag tag = database.tagDao().getTagByName(tagName);
        if (tag == null) {
            return new ArrayList<>();
        }
        return getJournalsWithTag(tag.getTagId());
    }
    
    /**
     * Get all tags
     * @return List of all tags
     */
    public List<Tag> getAllTags() {
        return database.tagDao().getAllTags();
    }
    
    /**
     * Get favorite tags
     * @return List of favorite tags
     */
    public List<Tag> getFavoriteTags() {
        return database.tagDao().getFavoriteTags();
    }
    
    /**
     * Get most used tags (for suggestions)
     * @param limit Maximum number of tags to return
     * @return List of most used tags
     */
    public List<Tag> getMostUsedTags(int limit) {
        return database.tagDao().getMostUsedTags(limit);
    }
    
    /**
     * Toggle favorite status of a tag
     * @param tagId Tag ID
     * @param isFavorite New favorite status
     */
    public void setTagFavorite(long tagId, boolean isFavorite) {
        database.tagDao().setTagFavorite(tagId, isFavorite);
    }
    
    /**
     * Delete a tag completely
     * @param tagId Tag ID
     */
    public void deleteTag(long tagId) {
        Tag tag = database.tagDao().getTagById(tagId);
        if (tag != null) {
            database.tagDao().deleteTag(tag);
        }
    }
    
    /**
     * Update a tag's name
     * @param tag Tag to update
     * @return true if successful, false otherwise
     */
    public boolean updateTag(Tag tag) {
        if (tag == null || tag.getName() == null || tag.getName().trim().isEmpty()) {
            return false;
        }
        
        try {
            // Normalize the name
            tag.setName(tag.getName().trim());
            
            // Update the tag
            database.tagDao().updateTag(tag);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Search tags by name
     * @param query Search query
     * @return List of matching tags
     */
    public List<Tag> searchTags(String query) {
        return database.tagDao().searchTags(query);
    }
}
