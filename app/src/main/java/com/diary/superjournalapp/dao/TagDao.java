package com.diary.superjournalapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.diary.superjournalapp.entity.Journal;
import com.diary.superjournalapp.entity.JournalTag;
import com.diary.superjournalapp.entity.Tag;

import java.util.List;

/**
 * Data Access Object for Tag-related operations
 */
@Dao
public interface TagDao {
    
    /**
     * Insert a new tag
     * If tag with same name exists, ignore
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertTag(Tag tag);
    
    /**
     * Update an existing tag
     */
    @Update
    void updateTag(Tag tag);
    
    /**
     * Delete a tag
     */
    @Delete
    void deleteTag(Tag tag);
    
    /**
     * Get a tag by ID
     */
    @Query("SELECT * FROM tags WHERE tagId = :tagId")
    Tag getTagById(long tagId);
    
    /**
     * Get a tag by name
     */
    @Query("SELECT * FROM tags WHERE name = :name")
    Tag getTagByName(String name);
    
    /**
     * Get all tags
     */
    @Query("SELECT * FROM tags ORDER BY usageCount DESC")
    List<Tag> getAllTags();
    
    /**
     * Get favorite tags
     */
    @Query("SELECT * FROM tags WHERE isFavorite = 1 ORDER BY usageCount DESC")
    List<Tag> getFavoriteTags();
    
    /**
     * Get most used tags (for suggestions)
     */
    @Query("SELECT * FROM tags ORDER BY usageCount DESC LIMIT :limit")
    List<Tag> getMostUsedTags(int limit);
    
    /**
     * Search tags by name
     */
    @Query("SELECT * FROM tags WHERE name LIKE '%' || :query || '%' ORDER BY usageCount DESC")
    List<Tag> searchTags(String query);
    
    /**
     * Insert journal-tag relationship
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertJournalTag(JournalTag journalTag);
    
    /**
     * Delete journal-tag relationship
     */
    @Delete
    void deleteJournalTag(JournalTag journalTag);
    
    /**
     * Delete all tags for a journal
     */
    @Query("DELETE FROM journal_tags WHERE journalId = :journalId")
    void deleteAllTagsForJournal(long journalId);
    
    /**
     * Get all tags for a journal
     */
    @Query("SELECT t.* FROM tags t INNER JOIN journal_tags jt ON t.tagId = jt.tagId WHERE jt.journalId = :journalId ORDER BY t.name")
    List<Tag> getTagsForJournal(long journalId);
    
    /**
     * Get all journals with a specific tag
     */
    @Query("SELECT j.* FROM journals j INNER JOIN journal_tags jt ON j.journalId = jt.journalId WHERE jt.tagId = :tagId ORDER BY j.journal_created_on DESC")
    List<Journal> getJournalsWithTag(long tagId);
    
    /**
     * Increment the usage count of a tag
     */
    @Query("UPDATE tags SET usageCount = usageCount + 1 WHERE tagId = :tagId")
    void incrementTagUsageCount(long tagId);
    
    /**
     * Check if a journal has a specific tag
     */
    @Query("SELECT COUNT(*) FROM journal_tags WHERE journalId = :journalId AND tagId = :tagId")
    int hasTag(long journalId, long tagId);
    
    /**
     * Get the count of journals using a tag
     */
    @Query("SELECT COUNT(*) FROM journal_tags WHERE tagId = :tagId")
    int getJournalCountForTag(long tagId);
    
    /**
     * Toggle favorite status of a tag
     */
    @Query("UPDATE tags SET isFavorite = :isFavorite WHERE tagId = :tagId")
    void setTagFavorite(long tagId, boolean isFavorite);
}
