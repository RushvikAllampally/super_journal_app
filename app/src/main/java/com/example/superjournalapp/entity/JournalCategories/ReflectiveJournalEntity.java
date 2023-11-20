package com.example.superjournalapp.entity.JournalCategories;

import androidx.room.Delete;
import androidx.room.Entity;

import com.example.superjournalapp.entity.Journal;

@Entity(tableName = "reflective_journal_content")
public class ReflectiveJournalEntity extends Journal {

    String journalContent;

    public String getJournalContent() {
        return journalContent;
    }

    public void setJournalContent(String journalContent) {
        this.journalContent = journalContent;
    }
}
