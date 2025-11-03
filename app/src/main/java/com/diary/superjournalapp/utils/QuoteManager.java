package com.diary.superjournalapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.diary.superjournalapp.constants.ApplicationConstants;
import com.diary.superjournalapp.dto.QuoteDto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

/**
 * Manages quotes and affirmations with smart tracking:
 * - Same quote/affirmation shown throughout the day
 * - Never repeats until all are used
 * - Automatic reset when exhausted
 */
public class QuoteManager {
    
    private static final String PREF_NAME = "QuoteManagerPrefs";
    private static final String KEY_CURRENT_DATE = "current_date";
    private static final String KEY_DAILY_QUOTE_INDEX = "daily_quote_index";
    private static final String KEY_DAILY_AFFIRMATION_INDEX = "daily_affirmation_index";
    private static final String KEY_USED_QUOTES = "used_quotes";
    private static final String KEY_USED_AFFIRMATIONS = "used_affirmations";
    
    private final SharedPreferences prefs;
    private final SimpleDateFormat dateFormat;
    
    public QuoteManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }
    
    /**
     * Get the quote of the day - same quote throughout the day
     * @return Quote for today
     */
    public QuoteDto getQuoteOfTheDay() {
        String today = getTodayDate();
        String savedDate = prefs.getString(KEY_CURRENT_DATE, "");
        
        // Check if it's a new day
        if (!today.equals(savedDate)) {
            // New day - select a new quote
            int newQuoteIndex = getNextUnusedQuoteIndex();
            prefs.edit()
                    .putString(KEY_CURRENT_DATE, today)
                    .putInt(KEY_DAILY_QUOTE_INDEX, newQuoteIndex)
                    .apply();
            return ApplicationConstants.QUOTES_ARRAY.get(newQuoteIndex);
        } else {
            // Same day - return saved quote
            int savedIndex = prefs.getInt(KEY_DAILY_QUOTE_INDEX, 0);
            int totalQuotes = ApplicationConstants.QUOTES_ARRAY.size();
            if (savedIndex < 0 || savedIndex >= totalQuotes) {
                // Stored index is out of range (likely after a quotes list update) – pick a new valid one
                int newQuoteIndex = getNextUnusedQuoteIndex();
                prefs.edit().putInt(KEY_DAILY_QUOTE_INDEX, newQuoteIndex).apply();
                return ApplicationConstants.QUOTES_ARRAY.get(newQuoteIndex);
            }
            return ApplicationConstants.QUOTES_ARRAY.get(savedIndex);
        }
    }
    
    /**
     * Get the affirmation of the day - same affirmation throughout the day
     * @return Affirmation for today
     */
    public String getAffirmationOfTheDay() {
        String today = getTodayDate();
        String savedDate = prefs.getString(KEY_CURRENT_DATE, "");
        
        // Check if it's a new day
        if (!today.equals(savedDate)) {
            // New day - select a new affirmation
            int newAffirmationIndex = getNextUnusedAffirmationIndex();
            prefs.edit()
                    .putString(KEY_CURRENT_DATE, today)
                    .putInt(KEY_DAILY_AFFIRMATION_INDEX, newAffirmationIndex)
                    .apply();
            return ApplicationConstants.AFFIRMATIONS[newAffirmationIndex];
        } else {
            // Same day - return saved affirmation
            int savedIndex = prefs.getInt(KEY_DAILY_AFFIRMATION_INDEX, 0);
            return ApplicationConstants.AFFIRMATIONS[savedIndex];
        }
    }
    
    /**
     * Get next unused quote index. Never repeats until all quotes are used.
     * @return Index of an unused quote
     */
    private int getNextUnusedQuoteIndex() {
        Set<String> usedQuotes = prefs.getStringSet(KEY_USED_QUOTES, new HashSet<>());
        int totalQuotes = ApplicationConstants.QUOTES_ARRAY.size();
        
        // If all quotes have been used, reset
        if (usedQuotes.size() >= totalQuotes) {
            usedQuotes.clear();
        }
        
        // Find unused quotes
        List<Integer> unusedIndexes = new ArrayList<>();
        for (int i = 0; i < totalQuotes; i++) {
            if (!usedQuotes.contains(String.valueOf(i))) {
                unusedIndexes.add(i);
            }
        }
        
        // Select random from unused
        int selectedIndex = unusedIndexes.get(new Random().nextInt(unusedIndexes.size()));
        
        // Mark as used
        usedQuotes.add(String.valueOf(selectedIndex));
        prefs.edit().putStringSet(KEY_USED_QUOTES, usedQuotes).apply();
        
        return selectedIndex;
    }
    
    /**
     * Get next unused affirmation index. Never repeats until all affirmations are used.
     * @return Index of an unused affirmation
     */
    private int getNextUnusedAffirmationIndex() {
        Set<String> usedAffirmations = prefs.getStringSet(KEY_USED_AFFIRMATIONS, new HashSet<>());
        int totalAffirmations = ApplicationConstants.AFFIRMATIONS.length;
        
        // If all affirmations have been used, reset
        if (usedAffirmations.size() >= totalAffirmations) {
            usedAffirmations.clear();
        }
        
        // Find unused affirmations
        List<Integer> unusedIndexes = new ArrayList<>();
        for (int i = 0; i < totalAffirmations; i++) {
            if (!usedAffirmations.contains(String.valueOf(i))) {
                unusedIndexes.add(i);
            }
        }
        
        // Select random from unused
        int selectedIndex = unusedIndexes.get(new Random().nextInt(unusedIndexes.size()));
        
        // Mark as used
        usedAffirmations.add(String.valueOf(selectedIndex));
        prefs.edit().putStringSet(KEY_USED_AFFIRMATIONS, usedAffirmations).apply();
        
        return selectedIndex;
    }
    
    /**
     * Get today's date as a string
     * @return Today's date in yyyy-MM-dd format
     */
    private String getTodayDate() {
        return dateFormat.format(new Date());
    }
    
    /**
     * Get statistics about quote usage
     * @return String with usage stats
     */
    public String getQuoteStats() {
        Set<String> usedQuotes = prefs.getStringSet(KEY_USED_QUOTES, new HashSet<>());
        int total = ApplicationConstants.QUOTES_ARRAY.size();
        int used = usedQuotes.size();
        return String.format(Locale.getDefault(), "Quotes: %d/%d used", used, total);
    }
    
    /**
     * Get statistics about affirmation usage
     * @return String with usage stats
     */
    public String getAffirmationStats() {
        Set<String> usedAffirmations = prefs.getStringSet(KEY_USED_AFFIRMATIONS, new HashSet<>());
        int total = ApplicationConstants.AFFIRMATIONS.length;
        int used = usedAffirmations.size();
        return String.format(Locale.getDefault(), "Affirmations: %d/%d used", used, total);
    }
    
    /**
     * Reset all usage tracking (for testing or user preference)
     */
    public void resetAllUsage() {
        prefs.edit()
                .remove(KEY_USED_QUOTES)
                .remove(KEY_USED_AFFIRMATIONS)
                .apply();
    }
}
