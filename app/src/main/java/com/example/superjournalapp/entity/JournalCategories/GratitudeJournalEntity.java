package com.example.superjournalapp.entity.JournalCategories;

import androidx.room.Delete;
import androidx.room.Entity;

import com.example.superjournalapp.entity.Journal;
import com.example.superjournalapp.screens.journals.GratitudeJournal;

@Entity(tableName = "gratitude_journal_content")
public class GratitudeJournalEntity extends Journal {

    private String journalContent;

    public String getJournalContent() {
        return journalContent;
    }

    public void setJournalContent(String journalContent) {
        this.journalContent = journalContent;
    }

}
