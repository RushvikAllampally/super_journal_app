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
import com.diary.superjournalapp.utils.PremiumFeatureManager;
import com.diary.superjournalapp.utils.JournalLockManager;

import java.util.ArrayList;
import java.util.List;

public class JournalRecyclerAdaptor extends RecyclerView.Adapter<JournalRecyclerAdaptor.ViewHolder> {

    Context context;
    ArrayList<Journal> journalArrayList;
    private TagManager tagManager;
    private PremiumFeatureManager premiumFeatureManager;
    private JournalLockManager lockManager;

    public JournalRecyclerAdaptor(Context context,ArrayList<Journal> journalsList){
        this.context=context;
        System.out.println(journalsList);
        this.journalArrayList =journalsList;
        this.tagManager = new TagManager(context);
        this.premiumFeatureManager = PremiumFeatureManager.getInstance(context);
        this.lockManager = JournalLockManager.getInstance(context);
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
        
        // Handle locked journal display
        if (journal.isLocked()) {
            holder.journalContent.setText("🔒 This journal is locked");
            holder.journalContent.setAlpha(0.8f);
            holder.journalContent.setTypeface(holder.journalContent.getTypeface(), android.graphics.Typeface.ITALIC);
            holder.lockIcon.setVisibility(View.VISIBLE);
            // Hide tags for privacy when locked
            holder.journalTagsGroup.setVisibility(View.GONE);
        } else {
            holder.journalContent.setText(journal.getJournalStartText());
            holder.journalContent.setAlpha(1.0f);
            holder.journalContent.setTypeface(holder.journalContent.getTypeface(), android.graphics.Typeface.NORMAL);
            holder.lockIcon.setVisibility(View.GONE);
            holder.journalTagsGroup.setVisibility(View.VISIBLE);
            // Setup tag chips for unlocked journal - don't allow editing on click
            setupTagChips(holder.journalTagsGroup, journal, false);
        }
        
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
                int position = holder.getAdapterPosition();
                if (position == RecyclerView.NO_POSITION) return;
                
                Journal journal = journalArrayList.get(position);
                
                // Check if journal is locked and needs authentication
                if (journal.isLocked() && !lockManager.isTemporarilyUnlocked(journal.getJournalId())) {
                    // Check if app lock is set up
                    if (!lockManager.isAppPasscodeEnabled()) {
                        showSetupAppLockDialog();
                        return;
                    }
                    
                    // Launch AppLock for authentication, then open journal
                    Intent lockIntent = new Intent(view.getContext(), com.diary.superjournalapp.applock.AppLock.class);
                    lockIntent.putExtra("journal_access", true);
                    lockIntent.putExtra("journal_id", journal.getJournalId());
                    lockIntent.putExtra("journal_category", journal.getJournalCategory());
                    
                    // Start AppLock activity - it will handle opening the journal after authentication
                    view.getContext().startActivity(lockIntent);
                    return;
                }
                
                // Journal is accessible, open normally
                openJournal(journal, view.getContext());
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
        ImageView lockIcon;
        com.google.android.material.chip.ChipGroup journalTagsGroup;
        com.google.android.material.chip.Chip journalTag; // This is the sample tag in XML
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            journalTitle = itemView.findViewById(R.id.journal_title);
            journalContent = itemView.findViewById(R.id.journal_content);
            journalDate = itemView.findViewById(R.id.journal_date);
            journalTypeIcon = itemView.findViewById(R.id.journal_type_icon);
            bookmarkIcon = itemView.findViewById(R.id.journal_bookmark);
            lockIcon = itemView.findViewById(R.id.journal_lock);
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
            
            // Setup lock icon click listener - just show info, actual lock/unlock in journal detail
            lockIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.widget.Toast.makeText(context, 
                        "Open the journal to lock/unlock it", 
                        android.widget.Toast.LENGTH_SHORT).show();
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
     * Open a journal based on its category
     * 
     * @param journal The journal to open
     * @param context The context to start the activity from
     */
    private void openJournal(Journal journal, Context context) {
        String journalCategory = journal.getJournalCategory();
        Intent intent;
        
        switch (journalCategory) {
            case "Gratitude Journal":
                intent = new Intent(context, GratitudeJournal.class);
                break;
            case "Bullet Journal":
                intent = new Intent(context, BulletJournal.class);
                break;
            case "Dream Journal":
                intent = new Intent(context, DreamJournal.class);
                break;
            case "My Diary":
            default:
                intent = new Intent(context, ReflectiveJournal.class);
                break;
        }
        
        intent.putExtra("journalId", String.valueOf(journal.getJournalId()));
        context.startActivity(intent);
        
        // Reset auto-lock timer if journal was temporarily unlocked
        if (journal.isLocked() && lockManager.isTemporarilyUnlocked(journal.getJournalId())) {
            lockManager.resetAutoLockTimer(journal.getJournalId());
        }
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
     * Toggle lock status for a journal entry
     * 
     * @param journal The journal to toggle lock for
     * @param position Position in adapter for UI update
     */
    private void toggleLock(Journal journal, int position) {
        boolean newStatus = !journal.isLocked();
        
        // If trying to lock, check premium limits
        if (newStatus && !premiumFeatureManager.canLockMoreJournals()) {
            showUpgradeDialog();
            return;
        }
        
        // Toggle lock status
        journal.setLocked(newStatus);
        
        // Update database
        DatabaseHelper databaseHelper = DatabaseHelper.getDb(context);
        databaseHelper.journalDao().updateLockStatus(journal.getJournalId(), newStatus);
        
        // Update UI
        notifyItemChanged(position);
        
        // Show feedback to user with premium status
        String message;
        if (newStatus) {
            String remaining = premiumFeatureManager.isPremiumUser() ? 
                "unlimited" : String.valueOf(premiumFeatureManager.getRemainingLockSlots());
            message = "Journal locked (" + remaining + " locks remaining)";
        } else {
            message = "Journal unlocked";
        }
        android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Show dialog to setup app lock first
     */
    private void showSetupAppLockDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(context)
            .setTitle("App Lock Required")
            .setMessage("To use journal locking, please set up App Lock first. The same password will be used for locked journals.")
            .setPositiveButton("Go to Settings", (dialog, which) -> {
                // Open settings screen
                Intent intent = new Intent(context, com.diary.superjournalapp.screens.settings.SettingsScreen.class);
                context.startActivity(intent);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    /**
     * Show upgrade dialog when free limit is reached
     */
    private void showUpgradeDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(context)
            .setTitle("Premium Feature")
            .setMessage(premiumFeatureManager.getUpgradeMessage())
            .setPositiveButton("Upgrade to Premium", (dialog, which) -> {
                // TODO: Launch premium upgrade activity
                android.widget.Toast.makeText(context, "Premium upgrade coming soon!", android.widget.Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Not Now", null)
            .show();
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
     * Updates the lock icon appearance based on lock status
     * 
     * @param lockIcon The ImageView to update
     * @param isLocked Current lock status
     */
    private void updateLockIcon(ImageView lockIcon, boolean isLocked) {
        if (isLocked) {
            lockIcon.setImageResource(R.drawable.ic_lock);
            lockIcon.setVisibility(View.VISIBLE);
        } else {
            lockIcon.setVisibility(View.GONE);
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
