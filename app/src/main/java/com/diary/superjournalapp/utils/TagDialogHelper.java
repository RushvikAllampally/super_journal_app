package com.diary.superjournalapp.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.database.DatabaseHelper;
import com.diary.superjournalapp.entity.Journal;
import com.google.android.flexbox.FlexboxLayout;

import java.util.List;
import java.util.Set;

/**
 * Helper class for displaying and managing tag dialog
 */
public class TagDialogHelper {

    private Dialog dialog;
    private Context context;
    private long journalId;
    private EditText tagInput;
    private FlexboxLayout tagsContainer;
    private FlexboxLayout suggestedTagsContainer;
    private Button addTagButton;
    private Button doneButton;
    private DatabaseHelper databaseHelper;
    
    /**
     * Constructor for the TagDialogHelper
     * 
     * @param context Context to create dialog
     * @param journalId The journal ID to manage tags for
     */
    public TagDialogHelper(Context context, long journalId) {
        this.context = context;
        this.journalId = journalId;
        this.databaseHelper = DatabaseHelper.getDb(context);
    }
    
    /**
     * Show the tag management dialog
     */
    public void showTagDialog() {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.tags_dialog);
        
        // Initialize views
        tagInput = dialog.findViewById(R.id.tag_input);
        tagsContainer = dialog.findViewById(R.id.tags_container);
        suggestedTagsContainer = dialog.findViewById(R.id.suggested_tags_container);
        addTagButton = dialog.findViewById(R.id.add_tag_button);
        doneButton = dialog.findViewById(R.id.done_button);
        
        // Set up button click listeners
        addTagButton.setOnClickListener(v -> {
            String tags = tagInput.getText().toString().trim();
            if (!tags.isEmpty()) {
                TagUtils.addTagsToJournal(context, journalId, tags);
                tagInput.setText("");
                refreshCurrentTags();
            }
        });
        
        doneButton.setOnClickListener(v -> dialog.dismiss());
        
        // Load current and suggested tags
        refreshCurrentTags();
        loadSuggestedTags();
        
        dialog.show();
    }
    
    /**
     * Refresh the list of current tags
     */
    private void refreshCurrentTags() {
        tagsContainer.removeAllViews();
        
        Journal journal = databaseHelper.journalDao().getMainJournalById(journalId);
        if (journal != null) {
            List<String> tags = TagUtils.getTagsForJournal(journal);
            
            for (String tag : tags) {
                addTagChip(tagsContainer, tag, true);
            }
        }
    }
    
    /**
     * Load suggested tags
     */
    private void loadSuggestedTags() {
        suggestedTagsContainer.removeAllViews();
        
        Set<String> allTags = TagUtils.getAllTags(context);
        Journal journal = databaseHelper.journalDao().getMainJournalById(journalId);
        
        if (journal != null) {
            List<String> currentTags = TagUtils.getTagsForJournal(journal);
            
            for (String tag : allTags) {
                if (!currentTags.contains(tag)) {
                    addTagChip(suggestedTagsContainer, tag, false);
                }
            }
        }
    }
    
    /**
     * Add a tag chip to the container
     * 
     * @param container The container to add the tag to
     * @param tag The tag text
     * @param removable Whether the tag can be removed
     */
    private void addTagChip(FlexboxLayout container, String tag, boolean removable) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View tagView = inflater.inflate(R.layout.tag_chip_item, container, false);
        
        TextView tagText = tagView.findViewById(R.id.tag_text);
        ImageView removeButton = tagView.findViewById(R.id.tag_remove_button);
        
        tagText.setText(tag);
        
        if (removable) {
            removeButton.setVisibility(View.VISIBLE);
            removeButton.setOnClickListener(v -> {
                TagUtils.removeTagFromJournal(context, journalId, tag);
                refreshCurrentTags();
            });
        } else {
            removeButton.setVisibility(View.GONE);
            
            // For suggested tags, clicking on the tag adds it
            tagView.setOnClickListener(v -> {
                TagUtils.addTagsToJournal(context, journalId, tag);
                refreshCurrentTags();
                loadSuggestedTags();
            });
        }
        
        container.addView(tagView);
    }
}
