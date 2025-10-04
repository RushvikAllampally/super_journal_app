package com.diary.superjournalapp.utils;

import android.content.Context;

import com.diary.superjournalapp.constants.ApplicationConstants;
import com.diary.superjournalapp.database.DatabaseHelper;
import com.diary.superjournalapp.entity.Journal;
import com.diary.superjournalapp.entity.MoodTracker;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;

public class StatisticsUtils {
    /**
     * Get the total number of journal entries
     * 
     * @param context The context
     * @return The total number of journal entries
     */
    public static int getTotalJournalCount(Context context) {
        DatabaseHelper databaseHelper = DatabaseHelper.getDb(context);
        return databaseHelper.journalDao().getTotalJournalsCount();
    }
    
    /**
     * Get journal count by type
     * 
     * @param context The context
     * @return Map of journal counts by type
     */
    public static Map<String, Integer> getJournalCountByType(Context context) {
        DatabaseHelper databaseHelper = DatabaseHelper.getDb(context);
        List<Journal> journals = databaseHelper.journalDao().getAllJournal();
        
        Map<String, Integer> countByType = new HashMap<>();
        countByType.put(ApplicationConstants.REFLECTIVE_JOURNAL, 0);
        countByType.put(ApplicationConstants.GRATITUDE_JOURNAL, 0);
        countByType.put(ApplicationConstants.BULLET_JOURNAL, 0);
        countByType.put(ApplicationConstants.DREAM_JOURNAL, 0);
        
        for (Journal journal : journals) {
            String type = journal.getJournalCategory();
            countByType.put(type, countByType.getOrDefault(type, 0) + 1);
        }
        
        return countByType;
    }
    
    /**
     * Get the current streak count
     *
     * @param context The context
     * @return The current streak count
     */
    public static int getCurrentStreak(Context context) {
        return JournalUtils.updateStreakOnLoad(context);
    }
    
    /**
     * Get the average mood level for the past month
     *
     * @param context The context
     * @return The average mood level (1-5) or 0 if no data
     */
    public static float getAverageMoodLevel(Context context) {
        DatabaseHelper databaseHelper = DatabaseHelper.getDb(context);
        
        // Calculate date range for the past month
        Calendar calendarEnd = Calendar.getInstance();
        Date endDate = calendarEnd.getTime();
        
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.add(Calendar.MONTH, -1);
        Date startDate = calendarStart.getTime();
        
        // Get all mood entries from the past month
        List<MoodTracker> moodEntries = databaseHelper.moodTrackerDao().getAllMoods(startDate, endDate);
        
        if (moodEntries.isEmpty()) {
            return 0;
        }
        
        int totalMoodLevel = 0;
        
        for (MoodTracker entry : moodEntries) {
            totalMoodLevel += entry.getMoodLevel();
        }
        
        // Return the average mood level
        return (float) totalMoodLevel / moodEntries.size();
    }
    
    /**
     * Get the number of journal entries in the past week
     *
     * @param context The context
     * @return The number of journal entries in the past week
     */
    public static int getJournalCountLastWeek(Context context) {
        DatabaseHelper databaseHelper = DatabaseHelper.getDb(context);
        List<Journal> journals = databaseHelper.journalDao().getAllJournal();
        
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        Date oneWeekAgo = calendar.getTime();
        
        int count = 0;
        for (Journal journal : journals) {
            if (journal.getJournalCreatedOn().after(oneWeekAgo)) {
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * Get the most productive day of the week based on journal entries
     *
     * @param context The context
     * @return The most productive day (0 = Sunday, 6 = Saturday), or -1 if no data
     */
    public static int getMostProductiveDay(Context context) {
        DatabaseHelper databaseHelper = DatabaseHelper.getDb(context);
        List<Journal> journals = databaseHelper.journalDao().getAllJournal();
        
        if (journals.isEmpty()) {
            return -1;
        }
        
        int[] dayCount = new int[7];
        Calendar calendar = Calendar.getInstance();
        
        for (Journal journal : journals) {
            calendar.setTime(journal.getJournalCreatedOn());
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 0-based
            dayCount[dayOfWeek]++;
        }
        
        int mostProductiveDay = 0;
        for (int i = 1; i < 7; i++) {
            if (dayCount[i] > dayCount[mostProductiveDay]) {
                mostProductiveDay = i;
            }
        }
        
        return mostProductiveDay;
    }
    
    /**
     * Get day of week as string
     * 
     * @param dayOfWeek 0-based day of week (0 = Sunday)
     * @return The day name
     */
    public static String getDayName(int dayOfWeek) {
        switch (dayOfWeek) {
            case 0: return "Sunday";
            case 1: return "Monday";
            case 2: return "Tuesday";
            case 3: return "Wednesday";
            case 4: return "Thursday";
            case 5: return "Friday";
            case 6: return "Saturday";
            default: return "Unknown";
        }
    }
    
    /**
     * Get the average word count for all journal entries
     * 
     * @param context The context
     * @return The average word count, or 0 if no journals
     */
    public static int getAverageWordCount(Context context) {
        DatabaseHelper databaseHelper = DatabaseHelper.getDb(context);
        List<Journal> journals = databaseHelper.journalDao().getAllJournal();
        
        if (journals == null || journals.isEmpty()) {
            return 0;
        }
        
        int totalWords = 0;
        int journalCount = 0;
        
        for (Journal journal : journals) {
            // Get journal content based on type
            String content = journal.getJournalStartText();
            if (content != null && !content.trim().isEmpty()) {
                // Simple word count (just split by whitespace)
                totalWords += content.trim().split("\\s+").length;
                journalCount++;
            }
        }
        
        return journalCount > 0 ? totalWords / journalCount : 0;
    }
}
