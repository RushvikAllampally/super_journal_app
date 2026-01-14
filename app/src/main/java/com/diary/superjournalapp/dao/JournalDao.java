package com.diary.superjournalapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.diary.superjournalapp.entity.Journal;

import java.util.Date;
import java.util.List;


@Dao
public interface JournalDao {

    @Transaction
    @Query("select * from journals order by journal_created_on desc")
    public List<Journal> getAllJournal();

    @Transaction
    @Query("select * from journals where journal_created_on BETWEEN :journalCreatedDateStart AND :journalCreatedDateEnd AND journal_category LIKE '%' || :category || '%' order by journal_created_on desc")
    public List<Journal> getAllJournalsByDateAndCategory(Date journalCreatedDateStart, Date journalCreatedDateEnd, String category);

    @Insert
    long addJournal(Journal journal);

    @Update
    void updateJournal(Journal journal);

    @Transaction
    @Query("select * from journals where journalId=:journalId")
    public Journal getMainJournalById(long journalId);

    @Delete
    void deleteJournal(Journal journal);

    @Query("select count(*) from journals")
    public int getTotalJournalsCount();
    
    @Transaction
    @Query("select * from journals where is_bookmarked = 1 order by journal_created_on desc")
    public List<Journal> getBookmarkedJournals();
    
    @Query("update journals set is_bookmarked = :isBookmarked where journalId = :journalId")
    void updateBookmarkStatus(long journalId, boolean isBookmarked);
    
    @Query("update journals set is_locked = :isLocked where journalId = :journalId")
    void updateLockStatus(long journalId, boolean isLocked);
    
    @Transaction
    @Query("select * from journals where is_locked = 1 order by journal_created_on desc")
    public List<Journal> getLockedJournals();
    
    /**
     * @deprecated This method is deprecated. Use TagRepository.addTagsToJournal() instead
     * This is kept for API compatibility but doesn't actually update tags anymore
     */
    @Deprecated
    default void updateJournalTags(long journalId, String tags) {
        // Tags are now stored in a separate table, this method is kept for API compatibility
        // but does nothing. Use TagRepository methods instead.
    }
    
    /**
     * @deprecated This method is deprecated. Use TagDao.getJournalsWithTag() instead
     * This is kept for API compatibility but returns an empty list
     */
    @Deprecated
    @Transaction
    default List<Journal> getJournalsByTag(String tag) {
        // Tags are now stored in a separate table, this method is kept for API compatibility
        // Use TagRepository.getJournalsWithTagName() instead
        return List.of();
    }
    
    /**
     * Get journals created in the last specified number of days
     * 
     * @param days Number of days to look back
     * @return List of journals from the last N days
     */
    @Transaction
    @Query("select * from journals where journal_created_on >= datetime('now', '-' || :days || ' days') order by journal_created_on desc")
    public List<Journal> getJournalsInLastDays(int days);
    
    /**
     * Get journals that have all the specified tags
     * This uses the new tag system with JournalTag junction table
     * 
     * @param tagIds List of tag IDs to search for
     * @param tagCount Number of tags that must be matched (all of them)
     * @return List of journals that have all the specified tags
     */
    @Transaction
    @Query("SELECT j.* FROM journals j WHERE "
          + "(SELECT COUNT(DISTINCT jt.tagId) FROM journal_tags jt "
          + "WHERE jt.journalId = j.journalId AND jt.tagId IN (:tagIds)) = :tagCount "
          + "ORDER BY j.journal_created_on DESC")
    public List<Journal> getJournalsWithAllTags(List<Long> tagIds, int tagCount);
    
    /**
     * Get journal count grouped by date for a specific month
     * Used for calendar heatmap visualization
     * 
     * @param monthStart Start date of the month
     * @param monthEnd End date of the month
     * @return Map of date string (YYYY-MM-DD) to journal count
     */
    @Query("SELECT DATE(journal_created_on / 1000, 'unixepoch') as date, COUNT(*) as count "
          + "FROM journals "
          + "WHERE journal_created_on BETWEEN :monthStart AND :monthEnd "
          + "GROUP BY DATE(journal_created_on / 1000, 'unixepoch')")
    public List<JournalCountByDate> getJournalCountsByDate(Date monthStart, Date monthEnd);
    
    /**
     * Get journals grouped by date and category for a specific month
     * Used for showing category-specific event dots on calendar
     * 
     * @param monthStart Start date of the month
     * @param monthEnd End date of the month
     * @return List of journals in the month
     */
    @Transaction
    @Query("SELECT * FROM journals "
          + "WHERE journal_created_on BETWEEN :monthStart AND :monthEnd "
          + "ORDER BY journal_created_on DESC")
    public List<Journal> getJournalsForMonth(Date monthStart, Date monthEnd);
    
    /**
     * Get all distinct dates that have journal entries
     * Used for calculating consistency streaks
     * 
     * @return List of dates with journals
     */
    @Query("SELECT DISTINCT DATE(journal_created_on / 1000, 'unixepoch') as date "
          + "FROM journals "
          + "ORDER BY date DESC")
    public List<String> getAllJournalDates();
    
    /**
     * Inner class for journal count by date query result
     */
    class JournalCountByDate {
        public String date;
        public int count;
    }
}
