package com.diary.superjournalapp.screens.tags;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.base.ThemedActivity;
import com.diary.superjournalapp.entity.Tag;
import com.diary.superjournalapp.repository.TagRepository;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for managing tags (edit, delete)
 */
public class TagManagementActivity extends ThemedActivity implements TagManagementAdapter.TagInteractionListener {

    private TagRepository tagRepository;
    private RecyclerView tagRecyclerView;
    private TagManagementAdapter tagAdapter;
    private TextInputEditText searchEditText;
    private LinearLayout emptyStateContainer;
    private List<Tag> allTags = new ArrayList<>();
    private List<Tag> filteredTags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_management);

        // Initialize repository
        tagRepository = new TagRepository(this);

        // Initialize views
        tagRecyclerView = findViewById(R.id.tag_recycler_view);
        searchEditText = findViewById(R.id.search_edit_text);
        emptyStateContainer = findViewById(R.id.empty_state_container);
        ImageButton backButton = findViewById(R.id.back_button);

        // Set up adapter
        tagAdapter = new TagManagementAdapter(filteredTags, this);
        tagRecyclerView.setAdapter(tagAdapter);

        // Set up search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTags(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Set up back button
        backButton.setOnClickListener(v -> finish());

        // Load tags
        loadTags();
    }

    /**
     * Load all tags from the repository
     */
    private void loadTags() {
        // Get all tags
        allTags = tagRepository.getAllTags();
        
        // Update the filtered list
        filterTags(searchEditText.getText().toString());
    }

    /**
     * Filter tags based on search query
     * @param query Search query
     */
    private void filterTags(String query) {
        filteredTags.clear();
        
        if (query == null || query.trim().isEmpty()) {
            // If no query, show all tags
            filteredTags.addAll(allTags);
        } else {
            // Filter tags based on query
            String lowercaseQuery = query.toLowerCase().trim();
            for (Tag tag : allTags) {
                if (tag.getName().toLowerCase().contains(lowercaseQuery)) {
                    filteredTags.add(tag);
                }
            }
        }
        
        // Update UI
        updateUI();
    }

    /**
     * Update the UI based on the filtered tags
     */
    private void updateUI() {
        // Show/hide empty state
        if (filteredTags.isEmpty()) {
            emptyStateContainer.setVisibility(View.VISIBLE);
            tagRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateContainer.setVisibility(View.GONE);
            tagRecyclerView.setVisibility(View.VISIBLE);
            
            // Update adapter
            tagAdapter.updateTags(filteredTags);
        }
    }

    /**
     * Edit tag implementation
     * @param tag Tag to edit
     */
    @Override
    public void onEditTag(Tag tag) {
        // Create container layout with padding
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int paddingDp = 24;
        int paddingPx = (int) (paddingDp * getResources().getDisplayMetrics().density);
        container.setPadding(paddingPx, paddingPx/2, paddingPx, paddingPx/2);
        
        // Create tag icon and header
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setGravity(Gravity.CENTER_VERTICAL);
        
        ImageView tagIcon = new ImageView(this);
        tagIcon.setImageResource(R.drawable.tag_24);
        tagIcon.setColorFilter(getResources().getColor(R.color.text_primary));
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                (int) (24 * getResources().getDisplayMetrics().density),
                (int) (24 * getResources().getDisplayMetrics().density));
        iconParams.setMarginEnd(paddingPx/2);
        tagIcon.setLayoutParams(iconParams);
        
        // Add icon to header layout
        headerLayout.addView(tagIcon);
        
        // Create title text
        TextView titleText = new TextView(this);
        titleText.setText("Edit Tag");
        titleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        titleText.setTypeface(null, Typeface.BOLD);
        titleText.setTextColor(getResources().getColor(R.color.text_primary));
        
        // Add title to header layout
        headerLayout.addView(titleText);
        
        // Add header layout to container
        container.addView(headerLayout);
        
        // Create edit text with material styling
        final EditText editTagInput = new EditText(this);
        editTagInput.setText(tag.getName());
        editTagInput.setSelection(editTagInput.getText().length());
        editTagInput.setHint("Tag name");
        editTagInput.setBackground(getDrawable(R.drawable.edit_text_background));
        editTagInput.setPadding(paddingPx/2, paddingPx/2, paddingPx/2, paddingPx/2);
        
        // Set layout parameters for edit text
        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        inputParams.setMargins(0, paddingPx/2, 0, paddingPx/2);
        editTagInput.setLayoutParams(inputParams);
        
        // Add edit text to container
        container.addView(editTagInput);
        
        // Create dialog using AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(null) // Remove builder title since we have our own title view
                .setIcon(null) // Remove icon since we have our own
                .setView(container)
                .setPositiveButton("Save", null) // Set in onShowListener below
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        
        // Create and show dialog
        AlertDialog dialog = builder.create();
        
        // Override the positive button click to validate before dismissing
        dialog.setOnShowListener(dialogInterface -> {
            Button saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            saveButton.setTextColor(getResources().getColor(R.color.text_primary));
            saveButton.setTypeface(null, Typeface.BOLD);
            
            Button cancelButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            cancelButton.setTextColor(getResources().getColor(R.color.text_primary));
            
            saveButton.setOnClickListener(v -> {
                String newName = editTagInput.getText().toString().trim();
                
                // Validate input
                if (newName.isEmpty()) {
                    editTagInput.setError("Tag name cannot be empty");
                    return;
                }
                
                // Check if name exists (but isn't the current tag)
                for (Tag existingTag : allTags) {
                    if (existingTag.getName().equalsIgnoreCase(newName) && existingTag.getTagId() != tag.getTagId()) {
                        editTagInput.setError("Tag already exists");
                        return;
                    }
                }
                
                // Update tag name
                tag.setName(newName);
                updateTag(tag);
                dialog.dismiss();
                
                // Show success message
                Toast.makeText(TagManagementActivity.this, "Tag updated successfully", Toast.LENGTH_SHORT).show();
            });
        });
        
        dialog.show();
    }

    /**
     * Delete tag implementation
     * @param tag Tag to delete
     */
    @Override
    public void onDeleteTag(Tag tag) {
        // Create container layout with padding
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int paddingDp = 24;
        int paddingPx = (int) (paddingDp * getResources().getDisplayMetrics().density);
        container.setPadding(paddingPx, paddingPx/2, paddingPx, paddingPx/2);
        
        // Create header layout with icon and title side by side
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setGravity(Gravity.START); // Left-aligned like the edit modal
        LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        headerParams.setMargins(0, paddingPx/2, 0, paddingPx/2);
        headerLayout.setLayoutParams(headerParams);
        
        // Create warning icon
        ImageView warningIcon = new ImageView(this);
        warningIcon.setImageResource(R.drawable.ic_delete_outline);
        warningIcon.setColorFilter(getResources().getColor(R.color.error));
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                (int) (24 * getResources().getDisplayMetrics().density),
                (int) (24 * getResources().getDisplayMetrics().density));
        iconParams.setMarginEnd(paddingPx/2);
        iconParams.gravity = Gravity.CENTER_VERTICAL;
        warningIcon.setLayoutParams(iconParams);
        headerLayout.addView(warningIcon);
        
        // Create title text
        TextView titleText = new TextView(this);
        titleText.setText("Delete Tag");
        titleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        titleText.setTypeface(null, Typeface.BOLD);
        titleText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        titleText.setGravity(Gravity.CENTER_VERTICAL);
        headerLayout.addView(titleText);
        
        // Add header to container
        container.addView(headerLayout);
        
        // Create message text
        TextView messageText = new TextView(this);
        messageText.setText("Are you sure you want to delete \"" + tag.getName() + "\"? This will remove the tag from all journals.");
        messageText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        messageText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START); // Left-aligned like the edit modal
        LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        messageParams.setMargins(0, 0, 0, paddingPx);
        messageText.setLayoutParams(messageParams);
        container.addView(messageText);
        
        // Create dialog using AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(null) // Remove builder title since we have our own
                .setIcon(null) // Remove icon since we have our own
                .setView(container)
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Delete tag
                    tagRepository.deleteTag(tag.getTagId());
                    
                    // Reload tags
                    loadTags();
                    
                    // Show success message
                    Toast.makeText(TagManagementActivity.this, "Tag deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        
        // Create and show dialog
        AlertDialog dialog = builder.create();
        
        // Style the buttons
        dialog.setOnShowListener(dialogInterface -> {
            Button deleteButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            deleteButton.setTextColor(getResources().getColor(R.color.error));
            deleteButton.setTypeface(null, Typeface.BOLD);
            
            Button cancelButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            cancelButton.setTextColor(getResources().getColor(R.color.text_primary));
        });
        
        dialog.show();
    }

    /**
     * Update tag in the repository
     * @param tag Tag to update
     */
    private void updateTag(Tag tag) {
        // Update tag in the repository
        boolean success = tagRepository.updateTag(tag);
        
        if (success) {
            // Update local list
            for (int i = 0; i < allTags.size(); i++) {
                if (allTags.get(i).getTagId() == tag.getTagId()) {
                    allTags.set(i, tag);
                    break;
                }
            }
        } else {
            // Show error message
            Toast.makeText(this, "Failed to update tag", Toast.LENGTH_SHORT).show();
        }
        
        // Update UI
        filterTags(searchEditText.getText().toString());
    }
}
