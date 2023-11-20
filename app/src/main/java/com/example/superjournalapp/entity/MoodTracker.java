package com.example.superjournalapp.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "mood_tracker")
public class MoodTracker {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "mood_date")
    private String moodDate;  //DDMMYYYY - string in this form

    @ColumnInfo(name = "created_date")
    private Date createdDate;

    @ColumnInfo(name = "mood_level")
    private int moodLevel; //1 being least and 5 highest and good

    @ColumnInfo(name = "reason")
    private String reasonForTheMood; // here i not the reason for mood and will show it in analytics

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMoodDate() {
        return moodDate;
    }

    public void setMoodDate(String moodDate) {
        this.moodDate = moodDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public int getMoodLevel() {
        return moodLevel;
    }

    public void setMoodLevel(int moodLevel) {
        this.moodLevel = moodLevel;
    }

    public String getReasonForTheMood() {
        return reasonForTheMood;
    }

    public void setReasonForTheMood(String reasonForTheMood) {
        this.reasonForTheMood = reasonForTheMood;
    }
}
