package com.diary.superjournalapp.screens.tags;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.entity.Tag;

import java.util.List;

/**
 * Adapter for displaying and managing tags in a RecyclerView
 */
public class TagManagementAdapter extends RecyclerView.Adapter<TagManagementAdapter.ViewHolder> {

    private List<Tag> tags;
    private TagInteractionListener listener;

    /**
     * Interface for tag interaction callbacks
     */
    public interface TagInteractionListener {
        void onEditTag(Tag tag);
        void onDeleteTag(Tag tag);
    }

    /**
     * Constructor
     * @param tags List of tags
     * @param listener Listener for tag interactions
     */
    public TagManagementAdapter(List<Tag> tags, TagInteractionListener listener) {
        this.tags = tags;
        this.listener = listener;
    }

    /**
     * Update the list of tags
     * @param newTags New list of tags
     */
    public void updateTags(List<Tag> newTags) {
        this.tags = newTags;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tag_management_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tag tag = tags.get(position);
        
        // Set tag name
        holder.tagNameText.setText(tag.getName());
        
        // Set usage count
        int usageCount = tag.getUsageCount();
        holder.usageCountText.setText(usageCount + (usageCount == 1 ? " use" : " uses"));
        
        // Set up edit button
        holder.editButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditTag(tag);
            }
        });
        
        // Set up delete button
        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteTag(tag);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    /**
     * ViewHolder for tag items
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tagNameText;
        TextView usageCountText;
        ImageButton editButton;
        ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tagNameText = itemView.findViewById(R.id.tag_name_text);
            usageCountText = itemView.findViewById(R.id.usage_count_text);
            editButton = itemView.findViewById(R.id.edit_tag_button);
            deleteButton = itemView.findViewById(R.id.delete_tag_button);
        }
    }
}
