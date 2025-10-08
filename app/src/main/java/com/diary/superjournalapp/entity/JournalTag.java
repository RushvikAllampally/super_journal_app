package com.diary.superjournalapp.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

/**
 * Junction entity for the many-to-many relationship between Journal and Tag
 */
@Entity(
    tableName = "journal_tags",
    primaryKeys = {"journalId", "tagId"},
    foreignKeys = {
        @ForeignKey(
            entity = Journal.class,
            parentColumns = "journalId",
            childColumns = "journalId",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = Tag.class,
            parentColumns = "tagId",
            childColumns = "tagId",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {
        @Index("journalId"),
        @Index("tagId")
    }
)
public class JournalTag {
    private long journalId;
    private long tagId;
    
    // Constructor for Room
    public JournalTag() {
    }
    
    // Constructor for convenience
    @Ignore
    public JournalTag(long journalId, long tagId) {
        this.journalId = journalId;
        this.tagId = tagId;
    }
    
    // Getters and setters
    public long getJournalId() {
        return journalId;
    }
    
    public void setJournalId(long journalId) {
        this.journalId = journalId;
    }
    
    public long getTagId() {
        return tagId;
    }
    
    public void setTagId(long tagId) {
        this.tagId = tagId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        JournalTag that = (JournalTag) o;
        return journalId == that.journalId && tagId == that.tagId;
    }
    
    @Override
    public int hashCode() {
        int result = (int) (journalId ^ (journalId >>> 32));
        result = 31 * result + (int) (tagId ^ (tagId >>> 32));
        return result;
    }
}
