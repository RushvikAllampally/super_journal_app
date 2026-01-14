package com.diary.superjournalapp.utils;

import android.content.Context;

import com.diary.superjournalapp.constants.ApplicationConstants;
import com.diary.superjournalapp.database.DatabaseHelper;
import com.diary.superjournalapp.entity.Journal;
import com.diary.superjournalapp.entity.MoodTracker;
import com.diary.superjournalapp.entity.JournalCategories.ReflectiveJournalEntity;
import com.diary.superjournalapp.entity.JournalCategories.GratitudeJournalEntity;
import com.diary.superjournalapp.entity.JournalCategories.DreamJournalEntity;
import com.diary.superjournalapp.entity.JournalCategories.BulletJournalEntity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;

public class StatisticsUtils {
    
    public static final int THIS_MONTH = 0;
    public static final int LAST_MONTH = 1;
    public static final int THIS_YEAR = 2;
    public static final int ALL_TIME = 3;
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
        // Default to past month
        return getAverageMoodLevel(context, THIS_MONTH);
    }
    
    /**
     * Get the average mood level for a specific time period
     *
     * @param context The context
     * @param timePeriod The time period (THIS_MONTH, LAST_MONTH, THIS_YEAR, ALL_TIME)
     * @return The average mood level (1-5) or 0 if no data
     */
    public static float getAverageMoodLevel(Context context, int timePeriod) {
        DatabaseHelper databaseHelper = DatabaseHelper.getDb(context);
        
        Date[] dateRange = getDateRangeForPeriod(timePeriod);
        Date startDate = dateRange[0];
        Date endDate = dateRange[1];
        
        // Get mood entries for the specified period
        List<MoodTracker> moodEntries;
        if (startDate == null) {
            moodEntries = databaseHelper.moodTrackerDao().getAllMoods();
        } else {
            moodEntries = databaseHelper.moodTrackerDao().getAllMoods(startDate, endDate);
        }
        
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
        // Default to all time
        return getMostProductiveDay(context, THIS_MONTH);
    }
    
    /**
     * Get the most productive day of the week based on journal entries for a specific time period
     *
     * @param context The context
     * @param timePeriod The time period (THIS_MONTH, LAST_MONTH, THIS_YEAR, ALL_TIME)
     * @return The most productive day (0 = Sunday, 6 = Saturday), or -1 if no data
     */
    public static int getMostProductiveDay(Context context, int timePeriod) {
        DatabaseHelper databaseHelper = DatabaseHelper.getDb(context);
        List<Journal> journals = databaseHelper.journalDao().getAllJournal();
        
        if (journals.isEmpty()) {
            return -1;
        }
        
        // Get date range
        Date[] dateRange = getDateRangeForPeriod(timePeriod);
        Date startDate = dateRange[0];
        Date endDate = dateRange[1];
        
        int[] dayCount = new int[7];
        Calendar calendar = Calendar.getInstance();
        
        for (Journal journal : journals) {
            // Skip if outside date range
            if (startDate != null && journal.getJournalCreatedOn().before(startDate)) {
                continue;
            }
            if (endDate != null && journal.getJournalCreatedOn().after(endDate)) {
                continue;
            }
            
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
        // Default to current month
        return getAverageWordCount(context, THIS_MONTH);
    }
    
    /**
     * Get the average word count for journal entries in a specific time period
     * 
     * @param context The context
     * @param timePeriod The time period (THIS_MONTH, LAST_MONTH, THIS_YEAR, ALL_TIME)
     * @return The average word count, or 0 if no journals
     */
    public static int getAverageWordCount(Context context, int timePeriod) {
        DatabaseHelper databaseHelper = DatabaseHelper.getDb(context);
        
        // Get date range
        Date[] dateRange = getDateRangeForPeriod(timePeriod);
        Date startDate = dateRange[0];
        Date endDate = dateRange[1];
        
        List<Journal> journals = databaseHelper.journalDao().getAllJournal();
        if (journals == null || journals.isEmpty()) {
            return 0;
        }
        
        int totalWords = 0;
        int journalCount = 0;
        
        for (Journal journal : journals) {
            // Skip if outside date range
            if (startDate != null && journal.getJournalCreatedOn().before(startDate)) {
                continue;
            }
            if (endDate != null && journal.getJournalCreatedOn().after(endDate)) {
                continue;
            }
            
            // Get full journal content based on type
            String content = getFullJournalContent(databaseHelper, journal);
            if (content != null && !content.trim().isEmpty()) {
                // Simple word count (just split by whitespace)
                totalWords += content.trim().split("\\s+").length;
                journalCount++;
            }
        }
        
        return journalCount > 0 ? totalWords / journalCount : 0;
    }
    
    /**
     * Get date range for a specific time period
     * 
     * @param timePeriod The time period constant
     * @return Array with [startDate, endDate]
     */
    public static Date[] getDateRangeForPeriod(int timePeriod) {
        Date startDate = null;
        Date endDate = Calendar.getInstance().getTime();
        
        Calendar cal = Calendar.getInstance();
        
        // Determine date range based on filter
        switch (timePeriod) {
            case THIS_MONTH:
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                startDate = cal.getTime();
                break;
                
            case LAST_MONTH:
                cal.add(Calendar.MONTH, -1);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                startDate = cal.getTime();
                
                cal.add(Calendar.MONTH, 1);
                cal.add(Calendar.MILLISECOND, -1);
                endDate = cal.getTime();
                break;
                
            case THIS_YEAR:
                cal.set(Calendar.MONTH, Calendar.JANUARY);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                startDate = cal.getTime();
                break;
                
            case ALL_TIME:
                startDate = null; // null means all time
                break;
        }
        
        return new Date[]{startDate, endDate};
    }
    
    /**
     * Get the full content of a journal entry based on its type
     * 
     * @param databaseHelper The database helper
     * @param journal The journal entry
     * @return The full content string, or null if not found
     */
    public static String getFullJournalContent(DatabaseHelper databaseHelper, Journal journal) {
        String category = journal.getJournalCategory();
        long journalId = journal.getJournalId();
        
        try {
            switch (category) {
                case ApplicationConstants.REFLECTIVE_JOURNAL:
                    ReflectiveJournalEntity reflectiveJournal = databaseHelper.reflectiveJournalContentDao().getReflectiveJournalById(journalId);
                    return reflectiveJournal != null ? reflectiveJournal.getJournalContent() : journal.getJournalStartText();
                    
                case ApplicationConstants.GRATITUDE_JOURNAL:
                    GratitudeJournalEntity gratitudeJournal = databaseHelper.gratitudeJournalContentDao().getGratitudeJournalById(journalId);
                    return gratitudeJournal != null ? gratitudeJournal.getJournalContent() : journal.getJournalStartText();
                    
                case ApplicationConstants.DREAM_JOURNAL:
                    DreamJournalEntity dreamJournal = databaseHelper.dreamJournalContentDao().getDreamJournalById(journalId);
                    return dreamJournal != null ? dreamJournal.getJournalContent() : journal.getJournalStartText();
                    
                case ApplicationConstants.BULLET_JOURNAL:
                    BulletJournalEntity bulletJournal = databaseHelper.bulletJournalContentDao().getBulletJournalById(journalId);
                    return bulletJournal != null ? bulletJournal.getTaskListJson() : journal.getJournalStartText();
                    
                default:
                    return journal.getJournalStartText();
            }
        } catch (Exception e) {
            // Fallback to journalStartText if there's any error
            return journal.getJournalStartText();
        }
    }
    
    /**
     * Get formatted period name (e.g., "October 2023", "All time")
     * 
     * @param timePeriod The time period constant
     * @return Formatted string representing the period
     */
    public static String getFormattedPeriodName(int timePeriod) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        
        switch (timePeriod) {
            case THIS_MONTH:
                return monthYearFormat.format(cal.getTime());
                
            case LAST_MONTH:
                cal.add(Calendar.MONTH, -1);
                return monthYearFormat.format(cal.getTime());
                
            case THIS_YEAR:
                return String.valueOf(cal.get(Calendar.YEAR));
                
            case ALL_TIME:
                return "All time";
                
            default:
                return "";
        }
    }
}
