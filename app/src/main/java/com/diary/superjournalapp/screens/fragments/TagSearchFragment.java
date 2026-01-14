package com.diary.superjournalapp.screens.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private Button searchByTagButton;
    private RecyclerView recyclerView;
    private TextView searchResultsLabel;
    private TextView noResultsText;
    private TextView instructionsText;
    private TextView selectedCountText;
    private View tagsScrollContainer;
    private View tagsCardContainer;
    private View noTagsContainer;
    private View actionContainer;
    private View divider;
    private DatabaseHelper databaseHelper;
    private TagManager tagManager;
    private TagRepository tagRepository;
    private Set<Long> selectedTagIds = new HashSet<>();
    private List<Journal> searchResults = new ArrayList<>();
    private String currentSearchQuery = "";
    private List<Tag> allTagsList = new ArrayList<>();
    
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
        instructionsText = view.findViewById(R.id.instructions_text);
        selectedCountText = view.findViewById(R.id.selected_count_text);
        tagsScrollContainer = view.findViewById(R.id.tags_scroll_container);
        tagsCardContainer = view.findViewById(R.id.tags_card_container);
        noTagsContainer = view.findViewById(R.id.no_tags_container);
        actionContainer = view.findViewById(R.id.action_container);
        divider = view.findViewById(R.id.divider);
        
        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Set up search button
        searchByTagButton.setOnClickListener(v -> performSearch());
        
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
     * Load all available tags
     */
    private void loadAllTags() {
        // Get all tags from both TagManager and TagRepository for comparison
        List<Tag> tagsFromManager = tagManager.getAllTags();
        List<Tag> tagsFromRepository = tagRepository.getAllTags();
        
        // Use the one with more tags (should be the same, but just in case)
        allTagsList = (tagsFromManager.size() >= tagsFromRepository.size()) ? 
                      tagsFromManager : tagsFromRepository;
        
        // Display all tags
        displayTags();
    }
    
    /**
     * Display all tags
     */
    private void displayTags() {
        allTagsContainer.removeAllViews();
        
        if (allTagsList.isEmpty()) {
            // Show empty state
            tagsScrollContainer.setVisibility(View.GONE);
            tagsCardContainer.setVisibility(View.GONE);
            noTagsContainer.setVisibility(View.VISIBLE);
            instructionsText.setVisibility(View.GONE);
            actionContainer.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        } else {
            // Show tags
            tagsScrollContainer.setVisibility(View.VISIBLE);
            tagsCardContainer.setVisibility(View.VISIBLE);
            noTagsContainer.setVisibility(View.GONE);
            instructionsText.setVisibility(View.VISIBLE);
            actionContainer.setVisibility(View.VISIBLE);
            divider.setVisibility(View.GONE);
            
            // Add all tag chips
            for (Tag tag : allTagsList) {
                addTagChip(tag);
            }
            
            // Update the UI state
            updateSelectionUI();
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
            
            // Update selection UI
            updateSelectionUI();
        });
        
        allTagsContainer.addView(tagView);
    }
    
    /**
     * Perform search based on selected tags
     */
    private void performSearch() {
        searchResults.clear();
        
        if (selectedTagIds.isEmpty()) {
            Toast.makeText(requireContext(), "Please select at least one tag", Toast.LENGTH_SHORT).show();
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
            divider.setVisibility(View.GONE);
        } else {
            searchResultsLabel.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            noResultsText.setVisibility(View.GONE);
            divider.setVisibility(View.VISIBLE);
            
            // Update results count
            searchResultsLabel.setText("Journal Results (" + searchResults.size() + ")");
            
            // Update RecyclerView
            JournalRecyclerAdaptor adapter = new JournalRecyclerAdaptor(
                    requireContext(), new ArrayList<>(searchResults));
            recyclerView.setAdapter(adapter);
            
            // Make sure tag section is compact to give more space to results
            if (tagsCardContainer != null && tagsCardContainer.getLayoutParams() != null) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tagsCardContainer.getLayoutParams();
                params.bottomMargin = 4;
                tagsCardContainer.setLayoutParams(params);
            }
            
            // Ensure RecyclerView has the correct layout parameters for visibility
            if (recyclerView.getLayoutParams() instanceof LinearLayout.LayoutParams) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) recyclerView.getLayoutParams();
                layoutParams.height = 0;
                layoutParams.weight = 1;
                recyclerView.setLayoutParams(layoutParams);
            }
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
     * @param tagView The tag view
     * @param tag The tag data
     */
    private void updateTagViewState(View tagView, Tag tag) {
        // Get the MaterialCardView from the parent
        com.google.android.material.card.MaterialCardView cardView = 
                (com.google.android.material.card.MaterialCardView) tagView;
        TextView tagText = tagView.findViewById(R.id.tag_text);
        ImageView tagIcon = tagView.findViewById(R.id.tag_icon);
        
        // Remove animations
        cardView.setClickable(true);
        cardView.setCheckable(false);
        cardView.setStateListAnimator(null);
        
        if (selectedTagIds.contains(tag.getTagId())) {
            // Selected state - using primary text color styling
            cardView.setCardBackgroundColor(getResources().getColor(R.color.text_primary));
            cardView.setStrokeColor(getResources().getColor(R.color.text_primary));
            cardView.setStrokeWidth(1);
            cardView.setCardElevation(0f); // No elevation to match journal modal
            tagText.setTextColor(getResources().getColor(android.R.color.white));
            tagText.setTypeface(tagText.getTypeface(), android.graphics.Typeface.NORMAL);
            if (tagIcon != null) {
                tagIcon.setColorFilter(getResources().getColor(android.R.color.white));
            }
        } else {
            // Unselected state - using transparent with text_primary stroke
            cardView.setCardBackgroundColor(getResources().getColor(android.R.color.transparent));
            cardView.setStrokeColor(getResources().getColor(R.color.text_primary));
            cardView.setStrokeWidth(1);
            cardView.setCardElevation(0f);
            tagText.setTextColor(getResources().getColor(R.color.text_primary));
            tagText.setTypeface(null, android.graphics.Typeface.NORMAL);
            if (tagIcon != null) {
                tagIcon.setColorFilter(getResources().getColor(R.color.text_primary));
            }
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
    
    /**
     * Update the selection UI (count text and button visibility)
     */
    private void updateSelectionUI() {
        int count = selectedTagIds.size();
        
        if (count == 0) {
            selectedCountText.setText("Tap tags to select them");
            searchByTagButton.setVisibility(View.GONE);
            updateClearButtonVisibility();
        } else {
            selectedCountText.setText(count + (count == 1 ? " tag selected" : " tags selected"));
            searchByTagButton.setVisibility(View.VISIBLE);
            updateClearButtonVisibility();
        }
    }
}
