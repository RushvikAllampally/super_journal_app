package com.example.superjournalapp.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "journals")
public class Journal {

    @PrimaryKey(autoGenerate = true)
    private long journalId;
    @ColumnInfo(name = "journal_created_on")
    private Date journalCreatedOn;

    private String title;

    private String images;

    private String journalStartText;

    @ColumnInfo(name="journal_category")
    private String journalCategory;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getJournalStartText() {
        return journalStartText;
    }

    public void setJournalStartText(String journalStartText) {
        this.journalStartText = journalStartText;
    }

    public long getJournalId() {
        return journalId;
    }

    public void setJournalId(long journalId) {
        this.journalId = journalId;
    }

    public Date getJournalCreatedOn() {
        return journalCreatedOn;
    }

    public void setJournalCreatedOn(Date journalCreatedOn) {
        this.journalCreatedOn = journalCreatedOn;
    }

    public String getJournalCategory() {
        return journalCategory;
    }

    public void setJournalCategory(String journalCategory) {
        this.journalCategory = journalCategory;
    }

    @Override
    public String toString() {
        return "Journal{" +
                "journalId=" + journalId +
                ", journalCreatedOn=" + journalCreatedOn +
                ", title='" + title + '\'' +
                ", images='" + images + '\'' +
                ", journalStartText='" + journalStartText + '\'' +
                ", journalCategory='" + journalCategory + '\'' +
                '}';
    }
}
