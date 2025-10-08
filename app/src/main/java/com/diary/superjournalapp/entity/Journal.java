package com.diary.superjournalapp.entity;

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
    
    @ColumnInfo(name="is_bookmarked")
    private boolean isBookmarked = false;
    
    // The tags field is no longer used for storage
    // It's kept temporarily for backward compatibility during migration
    @Deprecated
    @ColumnInfo(name="tags")
    private String tags = "";

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
    
    public boolean isBookmarked() {
        return isBookmarked;
    }
    
    public void setBookmarked(boolean bookmarked) {
        isBookmarked = bookmarked;
    }
    
    /**
     * @deprecated Use TagManager.getTagsForJournal(journalId) instead
     * Kept for backward compatibility during migration
     */
    @Deprecated
    public String getTags() {
        return tags;
    }
    
    /**
     * @deprecated Use TagManager.addTagsToJournal(journalId, tags) instead
     * Kept for backward compatibility during migration
     */
    @Deprecated
    public void setTags(String tags) {
        this.tags = tags;
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
                ", isBookmarked=" + isBookmarked +
                ", tags='" + tags + '\'' +
                '}';
    }
}
