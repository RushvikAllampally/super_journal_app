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
import java.util.List;

/**
 * Dialog fragment for managing tags on a journal entry
 */
public class TagDialogFragment extends DialogFragment {
    
    private static final String ARG_JOURNAL_ID = "journal_id";
    private static final String TAG_SEPARATOR = ",";
    
    private long journalId;
    private TagManager tagManager;
    private TagRepository tagRepository;
    
    private EditText tagInputField;
    private Button addTagButton;
    private Button doneButton;
    private ChipGroup currentTagsGroup;
    private ChipGroup suggestedTagsGroup;
    private TextView emptyTagsMessage;
    
    /**
     * Create a new instance of the dialog
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
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (getArguments() != null) {
            journalId = getArguments().getLong(ARG_JOURNAL_ID);
        }
        
        tagManager = new TagManager(requireContext());
        tagRepository = new TagRepository(requireContext());
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
        
        // Add the tags
        tagManager.addTagsToJournal(journalId, input);
        
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
    
    /**
     * Load and display suggested tags
     */
    private void loadSuggestedTags() {
        List<Tag> currentTags = tagManager.getTagsForJournal(journalId);
        List<Tag> allTags = tagManager.getAllTags();
        
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
