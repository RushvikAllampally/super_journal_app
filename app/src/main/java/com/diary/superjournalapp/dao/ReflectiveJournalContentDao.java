package com.diary.superjournalapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.diary.superjournalapp.entity.JournalCategories.ReflectiveJournalEntity;

import java.util.List;

@Dao
public interface ReflectiveJournalContentDao {

    @Insert
    void insert(ReflectiveJournalEntity content);

    @Transaction
    @Query("select * from reflective_journal_content order by journal_created_on desc")
    public List<ReflectiveJournalEntity> getAllReflectiveJournals();

    @Transaction
    @Query("select * from reflective_journal_content where journalId=:journalId")
    public ReflectiveJournalEntity getReflectiveJournalById(long journalId);

    @Update
    void updateReflectiveJournalEntity(ReflectiveJournalEntity reflectiveJournal);

    @Delete
    public void deleteJournal(ReflectiveJournalEntity reflectiveJournalEntity);
}
