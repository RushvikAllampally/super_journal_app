package com.diary.superjournalapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.diary.superjournalapp.entity.JournalCategories.GratitudeJournalEntity;

import java.util.List;

@Dao
public interface GratitudeJournalContentDao {

    @Insert
    void insert(GratitudeJournalEntity content);

    @Transaction
    @Query("select * from gratitude_journal_content order by journal_created_on desc")
    public List<GratitudeJournalEntity> getAllGratitudeJournals();

    @Transaction
    @Query("select * from gratitude_journal_content where journalId=:journalId")
    public GratitudeJournalEntity getGratitudeJournalById(long journalId);

    @Update
    void updateGratitudeJournalEntity(GratitudeJournalEntity gratitudeJournalEntity);

    @Delete
    public void deleteJournal(GratitudeJournalEntity gratitudeJournalEntity);
}
