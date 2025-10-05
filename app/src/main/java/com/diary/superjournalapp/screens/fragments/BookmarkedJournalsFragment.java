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
    private View progressIndicator;
    
    // Track all active instances for notification
    private static final List<BookmarkedJournalsFragment> activeInstances = new ArrayList<>();

    // This method remains for backward compatibility with existing code
    public static BookmarkedJournalsFragment getInstance() {
        BookmarkedJournalsFragment fragment = new BookmarkedJournalsFragment();
        return fragment;
    }
    
    @Override
    public void onAttach(@NonNull android.content.Context context) {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        
        recyclerView = view.findViewById(R.id.bookmarks_recycler_view);
        View emptyView = view.findViewById(R.id.empty_view);
        if (emptyView != null) {
            ImageView imageView = emptyView.findViewById(R.id.no_bookmarks_image);
            if (imageView != null) {
                noBookmarksImage = imageView;
            }
            
            TextView textView = emptyView.findViewById(R.id.no_bookmarks_text);
            if (textView != null) {
                noBookmarksText = textView;
            }
        }
        
        progressIndicator = view.findViewById(R.id.progress_indicator);
        
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
        if (databaseHelper == null || !isAdded()) return;
        
        // Show progress indicator while loading
        if (progressIndicator != null) {
            progressIndicator.setVisibility(View.VISIBLE);
        }
        
        List<Journal> bookmarkedJournals = databaseHelper.journalDao().getBookmarkedJournals();
        View emptyView = getView() != null ? getView().findViewById(R.id.empty_view) : null;
        
        // Hide progress indicator now that loading is complete
        if (progressIndicator != null) {
            progressIndicator.setVisibility(View.GONE);
        }
        
        if (bookmarkedJournals.size() == 0) {
            // Show "no bookmarks" message
            if (recyclerView != null) {
                recyclerView.setVisibility(View.GONE);
            }
            if (emptyView != null) {
                emptyView.setVisibility(View.VISIBLE);
            }
        } else {
            // Show recycler view with bookmarked journals
            if (recyclerView != null) {
                recyclerView.setVisibility(View.VISIBLE);
                journalRecyclerAdaptor = new JournalRecyclerAdaptor(requireContext(), 
                        new ArrayList<>(bookmarkedJournals));
                recyclerView.setAdapter(journalRecyclerAdaptor);
            }
            if (emptyView != null) {
                emptyView.setVisibility(View.GONE);
            }
        }
    }
    
    /**
     * Updates the recycler view when bookmark status changes
     * Now refreshes all active instances
     */
    public static void notifyBookmarksChanged() {
        synchronized (activeInstances) {
            for (BookmarkedJournalsFragment fragment : activeInstances) {
                if (fragment.isAdded() && fragment.getActivity() != null) {
                    fragment.getActivity().runOnUiThread(() -> {
                        fragment.loadBookmarkedJournals();
                    });
                }
            }
        }
    }
}