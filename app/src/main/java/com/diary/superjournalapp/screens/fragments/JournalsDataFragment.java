package com.diary.superjournalapp.screens.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.constants.ApplicationConstants;
import com.diary.superjournalapp.database.DatabaseHelper;
import com.diary.superjournalapp.entity.Journal;
import com.diary.superjournalapp.entity.MoodTracker;
import com.diary.superjournalapp.utils.StatisticsUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import android.graphics.Color;
import java.util.Collections;
import java.util.Comparator;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A Fragment that shows journal statistics and insights
 */
public class JournalsDataFragment extends Fragment {

    // Overview stats
    private TextView totalEntries;
    private TextView currentStreak;
    private TextView lastWeekEntries;
    private TextView mostProductiveDay;
    private TextView averageMood;
    private TextView averageWordCount;
    private TextView longestStreak;
    private TextView totalWords;
    private TextView bookmarkedCount;
    private TextView mostActiveMonth;
    private TextView favoriteJournalType;
    
    // Charts
    private PieChart journalTypesChart;
    private BarChart moodTrendsChart;
    
    // Spinner
    private android.widget.Spinner moodMonthSpinner;
    private int selectedMoodPeriod = StatisticsUtils.THIS_MONTH;

    public JournalsDataFragment() {
        // Required empty public constructor
    }

    /**
     * Creates a new instance of the fragment
     * 
     * @return A new instance of JournalsDataFragment
     */
    public static JournalsDataFragment newInstance() {
        return new JournalsDataFragment();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_journals_data, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize text views
        totalEntries = view.findViewById(R.id.total_entries);
        currentStreak = view.findViewById(R.id.current_streak);
        lastWeekEntries = view.findViewById(R.id.last_week_entries);
        mostProductiveDay = view.findViewById(R.id.most_productive_day);
        averageMood = view.findViewById(R.id.average_mood);
        averageWordCount = view.findViewById(R.id.average_word_count);
        longestStreak = view.findViewById(R.id.longest_streak);
        totalWords = view.findViewById(R.id.total_words);
        bookmarkedCount = view.findViewById(R.id.bookmarked_count);
        mostActiveMonth = view.findViewById(R.id.most_active_month);
        favoriteJournalType = view.findViewById(R.id.favorite_journal_type);
        
        // Initialize charts
        journalTypesChart = view.findViewById(R.id.journal_types_chart);
        moodTrendsChart = view.findViewById(R.id.mood_trends_chart);
        
        // Initialize spinner
        moodMonthSpinner = view.findViewById(R.id.mood_month_spinner);
        setupMoodMonthSpinner();
        
        // Load all statistics
        loadStatistics();
        setupJournalTypesChart();
        setupMoodTrendsChart();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        loadStatistics();
        setupJournalTypesChart();
        setupMoodTrendsChart();
    }

    /**
     * Load and display text statistics
     */
    private void loadStatistics() {
        if (getContext() == null) return;

        // Get total entries
        int totalEntryCount = StatisticsUtils.getTotalJournalCount(getContext());
        totalEntries.setText(formatNumber(totalEntryCount));

        // Get current streak
        int streak = StatisticsUtils.getCurrentStreak(getContext());
        currentStreak.setText(String.valueOf(streak));

        // Get entries from last week
        int weeklyCount = StatisticsUtils.getJournalCountLastWeek(getContext());
        lastWeekEntries.setText(String.valueOf(weeklyCount));

        // Get most productive day
        int productiveDay = StatisticsUtils.getMostProductiveDay(getContext());
        if (productiveDay >= 0) {
            mostProductiveDay.setText(StatisticsUtils.getDayName(productiveDay));
        } else {
            mostProductiveDay.setText("N/A");
        }

        // Get average mood
        float avgMood = StatisticsUtils.getAverageMoodLevel(getContext());
        if (avgMood > 0) {
            DecimalFormat df = new DecimalFormat("#.#");
            averageMood.setText(df.format(avgMood) + " / 5");
        } else {
            averageMood.setText("N/A");
        }
        
        // Get average word count
        int avgWordCount = StatisticsUtils.getAverageWordCount(getContext());
        if (avgWordCount > 0) {
            averageWordCount.setText(formatNumber(avgWordCount));
        } else {
            averageWordCount.setText("N/A");
        }
        
        // Get longest streak
        int longestStreakCount = getLongestStreak();
        longestStreak.setText(String.valueOf(longestStreakCount));
        
        // Get total words written
        int totalWordsCount = getTotalWordsWritten();
        totalWords.setText(formatNumber(totalWordsCount));
        
        // Get bookmarked entries count
        int bookmarkedCountValue = getBookmarkedCount();
        bookmarkedCount.setText(String.valueOf(bookmarkedCountValue));
        
        // Get most active month
        String mostActiveMonthValue = getMostActiveMonth();
        mostActiveMonth.setText(mostActiveMonthValue);
        
        // Get favorite journal type
        String favoriteType = getFavoriteJournalType();
        favoriteJournalType.setText(favoriteType);
    }
    
    /**
     * Setup the journal types pie chart
     */
    private void setupJournalTypesChart() {
        if (getContext() == null) return;
        
        // Get journal counts by type
        Map<String, Integer> journalCounts = StatisticsUtils.getJournalCountByType(getContext());
        
        // Create pie chart entries
        List<PieEntry> entries = new ArrayList<>();
        
        // Add non-zero entries
        int reflectiveCount = journalCounts.get(ApplicationConstants.REFLECTIVE_JOURNAL);
        if (reflectiveCount > 0) {
            entries.add(new PieEntry(reflectiveCount, "My Diary"));
        }
        
        int gratitudeCount = journalCounts.get(ApplicationConstants.GRATITUDE_JOURNAL);
        if (gratitudeCount > 0) {
            entries.add(new PieEntry(gratitudeCount, "Gratitude"));
        }
        
        int dreamCount = journalCounts.get(ApplicationConstants.DREAM_JOURNAL);
        if (dreamCount > 0) {
            entries.add(new PieEntry(dreamCount, "Dream"));
        }
        
        int bulletCount = journalCounts.get(ApplicationConstants.BULLET_JOURNAL);
        if (bulletCount > 0) {
            entries.add(new PieEntry(bulletCount, "Bullet"));
        }
        
        // If no entries, add placeholder
        if (entries.isEmpty()) {
            entries.add(new PieEntry(1, "No Data"));
        }
        
        PieDataSet dataSet = new PieDataSet(entries, "Journal Types");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(14f);
        
        PieData pieData = new PieData(dataSet);
        
        // Configure chart
        journalTypesChart.setData(pieData);
        journalTypesChart.getDescription().setEnabled(false);
        journalTypesChart.setEntryLabelTextSize(14f);
        journalTypesChart.setHoleRadius(40f);
        journalTypesChart.setTransparentCircleRadius(45f);
        journalTypesChart.animateY(1000);
        journalTypesChart.invalidate();
    }
    
    /**
     * Setup mood month spinner
     */
    private void setupMoodMonthSpinner() {
        if (getContext() == null) return;
        
        android.widget.ArrayAdapter<CharSequence> adapter = android.widget.ArrayAdapter.createFromResource(
            getContext(),
            R.array.time_period_options,
            android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moodMonthSpinner.setAdapter(adapter);
        
        moodMonthSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedMoodPeriod = position;
                setupMoodTrendsChart();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // Do nothing
            }
        });
    }
    
    /**
     * Setup the mood trends bar chart
     */
    private void setupMoodTrendsChart() {
        if (getContext() == null) return;
        
        DatabaseHelper databaseHelper = DatabaseHelper.getDb(getContext());
        
        // Get date range based on selected period
        Date[] dateRange = StatisticsUtils.getDateRangeForPeriod(selectedMoodPeriod);
        Date startDate = dateRange[0];
        Date endDate = dateRange[1];
        
        // Get all mood entries for the selected period
        List<MoodTracker> moodEntries;
        if (startDate == null) {
            moodEntries = databaseHelper.moodTrackerDao().getAllMoods();
        } else {
            moodEntries = databaseHelper.moodTrackerDao().getAllMoods(startDate, endDate);
        }
        
        // Group by mood level (1-5)
        int[] moodCounts = new int[5];
        
        for (MoodTracker entry : moodEntries) {
            int moodLevel = entry.getMoodLevel();
            if (moodLevel >= 1 && moodLevel <= 5) {
                moodCounts[moodLevel - 1]++;
            }
        }
        
        // Create chart data
        ArrayList<BarEntry> entries = new ArrayList<>();
        String[] moodLabels = getResources().getStringArray(R.array.mood_levels);
        
        for (int i = 0; i < 5; i++) {
            entries.add(new BarEntry(i, moodCounts[i]));
        }
        
        BarDataSet dataSet = new BarDataSet(entries, "Mood Frequency");
        
        // Colors representing different moods
        int[] colors = new int[]{
            Color.rgb(220, 53, 69),   // Red (Awful)
            Color.rgb(253, 126, 20),  // Orange (Sad)
            Color.rgb(255, 193, 7),   // Yellow (Good)
            Color.rgb(40, 167, 69),   // Green (Happy)
            Color.rgb(111, 66, 193)   // Purple (Excited)
        };
        
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        
        BarData barData = new BarData(dataSet);
        
        // Configure chart
        moodTrendsChart.setData(barData);
        moodTrendsChart.getDescription().setEnabled(false);
        
        // X-axis styling
        XAxis xAxis = moodTrendsChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(moodLabels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        
        // Y-axis styling
        YAxis leftAxis = moodTrendsChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(1f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });
        
        moodTrendsChart.getAxisRight().setEnabled(false);
        moodTrendsChart.getLegend().setEnabled(false);
        
        // Animate
        moodTrendsChart.animateY(1000);
        moodTrendsChart.invalidate();
    }
    
    /**
     * Get the longest streak ever achieved
     */
    private int getLongestStreak() {
        if (getContext() == null) return 0;
        
        DatabaseHelper databaseHelper = DatabaseHelper.getDb(getContext());
        List<Journal> journals = databaseHelper.journalDao().getAllJournal();
        
        if (journals.isEmpty()) return 0;
        
        // Sort journals by date
        Collections.sort(journals, new Comparator<Journal>() {
            @Override
            public int compare(Journal o1, Journal o2) {
                return o1.getJournalCreatedOn().compareTo(o2.getJournalCreatedOn());
            }
        });
        
        int longestStreak = 0;
        int currentStreak = 1;
        
        Calendar prevCal = Calendar.getInstance();
        prevCal.setTime(journals.get(0).getJournalCreatedOn());
        
        for (int i = 1; i < journals.size(); i++) {
            Calendar currCal = Calendar.getInstance();
            currCal.setTime(journals.get(i).getJournalCreatedOn());
            
            // Check if dates are consecutive days
            int prevDay = prevCal.get(Calendar.DAY_OF_YEAR);
            int prevYear = prevCal.get(Calendar.YEAR);
            int currDay = currCal.get(Calendar.DAY_OF_YEAR);
            int currYear = currCal.get(Calendar.YEAR);
            
            if ((currYear == prevYear && currDay == prevDay + 1) || 
                (currYear == prevYear + 1 && prevCal.get(Calendar.MONTH) == Calendar.DECEMBER && 
                 prevDay == prevCal.getActualMaximum(Calendar.DAY_OF_YEAR) && currDay == 1)) {
                currentStreak++;
            } else if (currYear == prevYear && currDay == prevDay) {
                // Same day, don't break streak
            } else {
                longestStreak = Math.max(longestStreak, currentStreak);
                currentStreak = 1;
            }
            
            prevCal = currCal;
        }
        
        return Math.max(longestStreak, currentStreak);
    }
    
    /**
     * Get total words written across all journals
     */
    private int getTotalWordsWritten() {
        if (getContext() == null) return 0;
        
        DatabaseHelper databaseHelper = DatabaseHelper.getDb(getContext());
        List<Journal> journals = databaseHelper.journalDao().getAllJournal();
        
        int totalWords = 0;
        for (Journal journal : journals) {
            String content = journal.getJournalStartText();
            if (content != null && !content.trim().isEmpty()) {
                totalWords += content.trim().split("\\s+").length;
            }
        }
        
        return totalWords;
    }
    
    /**
     * Get count of bookmarked entries
     */
    private int getBookmarkedCount() {
        if (getContext() == null) return 0;
        
        DatabaseHelper databaseHelper = DatabaseHelper.getDb(getContext());
        List<Journal> journals = databaseHelper.journalDao().getAllJournal();
        
        int count = 0;
        for (Journal journal : journals) {
            if (journal.isBookmarked()) {
                count++;
            }
        }
        
        return count;
    }
    
    /**
     * Get the most active month
     */
    private String getMostActiveMonth() {
        if (getContext() == null) return "N/A";
        
        DatabaseHelper databaseHelper = DatabaseHelper.getDb(getContext());
        List<Journal> journals = databaseHelper.journalDao().getAllJournal();
        
        if (journals.isEmpty()) return "N/A";
        
        // Count entries by month-year
        Map<String, Integer> monthCounts = new java.util.HashMap<>();
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
        
        for (Journal journal : journals) {
            String monthYear = monthYearFormat.format(journal.getJournalCreatedOn());
            monthCounts.put(monthYear, monthCounts.getOrDefault(monthYear, 0) + 1);
        }
        
        // Find month with max entries
        String mostActiveMonth = "N/A";
        int maxCount = 0;
        
        for (Map.Entry<String, Integer> entry : monthCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostActiveMonth = entry.getKey();
            }
        }
        
        return mostActiveMonth;
    }
    
    /**
     * Get favorite journal type (most used)
     */
    private String getFavoriteJournalType() {
        if (getContext() == null) return "N/A";
        
        Map<String, Integer> journalCounts = StatisticsUtils.getJournalCountByType(getContext());
        
        String favoriteType = "N/A";
        int maxCount = 0;
        
        for (Map.Entry<String, Integer> entry : journalCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                String type = entry.getKey();
                
                // Convert type to friendly name
                switch (type) {
                    case ApplicationConstants.REFLECTIVE_JOURNAL:
                        favoriteType = "My Diary";
                        break;
                    case ApplicationConstants.GRATITUDE_JOURNAL:
                        favoriteType = "Gratitude";
                        break;
                    case ApplicationConstants.DREAM_JOURNAL:
                        favoriteType = "Dream";
                        break;
                    case ApplicationConstants.BULLET_JOURNAL:
                        favoriteType = "Bullet";
                        break;
                    default:
                        favoriteType = type;
                }
            }
        }
        
        return maxCount > 0 ? favoriteType : "N/A";
    }
    
    /**
     * Format large numbers with k suffix for better display
     * Examples: 1234 -> "1.2k", 999 -> "999", 10000 -> "10k"
     */
    private String formatNumber(int number) {
        if (number >= 10000) {
            // For numbers >= 10k, show without decimal (e.g., "10k")
            return String.format(Locale.getDefault(), "%dk", number / 1000);
        } else if (number >= 1000) {
            // For numbers >= 1k, show one decimal (e.g., "1.2k")
            return String.format(Locale.getDefault(), "%.1fk", number / 1000.0);
        } else {
            // For numbers < 1k, show as-is
            return String.valueOf(number);
        }
    }
}