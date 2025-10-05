package com.diary.superjournalapp.screens.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import com.diary.superjournalapp.utils.TagUtils;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Fragment for searching journals by tags
 */
public class TagSearchFragment extends Fragment implements Searchable {

    private FlexboxLayout allTagsContainer;
    private ImageButton searchByTagButton;
    private RecyclerView recyclerView;
    private TextView searchResultsLabel;
    private TextView noResultsText;
    private DatabaseHelper databaseHelper;
    private Set<String> selectedTags = new HashSet<>();
    private List<Journal> searchResults = new ArrayList<>();
    private String currentSearchQuery = "";
    
    public TagSearchFragment() {
        // Required empty public constructor
    }

    public static TagSearchFragment newInstance() {
        return new TagSearchFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tag_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        databaseHelper = DatabaseHelper.getDb(requireContext());
        
        // Initialize views
        allTagsContainer = view.findViewById(R.id.all_tags_container);
        searchByTagButton = view.findViewById(R.id.search_by_tag_button);
        recyclerView = view.findViewById(R.id.tag_search_recycler_view);
        searchResultsLabel = view.findViewById(R.id.search_results_label);
        noResultsText = view.findViewById(R.id.no_results_text);
        
        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Set up search button
        searchByTagButton.setOnClickListener(v -> {
            if (selectedTags.isEmpty()) {
                Toast.makeText(requireContext(), "Please select at least one tag", Toast.LENGTH_SHORT).show();
            } else {
                performSearch();
            }
        });
        
        // Load all available tags
        loadAllTags();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        // Refresh tags when returning to this fragment
        loadAllTags();
        
        // Add a clear button to the top
        if (getView() != null && !selectedTags.isEmpty()) {
            View clearButton = getView().findViewById(R.id.clear_tags_button);
            if (clearButton != null) {
                clearButton.setVisibility(View.VISIBLE);
                clearButton.setOnClickListener(v -> showClearTagsDialog());
            }
            
            // Refresh search results with current selection
            performSearch();
        }
    }
    
    /**
     * Load and display all available tags
     */
    private void loadAllTags() {
        allTagsContainer.removeAllViews();
        
        Set<String> allTags = TagUtils.getAllTags(requireContext());
        
        if (allTags.isEmpty()) {
            // No tags available yet
            TextView noTagsText = new TextView(requireContext());
            noTagsText.setText("No tags available yet");
            noTagsText.setTextSize(16);
            allTagsContainer.addView(noTagsText);
        } else {
            // Add tag chips
            for (String tag : allTags) {
                addTagChip(tag);
            }
        }
    }
    
    /**
     * Add a tag chip to the container
     * 
     * @param tag The tag text
     */
    private void addTagChip(String tag) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View tagView = inflater.inflate(R.layout.tag_chip_item, allTagsContainer, false);
        
        TextView tagText = tagView.findViewById(R.id.tag_text);
        tagText.setText(tag);
        
        // Hide the remove button
        tagView.findViewById(R.id.tag_remove_button).setVisibility(View.GONE);
        
        // Set the initial state
        updateTagViewState(tagView, tag);
        
        // Toggle selection on click
        tagView.setOnClickListener(v -> {
            if (selectedTags.contains(tag)) {
                selectedTags.remove(tag);
            } else {
                selectedTags.add(tag);
            }
            
            // Update the visual state
            updateTagViewState(tagView, tag);
            
            // Show toast with current selection
            if (selectedTags.isEmpty()) {
                Toast.makeText(requireContext(), "No tags selected", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), selectedTags.size() + " tag(s) selected", Toast.LENGTH_SHORT).show();
            }
        });
        
        allTagsContainer.addView(tagView);
    }
    
    /**
     * Perform search based on selected tags
     */
    private void performSearch() {
        searchResults.clear();
        
        if (selectedTags.isEmpty()) {
            updateUIForResults();
            return;
        }
        
        // Search for journals that contain ALL selected tags (AND operation)
        List<Journal> allJournals = databaseHelper.journalDao().getAllJournal();
        for (Journal journal : allJournals) {
            if (journalContainsAllTags(journal, selectedTags)) {
                searchResults.add(journal);
            }
        }
        
        // Apply text search filter if it exists
        if (currentSearchQuery != null && !currentSearchQuery.isEmpty()) {
            List<Journal> filteredResults = new ArrayList<>();
            String lowerCaseQuery = currentSearchQuery.toLowerCase();
            
            for (Journal journal : searchResults) {
                // Search in title
                if (journal.getTitle() != null && 
                    journal.getTitle().toLowerCase().contains(lowerCaseQuery)) {
                    filteredResults.add(journal);
                    continue;
                }
                
                // Search in content
                if (journal.getJournalStartText() != null && 
                    journal.getJournalStartText().toLowerCase().contains(lowerCaseQuery)) {
                    filteredResults.add(journal);
                    continue;
                }
                
                // Search in category
                if (journal.getJournalCategory() != null && 
                    journal.getJournalCategory().toLowerCase().contains(lowerCaseQuery)) {
                    filteredResults.add(journal);
                }
            }
            
            searchResults = filteredResults;
        }
        
        updateUIForResults();
    }
    
    /**
     * Update UI to show search results
     */
    private void updateUIForResults() {
        if (searchResults.isEmpty()) {
            searchResultsLabel.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            noResultsText.setVisibility(View.VISIBLE);
        } else {
            searchResultsLabel.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            noResultsText.setVisibility(View.GONE);
            
            // Update results count
            searchResultsLabel.setText("Journal Results (" + searchResults.size() + ")");
            
            // Update RecyclerView
            JournalRecyclerAdaptor adapter = new JournalRecyclerAdaptor(
                    requireContext(), new ArrayList<>(searchResults));
            recyclerView.setAdapter(adapter);
        }
    }
    
    /**
     * Show dialog to clear all selected tags
     */
    private void showClearTagsDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Clear Tags")
                .setMessage("Do you want to clear all selected tags?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    selectedTags.clear();
                    loadAllTags();
                    searchResults.clear();
                    updateUIForResults();
                    
                    // Hide clear button
                    if (getView() != null) {
                        View clearButton = getView().findViewById(R.id.clear_tags_button);
                        if (clearButton != null) {
                            clearButton.setVisibility(View.GONE);
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
    
    /**
     * Check if a journal contains all the given tags
     * 
     * @param journal The journal to check
     * @param tags The set of tags to look for
     * @return True if the journal has all the specified tags
     */
    private boolean journalContainsAllTags(Journal journal, Set<String> tags) {
        // If journal doesn't have tags or tags is empty
        if (journal.getTags() == null || journal.getTags().isEmpty() || tags.isEmpty()) {
            return false;
        }
        
        // Split journal tags and convert to set for efficient lookup
        Set<String> journalTags = new HashSet<>();
        String[] tagArray = journal.getTags().split(",");
        for (String tag : tagArray) {
            journalTags.add(tag.trim());
        }
        
        // Check if all required tags are in the journal's tags
        for (String tag : tags) {
            if (!journalTags.contains(tag)) {
                return false; // Missing at least one required tag
            }
        }
        
        return true; // All tags were found
    }
    
    /**
     * Implementation of Searchable interface
     * 
     * @param query The search query to filter journals by
     */
    @Override
    public void onSearch(String query) {
        this.currentSearchQuery = query;
        if (selectedTags != null && !selectedTags.isEmpty()) {
            performSearch(); // Refresh search with the new query
        }
    }
    
    /**
     * Update the visual state of a tag view based on selection state
     * 
     * @param tagView The tag view to update
     * @param tag The tag text
     */
    private void updateTagViewState(View tagView, String tag) {
        if (selectedTags.contains(tag)) {
            tagView.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
            TextView textView = tagView.findViewById(R.id.tag_text);
            textView.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            tagView.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
            TextView textView = tagView.findViewById(R.id.tag_text);
            textView.setTextColor(getResources().getColor(android.R.color.white));
        }
    }
}
