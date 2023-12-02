package com.diary.superjournalapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.diary.superjournalapp.entity.JournalCategories.BulletJournalEntity;

import java.util.List;

@Dao
public interface BulletJournalContentDao {

    @Insert
    void insert(BulletJournalEntity content);

    @Transaction
    @Query("select * from bullet_journal_content order by journal_created_on desc")
    public List<BulletJournalEntity> getAllBulletJournalJournals();

    @Transaction
    @Query("select * from bullet_journal_content where journalId=:journalId")
    public BulletJournalEntity getBulletJournalById(long journalId);

    @Query("SELECT * FROM bullet_journal_content ORDER BY journal_created_on DESC LIMIT 1")
    public BulletJournalEntity getMostRecentBulletJournal();

    @Query("SELECT * FROM bullet_journal_content where is_list_pinned=:isListPinned ORDER BY journal_created_on DESC")
    public List<BulletJournalEntity> getPinnedBulletJournals(boolean isListPinned);

    @Update
    void updateBulletJournalEntity(BulletJournalEntity bulletJournalEntity);

    @Delete
    public void deleteJournal(BulletJournalEntity bulletJournalEntity);

}
