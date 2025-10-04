package com.diary.superjournalapp.screens.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.diary.superjournalapp.R;

/**
 * A fragment that provides access to all journal content types (All, Bookmarks, Tags)
 */
public class LibraryFragment extends Fragment {

    private Button allJournalsButton;
    private Button bookmarksButton;
    private Button tagsButton;
    
    public LibraryFragment() {
        // Required empty public constructor
    }

    public static LibraryFragment newInstance() {
        return new LibraryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize buttons
        allJournalsButton = view.findViewById(R.id.all_journals_button);
        bookmarksButton = view.findViewById(R.id.bookmarks_button);
        tagsButton = view.findViewById(R.id.tags_button);
        
        // Set click listeners
        allJournalsButton.setOnClickListener(v -> {
            loadFragment(new JournalListFragment());
        });
        
        bookmarksButton.setOnClickListener(v -> {
            loadFragment(BookmarkedJournalsFragment.getInstance());
        });
        
        tagsButton.setOnClickListener(v -> {
            loadFragment(TagSearchFragment.newInstance());
        });
        
        // By default, show the all journals view
        loadFragment(new JournalListFragment());
    }
    
    /**
     * Load a fragment within the library container
     * 
     * @param fragment The fragment to load
     */
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.library_content_container, fragment);
        transaction.commit();
    }
}
