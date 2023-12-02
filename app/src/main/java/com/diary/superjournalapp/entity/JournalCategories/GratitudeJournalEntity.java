package com.diary.superjournalapp.entity.JournalCategories;

import androidx.room.Entity;

import com.diary.superjournalapp.entity.Journal;

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
