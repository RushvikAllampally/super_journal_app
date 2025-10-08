package com.diary.superjournalapp.recyclerviews;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.entity.Journal;
import com.diary.superjournalapp.screens.journals.BulletJournal;
import com.diary.superjournalapp.screens.journals.DreamJournal;
import com.diary.superjournalapp.screens.journals.GratitudeJournal;
import com.diary.superjournalapp.screens.journals.ReflectiveJournal;
import com.diary.superjournalapp.database.DatabaseHelper;
import com.diary.superjournalapp.utils.JournalUtils;
import com.diary.superjournalapp.adapters.TagChipAdapter;
import com.diary.superjournalapp.entity.Tag;
import com.diary.superjournalapp.utils.TagManager;

import java.util.ArrayList;
import java.util.List;

public class JournalRecyclerAdaptor extends RecyclerView.Adapter<JournalRecyclerAdaptor.ViewHolder> {

    Context context;
    ArrayList<Journal> journalArrayList;
    private TagManager tagManager;

    public JournalRecyclerAdaptor(Context context,ArrayList<Journal> journalsList){
        this.context=context;
        System.out.println(journalsList);
        this.journalArrayList =journalsList;
        this.tagManager = new TagManager(context);
    }

    @NonNull
    @Override
    public JournalRecyclerAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.journal_row_improved, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalRecyclerAdaptor.ViewHolder holder, int position) {
        Journal journal = journalArrayList.get(position);
        
        // Set journal text content
        holder.journalTitle.setText(journal.getTitle());
        holder.journalContent.setText(journal.getJournalStartText());
        
        // Use the improved date format
        holder.journalDate.setText(JournalUtils.getCompactDateFormat(journal.getJournalCreatedOn()));
        
        setJournalTypeIcon(holder.journalTypeIcon, journal.getJournalCategory());
        
        // Set bookmark icon based on bookmark status
        updateBookmarkIconImproved(holder.bookmarkIcon, journal.isBookmarked());
        
        // Setup tag chips for a journal - don't allow editing on click
        setupTagChips(holder.journalTagsGroup, journal, false);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String journalCategory = journalArrayList.get(holder.getAdapterPosition()).getJournalCategory();
                Intent intent;
                switch (journalCategory){
                    case "Gratitude Journal":
                        intent = new Intent(view.getContext(), GratitudeJournal.class);
                        intent.putExtra("journalId",String.valueOf(journalArrayList.get(holder.getAdapterPosition()).getJournalId()));
                        view.getContext().startActivity(intent);
                        break;
                    case "Bullet Journal":
                        intent = new Intent(view.getContext(), BulletJournal.class);
                        intent.putExtra("journalId",String.valueOf(journalArrayList.get(holder.getAdapterPosition()).getJournalId()));
                        view.getContext().startActivity(intent);
                        break;
                    case "Dream Journal":
                        intent = new Intent(view.getContext(), DreamJournal.class);
                        intent.putExtra("journalId",String.valueOf(journalArrayList.get(holder.getAdapterPosition()).getJournalId()));
                        view.getContext().startActivity(intent);
                        break;
                    case "My Diary":
                        intent = new Intent(view.getContext(), ReflectiveJournal.class);
                        intent.putExtra("journalId",String.valueOf(journalArrayList.get(holder.getAdapterPosition()).getJournalId()));
                        view.getContext().startActivity(intent);
                        break;
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return journalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView journalTitle;
        TextView journalContent;
        TextView journalDate;
        ImageView journalTypeIcon;
        ImageView bookmarkIcon;
        com.google.android.material.chip.ChipGroup journalTagsGroup;
        com.google.android.material.chip.Chip journalTag; // This is the sample tag in XML
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            journalTitle = itemView.findViewById(R.id.journal_title);
            journalContent = itemView.findViewById(R.id.journal_content);
            journalDate = itemView.findViewById(R.id.journal_date);
            journalTypeIcon = itemView.findViewById(R.id.journal_type_icon);
            bookmarkIcon = itemView.findViewById(R.id.journal_bookmark);
            journalTagsGroup = itemView.findViewById(R.id.journal_tags_group);
            journalTag = itemView.findViewById(R.id.journal_tag);
            
            // Setup bookmark icon click listener
            bookmarkIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Journal journal = journalArrayList.get(position);
                        toggleBookmark(journal, position);
                    }
                }
            });
            
            // We don't need a click listener for the example tag in XML 
            // since we're dynamically creating all tags
        }
    }

    public void updateData() {
        // Notify the adapter of the data change
        notifyDataSetChanged();
    }
    
    /**
     * Toggle bookmark status for a journal entry
     * 
     * @param journal The journal to toggle bookmark for
     * @param position Position in adapter for UI update
     */
    private void toggleBookmark(Journal journal, int position) {
        // Toggle bookmark status
        boolean newStatus = !journal.isBookmarked();
        journal.setBookmarked(newStatus);
        
        // Update database
        DatabaseHelper databaseHelper = DatabaseHelper.getDb(context);
        databaseHelper.journalDao().updateBookmarkStatus(journal.getJournalId(), newStatus);
        
        // Update UI
        notifyItemChanged(position);
    }
    
    /**
     * Updates the bookmark icon appearance based on bookmark status
     * 
     * @param bookmarkIcon The ImageView to update
     * @param isBookmarked Current bookmark status
     */
    private void updateBookmarkIconImproved(ImageView bookmarkIcon, boolean isBookmarked) {
        if (isBookmarked) {
            bookmarkIcon.setImageResource(R.drawable.ic_bookmark_filled);
        } else {
            bookmarkIcon.setImageResource(R.drawable.ic_bookmark);
        }
    }
    
    /**
     * Show tag management dialog for a journal
     * 
     * @param journalId The journal ID to edit tags for
     */
    private void showTagsDialog(long journalId) {
        // Use the new TagDialogFragment instead of TagDialogHelper
        com.diary.superjournalapp.dialogs.TagDialogFragment dialogFragment = 
            com.diary.superjournalapp.dialogs.TagDialogFragment.newInstance(journalId);
        dialogFragment.show(((androidx.fragment.app.FragmentActivity)context).getSupportFragmentManager(), "tag_dialog");
    }
    
    /**
     * Setup tag chips for a journal
     * 
     * @param chipGroup The ChipGroup to populate
     * @param journal Journal to get tags from
     * @param allowClickToEdit Whether clicking the tags should open the tag editor
     */
    private void setupTagChips(com.google.android.material.chip.ChipGroup chipGroup, Journal journal, boolean allowClickToEdit) {
        List<Tag> tags = tagManager.getTagsForJournal(journal);
        
        // Use TagChipAdapter to handle chip creation and display
        TagChipAdapter tagChipAdapter = new TagChipAdapter(context, chipGroup)
            .setSmallChips(true)
            .setShowCloseIcon(false)
            .setMaxVisibleTags(2); // Only show 2 tags max on card, rest as overflow
            
        // Only set click listener if editing is allowed
        if (allowClickToEdit) {
            tagChipAdapter.setOnTagClickListener(tag -> showTagsDialog(journal.getJournalId()));
        }
        
        tagChipAdapter.setTags(tags);
    }
    
    /**
     * Sets the journal type icon based on category
     * 
     * @param iconView The ImageView to update
     * @param category The journal category
     */
    private void setJournalTypeIcon(ImageView iconView, String category) {
        switch (category) {
            case "Gratitude Journal":
                iconView.setImageResource(R.drawable.ic_gratitude);
                break;
            case "Bullet Journal":
                iconView.setImageResource(R.drawable.ic_bullet);
                break;
            case "Dream Journal":
                iconView.setImageResource(R.drawable.ic_dream);
                break;
            case "My Diary":
            default:
                iconView.setImageResource(R.drawable.ic_diary);
                break;
        }
    }

}
