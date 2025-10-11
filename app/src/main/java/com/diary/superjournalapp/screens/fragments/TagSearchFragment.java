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
import com.diary.superjournalapp.entity.Tag;
import com.diary.superjournalapp.recyclerviews.JournalRecyclerAdaptor;
import com.diary.superjournalapp.repository.TagRepository;
import com.diary.superjournalapp.screens.fragments.LibraryFragment.Searchable;
import com.diary.superjournalapp.utils.TagManager;
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
    private TagManager tagManager;
    private TagRepository tagRepository;
    private Set<Long> selectedTagIds = new HashSet<>();
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
        tagManager = new TagManager(requireContext());
        tagRepository = new TagRepository(requireContext());
        
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
            if (selectedTagIds.isEmpty()) {
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
        
        // Update clear button visibility
        updateClearButtonVisibility();
        
        // Refresh search results with current selection if any tags are selected
        if (!selectedTagIds.isEmpty()) {
            performSearch();
        }
    }
    
    /**
     * Load and display all available tags
     */
    private void loadAllTags() {
        allTagsContainer.removeAllViews();
        
        // Make sure we're getting ALL tags from the database
        List<Tag> allTags = tagRepository.getAllTags();
        
        if (allTags == null || allTags.isEmpty()) {
            // No tags available yet
            TextView noTagsText = new TextView(requireContext());
            noTagsText.setText("No tags available yet");
            noTagsText.setTextSize(16);
            allTagsContainer.addView(noTagsText);
        } else {
            // Log the number of tags found for debugging
            System.out.println("Found " + allTags.size() + " tags");
            
            // Add tag chips - make sure we're displaying all of them
            for (Tag tag : allTags) {
                addTagChip(tag);
                System.out.println("Adding tag: " + tag.getName() + " (ID: " + tag.getTagId() + ")");
            }
        }
    }
    
    /**
     * Add a tag chip to the container
     * 
     * @param tag The tag entity
     */
    private void addTagChip(Tag tag) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View tagView = inflater.inflate(R.layout.tag_chip_item, allTagsContainer, false);
        
        TextView tagText = tagView.findViewById(R.id.tag_text);
        tagText.setText(tag.getName());
        
        // Hide the remove button
        tagView.findViewById(R.id.tag_remove_button).setVisibility(View.GONE);
        
        // Set the initial state
        updateTagViewState(tagView, tag);
        
        // Toggle selection on click
        tagView.setOnClickListener(v -> {
            if (selectedTagIds.contains(tag.getTagId())) {
                selectedTagIds.remove(tag.getTagId());
            } else {
                selectedTagIds.add(tag.getTagId());
            }
            
            // Update the visual state
            updateTagViewState(tagView, tag);
            
            // Update clear button visibility
            updateClearButtonVisibility();
            
            // Show toast with current selection
            if (selectedTagIds.isEmpty()) {
                Toast.makeText(requireContext(), "No tags selected", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), selectedTagIds.size() + " tag(s) selected", Toast.LENGTH_SHORT).show();
            }
        });
        
        allTagsContainer.addView(tagView);
    }
    
    /**
     * Perform search based on selected tags
     */
    private void performSearch() {
        searchResults.clear();
        
        if (selectedTagIds.isEmpty()) {
            updateUIForResults();
            return;
        }
        
        // Get all journals with the selected tags using the new tag system
        List<Journal> journalsWithTags = new ArrayList<>();
        
        // Convert set to list for the repository method
        List<Long> tagIdsList = new ArrayList<>(selectedTagIds);
        
        // Get journals with ALL selected tags (AND operation)
        journalsWithTags = databaseHelper.journalDao().getJournalsWithAllTags(tagIdsList, tagIdsList.size());
        searchResults.addAll(journalsWithTags);
        
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
                    selectedTagIds.clear();
                    loadAllTags();
                    searchResults.clear();
                    updateUIForResults();
                    
                    // Update clear button visibility
                    updateClearButtonVisibility();
                })
                .setNegativeButton("No", null)
                .show();
    }
    
    /**
     * Implementation of Searchable interface for the Library search functionality
     * 
     * @param query The search query to filter journals by
     */
    
    @Override
    public void onSearch(String query) {
        this.currentSearchQuery = query;
        if (selectedTagIds != null && !selectedTagIds.isEmpty()) {
            performSearch(); // Refresh search with the new query
        }
    }
    
    /**
     * Update the visual state of a tag view based on selection state
     * 
     * @param tagView The tag view to update
     * @param tag The tag text
     */
    private void updateTagViewState(View tagView, Tag tag) {
        if (selectedTagIds.contains(tag.getTagId())) {
            tagView.setBackgroundColor(getResources().getColor(R.color.tag_selected));
            TextView textView = tagView.findViewById(R.id.tag_text);
            textView.setTextColor(getResources().getColor(R.color.white));
        } else {
            tagView.setBackgroundColor(getResources().getColor(R.color.tag_unselected));
            TextView textView = tagView.findViewById(R.id.tag_text);
            textView.setTextColor(getResources().getColor(R.color.on_surface));
        }
    }
    
    /**
     * Update clear button visibility based on whether tags are selected
     */
    private void updateClearButtonVisibility() {
        if (getView() != null) {
            View clearButton = getView().findViewById(R.id.clear_tags_button);
            if (clearButton != null) {
                if (!selectedTagIds.isEmpty()) {
                    clearButton.setVisibility(View.VISIBLE);
                    clearButton.setOnClickListener(v -> showClearTagsDialog());
                } else {
                    clearButton.setVisibility(View.GONE);
                }
            }
        }
    }
}
