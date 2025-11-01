package com.diary.superjournalapp.adapters;

import android.content.Context;
import android.view.View;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.entity.Tag;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;
import java.util.function.Consumer;

/**
 * Adapter for displaying tags as chips
 */
public class TagChipAdapter {

    private Context context;
    private ChipGroup chipGroup;
    private Consumer<Tag> onTagClickListener;
    private Consumer<Tag> onTagCloseListener;
    private boolean showCloseIcon;
    private boolean smallChips;
    private int maxVisibleTags = Integer.MAX_VALUE; // Default to show all tags

    /**
     * Constructor
     * 
     * @param context Context
     * @param chipGroup ChipGroup to display tags in
     */
    public TagChipAdapter(Context context, ChipGroup chipGroup) {
        this.context = context;
        this.chipGroup = chipGroup;
        this.showCloseIcon = false;
        this.smallChips = false;
    }

    /**
     * Set whether to show close icon on chips
     * 
     * @param showCloseIcon True to show close icon, false otherwise
     * @return This adapter for chaining
     */
    public TagChipAdapter setShowCloseIcon(boolean showCloseIcon) {
        this.showCloseIcon = showCloseIcon;
        return this;
    }

    /**
     * Set whether to use small chips (for journal cards)
     * 
     * @param smallChips True to use small chips, false for standard size
     * @return This adapter for chaining
     */
    public TagChipAdapter setSmallChips(boolean smallChips) {
        this.smallChips = smallChips;
        return this;
    }
    
    /**
     * Set maximum number of visible tags
     * 
     * @param maxVisibleTags Maximum number of tags to display before showing a "+X more" chip
     * @return This adapter for chaining
     */
    public TagChipAdapter setMaxVisibleTags(int maxVisibleTags) {
        this.maxVisibleTags = maxVisibleTags;
        return this;
    }

    /**
     * Set click listener for tags
     * 
     * @param listener Listener to call when a tag is clicked
     * @return This adapter for chaining
     */
    public TagChipAdapter setOnTagClickListener(Consumer<Tag> listener) {
        this.onTagClickListener = listener;
        return this;
    }

    /**
     * Set close listener for tags
     * 
     * @param listener Listener to call when a tag's close icon is clicked
     * @return This adapter for chaining
     */
    public TagChipAdapter setOnTagCloseListener(Consumer<Tag> listener) {
        this.onTagCloseListener = listener;
        return this;
    }

    /**
     * Set tags to display
     * 
     * @param tags List of tags
     */
    public void setTags(List<Tag> tags) {
        chipGroup.removeAllViews();
        
        if (tags == null || tags.isEmpty()) {
            chipGroup.setVisibility(View.GONE);
            return;
        }
        
        chipGroup.setVisibility(View.VISIBLE);
        
        // If we have more tags than maxVisibleTags, we'll show a +X more chip
        int overflowCount = tags.size() > maxVisibleTags ? tags.size() - maxVisibleTags : 0;
        int tagsToShow = Math.min(tags.size(), maxVisibleTags);
        
        // Add visible tags
        for (int i = 0; i < tagsToShow; i++) {
            addTagChip(tags.get(i));
        }
        
        // Add overflow chip if needed
        if (overflowCount > 0) {
            addOverflowChip(overflowCount);
        }
    }

    /**
     * Add a single tag chip
     * 
     * @param tag Tag to add
     */
    private void addTagChip(Tag tag) {
        Chip chip = new Chip(context);
        
        // Set text and basic properties
        chip.setText(tag.getName());
        chip.setClickable(onTagClickListener != null);
        chip.setCheckable(false);
        
        // Set chip appearance
        chip.setChipBackgroundColorResource(android.R.color.transparent);
        chip.setChipStrokeColorResource(R.color.text_primary);
        chip.setChipStrokeWidth(1);
        chip.setChipIconResource(R.drawable.tag_24);
        chip.setChipIconTintResource(R.color.text_primary);
        chip.setTextColor(context.getResources().getColor(R.color.text_primary));
        
        // Handle small chips (for journal cards)
        if (smallChips) {
            chip.setTextSize(10);
            chip.setChipIconSize(20);
            chip.setMinHeight(28);
            chip.setChipStartPadding(8);
            chip.setChipEndPadding(8);
            chip.setTextEndPadding(2);
            chip.setTextStartPadding(2);
            chip.setEnsureMinTouchTargetSize(false);
        } else {
            // Standard sized chips - ensure consistent height and padding
            chip.setChipMinHeight(36); // Increased from 32 to 36dp
            chip.setChipStartPadding(10);
            chip.setChipEndPadding(10);
            chip.setTextEndPadding(4);
            chip.setTextStartPadding(4);
            chip.setEnsureMinTouchTargetSize(false);
        }
        
        // Handle close icon
        if (showCloseIcon && onTagCloseListener != null) {
            chip.setCloseIconVisible(true);
            chip.setCloseIconTintResource(R.color.text_primary);
            if (smallChips) {
                chip.setCloseIconSize(12);
            }
        } else {
            chip.setCloseIconVisible(false);
        }
        
        // Set up listeners
        if (onTagClickListener != null) {
            chip.setOnClickListener(v -> onTagClickListener.accept(tag));
        }
        
        if (showCloseIcon && onTagCloseListener != null) {
            chip.setOnCloseIconClickListener(v -> onTagCloseListener.accept(tag));
        }
        
        // Add to chip group
        chipGroup.addView(chip);
    }
    
    /**
     * Add an overflow chip indicating there are more tags
     * 
     * @param overflowCount Number of additional tags not shown
     */
    private void addOverflowChip(int overflowCount) {
        Chip chip = new Chip(context);
        
        // Set text and basic properties
        chip.setText("+" + overflowCount + " more");
        chip.setClickable(onTagClickListener != null);
        chip.setCheckable(false);
        
        // Set chip appearance - different style for overflow
        chip.setChipBackgroundColorResource(R.color.secondary);
        chip.setTextColor(context.getResources().getColor(R.color.on_secondary));
        
        // Handle small chips (for journal cards)
        if (smallChips) {
            chip.setTextSize(10);
            chip.setMinHeight(28);
            chip.setChipStartPadding(8);
            chip.setChipEndPadding(8);
            chip.setTextEndPadding(2);
            chip.setTextStartPadding(2);
            chip.setEnsureMinTouchTargetSize(false);
        } else {
            // Standard sized chips - ensure consistent height and padding
            chip.setChipMinHeight(36); // Increased from 32 to 36dp
            chip.setChipStartPadding(10);
            chip.setChipEndPadding(10);
            chip.setTextEndPadding(4);
            chip.setTextStartPadding(4);
            chip.setEnsureMinTouchTargetSize(false); // Ensure consistent height with other chips
        }
        
        // Add to chip group
        chipGroup.addView(chip);
    }
    
    /**
     * Clear all tags
     */
    public void clearTags() {
        chipGroup.removeAllViews();
        chipGroup.setVisibility(View.GONE);
    }
}
