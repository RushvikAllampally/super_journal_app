package com.example.superjournalapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.superjournalapp.entity.JournalCategories.DreamJournalEntity;
import com.example.superjournalapp.entity.JournalCategories.GratitudeJournalEntity;
import com.example.superjournalapp.entity.JournalCategories.ReflectiveJournalEntity;

import java.util.List;

@Dao
public interface DreamJournalContentDao {

    @Insert
    void insert(DreamJournalEntity content);

    @Transaction
    @Query("select * from dream_journal_content order by journal_created_on desc")
    public List<DreamJournalEntity> getAllDreamJournals();

    @Transaction
    @Query("select * from dream_journal_content where journalId=:journalId")
    public DreamJournalEntity getDreamJournalById(long journalId);

    @Update
    void updateDreamJournalEntity(DreamJournalEntity dreamJournalEntity);

    @Delete
    public void deleteJournal(DreamJournalEntity dreamJournalEntity);

}
