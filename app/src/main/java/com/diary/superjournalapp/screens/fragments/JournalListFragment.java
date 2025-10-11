package com.diary.superjournalapp.screens.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.database.DatabaseHelper;
import com.diary.superjournalapp.entity.Journal;
import com.diary.superjournalapp.recyclerviews.JournalRecyclerAdaptor;
import com.diary.superjournalapp.screens.fragments.LibraryFragment.Searchable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JournalListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JournalListFragment extends Fragment implements Searchable {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView recyclerView;
    private ImageView nothingFoundImage;
    private JournalRecyclerAdaptor journalRecyclerAdaptor;

    private Date[] selectedDateRangeInSpinner;
    private DatabaseHelper databaseHelper;
    private String selectedCategoryInSpinner;
    private String currentSearchQuery = "";
    
    // Track all active instances for notification
    private static final List<JournalListFragment> activeInstances = new ArrayList<>();
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
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        synchronized (activeInstances) {
            activeInstances.add(this);
        }
    }
    
    @Override
    public void onDetach() {
        synchronized (activeInstances) {
            activeInstances.remove(this);
        }
        super.onDetach();
    }

    public static void notifyJournalListFragment() {
        synchronized (activeInstances) {
            for (JournalListFragment fragment : activeInstances) {
                if (fragment.isAdded() && fragment.getActivity() != null) {
                    fragment.getActivity().runOnUiThread(() -> {
                        fragment.refreshJournalList();
                    });
                }
            }
        }
    }
    
    /**
     * Refresh the journal list with current filters
     */
    public void refreshJournalList() {
        if (databaseHelper == null || getContext() == null) return;
        
        ArrayList<Journal> journalsList = new ArrayList<>();
        String queryCategory = (selectedCategoryInSpinner == null || selectedCategoryInSpinner.toLowerCase().equals("all")) ? "" : selectedCategoryInSpinner;
        
        if (selectedDateRangeInSpinner != null) {
            if (selectedDateRangeInSpinner.length == 2) {
                // For All Time option, the start date is null
                if (selectedDateRangeInSpinner[0] == null) {
                    // Get all journals and filter by category if needed
                    List<Journal> allJournals = databaseHelper.journalDao().getAllJournal();
                    if (queryCategory != null && !queryCategory.isEmpty()) {
                        for (Journal journal : allJournals) {
                            if (journal.getJournalCategory() != null && 
                                journal.getJournalCategory().contains(queryCategory)) {
                                journalsList.add(journal);
                            }
                        }
                    } else {
                        journalsList.addAll(allJournals);
                    }
                } else {
                    // Normal date range
                    journalsList.addAll(databaseHelper.journalDao().getAllJournalsByDateAndCategory(selectedDateRangeInSpinner[0], selectedDateRangeInSpinner[1], queryCategory));
                }
            }
        } else {
            // Use getAllJournal and filter manually if needed
            List<Journal> allJournals = databaseHelper.journalDao().getAllJournal();
            if (queryCategory != null && !queryCategory.isEmpty()) {
                for (Journal journal : allJournals) {
                    if (journal.getJournalCategory() != null && 
                        journal.getJournalCategory().contains(queryCategory)) {
                        journalsList.add(journal);
                    }
                }
            } else {
                journalsList.addAll(allJournals);
            }
        }
        
        // Apply search query filter if one exists
        if (currentSearchQuery != null && !currentSearchQuery.isEmpty()) {
            ArrayList<Journal> filteredList = new ArrayList<>();
            String lowerCaseQuery = currentSearchQuery.toLowerCase();
            
            for (Journal journal : journalsList) {
                // Search in title
                if (journal.getTitle() != null && 
                    journal.getTitle().toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(journal);
                    continue;
                }
                
                // Search in content
                if (journal.getJournalStartText() != null && 
                    journal.getJournalStartText().toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(journal);
                    continue;
                }
                
                // Search in category
                if (journal.getJournalCategory() != null && 
                    journal.getJournalCategory().toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(journal);
                }
            }
            
            journalsList = filteredList;
        }

        recyclerView.setVisibility(View.VISIBLE);
        nothingFoundImage.setVisibility(View.GONE);
        journalRecyclerAdaptor = new JournalRecyclerAdaptor(getContext(), journalsList);
        recyclerView.setAdapter(journalRecyclerAdaptor);

        if (journalsList.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            nothingFoundImage.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Update visibility of the nothingFoundImage for all instances
     */
    public static void refactorNotFoundImage(Integer sizeOfJournalsList) {
        synchronized (activeInstances) {
            for (JournalListFragment fragment : activeInstances) {
                if (fragment.isAdded() && fragment.getActivity() != null) {
                    fragment.getActivity().runOnUiThread(() -> {
                        if (fragment.nothingFoundImage != null) {
                            if (sizeOfJournalsList == 0) {
                                fragment.nothingFoundImage.setVisibility(View.VISIBLE);
                            } else {
                                fragment.nothingFoundImage.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_journal_list, container, false);

        recyclerView = view.findViewById(R.id.journal_entries_list);
        nothingFoundImage = view.findViewById(R.id.nothing_found_journals_list_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        databaseHelper = DatabaseHelper.getDb(getContext());

        selectedDateRangeInSpinner = getStartAndEndDates("This Month");
        System.out.println("start date : " + selectedDateRangeInSpinner[0] + " end date : " + selectedDateRangeInSpinner[1]);
//
//        journalRecyclerAdaptor = new JournalRecyclerAdaptor(view.getContext(), journalsList);
//
//        recyclerView.setAdapter(journalRecyclerAdaptor);

        notifyJournalListFragment();

        Spinner journalListOptionsSpinner = view.findViewById(R.id.journal_options_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.journal_list_btn_items, R.layout.custom_spinner_item);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
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
        ArrayAdapter<CharSequence> journalListDurationAdapter = ArrayAdapter.createFromResource(getContext(), R.array.journal_list_duration_items, R.layout.custom_spinner_item);
        journalListDurationAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        journalListDurationSpinner.setAdapter(journalListDurationAdapter);

        journalListDurationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String selectedValue = adapterView.getItemAtPosition(i).toString();

                selectedDateRangeInSpinner = getStartAndEndDates(selectedValue);
                System.out.println("selectedValue : " + selectedValue + "start date : " + selectedDateRangeInSpinner[0] + " end date : " + selectedDateRangeInSpinner[1]);

                ArrayList<Journal> journalsList = new ArrayList<>();
                String queryCategory = (selectedCategoryInSpinner == null || selectedCategoryInSpinner.toLowerCase().equals("all")) ? "" : selectedCategoryInSpinner;
                
                // Handle All Time option (null start date)
                if (selectedDateRangeInSpinner[0] == null) {
                    // Get all journals and filter by category if needed
                    List<Journal> allJournals = databaseHelper.journalDao().getAllJournal();
                    if (!queryCategory.isEmpty()) {
                        for (Journal journal : allJournals) {
                            if (journal.getJournalCategory() != null && 
                                journal.getJournalCategory().contains(queryCategory)) {
                                journalsList.add(journal);
                            }
                        }
                    } else {
                        journalsList.addAll(allJournals);
                    }
                } else {
                    // Normal date range
                    journalsList = (ArrayList<Journal>) databaseHelper.journalDao().getAllJournalsByDateAndCategory(
                        selectedDateRangeInSpinner[0], 
                        selectedDateRangeInSpinner[1], 
                        queryCategory);
                }

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
                
            case "All Time":
                // Set end date to current time
                endDate = calendar.getTime();
                
                // Set start date to null to indicate no lower bound
                startDate = null;
                break;

            default:
                startDate = null;
                endDate = null;
                break;
        }

        Date[] dateRange = {startDate, endDate};
        return dateRange;
    }
    
    /**
     * Implementation of Searchable interface
     * @param query The search query to filter journals by
     */
    @Override
    public void onSearch(String query) {
        this.currentSearchQuery = query;
        refreshJournalList();
    }
}