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

/**
 * Helper class to provide statistics data filtered by month/year
 */
public class StatsMonthlyDataProvider {
    
    public static final int THIS_MONTH = 0;
    public static final int LAST_MONTH = 1;
    public static final int THIS_YEAR = 2;
    public static final int ALL_TIME = 3;
    
    /**
     * Get journal counts by type filtered by selected period
     * @param context Application context
     * @param periodFilter Filter to apply (THIS_MONTH, LAST_MONTH, THIS_YEAR, ALL_TIME)
     * @return Map of journal counts by type
     */
    public static Map<String, Integer> getJournalCountsByPeriod(Context context, int periodFilter) {
        if (context == null) return new HashMap<>();
        
        DatabaseHelper databaseHelper = DatabaseHelper.getDb(context);
        List<Journal> allJournals = databaseHelper.journalDao().getAllJournal();
        
        Date startDate = null;
        Date endDate = Calendar.getInstance().getTime();
        
        Calendar cal = Calendar.getInstance();
        
        // Determine date range based on filter
        switch (periodFilter) {
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
        
        Map<String, Integer> countByType = new HashMap<>();
        countByType.put(ApplicationConstants.REFLECTIVE_JOURNAL, 0);
        countByType.put(ApplicationConstants.GRATITUDE_JOURNAL, 0);
        countByType.put(ApplicationConstants.BULLET_JOURNAL, 0);
        countByType.put(ApplicationConstants.DREAM_JOURNAL, 0);
        
        for (Journal journal : allJournals) {
            // Skip if outside date range
            if (startDate != null && journal.getJournalCreatedOn().before(startDate)) {
                continue;
            }
            if (endDate != null && journal.getJournalCreatedOn().after(endDate)) {
                continue;
            }
            
            String type = journal.getJournalCategory();
            countByType.put(type, countByType.getOrDefault(type, 0) + 1);
        }
        
        return countByType;
    }
    
    /**
     * Get average mood level for a specific period
     * @param context Application context
     * @param periodFilter Filter to apply (THIS_MONTH, LAST_MONTH, THIS_YEAR, ALL_TIME)
     * @return Average mood level or 0 if no data
     */
    public static float getAverageMoodByPeriod(Context context, int periodFilter) {
        if (context == null) return 0;
        
        DatabaseHelper databaseHelper = DatabaseHelper.getDb(context);
        
        Date startDate = null;
        Date endDate = Calendar.getInstance().getTime();
        
        Calendar cal = Calendar.getInstance();
        
        // Determine date range based on filter
        switch (periodFilter) {
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
            default:
                // For mood data, we still need some reasonable time frame, so use the last year
                cal.add(Calendar.YEAR, -1);
                startDate = cal.getTime();
                break;
        }
        
        // Get mood entries for the specified period
        List<MoodTracker> moodEntries = databaseHelper.moodTrackerDao().getAllMoods(startDate, endDate);
        
        if (moodEntries == null || moodEntries.isEmpty()) {
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
     * Get period description for display
     * @param periodFilter Filter to apply (THIS_MONTH, LAST_MONTH, THIS_YEAR, ALL_TIME)
     * @return String describing the period
     */
    public static String getPeriodDescription(int periodFilter) {
        Calendar cal = Calendar.getInstance();
        switch (periodFilter) {
            case THIS_MONTH:
                return getMonthYearString(cal);
            case LAST_MONTH:
                cal.add(Calendar.MONTH, -1);
                return getMonthYearString(cal);
            case THIS_YEAR:
                return String.valueOf(cal.get(Calendar.YEAR));
            case ALL_TIME:
                return "All Time";
            default:
                return "Current Period";
        }
    }
    
    private static String getMonthYearString(Calendar cal) {
        String[] months = {"January", "February", "March", "April", "May", "June", 
                           "July", "August", "September", "October", "November", "December"};
        return months[cal.get(Calendar.MONTH)] + " " + cal.get(Calendar.YEAR);
    }
}
