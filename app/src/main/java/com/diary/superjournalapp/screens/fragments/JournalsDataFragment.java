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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
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
    
    // Charts
    private PieChart journalTypesChart;
    private BarChart weeklyActivityChart;
    private LineChart moodTrendsChart;

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
        
        // Initialize charts
        journalTypesChart = view.findViewById(R.id.journal_types_chart);
        weeklyActivityChart = view.findViewById(R.id.weekly_activity_chart);
        moodTrendsChart = view.findViewById(R.id.mood_trends_chart);
        
        // Load all statistics
        loadStatistics();
        setupJournalTypesChart();
        setupWeeklyActivityChart();
        setupMoodTrendsChart();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        loadStatistics();
        setupJournalTypesChart();
        setupWeeklyActivityChart();
        setupMoodTrendsChart();
    }

    /**
     * Load and display text statistics
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
            averageWordCount.setText(String.valueOf(avgWordCount));
        } else {
            averageWordCount.setText("N/A");
        }
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
     * Setup the mood trends line chart
     */
    private void setupMoodTrendsChart() {
        if (getContext() == null) return;
        
        // Get mood data from the past month
        DatabaseHelper databaseHelper = DatabaseHelper.getDb(getContext());
        
        // Calculate date range for the past month
        Calendar calendarEnd = Calendar.getInstance();
        Date endDate = calendarEnd.getTime();
        
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.add(Calendar.MONTH, -1);
        Date startDate = calendarStart.getTime();
        
        // Get all mood entries from the past month
        List<MoodTracker> moodEntries = databaseHelper.moodTrackerDao().getAllMoods(startDate, endDate);
        
        // Use created date for sorting
        Collections.sort(moodEntries, new Comparator<MoodTracker>() {
            @Override
            public int compare(MoodTracker o1, MoodTracker o2) {
                return o1.getCreatedDate().compareTo(o2.getCreatedDate());
            }
        });
        
        // Create entries for the chart
        ArrayList<Entry> entries = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM", Locale.getDefault());
        ArrayList<String> dateLabels = new ArrayList<>();
        
        // If there are mood entries, add them to the chart
        if (!moodEntries.isEmpty()) {
            int day = 0;
            for (MoodTracker mood : moodEntries) {
                entries.add(new Entry(day, mood.getMoodLevel()));
                // Use created date for labels
                dateLabels.add(dateFormat.format(mood.getCreatedDate()));
                day++;
            }
        } else {
            // Add placeholder data if no mood entries
            entries.add(new Entry(0, 3)); // Neutral mood
            dateLabels.add("No Data");
        }
        
        LineDataSet dataSet = new LineDataSet(entries, "Mood Level");
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.BLUE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Makes the line curve smoothly
        
        LineData lineData = new LineData(dataSet);
        
        // Configure chart
        moodTrendsChart.setData(lineData);
        moodTrendsChart.getDescription().setEnabled(false);
        
        // X-axis styling
        XAxis xAxis = moodTrendsChart.getXAxis();
        if (moodEntries.size() > 0) {
            xAxis.setValueFormatter(new IndexAxisValueFormatter(dateLabels));
        }
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        
        // Y-axis limits and labels
        YAxis leftAxis = moodTrendsChart.getAxisLeft();
        leftAxis.setAxisMinimum(0.5f);
        leftAxis.setAxisMaximum(5.5f);
        leftAxis.setGranularity(1f);
        
        // Labels for the mood levels
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                switch ((int)value) {
                    case 1: return "Very Bad";
                    case 2: return "Bad";
                    case 3: return "Neutral";
                    case 4: return "Good";
                    case 5: return "Excellent";
                    default: return "";
                }
            }
        });
        
        // Disable right y-axis
        moodTrendsChart.getAxisRight().setEnabled(false);
        
        // Enable touch gestures
        moodTrendsChart.setTouchEnabled(true);
        moodTrendsChart.setDragEnabled(true);
        moodTrendsChart.setScaleEnabled(true);
        
        // Animate
        moodTrendsChart.animateY(1000);
        moodTrendsChart.invalidate();
    }

    /**
     * Setup the weekly activity bar chart with real data
     */
    private void setupWeeklyActivityChart() {
        if (getContext() == null) return;
        
        // Get the real data for journal entries by day of week
        int[] entriesByDayOfWeek = new int[7]; // Sunday to Saturday
        
        // Query the database for journals created in the last 30 days
        DatabaseHelper databaseHelper = DatabaseHelper.getDb(getContext());
        List<Journal> recentJournals = databaseHelper.journalDao().getJournalsInLastDays(30);
        
        // Count entries by day of week
        Calendar calendar = Calendar.getInstance();
        for (Journal journal : recentJournals) {
            calendar.setTime(journal.getJournalCreatedOn());
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 0 = Sunday
            entriesByDayOfWeek[dayOfWeek]++;
        }
        
        // Create entries for the chart
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            entries.add(new BarEntry(i, entriesByDayOfWeek[i]));
        }
        
        // If no data, add minimal placeholder data
        if (entries.isEmpty()) {
            for (int i = 0; i < 7; i++) {
                entries.add(new BarEntry(i, 0));
            }
        }
        
        BarDataSet barDataSet = new BarDataSet(entries, "Weekly Journal Activity");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextSize(14f);
        
        BarData barData = new BarData(barDataSet);
        
        // Set X axis labels (days of week)
        final String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        
        XAxis xAxis = weeklyActivityChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        
        // Configure chart
        weeklyActivityChart.setData(barData);
        weeklyActivityChart.getDescription().setEnabled(true);
        weeklyActivityChart.getDescription().setText("From the last 30 days");
        weeklyActivityChart.getDescription().setTextSize(12f);
        weeklyActivityChart.getDescription().setTextColor(Color.GRAY);
        weeklyActivityChart.animateY(1000);
        weeklyActivityChart.invalidate();
    }
}