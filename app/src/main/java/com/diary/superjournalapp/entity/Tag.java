package com.diary.superjournalapp.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Entity class representing a tag in the journal app
 */
@Entity(tableName = "tags", indices = {@Index(value = {"name"}, unique = true)})
public class Tag {

    @PrimaryKey(autoGenerate = true)
    private long tagId;

    @NonNull
    private String name;

    private String color;
    
    // For tracking frequency of use
    private int usageCount;
    
    // For suggested tags feature
    private boolean isFavorite;

    // Constructors
    public Tag() {
        // Default constructor required by Room
    }

    @Ignore
    public Tag(@NonNull String name) {
        this.name = name;
        this.usageCount = 1;
        this.isFavorite = false;
    }

    // Getters and setters
    public long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
    
    public int getUsageCount() {
        return usageCount;
    }
    
    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
    }
    
    public void incrementUsageCount() {
        this.usageCount++;
    }
    
    public boolean isFavorite() {
        return isFavorite;
    }
    
    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Tag tag = (Tag) o;
        return name.equalsIgnoreCase(tag.name);
    }
    
    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }
}
