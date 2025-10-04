package com.diary.superjournalapp.screens.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.database.DatabaseHelper;
import com.diary.superjournalapp.entity.Journal;
import com.diary.superjournalapp.recyclerviews.JournalRecyclerAdaptor;

import java.util.ArrayList;
import java.util.List;

public class BookmarkedJournalsFragment extends Fragment {

    private RecyclerView recyclerView;
    private JournalRecyclerAdaptor journalRecyclerAdaptor;
    private DatabaseHelper databaseHelper;
    private ImageView noBookmarksImage;
    private TextView noBookmarksText;
    private static BookmarkedJournalsFragment instance;

    public static BookmarkedJournalsFragment getInstance() {
        if (instance == null) {
            instance = new BookmarkedJournalsFragment();
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmarked_journals, container, false);
        
        recyclerView = view.findViewById(R.id.bookmarked_journals_recycler_view);
        noBookmarksImage = view.findViewById(R.id.no_bookmarks_image);
        noBookmarksText = view.findViewById(R.id.no_bookmarks_text);
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        databaseHelper = DatabaseHelper.getDb(requireContext());
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadBookmarkedJournals();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when returning to this fragment
        loadBookmarkedJournals();
    }
    
    /**
     * Load all bookmarked journals from the database
     */
    public void loadBookmarkedJournals() {
        List<Journal> bookmarkedJournals = databaseHelper.journalDao().getBookmarkedJournals();
        
        if (bookmarkedJournals.size() == 0) {
            // Show "no bookmarks" message
            recyclerView.setVisibility(View.GONE);
            noBookmarksImage.setVisibility(View.VISIBLE);
            noBookmarksText.setVisibility(View.VISIBLE);
        } else {
            // Show recycler view with bookmarked journals
            recyclerView.setVisibility(View.VISIBLE);
            noBookmarksImage.setVisibility(View.GONE);
            noBookmarksText.setVisibility(View.GONE);
            
            journalRecyclerAdaptor = new JournalRecyclerAdaptor(requireContext(), 
                    new ArrayList<>(bookmarkedJournals));
            recyclerView.setAdapter(journalRecyclerAdaptor);
        }
    }
    
    /**
     * Updates the recycler view when bookmark status changes
     */
    public static void notifyBookmarksChanged() {
        if (instance != null) {
            instance.loadBookmarkedJournals();
        }
    }
}
