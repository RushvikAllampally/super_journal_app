package com.diary.superjournalapp.screens.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.database.DatabaseHelper;
import com.diary.superjournalapp.entity.Journal;
import com.diary.superjournalapp.recyclerviews.JournalRecyclerAdaptor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalenderViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalenderViewFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private CalendarView calender_view;
    private RecyclerView recyclerView;
    private JournalRecyclerAdaptor journalRecyclerAdaptor;
    private ImageView notFoundImageView;
    // TODO: Rename and change types of parameters
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
        // Inflate the layout for this fragment
        calender_view = view.findViewById(R.id.calender_view);
        recyclerView = view.findViewById(R.id.calender_recycler_view);
        notFoundImageView=view.findViewById(R.id.nothing_found_calender_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        DatabaseHelper databaseHelper = DatabaseHelper.getDb(view.getContext());
        ArrayList<Journal> journalsList = new ArrayList<>();

        calender_view.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                // month is 0-based (0 = January, 8 = September)
                // Create a Calendar instance and set the year, month, and date
                Calendar calendar = Calendar.getInstance();
                calendar.set(i, i1, i2); //(year, month, day)

                // Set the time to the start of the day (midnight)
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                // Get the start date of the day
                Date startDate = calendar.getTime();

                // Set the time to the end of the day (11:59:59.999 PM)
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);

                // Get the end date of the day
                Date endDate = calendar.getTime();

                // Convert the Calendar instance to a Date object
//                Date date = calendar.getTime();

                ArrayList<Journal> journalsList = (ArrayList<Journal>) databaseHelper.journalDao().getAllJournalsByDateAndCategory(startDate, endDate,"");
                System.out.println(journalsList);

                if(journalsList.size()==0){
                    recyclerView.setVisibility(View.GONE);
                    notFoundImageView.setVisibility(View.VISIBLE);
                }else{
                    recyclerView.setVisibility(View.VISIBLE);
                    notFoundImageView.setVisibility(View.GONE);
                }

                journalRecyclerAdaptor = new JournalRecyclerAdaptor(view.getContext(), journalsList);

                recyclerView.setAdapter(journalRecyclerAdaptor);
            }
        });

        // Get the current date and time
        Calendar calendar = Calendar.getInstance();

        // Set the time to the start of the day (midnight)
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Get the start date of the current day
        Date startDate = calendar.getTime();

        // Set the time to the end of the day (11:59:59.999 PM)
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        // Get the end date of the current day
        Date endDate = calendar.getTime();


        journalsList = (ArrayList<Journal>) databaseHelper.journalDao().getAllJournalsByDateAndCategory(startDate,endDate,"");

        if(journalsList.size()==0){
            recyclerView.setVisibility(View.GONE);
            notFoundImageView.setVisibility(View.VISIBLE);
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            notFoundImageView.setVisibility(View.GONE);
        }

        journalRecyclerAdaptor = new JournalRecyclerAdaptor(view.getContext(), journalsList);
        recyclerView.setAdapter(journalRecyclerAdaptor);

        return view;
    }

}