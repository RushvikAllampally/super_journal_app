package com.diary.superjournalapp.screens.fragments;

import static com.github.mikephil.charting.utils.ColorTemplate.rgb;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.database.DatabaseHelper;
import com.diary.superjournalapp.entity.MoodTracker;
import com.github.mikephil.charting.charts.BarChart;
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
    ArrayList barEntriesArrayList;
    
    private TextView totalJournalCount;
    private ImageButton leftArrowBtn;
    private ImageButton rightArrowBtn;
    private TextView graphMonthName;
    private Date startDate;
    private Date endDate;
    private int currentMonthNumber;
    private Calendar calendar;

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

        barEntriesArrayList = new ArrayList();

        barEntriesArrayList.add(new BarEntry(0.5f, one));
        barEntriesArrayList.add(new BarEntry(1.5f, two));
        barEntriesArrayList.add(new BarEntry(2.5f, three));
        barEntriesArrayList.add(new BarEntry(3.5f, four));
        barEntriesArrayList.add(new BarEntry(4.5f, five));

        barChart = view.findViewById(R.id.idBarChart);

        barChart.animateXY(1000, 1000);

        barChart.setHighlightPerTapEnabled(true);
        barChart.setDrawMarkers(true);

        String[] moods = new String[]{"Awful", "Sad", "Good", "Happy", "Excited"};

        // Customize X Axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setAxisMinimum(0f); // Set the minimum value to 0
        xAxis.setDrawGridLines(false); // Hide grid lines
        xAxis.setValueFormatter(new IndexAxisValueFormatter(moods));
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(moods.length); // Ensure the correct number of labels are displayed

        // Customize Y Axis
        YAxis leftAxis = barChart.getAxisLeft();

        leftAxis.setAxisMinimum(0f); // Set the minimum value to 0
        leftAxis.setDrawGridLines(false); // Hide grid lines
        leftAxis.setDrawZeroLine(true); // Display a zero line
        leftAxis.setDrawTopYLabelEntry(true); // Show top label entry

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false); // Disable the right Y Axis

        // Adjust offsets
        barChart.setExtraBottomOffset(20f); // Creates space between X-axis labels and bottom of the chart

        // creating a new bar data set.
        barDataSet = new BarDataSet(barEntriesArrayList, "Mood Statistics");

        // creating a new bar data and
        // passing our bar data set.
        barData = new BarData(barDataSet);

        // below line is to set data
        // to our bar chart.
        barChart.setData(barData);

        // adding color to our bar data set.
        barDataSet.setColors(rgb("#E6E6FA"), rgb("#FFD700"), rgb("#98FB98"), rgb("#ADD8E6"), rgb("#FFB6C1"));

        // setting text color.
        barDataSet.setValueTextColor(Color.BLACK);

        // setting text size
        barDataSet.setValueTextSize(16f);
        barDataSet.setValueFormatter(new DefaultAxisValueFormatter(0));
        barChart.getDescription().setEnabled(false);
    }
}
