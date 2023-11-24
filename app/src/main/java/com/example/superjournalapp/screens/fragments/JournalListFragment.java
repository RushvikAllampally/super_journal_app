package com.example.superjournalapp.screens.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.superjournalapp.R;
import com.example.superjournalapp.database.DatabaseHelper;
import com.example.superjournalapp.entity.Journal;
import com.example.superjournalapp.recyclerviews.JournalRecyclerAdaptor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JournalListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JournalListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static RecyclerView recyclerView;
    private static ImageView nothingFoundImage;
    private static JournalRecyclerAdaptor journalRecyclerAdaptor;

    private static Date[] selectedDateRangeInSpinner;

    private static DatabaseHelper databaseHelper;

    private static Context context;

    private static String selectedCategoryInSpinner;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public JournalListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment JournalListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JournalListFragment newInstance(String param1, String param2) {
        JournalListFragment fragment = new JournalListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static void notifyJournalListFragment() {

        if (databaseHelper != null) {
            ArrayList<Journal> journalsList = new ArrayList<>();
            String queryCategory = (selectedCategoryInSpinner == null || selectedCategoryInSpinner.toLowerCase().equals("all")) ? "" : selectedCategoryInSpinner;
            journalsList = (ArrayList<Journal>) databaseHelper.journalDao().getAllJournalsByDateAndCategory(selectedDateRangeInSpinner[0], selectedDateRangeInSpinner[1], queryCategory);
            refactorNotFoundImage(journalsList.size());

            journalRecyclerAdaptor = new JournalRecyclerAdaptor(context, journalsList);

            recyclerView.setAdapter(journalRecyclerAdaptor);

            journalRecyclerAdaptor.notifyDataSetChanged();
        }

    }

    public static void refactorNotFoundImage(Integer sizeOfJournalsList) {
        if (sizeOfJournalsList == 0) {
            nothingFoundImage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            nothingFoundImage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_journal_list, container, false);

        context = view.getContext();

        recyclerView = view.findViewById(R.id.journal_entries_list);
        nothingFoundImage = view.findViewById(R.id.nothing_found_journals_list_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        databaseHelper = DatabaseHelper.getDb(view.getContext());

        selectedDateRangeInSpinner = getStartAndEndDates("This Month");
        System.out.println("start date : " + selectedDateRangeInSpinner[0] + " end date : " + selectedDateRangeInSpinner[1]);

//        ArrayList<Journal> journalsList = new ArrayList<>();
//        String queryCategory = (selectedCategoryInSpinner == null || selectedCategoryInSpinner.toLowerCase().equals("all")) ? "" : selectedCategoryInSpinner;
//        journalsList = (ArrayList<Journal>) databaseHelper.journalDao().getAllJournalsByDateAndCategory(selectedDateRangeInSpinner[0], selectedDateRangeInSpinner[1], queryCategory);
//        refactorNotFoundImage(journalsList.size());
//
//        journalRecyclerAdaptor = new JournalRecyclerAdaptor(view.getContext(), journalsList);
//
//        recyclerView.setAdapter(journalRecyclerAdaptor);

        notifyJournalListFragment();

        Spinner journalListOptionsSpinner = view.findViewById(R.id.journal_options_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.journal_list_btn_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        journalListOptionsSpinner.setAdapter(adapter);

        journalListOptionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedValue = adapterView.getItemAtPosition(i).toString();

                System.out.println("Selected category in spinner : " + selectedCategoryInSpinner);
                selectedCategoryInSpinner = selectedValue;
                ArrayList<Journal> journalsList = new ArrayList<>();
                String queryCategory = (selectedCategoryInSpinner == null || selectedCategoryInSpinner.toLowerCase().equals("all")) ? "" : selectedCategoryInSpinner;
                System.out.println("queryCategory : " + queryCategory + " selectedCategoryInSpinner : " + selectedCategoryInSpinner);
                journalsList = (ArrayList<Journal>) databaseHelper.journalDao().getAllJournalsByDateAndCategory(selectedDateRangeInSpinner[0], selectedDateRangeInSpinner[1], queryCategory);
                refactorNotFoundImage(journalsList.size());
                if (view != null) {
                    journalRecyclerAdaptor = new JournalRecyclerAdaptor(view.getContext(), journalsList);
                    recyclerView.setAdapter(journalRecyclerAdaptor);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        Spinner journalListDurationSpinner = view.findViewById(R.id.journal_list_duration_spinner);
        ArrayAdapter<CharSequence> journalListDurationAdapter = ArrayAdapter.createFromResource(getContext(), R.array.journal_list_duration_items, android.R.layout.simple_spinner_item);
        journalListDurationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        journalListDurationSpinner.setAdapter(journalListDurationAdapter);

        journalListDurationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String selectedValue = adapterView.getItemAtPosition(i).toString();

                selectedDateRangeInSpinner = getStartAndEndDates(selectedValue);
                System.out.println("selectedValue : " + selectedValue + "start date : " + selectedDateRangeInSpinner[0] + " end date : " + selectedDateRangeInSpinner[1]);

                ArrayList<Journal> journalsList = new ArrayList<>();
                journalsList = (ArrayList<Journal>) databaseHelper.journalDao().getAllJournalsByDateAndCategory(selectedDateRangeInSpinner[0], selectedDateRangeInSpinner[1], (selectedCategoryInSpinner == null || selectedCategoryInSpinner.toLowerCase().equals("all")) ? "" : selectedCategoryInSpinner);

                refactorNotFoundImage(journalsList.size());

                if(view != null){
                    journalRecyclerAdaptor = new JournalRecyclerAdaptor(view.getContext(), journalsList);
                    recyclerView.setAdapter(journalRecyclerAdaptor);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return view;
    }

    public Date[] getStartAndEndDates(String selectedOption) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        Date startDate, endDate;

        switch (selectedOption) {
            case "Today":
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                startDate = calendar.getTime();

                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                endDate = calendar.getTime();
                break;

            case "Yesterday":
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                startDate = calendar.getTime();

                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                endDate = calendar.getTime();
                break;

            case "This Week":
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                startDate = calendar.getTime();

                calendar.add(Calendar.DAY_OF_WEEK, 6); // End of the week
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                endDate = calendar.getTime();
                break;

            case "This Month":
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                startDate = calendar.getTime();

                calendar.add(Calendar.MONTH, 1);
                calendar.add(Calendar.DAY_OF_MONTH, -1); // Last day of the month
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                endDate = calendar.getTime();
                break;

            case "Last Month":
                calendar.add(Calendar.MONTH, -1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                startDate = calendar.getTime();

                calendar.add(Calendar.MONTH, 1);
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                endDate = calendar.getTime();
                break;

            case "Last 3 Months":
                // Set the end date to the last day of the current month
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                endDate = calendar.getTime();

                // Move to the first day of the current month
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.add(Calendar.MONTH, -2); // Go back two months
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                startDate = calendar.getTime();
                break;


            default:
                startDate = null;
                endDate = null;
                break;
        }

        Date[] dateRange = {startDate, endDate};
        return dateRange;
    }

}