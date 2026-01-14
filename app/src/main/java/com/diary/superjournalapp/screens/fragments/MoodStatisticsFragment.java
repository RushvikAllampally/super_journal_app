package com.diary.superjournalapp.screens.fragments;

import static com.github.mikephil.charting.utils.ColorTemplate.rgb;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.database.DatabaseHelper;
import com.diary.superjournalapp.entity.MoodTracker;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MoodStatisticsFragment extends Fragment {
    
    // variable for our bar chart
    BarChart barChart;
    // variable for our bar data.
    BarData barData;
    // variable for our bar data set.
    BarDataSet barDataSet;
    // array list for storing entries.
    ArrayList<BarEntry> barEntriesArrayList;
    
    private TextView totalJournalCount;
    private ImageButton leftArrowBtn;
    private ImageButton rightArrowBtn;
    private TextView graphMonthName;
    private Date startDate;
    private Date endDate;
    private int currentMonthNumber;
    private Calendar calendar;
    
    // Chart axis variables
    private XAxis xAxis;
    private YAxis leftAxis;

    public MoodStatisticsFragment() {
        // Required empty public constructor
    }

    public static MoodStatisticsFragment newInstance() {
        return new MoodStatisticsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mood_statistics, container, false);

        DatabaseHelper databaseHelper = DatabaseHelper.getDb(view.getContext());
        totalJournalCount = view.findViewById(R.id.total_journal_count);
        leftArrowBtn = view.findViewById(R.id.left_arrow_btn);
        rightArrowBtn = view.findViewById(R.id.right_arrow_btn);
        graphMonthName = view.findViewById(R.id.graph_month_name);

        totalJournalCount.setText(String.valueOf(databaseHelper.journalDao().getTotalJournalsCount()));

        String[] monthNames = new DateFormatSymbols().getMonths();
        Calendar calendar = Calendar.getInstance();

        currentMonthNumber = calendar.get(Calendar.MONTH);
        graphMonthName.setText(monthNames[currentMonthNumber]);

        leftArrowBtn.setOnClickListener(v -> {
            if (currentMonthNumber >= 1 && currentMonthNumber <= 11) {
                graphMonthName.setText(monthNames[--currentMonthNumber]);
                updateStartAndEndDates(currentMonthNumber, databaseHelper, view);
            }
        });

        rightArrowBtn.setOnClickListener(v -> {
            if (currentMonthNumber >= 0 && currentMonthNumber <= 10) {
                graphMonthName.setText(monthNames[++currentMonthNumber]);
                updateStartAndEndDates(currentMonthNumber, databaseHelper, view);
            }
        });

        updateStartAndEndDates(currentMonthNumber, databaseHelper, view);

        return view;
    }

    private void updateStartAndEndDates(int calenderMonth, DatabaseHelper databaseHelper, View view) {

        // Get the current date and time
        calendar = Calendar.getInstance();

        calendar.set(Calendar.MONTH, calenderMonth);

        // Set the time to the start of the day (midnight)
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Get the start date of the current day
        startDate = calendar.getTime();

        // Set the time to the end of the day (11:59:59.999 PM)
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        // Get the end date of the current day
        endDate = calendar.getTime();

        updateGraph(databaseHelper, view);
    }

    public void updateGraph(DatabaseHelper databaseHelper, View view) {
        List<MoodTracker> moodTrackers = databaseHelper.moodTrackerDao().getAllMoods(startDate, endDate);

        int one = 0, two = 0, three = 0, four = 0, five = 0;
        for (MoodTracker moodTracker : moodTrackers) {
            switch (moodTracker.getMoodLevel()) {
                case 1:
                    one++;
                    break;
                case 2:
                    two++;
                    break;
                case 3:
                    three++;
                    break;
                case 4:
                    four++;
                    break;
                case 5:
                    five++;
                    break;
            }
        }

        barEntriesArrayList = new ArrayList<BarEntry>();

        barEntriesArrayList.add(new BarEntry(0.5f, one));
        barEntriesArrayList.add(new BarEntry(1.5f, two));
        barEntriesArrayList.add(new BarEntry(2.5f, three));
        barEntriesArrayList.add(new BarEntry(3.5f, four));
        barEntriesArrayList.add(new BarEntry(4.5f, five));

        barChart = view.findViewById(R.id.idBarChart);

        // Check if we're in dark mode and adjust chart colors accordingly
        boolean isDarkMode = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        
        // Get colors from theme attributes for better dark mode support
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        int textColor = typedValue.data;

        // Set chart background color based on theme
        barChart.setBackgroundColor(Color.TRANSPARENT);
        
        barChart.animateXY(1000, 1000);
        barChart.setHighlightPerTapEnabled(true);
        barChart.setDrawMarkers(true);
        
        // Customize description
        barChart.getDescription().setEnabled(false);
        
        // Set legend appearance
        barChart.getLegend().setTextColor(textColor);
        barChart.getLegend().setTextSize(14f);
        barChart.getLegend().setTypeface(Typeface.DEFAULT_BOLD);
        barChart.getLegend().setForm(Legend.LegendForm.CIRCLE);
        barChart.getLegend().setFormSize(10f);

        String[] moods = new String[]{"Awful", "Sad", "Good", "Happy", "Excited"};

        // Customize X Axis with better dark mode support
        this.xAxis = barChart.getXAxis();
        this.xAxis.setAxisMinimum(0f);
        this.xAxis.setDrawGridLines(false);
        this.xAxis.setValueFormatter(new IndexAxisValueFormatter(moods));
        this.xAxis.setGranularity(1f);
        this.xAxis.setCenterAxisLabels(true);
        this.xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        this.xAxis.setLabelCount(moods.length);
        this.xAxis.setTextColor(textColor);
        this.xAxis.setTextSize(12f);
        this.xAxis.setYOffset(10f);

        // Customize Y Axis with better dark mode support
        this.leftAxis = barChart.getAxisLeft();
        this.leftAxis.setAxisMinimum(0f);
        this.leftAxis.setDrawGridLines(false);
        this.leftAxis.setDrawZeroLine(true);
        this.leftAxis.setDrawTopYLabelEntry(true);
        this.leftAxis.setTextColor(textColor);
        this.leftAxis.setTextSize(12f);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);

        // Create more space for labels
        barChart.setExtraBottomOffset(20f);
        barChart.setExtraLeftOffset(10f);

        // Create the bar data set with better contrast colors that work in both modes
        barDataSet = new BarDataSet(barEntriesArrayList, "Mood Statistics");
        
        // Colors that work well in both light and dark mode
        int[] colors = new int[]{
            ContextCompat.getColor(getContext(), R.color.awful_mood),
            ContextCompat.getColor(getContext(), R.color.sad_mood),
            ContextCompat.getColor(getContext(), R.color.good_mood),
            ContextCompat.getColor(getContext(), R.color.happy_mood),
            ContextCompat.getColor(getContext(), R.color.excited_mood)
        };
        
        barDataSet.setColors(colors);
        barDataSet.setValueTextColor(textColor);
        barDataSet.setValueTextSize(14f);
        barDataSet.setValueFormatter(new DefaultAxisValueFormatter(0));
        barDataSet.setValueTypeface(Typeface.DEFAULT_BOLD);
        
        // Set data to the chart
        barData = new BarData(barDataSet);
        barData.setBarWidth(0.8f);
        barChart.setData(barData);
        
        // Refresh the chart
        barChart.invalidate();
    }
}
