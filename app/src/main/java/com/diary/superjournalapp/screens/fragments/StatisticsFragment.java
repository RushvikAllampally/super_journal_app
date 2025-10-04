package com.diary.superjournalapp.screens.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.constants.ApplicationConstants;
import com.diary.superjournalapp.utils.StatisticsUtils;

import java.text.DecimalFormat;
import java.util.Map;

public class StatisticsFragment extends Fragment {

    private TextView totalEntries;
    private TextView currentStreak;
    private TextView lastWeekEntries;
    private TextView mostProductiveDay;
    private TextView reflectiveCount;
    private TextView gratitudeCount;
    private TextView dreamCount;
    private TextView bulletCount;
    private TextView averageMood;

    public StatisticsFragment() {
        // Required empty public constructor
    }

    public static StatisticsFragment newInstance() {
        return new StatisticsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize TextViews
        totalEntries = view.findViewById(R.id.total_entries);
        currentStreak = view.findViewById(R.id.current_streak);
        lastWeekEntries = view.findViewById(R.id.last_week_entries);
        mostProductiveDay = view.findViewById(R.id.most_productive_day);
        reflectiveCount = view.findViewById(R.id.reflective_count);
        gratitudeCount = view.findViewById(R.id.gratitude_count);
        dreamCount = view.findViewById(R.id.dream_count);
        bulletCount = view.findViewById(R.id.bullet_count);
        averageMood = view.findViewById(R.id.average_mood);

        // Load statistics
        loadStatistics();
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

        // Get journal counts by type
        Map<String, Integer> journalCounts = StatisticsUtils.getJournalCountByType(getContext());
        reflectiveCount.setText(String.valueOf(journalCounts.get(ApplicationConstants.REFLECTIVE_JOURNAL)));
        gratitudeCount.setText(String.valueOf(journalCounts.get(ApplicationConstants.GRATITUDE_JOURNAL)));
        dreamCount.setText(String.valueOf(journalCounts.get(ApplicationConstants.DREAM_JOURNAL)));
        bulletCount.setText(String.valueOf(journalCounts.get(ApplicationConstants.BULLET_JOURNAL)));

        // Get average mood
        float avgMood = StatisticsUtils.getAverageMoodLevel(getContext());
        if (avgMood > 0) {
            DecimalFormat df = new DecimalFormat("#.#");
            averageMood.setText(df.format(avgMood));
        } else {
            averageMood.setText("N/A");
        }
    }
}
