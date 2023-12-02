package com.diary.superjournalapp.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.diary.superjournalapp.entity.MoodTracker;

import java.util.Date;
import java.util.List;

@Dao
public interface MoodTrackerDao {

    @Insert
    long addMood(MoodTracker moodTracker);

    @Update
    public void updateMood(MoodTracker moodTracker);

    @Query("select * from mood_tracker where created_date BETWEEN :moodCreatedDateStart AND :moodCreatedDateEnd order by created_date desc")
    List<MoodTracker> getAllMoods(Date moodCreatedDateStart, Date moodCreatedDateEnd);

    @Query("select * from mood_tracker where mood_date = :moodDate")
    MoodTracker findMoodEntryByMoodDate(String moodDate);

}
