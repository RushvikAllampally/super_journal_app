package com.example.superjournalapp.entity.JournalCategories;

import androidx.room.Entity;

import com.example.superjournalapp.entity.Journal;

@Entity(tableName = "dream_journal_content")
public class DreamJournalEntity extends Journal {

    private String journalContent;

    public String getJournalContent() {
        return journalContent;
    }

    public void setJournalContent(String journalContent) {
        this.journalContent = journalContent;
    }
}
