package com.diary.superjournalapp.screens.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.constants.ApplicationConstants;
import com.diary.superjournalapp.database.DatabaseHelper;
import com.diary.superjournalapp.entity.Journal;
import com.diary.superjournalapp.entity.MoodTracker;
import com.diary.superjournalapp.utils.StatisticsUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatisticsFragmentImproved extends Fragment {

    private TextView totalEntries;
    private TextView currentStreak;
    private TextView lastWeekEntries;
    private TextView mostProductiveDay;
    private TextView reflectiveCount;
    private TextView gratitudeCount;
    private TextView dreamCount;
    private TextView bulletCount;
    private TextView averageMood;
    private TextView moodEmoji;
    private TextView averageWords;
    private TextView statsPeriodText;
    private Spinner journalTypesPeriodSpinner;
    private BarChart weeklyChart;
    private BarChart moodChart;

    // Period filters
    private static final int THIS_MONTH = 0;
    private static final int LAST_MONTH = 1;
    private static final int THIS_YEAR = 2;
    private static final int ALL_TIME = 3;

    // Current filter states
    private int currentJournalTypesFilter = THIS_MONTH;

    public StatisticsFragmentImproved() {
        // Required empty public constructor
    }

    public static StatisticsFragmentImproved newInstance() {
        return new StatisticsFragmentImproved();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics_improved, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize TextViews
        initializeViews(view);
        setupSpinner(view);
        setupCharts(view);

        // Load statistics
        loadStatistics();
    }

    private void initializeViews(View view) {
        totalEntries = view.findViewById(R.id.total_entries);
        currentStreak = view.findViewById(R.id.current_streak);
        lastWeekEntries = view.findViewById(R.id.last_week_entries);
        mostProductiveDay = view.findViewById(R.id.most_productive_day);
        reflectiveCount = view.findViewById(R.id.reflective_count);
        gratitudeCount = view.findViewById(R.id.gratitude_count);
        dreamCount = view.findViewById(R.id.dream_count);
        bulletCount = view.findViewById(R.id.bullet_count);
        averageMood = view.findViewById(R.id.average_mood);
        moodEmoji = view.findViewById(R.id.mood_emoji);
        averageWords = view.findViewById(R.id.average_words);
        statsPeriodText = view.findViewById(R.id.stats_period_text);
        journalTypesPeriodSpinner = view.findViewById(R.id.journal_types_period_spinner);
        weeklyChart = view.findViewById(R.id.weekly_chart);
        moodChart = view.findViewById(R.id.mood_chart);
        
        // Set current month/year
        Calendar cal = Calendar.getInstance();
        String monthName = new DateFormatSymbols().getMonths()[cal.get(Calendar.MONTH)];
        int year = cal.get(Calendar.YEAR);
        statsPeriodText.setText(monthName + " " + year);
    }

    private void setupSpinner(View view) {
        journalTypesPeriodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentJournalTypesFilter = position;
                loadJournalTypeCounts();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupCharts(View view) {
        // Configure weekly chart
        configureWeeklyChart();
        
        // Configure mood chart
        configureMoodChart();
    }

    private void configureWeeklyChart() {
        weeklyChart.getDescription().setEnabled(false);
        weeklyChart.setDrawGridBackground(false);
        weeklyChart.setPinchZoom(false);
        weeklyChart.setScaleEnabled(false);
        weeklyChart.setDrawBarShadow(false);
        weeklyChart.setDrawValueAboveBar(true);
        weeklyChart.setHighlightFullBarEnabled(false);
        
        XAxis xAxis = weeklyChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(getTextColor());
        
        YAxis leftAxis = weeklyChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextColor(getTextColor());
        leftAxis.setGranularity(1f);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });
        
        weeklyChart.getAxisRight().setEnabled(false);
        weeklyChart.getLegend().setEnabled(false);
    }

    private void configureMoodChart() {
        moodChart.getDescription().setEnabled(false);
        moodChart.setDrawGridBackground(false);
        moodChart.setPinchZoom(false);
        moodChart.setScaleEnabled(false);
        moodChart.setDrawBarShadow(false);
        moodChart.setDrawValueAboveBar(true);
        moodChart.setHighlightFullBarEnabled(false);
        
        XAxis xAxis = moodChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(getTextColor());
        
        YAxis leftAxis = moodChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextColor(getTextColor());
        leftAxis.setAxisMaximum(5f);
        leftAxis.setGranularity(1f);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });
        
        moodChart.getAxisRight().setEnabled(false);
        
        Legend legend = moodChart.getLegend();
        legend.setTextColor(getTextColor());
        legend.setForm(Legend.LegendForm.CIRCLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh statistics when returning to this fragment
        loadStatistics();
    }

    /**
     * Load and display all statistics
     */
    private void loadStatistics() {
        if (getContext() == null) return;

        // Get total entries
        int totalEntryCount = StatisticsUtils.getTotalJournalCount(getContext());
        totalEntries.setText(String.valueOf(totalEntryCount));

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
        
        // Get average words per entry
        int avgWords = StatisticsUtils.getAverageWordCount(getContext());
        averageWords.setText(String.valueOf(avgWords));

        // Load journal type counts based on current filter
        loadJournalTypeCounts();
        
        // Get average mood
        float avgMood = StatisticsUtils.getAverageMoodLevel(getContext());
        if (avgMood > 0) {
            DecimalFormat df = new DecimalFormat("#.#");
            averageMood.setText(df.format(avgMood));
            
            // Set mood emoji
            int moodIndex = Math.min(4, Math.max(0, (int) Math.round(avgMood) - 1));
            String[] emojis = getResources().getStringArray(R.array.mood_emojis);
            moodEmoji.setText(emojis[moodIndex]);
        } else {
            averageMood.setText("N/A");
            moodEmoji.setText("");
        }

        // Load weekly activity chart
        loadWeeklyActivityChart();
        
        // Load mood trends chart
        loadMoodTrendsChart();
    }
    
    /**
     * Load journal counts by type filtered by selected period
     */
    private void loadJournalTypeCounts() {
        if (getContext() == null) return;
        
        DatabaseHelper databaseHelper = DatabaseHelper.getDb(getContext());
        List<Journal> allJournals = databaseHelper.journalDao().getAllJournal();
        
        Date startDate = null;
        Date endDate = Calendar.getInstance().getTime();
        
        Calendar cal = Calendar.getInstance();
        
        // Determine date range based on filter
        switch (currentJournalTypesFilter) {
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
        
        // Update UI
        reflectiveCount.setText(String.valueOf(countByType.get(ApplicationConstants.REFLECTIVE_JOURNAL)));
        gratitudeCount.setText(String.valueOf(countByType.get(ApplicationConstants.GRATITUDE_JOURNAL)));
        dreamCount.setText(String.valueOf(countByType.get(ApplicationConstants.DREAM_JOURNAL)));
        bulletCount.setText(String.valueOf(countByType.get(ApplicationConstants.BULLET_JOURNAL)));
    }
    
    /**
     * Load weekly activity chart data
     */
    private void loadWeeklyActivityChart() {
        if (getContext() == null) return;
        
        DatabaseHelper databaseHelper = DatabaseHelper.getDb(getContext());
        List<Journal> allJournals = databaseHelper.journalDao().getAllJournal();
        
        // Get journals from the past week
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -6); // 7 days including today
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date startDate = cal.getTime();
        
        // Count journals per day
        int[] dayCounts = new int[7];
        Calendar journalCal = Calendar.getInstance();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault());
        
        for (Journal journal : allJournals) {
            if (journal.getJournalCreatedOn().after(startDate)) {
                journalCal.setTime(journal.getJournalCreatedOn());
                int dayOfWeek = journalCal.get(Calendar.DAY_OF_WEEK) - 1; // 0-6
                dayCounts[dayOfWeek]++;
            }
        }
        
        // Create chart data
        List<BarEntry> entries = new ArrayList<>();
        String[] dayLabels = new String[7];
        
        for (int i = 0; i < 7; i++) {
            Calendar labelCal = Calendar.getInstance();
            labelCal.add(Calendar.DAY_OF_YEAR, i - 6); // -6 to 0
            dayLabels[i] = dayFormat.format(labelCal.getTime());
            
            entries.add(new BarEntry(i, dayCounts[i]));
        }
        
        BarDataSet dataSet = new BarDataSet(entries, "Daily Journals");
        dataSet.setColor(ContextCompat.getColor(getContext(), R.color.app_blue));
        dataSet.setValueTextColor(getTextColor());
        dataSet.setValueTextSize(12f);
        
        BarData barData = new BarData(dataSet);
        weeklyChart.setData(barData);
        
        XAxis xAxis = weeklyChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dayLabels));
        
        weeklyChart.invalidate();
    }
    
    /**
     * Load mood trends chart
     */
    private void loadMoodTrendsChart() {
        if (getContext() == null) return;
        
        DatabaseHelper databaseHelper = DatabaseHelper.getDb(getContext());
        
        // Calculate date range for the past month
        Calendar calendarEnd = Calendar.getInstance();
        Date endDate = calendarEnd.getTime();
        
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.add(Calendar.MONTH, -1);
        Date startDate = calendarStart.getTime();
        
        // Get all mood entries from the past month
        List<MoodTracker> moodEntries = databaseHelper.moodTrackerDao().getAllMoods(startDate, endDate);
        
        // Group by mood level (1-5)
        int[] moodCounts = new int[5];
        
        for (MoodTracker entry : moodEntries) {
            int moodLevel = entry.getMoodLevel();
            if (moodLevel >= 1 && moodLevel <= 5) {
                moodCounts[moodLevel - 1]++;
            }
        }
        
        // Create chart data
        List<BarEntry> entries = new ArrayList<>();
        String[] moodLabels = getResources().getStringArray(R.array.mood_levels);
        
        for (int i = 0; i < 5; i++) {
            entries.add(new BarEntry(i, moodCounts[i]));
        }
        
        BarDataSet dataSet = new BarDataSet(entries, "Mood Frequency");
        
        // Colors representing different moods
        int[] colors = new int[]{
            Color.rgb(220, 53, 69),   // Red (Awful)
            Color.rgb(253, 126, 20),  // Orange (Sad)
            Color.rgb(40, 167, 69),   // Green (Good)
            Color.rgb(23, 162, 184),  // Blue (Happy)
            Color.rgb(111, 66, 193)   // Purple (Excited)
        };
        
        dataSet.setColors(colors);
        dataSet.setValueTextColor(getTextColor());
        dataSet.setValueTextSize(12f);
        
        BarData barData = new BarData(dataSet);
        moodChart.setData(barData);
        
        XAxis xAxis = moodChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(moodLabels));
        
        moodChart.invalidate();
    }
    
    /**
     * Get text color based on current theme
     */
    private int getTextColor() {
        if (getContext() == null) return Color.BLACK;
        
        int[] attrs = new int[]{android.R.attr.textColorPrimary};
        return ContextCompat.getColor(getContext(), attrs[0]);
    }
}
