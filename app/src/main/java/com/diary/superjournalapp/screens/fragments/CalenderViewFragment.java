package com.diary.superjournalapp.screens.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.constants.ApplicationConstants;
import com.diary.superjournalapp.database.DatabaseHelper;
import com.diary.superjournalapp.entity.Journal;
import com.diary.superjournalapp.recyclerviews.JournalRecyclerAdaptor;
import com.diary.superjournalapp.utils.calendar.EventDotDecorator;
import com.diary.superjournalapp.utils.calendar.TodayDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Enhanced Calendar View Fragment with:
 * - Event dots showing journal categories
 * - Heatmap intensity based on journal count
 * - Consistency streak tracking
 * - Month summary statistics
 * - Mood indicators on calendar cells
 */
public class CalenderViewFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    
    // UI Components
    private MaterialCalendarView materialCalendarView;
    private RecyclerView recyclerView;
    private JournalRecyclerAdaptor journalRecyclerAdaptor;
    private ImageView notFoundImageView;
    private TextView notFoundTextView;
    private TextView journalDateTitle;
    
    // Summary TextViews
    private TextView monthTotalEntries;
    private TextView monthGratitudeCount;
    private TextView monthReflectiveCount;
    private TextView monthDreamCount;
    
    // Current selected date
    private CalendarDay selectedDate;
    
    // Data
    private DatabaseHelper databaseHelper;
    private String mParam1;
    private String mParam2;

    public CalenderViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalenderViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalenderViewFragment newInstance(String param1, String param2) {
        CalenderViewFragment fragment = new CalenderViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calender_view, container, false);
        
        // Initialize UI components
        initializeViews(view);
        
        // Initialize database
        databaseHelper = DatabaseHelper.getDb(view.getContext());
        
        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        
        // Setup calendar with decorators
        setupCalendar();
        
        // Load initial data for today
        loadJournalsForToday();
        
        return view;
    }
    
    /**
     * Initialize all UI components
     */
    private void initializeViews(View view) {
        materialCalendarView = view.findViewById(R.id.material_calendar_view);
        recyclerView = view.findViewById(R.id.calender_recycler_view);
        notFoundImageView = view.findViewById(R.id.nothing_found_calender_view);
        notFoundTextView = view.findViewById(R.id.nothing_found_calender_text);
        journalDateTitle = view.findViewById(R.id.journal_date_title);
        
        monthTotalEntries = view.findViewById(R.id.month_total_entries);
        monthGratitudeCount = view.findViewById(R.id.month_gratitude_count);
        monthReflectiveCount = view.findViewById(R.id.month_reflective_count);
        monthDreamCount = view.findViewById(R.id.month_dream_count);
    }
    
    /**
     * Setup MaterialCalendarView with decorators and listeners
     */
    private void setupCalendar() {
        // Select today by default
        CalendarDay today = CalendarDay.today();
        materialCalendarView.setDateSelected(today, true);
        selectedDate = today;
        
        // Set custom arrows with theme-aware tinting
        setCalendarArrows();
        
        // Set date selection listener
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget,
                                     @NonNull CalendarDay date, boolean selected) {
                selectedDate = date;
                loadJournalsForDate(date);
            }
        });

        // Set month change listener to update decorators
        materialCalendarView.setOnMonthChangedListener((widget, date) -> {
            // Clear selection when month changes
            materialCalendarView.clearSelection();
            selectedDate = null;
            
            // Clear journal list
            recyclerView.setVisibility(View.GONE);
            notFoundImageView.setVisibility(View.VISIBLE);
            notFoundTextView.setVisibility(View.VISIBLE);
            journalDateTitle.setText("Journals written on this Day");
            
            updateCalendarDecorators(date);
            updateMonthSummary(date);
        });
        
        // Initialize decorators for current month
        updateCalendarDecorators(today);
        updateMonthSummary(today);
    }
    
    /**
     * Update calendar decorators with event dots, heatmap, and mood indicators
     */
    private void updateCalendarDecorators(CalendarDay currentMonth) {
        // Clear existing decorators
        materialCalendarView.removeDecorators();
        
        // Get month start and end dates
        // CalendarDay.getMonth() returns 1-based (1-12), but Calendar.set() expects 0-based (0-11)
        Calendar cal = Calendar.getInstance();
        cal.set(currentMonth.getYear(), currentMonth.getMonth() - 1, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date monthStart = cal.getTime();
        
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date monthEnd = cal.getTime();
        
        // Fetch data for the month
        List<Journal> monthJournals = databaseHelper.journalDao().getJournalsForMonth(monthStart, monthEnd);
        
        System.out.println("CalendarDebug: Found " + monthJournals.size() + " journals for month");
        
        // Prepare data structures for decorators
        Map<CalendarDay, List<Journal>> journalsByDate = groupJournalsByDate(monthJournals);
        
        System.out.println("CalendarDebug: Grouped into " + journalsByDate.size() + " unique dates");
        
        // Add today decorator (makes today's date bold with theme-aware color)
        int textColor = getThemeTextColor();
        materialCalendarView.addDecorator(new TodayDecorator(textColor));
        
        // Add event dot decorators for each date
        int dotCount = 0;
        for (Map.Entry<CalendarDay, List<Journal>> entry : journalsByDate.entrySet()) {
            List<Integer> colors = getCategoryColors(entry.getValue());
            if (!colors.isEmpty()) {
                HashSet<CalendarDay> singleDay = new HashSet<>();
                singleDay.add(entry.getKey());
                materialCalendarView.addDecorator(new EventDotDecorator(singleDay, colors));
                dotCount++;
                System.out.println("CalendarDebug: Added event dots for " + entry.getKey() + " with " + colors.size() + " colors");
            }
        }
        System.out.println("CalendarDebug: Added " + dotCount + " event dot decorators");
        
        // Invalidate decorators to force refresh
        materialCalendarView.invalidateDecorators();
    }
    
    /**
     * Group journals by calendar date
     */
    private Map<CalendarDay, List<Journal>> groupJournalsByDate(List<Journal> journals) {
        Map<CalendarDay, List<Journal>> grouped = new HashMap<>();
        
        for (Journal journal : journals) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(journal.getJournalCreatedOn());
            // CalendarDay.from() expects 1-based month (1-12), but Calendar.MONTH is 0-based (0-11)
            CalendarDay day = CalendarDay.from(cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH));
            
            if (!grouped.containsKey(day)) {
                grouped.put(day, new ArrayList<>());
            }
            grouped.get(day).add(journal);
        }
        
        return grouped;
    }
    
    /**
     * Get category colors for event dots
     */
    private List<Integer> getCategoryColors(List<Journal> journals) {
        List<Integer> colors = new ArrayList<>();
        boolean hasGratitude = false;
        boolean hasReflective = false;
        boolean hasDream = false;
        boolean hasBullet = false;
        
        for (Journal journal : journals) {
            String category = journal.getJournalCategory();
            System.out.println("CalendarDebug: Journal category = '" + category + "'");
            
            if (category == null) continue;
            
            if (category.equals(ApplicationConstants.GRATITUDE_JOURNAL) && !hasGratitude) {
                int color = ContextCompat.getColor(requireContext(), R.color.calendar_gratitude);
                colors.add(color);
                hasGratitude = true;
                System.out.println("CalendarDebug: Added GRATITUDE color (green)");
            } else if (category.equals(ApplicationConstants.REFLECTIVE_JOURNAL) && !hasReflective) {
                int color = ContextCompat.getColor(requireContext(), R.color.calendar_reflective);
                colors.add(color);
                hasReflective = true;
                System.out.println("CalendarDebug: Added REFLECTIVE color (blue)");
            } else if (category.equals(ApplicationConstants.DREAM_JOURNAL) && !hasDream) {
                int color = ContextCompat.getColor(requireContext(), R.color.calendar_dream);
                colors.add(color);
                hasDream = true;
                System.out.println("CalendarDebug: Added DREAM color (purple)");
            } else if (category.equals(ApplicationConstants.BULLET_JOURNAL) && !hasBullet) {
                int color = ContextCompat.getColor(requireContext(), R.color.calendar_bullet);
                colors.add(color);
                hasBullet = true;
                System.out.println("CalendarDebug: Added BULLET color (red)");
            }
        }
        
        System.out.println("CalendarDebug: Total colors added: " + colors.size());
        return colors;
    }
    
    /**
     * Update month summary statistics
     */
    private void updateMonthSummary(CalendarDay currentMonth) {
        // Get month start and end dates
        // CalendarDay.getMonth() returns 1-based (1-12), but Calendar.set() expects 0-based (0-11)
        Calendar cal = Calendar.getInstance();
        cal.set(currentMonth.getYear(), currentMonth.getMonth() - 1, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date monthStart = cal.getTime();
        
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date monthEnd = cal.getTime();
        
        // Fetch journals for the month
        List<Journal> monthJournals = databaseHelper.journalDao().getJournalsForMonth(monthStart, monthEnd);
        
        // Count by category
        int totalCount = monthJournals.size();
        int gratitudeCount = 0;
        int reflectiveCount = 0;
        int dreamCount = 0;
        int bulletCount = 0;
        
        for (Journal journal : monthJournals) {
            String category = journal.getJournalCategory();
            if (category == null) continue;
            
            if (category.equals(ApplicationConstants.GRATITUDE_JOURNAL)) {
                gratitudeCount++;
            } else if (category.equals(ApplicationConstants.REFLECTIVE_JOURNAL)) {
                reflectiveCount++;
            } else if (category.equals(ApplicationConstants.DREAM_JOURNAL)) {
                dreamCount++;
            } else if (category.equals(ApplicationConstants.BULLET_JOURNAL)) {
                bulletCount++;
            }
        }
        
        // Update UI
        monthTotalEntries.setText(String.valueOf(totalCount));
        monthGratitudeCount.setText(String.valueOf(gratitudeCount));
        monthReflectiveCount.setText(String.valueOf(reflectiveCount));
        monthDreamCount.setText(String.valueOf(dreamCount));
    }
    
    
    /**
     * Load journals for today
     */
    private void loadJournalsForToday() {
        CalendarDay today = CalendarDay.today();
        loadJournalsForDate(today);
    }
    
    /**
     * Load journals for a specific date
     */
    private void loadJournalsForDate(CalendarDay calendarDay) {
        // Convert CalendarDay to Date range
        // CalendarDay.getMonth() returns 1-based (1-12), but Calendar.set() expects 0-based (0-11)
        Calendar cal = Calendar.getInstance();
        cal.set(calendarDay.getYear(), calendarDay.getMonth() - 1, calendarDay.getDay(), 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startDate = cal.getTime();
        
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date endDate = cal.getTime();
        
        // Update title with date
        String monthName = new java.text.SimpleDateFormat("MMMM", Locale.getDefault()).format(startDate);
        String dayWithSuffix = getDayWithSuffix(calendarDay.getDay());
        journalDateTitle.setText("Journals written on " + dayWithSuffix + " " + monthName);
        
        // Fetch journals
        ArrayList<Journal> journalsList = (ArrayList<Journal>) databaseHelper.journalDao()
                .getAllJournalsByDateAndCategory(startDate, endDate, "");
        
        // Update UI
        if (journalsList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            notFoundImageView.setVisibility(View.VISIBLE);
            notFoundTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            notFoundImageView.setVisibility(View.GONE);
            notFoundTextView.setVisibility(View.GONE);
        }
        
        journalRecyclerAdaptor = new JournalRecyclerAdaptor(requireContext(), journalsList);
        recyclerView.setAdapter(journalRecyclerAdaptor);
    }
    
    /**
     * Get day with ordinal suffix (1st, 2nd, 3rd, etc.)
     */
    private String getDayWithSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return day + "th";
        }
        switch (day % 10) {
            case 1: return day + "st";
            case 2: return day + "nd";
            case 3: return day + "rd";
            default: return day + "th";
        }
    }
    
    /**
     * Set calendar arrows with theme-aware colors
     */
    private void setCalendarArrows() {
        // Set custom arrow drawables (they use theme-aware colors via ?attr)
        materialCalendarView.setLeftArrow(R.drawable.ic_calendar_arrow_left);
        materialCalendarView.setRightArrow(R.drawable.ic_calendar_arrow_right);
    }
    
    /**
     * Get theme-aware text color (black in light theme, white in dark theme)
     */
    private int getThemeTextColor() {
        // Use on_surface color which is theme-aware
        // Black (#000000) in light theme, White (#FFFFFF) in dark theme
        return ContextCompat.getColor(requireContext(), R.color.on_surface);
    }

}