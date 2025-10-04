package com.diary.superjournalapp.recyclerviews;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.diary.superjournalapp.utils.TagDialogHelper;
import com.diary.superjournalapp.utils.TagUtils;

import java.util.ArrayList;
import java.util.List;

public class JournalRecyclerAdaptor extends RecyclerView.Adapter<JournalRecyclerAdaptor.ViewHolder> {

    Context context;
    ArrayList<Journal> journalArrayList;

    public JournalRecyclerAdaptor(Context context,ArrayList<Journal> journalsList){
        this.context=context;
        System.out.println(journalsList);
        this.journalArrayList =journalsList;
    }

    @NonNull
    @Override
    public JournalRecyclerAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.journal_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalRecyclerAdaptor.ViewHolder holder, int position) {
        Journal journal = journalArrayList.get(position);
        
        holder.journalDate.setText(JournalUtils.getDateFromJavaDate(journal.getJournalCreatedOn()));
        holder.journalMonth.setText(JournalUtils.getMonthFromJavaDate(journal.getJournalCreatedOn()));
        holder.journalTitle.setText(journal.getTitle());
        holder.journalContent.setText(journal.getJournalStartText());
        
        // Set bookmark icon based on bookmark status
        updateBookmarkIcon(holder.bookmarkButton, journal.isBookmarked());
        
        // Handle tags
        setupTags(holder, journal);

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

        TextView journalDate;
        TextView journalMonth;
        TextView journalTitle;
        TextView journalContent;
        ImageButton bookmarkButton;
        LinearLayout tagsContainer;
        TextView tagsText;
        ImageButton tagsButton;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            journalDate = itemView.findViewById(R.id.journal_row_date);
            journalMonth = itemView.findViewById(R.id.journal_row_month);
            journalTitle = itemView.findViewById(R.id.journal_row_title);
            journalContent = itemView.findViewById(R.id.journal_row_details);
            bookmarkButton = itemView.findViewById(R.id.journal_bookmark_button);
            tagsContainer = itemView.findViewById(R.id.journal_row_tags_container);
            tagsText = itemView.findViewById(R.id.journal_row_tags);
            tagsButton = itemView.findViewById(R.id.journal_tags_button);
            
            // Setup bookmark button click listener
            bookmarkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Journal journal = journalArrayList.get(position);
                        toggleBookmark(journal, position);
                    }
                }
            });
            
            // Setup tags button click listener
            tagsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Journal journal = journalArrayList.get(position);
                        showTagsDialog(journal.getJournalId());
                    }
                }
            });
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
     * Updates the bookmark button appearance based on bookmark status
     * 
     * @param bookmarkButton The button to update
     * @param isBookmarked Current bookmark status
     */
    private void updateBookmarkIcon(ImageButton bookmarkButton, boolean isBookmarked) {
        if (isBookmarked) {
            bookmarkButton.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            bookmarkButton.setImageResource(android.R.drawable.btn_star_big_off);
        }
    }
    
    /**
     * Show tag management dialog for a journal
     * 
     * @param journalId The journal ID to edit tags for
     */
    private void showTagsDialog(long journalId) {
        TagDialogHelper tagDialogHelper = new TagDialogHelper(context, journalId);
        tagDialogHelper.showTagDialog();
    }
    
    /**
     * Setup tags display for a journal
     * 
     * @param holder ViewHolder to update
     * @param journal Journal to get tags from
     */
    private void setupTags(ViewHolder holder, Journal journal) {
        List<String> tags = TagUtils.getTagsForJournal(journal);
        
        if (tags.isEmpty()) {
            holder.tagsContainer.setVisibility(View.GONE);
        } else {
            holder.tagsContainer.setVisibility(View.VISIBLE);
            holder.tagsText.setText("Tags: " + String.join(", ", tags));
        }
    }

}
