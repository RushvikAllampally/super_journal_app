package com.diary.superjournalapp.screens.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
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
    
    private static final String TAG = "StatisticsFragment";

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
    private TextView averageMoodOverview;
    private TextView moodEmojiOverview;
    private TextView averageWords;
    private ImageButton prevPeriodButton;
    private ImageButton nextPeriodButton;
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
    private int currentOverviewPeriod = THIS_MONTH;

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
        averageMoodOverview = view.findViewById(R.id.average_mood_overview);
        moodEmojiOverview = view.findViewById(R.id.mood_emoji_overview);
        averageWords = view.findViewById(R.id.average_words);
        statsPeriodText = view.findViewById(R.id.stats_period_text);
        journalTypesPeriodSpinner = view.findViewById(R.id.journal_types_period_spinner);
        weeklyChart = view.findViewById(R.id.weekly_chart);
        moodChart = view.findViewById(R.id.mood_chart);
        prevPeriodButton = view.findViewById(R.id.prev_period_button);
        nextPeriodButton = view.findViewById(R.id.next_period_button);
        
        // Set current month/year
        Calendar cal = Calendar.getInstance();
        String monthName = new DateFormatSymbols().getMonths()[cal.get(Calendar.MONTH)];
        int year = cal.get(Calendar.YEAR);
        statsPeriodText.setText(monthName + " " + year);
    }

    private void setupSpinner(View view) {
        // Setup period spinner adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.time_period_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        journalTypesPeriodSpinner.setAdapter(adapter);
        
        journalTypesPeriodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentJournalTypesFilter = position;
                loadJournalTypeCounts();
                
                // Update the journal types section title to reflect the time period
                TextView journalTypesTitle = getView().findViewById(R.id.journal_types_title);
                if (journalTypesTitle != null) {
                    journalTypesTitle.setText("Journal Types - " + StatisticsUtils.getFormattedPeriodName(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        
        // Setup period navigation buttons
        prevPeriodButton = view.findViewById(R.id.prev_period_button);
        nextPeriodButton = view.findViewById(R.id.next_period_button);
        
        if (prevPeriodButton != null && nextPeriodButton != null) {
            prevPeriodButton.setOnClickListener(v -> {
                currentOverviewPeriod = Math.min(ALL_TIME, currentOverviewPeriod + 1);
                updateOverviewPeriod();
                loadStatistics();
            });
            
            nextPeriodButton.setOnClickListener(v -> {
                currentOverviewPeriod = Math.max(THIS_MONTH, currentOverviewPeriod - 1);
                updateOverviewPeriod();
                loadStatistics();
            });
            
            // Set initial state
            updateOverviewPeriod();
        }
    }
    
    /**
     * Update the period text and button states for the overview section
     */
    private void updateOverviewPeriod() {
        String periodName = StatisticsUtils.getFormattedPeriodName(currentOverviewPeriod);
        statsPeriodText.setText(periodName);
        
        // Update button states
        nextPeriodButton.setEnabled(currentOverviewPeriod > THIS_MONTH);
        prevPeriodButton.setEnabled(currentOverviewPeriod < ALL_TIME);
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
        
        Log.d(TAG, "Loading statistics for period: " + StatisticsUtils.getFormattedPeriodName(currentOverviewPeriod));

        // Get total entries
        int totalEntryCount = StatisticsUtils.getTotalJournalCount(getContext());
        totalEntries.setText(String.valueOf(totalEntryCount));
        Log.d(TAG, "Total entries: " + totalEntryCount);

        // Get current streak
        int streak = StatisticsUtils.getCurrentStreak(getContext());
        currentStreak.setText(String.valueOf(streak));

        // Get entries from last week
        int weeklyCount = StatisticsUtils.getJournalCountLastWeek(getContext());
        lastWeekEntries.setText(String.valueOf(weeklyCount));

        // Get most productive day for the current period
        int productiveDay = StatisticsUtils.getMostProductiveDay(getContext(), currentOverviewPeriod);
        if (productiveDay >= 0) {
            String dayName = StatisticsUtils.getDayName(productiveDay);
            mostProductiveDay.setText(dayName);
            Log.d(TAG, "Most productive day: " + dayName);
        } else {
            mostProductiveDay.setText("N/A");
            Log.d(TAG, "Most productive day: N/A");
        }
        
        // Get average words per entry for the current period
        int avgWords = StatisticsUtils.getAverageWordCount(getContext(), currentOverviewPeriod);
        averageWords.setText(String.valueOf(avgWords));
        Log.d(TAG, "Average words: " + avgWords);

        // Load journal type counts based on current filter
        loadJournalTypeCounts();
        
        // Get average mood for the current period
        float avgMood = StatisticsUtils.getAverageMoodLevel(getContext(), currentOverviewPeriod);
        if (avgMood > 0) {
            DecimalFormat df = new DecimalFormat("#.#");
            String formattedMood = df.format(avgMood);
            averageMood.setText(formattedMood);
            averageMoodOverview.setText(formattedMood);
            
            // Set mood emoji
            int moodIndex = Math.min(4, Math.max(0, (int) Math.round(avgMood) - 1));
            String[] emojis = getResources().getStringArray(R.array.mood_emojis);
            moodEmoji.setText(emojis[moodIndex]);
            moodEmojiOverview.setText(emojis[moodIndex]);
            Log.d(TAG, "Average mood: " + formattedMood + " (" + emojis[moodIndex] + ")");
        } else {
            averageMood.setText("N/A");
            moodEmoji.setText("");
            averageMoodOverview.setText("N/A");
            moodEmojiOverview.setText("");
            Log.d(TAG, "Average mood: N/A");
        }
        
        // Apply background to statistic numbers for better visibility in dark mode
        applyBackgroundToStatNumbers();

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
        
        Log.d(TAG, "Loading journal type counts for period: " + StatisticsUtils.getFormattedPeriodName(currentJournalTypesFilter));
        
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
        int reflectiveJournals = countByType.get(ApplicationConstants.REFLECTIVE_JOURNAL);
        int gratitudeJournals = countByType.get(ApplicationConstants.GRATITUDE_JOURNAL);
        int dreamJournals = countByType.get(ApplicationConstants.DREAM_JOURNAL);
        int bulletJournals = countByType.get(ApplicationConstants.BULLET_JOURNAL);
        
        reflectiveCount.setText(String.valueOf(reflectiveJournals));
        gratitudeCount.setText(String.valueOf(gratitudeJournals));
        dreamCount.setText(String.valueOf(dreamJournals));
        bulletCount.setText(String.valueOf(bulletJournals));
        
        Log.d(TAG, "Journal counts - Reflective: " + reflectiveJournals + 
              ", Gratitude: " + gratitudeJournals + 
              ", Dream: " + dreamJournals + 
              ", Bullet: " + bulletJournals);
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
    
    /**
     * Apply background to all statistic number TextViews for better visibility in dark mode
     */
    private void applyBackgroundToStatNumbers() {
        // List of all statistic TextViews
        TextView[] statTextViews = {
            totalEntries, currentStreak, lastWeekEntries, mostProductiveDay,
            reflectiveCount, gratitudeCount, dreamCount, bulletCount,
            averageMood, averageMoodOverview, averageWords
        };
        
        int backgroundColor = ContextCompat.getColor(requireContext(), android.R.color.white);
        int textColor = ContextCompat.getColor(requireContext(), android.R.color.black);
        float cornerRadius = getResources().getDimension(R.dimen.card_corner_radius);
        
        // Apply style to each TextView
        for (TextView textView : statTextViews) {
            if (textView != null) {
                // Set background with rounded corners
                GradientDrawable shape = new GradientDrawable();
                shape.setCornerRadius(cornerRadius);
                shape.setColor(backgroundColor);
                textView.setBackground(shape);
                
                // Set text color to always be black for readability
                textView.setTextColor(textColor);
                
                // Add padding
                int padding = (int) getResources().getDimension(R.dimen.small_padding);
                textView.setPadding(padding, padding, padding, padding);
            }
        }
    }
}
