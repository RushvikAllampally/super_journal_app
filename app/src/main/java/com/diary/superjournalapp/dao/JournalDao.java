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

}
