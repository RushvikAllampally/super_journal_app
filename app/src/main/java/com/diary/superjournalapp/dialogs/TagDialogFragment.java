package com.diary.superjournalapp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.adapters.TagChipAdapter;
import com.diary.superjournalapp.entity.Tag;
import com.diary.superjournalapp.repository.TagRepository;
import com.diary.superjournalapp.utils.TagManager;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dialog fragment for managing tags on a journal entry
 */
public class TagDialogFragment extends DialogFragment {
    
    private static final String ARG_JOURNAL_ID = "journal_id";
    private static final String ARG_TEMPORARY_TAGS = "temporary_tags";
    private static final String TAG_SEPARATOR = ",";
    
    private long journalId;
    private ArrayList<String> temporaryTags;  // For unsaved journals
    private boolean isTemporaryMode = false;  // True when journal is not yet saved
    private TagManager tagManager;
    private TagRepository tagRepository;
    private TagUpdateListener tagUpdateListener;
    
    private EditText tagInputField;
    private Button addTagButton;
    private Button doneButton;
    private ChipGroup currentTagsGroup;
    private ChipGroup suggestedTagsGroup;
    private TextView emptyTagsMessage;
    
    /**
     * Interface for updating temporary tags
     */
    public interface TagUpdateListener {
        void onTagsUpdated(ArrayList<String> tags);
    }
    
    /**
     * Create a new instance of the dialog for a saved journal
     * 
     * @param journalId The journal ID to manage tags for
     * @return A new instance of TagDialogFragment
     */
    public static TagDialogFragment newInstance(long journalId) {
        TagDialogFragment fragment = new TagDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_JOURNAL_ID, journalId);
        fragment.setArguments(args);
        return fragment;
    }
    
    /**
     * Create a new instance of the dialog for an unsaved journal
     * Uses temporary tag storage that will be persisted when journal is saved
     * 
     * @param temporaryTags Current temporary tags for the unsaved journal
     * @param listener Listener to receive tag updates
     * @return A new instance of TagDialogFragment
     */
    public static TagDialogFragment newInstanceTemporary(ArrayList<String> temporaryTags, TagUpdateListener listener) {
        TagDialogFragment fragment = new TagDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_JOURNAL_ID, 0);  // 0 indicates unsaved journal
        args.putStringArrayList(ARG_TEMPORARY_TAGS, temporaryTags != null ? temporaryTags : new ArrayList<>());
        fragment.setArguments(args);
        fragment.tagUpdateListener = listener;
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (getArguments() != null) {
            journalId = getArguments().getLong(ARG_JOURNAL_ID);
            temporaryTags = getArguments().getStringArrayList(ARG_TEMPORARY_TAGS);
            
            // If journalId is 0, we're in temporary mode
            isTemporaryMode = (journalId == 0);
            
            if (!isTemporaryMode) {
                tagManager = new TagManager(requireContext());
                tagRepository = new TagRepository(requireContext());
            } else {
                // Still need these for suggested tags even in temporary mode
                tagManager = new TagManager(requireContext());
                if (temporaryTags == null) {
                    temporaryTags = new ArrayList<>();
                }
            }
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tag_dialog, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        tagInputField = view.findViewById(R.id.tag_input_field);
        addTagButton = view.findViewById(R.id.add_tag_button);
        doneButton = view.findViewById(R.id.done_button);
        currentTagsGroup = view.findViewById(R.id.current_tags_group);
        suggestedTagsGroup = view.findViewById(R.id.suggested_tags_group);
        emptyTagsMessage = view.findViewById(R.id.empty_tags_message);
        
        // Setup click listeners
        addTagButton.setOnClickListener(v -> {
            addTagsFromInput();
        });
        
        doneButton.setOnClickListener(v -> {
            dismiss();
        });
        
        // Load tags
        loadCurrentTags();
        loadSuggestedTags();
    }
    
    @Override
    public void onStart() {
        super.onStart();
        
        // Make dialog full width
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
    
    /**
     * Add tags from the input field
     */
    private void addTagsFromInput() {
        String input = tagInputField.getText().toString().trim();
        if (input.isEmpty()) {
            return;
        }
        
        if (isTemporaryMode) {
            // Add to temporary list
            List<String> newTags = Arrays.stream(input.split(TAG_SEPARATOR))
                    .map(String::trim)
                    .filter(tag -> !tag.isEmpty() && !temporaryTags.contains(tag))
                    .collect(Collectors.toList());
            temporaryTags.addAll(newTags);
            
            // Notify listener
            if (tagUpdateListener != null) {
                tagUpdateListener.onTagsUpdated(temporaryTags);
            }
        } else {
            // Add to database
            tagManager.addTagsToJournal(journalId, input);
        }
        
        // Clear the input
        tagInputField.setText("");
        
        // Refresh the tags display
        loadCurrentTags();
        loadSuggestedTags();
    }
    
    /**
     * Load and display the current tags for the journal
     */
    private void loadCurrentTags() {
        if (isTemporaryMode) {
            // Show temporary tags
            currentTagsGroup.removeAllViews();
            
            for (String tagName : temporaryTags) {
                com.google.android.material.chip.Chip chip = new com.google.android.material.chip.Chip(requireContext());
                chip.setText(tagName);
                chip.setCloseIconVisible(true);
                chip.setTextSize(12);
                chip.setChipBackgroundColorResource(android.R.color.holo_blue_bright);
                chip.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(0xFFE1F5FE));
                chip.setChipStrokeWidth(1);
                chip.setChipStrokeColor(android.content.res.ColorStateList.valueOf(0xFF81D4FA));
                chip.setTextColor(0xFF0277BD);
                chip.setCloseIconTint(android.content.res.ColorStateList.valueOf(0xFF0277BD));
                // Increase minimum chip height for better touch targets on mobile
                chip.setChipMinHeight(40);
                // Add proper padding for the chip content
                chip.setChipStartPadding(12);
                chip.setChipEndPadding(12);
                chip.setTextEndPadding(4);
                chip.setTextStartPadding(4);
                chip.setOnCloseIconClickListener(v -> {
                    temporaryTags.remove(tagName);
                    if (tagUpdateListener != null) {
                        tagUpdateListener.onTagsUpdated(temporaryTags);
                    }
                    loadCurrentTags();
                    loadSuggestedTags();
                });
                currentTagsGroup.addView(chip);
            }
            
            // Show empty message if needed
            if (temporaryTags.isEmpty()) {
                emptyTagsMessage.setVisibility(View.VISIBLE);
            } else {
                emptyTagsMessage.setVisibility(View.GONE);
            }
        } else {
            // Load from database
            List<Tag> currentTags = tagManager.getTagsForJournal(journalId);
            
            // Setup the chip adapter
            TagChipAdapter adapter = new TagChipAdapter(requireContext(), currentTagsGroup)
                    .setShowCloseIcon(true)
                    .setOnTagCloseListener(tag -> {
                        // Remove the tag when the close icon is clicked
                        tagRepository.removeTagFromJournal(journalId, tag.getTagId());
                        loadCurrentTags();
                        loadSuggestedTags();
                    });
            
            // Set the tags
            adapter.setTags(currentTags);
            
            // Show empty message if needed
            if (currentTags.isEmpty()) {
                emptyTagsMessage.setVisibility(View.VISIBLE);
            } else {
                emptyTagsMessage.setVisibility(View.GONE);
            }
        }
    }
    
    /**
     * Load and display suggested tags
     */
    private void loadSuggestedTags() {
        List<Tag> allTags = tagManager.getAllTags();
        
        if (isTemporaryMode) {
            // Filter out tags already in temporary list
            suggestedTagsGroup.removeAllViews();
            
            for (Tag tag : allTags) {
                if (!temporaryTags.contains(tag.getName())) {
                    com.google.android.material.chip.Chip chip = new com.google.android.material.chip.Chip(requireContext());
                    chip.setText(tag.getName());
                    chip.setClickable(true);
                    chip.setCheckable(false);
                    chip.setTextSize(12);
                    chip.setChipBackgroundColorResource(android.R.color.holo_blue_bright);
                    chip.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(0xFFE1F5FE));
                    chip.setChipStrokeWidth(1);
                    chip.setChipStrokeColor(android.content.res.ColorStateList.valueOf(0xFF81D4FA));
                    chip.setTextColor(0xFF0277BD);
                    // Increase minimum chip height for better touch targets on mobile
                    chip.setChipMinHeight(40);
                    // Add proper padding for the chip content
                    chip.setChipStartPadding(12);
                    chip.setChipEndPadding(12);
                    chip.setTextEndPadding(4);
                    chip.setTextStartPadding(4);
                    chip.setOnClickListener(v -> {
                        temporaryTags.add(tag.getName());
                        if (tagUpdateListener != null) {
                            tagUpdateListener.onTagsUpdated(temporaryTags);
                        }
                        loadCurrentTags();
                        loadSuggestedTags();
                    });
                    suggestedTagsGroup.addView(chip);
                }
            }
        } else {
            // Load from database
            List<Tag> currentTags = tagManager.getTagsForJournal(journalId);
            
            // Filter out tags that are already added to the journal
            List<Tag> suggestedTags = new ArrayList<>();
            for (Tag tag : allTags) {
                boolean isAlreadyAdded = false;
                for (Tag currentTag : currentTags) {
                    if (currentTag.getTagId() == tag.getTagId()) {
                        isAlreadyAdded = true;
                        break;
                    }
                }
                if (!isAlreadyAdded) {
                    suggestedTags.add(tag);
                }
            }
            
            // Setup the chip adapter
            TagChipAdapter adapter = new TagChipAdapter(requireContext(), suggestedTagsGroup)
                    .setOnTagClickListener(tag -> {
                        // Add the tag when clicked
                        tagManager.addTagToJournal(journalId, tag.getName());
                        loadCurrentTags();
                        loadSuggestedTags();
                    });
            
            // Set the tags
            adapter.setTags(suggestedTags);
        }
    }
}
