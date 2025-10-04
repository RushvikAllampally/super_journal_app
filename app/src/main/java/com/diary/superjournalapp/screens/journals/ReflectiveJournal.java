package com.diary.superjournalapp.screens.journals;

import static com.diary.superjournalapp.utils.JournalUtils.getMonthName;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.constants.ApplicationConstants;
import com.diary.superjournalapp.database.DatabaseHelper;
import com.diary.superjournalapp.entity.Journal;
import com.diary.superjournalapp.entity.JournalCategories.ReflectiveJournalEntity;
import com.diary.superjournalapp.screens.fragments.HomeFragment;
import com.diary.superjournalapp.screens.fragments.JournalListFragment;
import com.diary.superjournalapp.utils.LiveTextStyler;
import com.diary.superjournalapp.utils.TagUtils;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import com.diary.superjournalapp.utils.JournalUtils;
import com.diary.superjournalapp.utils.TagDialogHelper;
import com.diary.superjournalapp.utils.TextEditorUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.vanniktech.emoji.EmojiPopup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

public class ReflectiveJournal extends AppCompatActivity {

    private ImageButton saveJournalButton;
    private ImageView closeJournalButton;
    private ImageView journalCalenderIconReflective;
    private ImageButton manageTagsButton;
    private TextView selectJournalDate;
    private TextView selectedJournalDate;
    private EditText journalTitle;
    private EditText journalContent;
    private Journal journal;
    private ReflectiveJournalEntity reflectiveJournalEntity;
    private ImageButton reflectiveDeleteIcon;
    private ImageButton promptIcon;
    private DatabaseHelper databaseHelper;
    private ImageView calenderImage;
    private ImageButton emojiesBtn;
    private BottomSheetDialog bottomSheetDialog;
    private ImageButton colorPalette;
    private ImageButton textStylesBtn;
    private Date selectedDate;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reflective_journal);
        
        journal = new Journal();
        databaseHelper = DatabaseHelper.getDb(this);

        // Initialize UI components
        closeJournalButton = findViewById(R.id.close_journal_reflective);
        journalCalenderIconReflective = findViewById(R.id.journal_calender_icon_reflective);
        selectJournalDate = findViewById(R.id.selected_journal_date_reflective);
        selectedJournalDate = findViewById(R.id.selected_journal_date_reflective);
        journalTitle = findViewById(R.id.journal_title_reflective);
        journalContent = findViewById(R.id.journal_content_reflective);
        reflectiveDeleteIcon = findViewById(R.id.reflective_delete_icon);
        promptIcon = findViewById(R.id.reflective_prompt_icon);
        manageTagsButton = findViewById(R.id.manage_tags_button);
        emojiesBtn = findViewById(R.id.reflective_emoji_icon);
        calenderImage = findViewById(R.id.journal_calender_icon_reflective);
        saveJournalButton = findViewById(R.id.save_journal_reflective);
        colorPalette = findViewById(R.id.reflective_color_palette);
        textStylesBtn = findViewById(R.id.reflective_text_style_icon);
        
        // Set up live styling by default
        LiveTextStyler.setupLiveEditText(journalContent);
        // Set up prompt icon click listener if prompts are enabled
        if (com.diary.superjournalapp.utils.PromptUtils.arePromptsEnabled(this)) {
            promptIcon.setVisibility(View.VISIBLE);
            promptIcon.setOnClickListener(v -> {
                com.diary.superjournalapp.utils.PromptUtils.showPromptDialog(
                        ReflectiveJournal.this, 
                        ApplicationConstants.REFLECTIVE_JOURNAL, 
                        journalTitle, 
                        journalContent);
            });
        } else {
            promptIcon.setVisibility(View.GONE);
        }
        
        // Set up tag management with temporary tags support
        manageTagsButton.setOnClickListener(v -> {
            if (journal != null && journal.getJournalId() > 0) {
                showTagsDialog(journal.getJournalId());
            } else {
                showTemporaryTagsDialog();
            }
        });
        
        // Set up emoji popup
        EmojiPopup popup = EmojiPopup.Builder
                .fromRootView(findViewById(R.id.reflective_journal_root))
                .build(journalContent);
                
        emojiesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.toggle();
            }
        });

        // Set up formatting buttons
        colorPalette.setOnClickListener(view -> {
            TextEditorUtils.colorPaletteOnClickListener(bottomSheetDialog, journalContent, ReflectiveJournal.this);
        });
        
        textStylesBtn.setOnClickListener(view -> {
            TextEditorUtils.textStylesOnClickListener(bottomSheetDialog, journalContent, ReflectiveJournal.this);
        });


        // Retrieve the data from the Intent
        Intent intent = getIntent();
        if (intent != null) {
            String journalId = intent.getStringExtra(ApplicationConstants.JOURNAL_ID_INTENT); // -1 is a default value in case the key is not found
            System.out.println("journalId : " + journalId);
            if (!(journalId == null || journalId.isEmpty())) {
                // Now you have the journalId, and you can use it in your Activity
                reflectiveJournalEntity = databaseHelper.reflectiveJournalContentDao().getReflectiveJournalById(Long.parseLong(journalId));
                journal = databaseHelper.journalDao().getMainJournalById(Long.parseLong(journalId));

                journal.setJournalId(Long.parseLong(journalId));

                journalTitle.setText(reflectiveJournalEntity.getTitle());

                /// Convert the HTML-formatted string back to a Spannable
                Spanned spanned = Html.fromHtml(reflectiveJournalEntity.getJournalContent(), Html.FROM_HTML_MODE_LEGACY, null, null);
                journalContent.setText(spanned);
                
                // Load journal content normally

                // Define the desired date format
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd, MMMM yyyy", Locale.ENGLISH);

                // Convert the date to a string with the specified format
                String formattedDate = dateFormat.format(reflectiveJournalEntity.getJournalCreatedOn());

                selectJournalDate.setText(formattedDate);
                selectedDate = reflectiveJournalEntity.getJournalCreatedOn();

            } else {
                Calendar calendar = Calendar.getInstance();
                selectJournalDate.setText(calendar.get(Calendar.DAY_OF_MONTH) + ", " + getMonthName(String.valueOf(calendar.get(Calendar.MONTH) + 1)) + " " + calendar.get(Calendar.YEAR));
                
                // New journal
            }
        }


        closeJournalButton.setOnClickListener(view -> {
            showConfirmationDialog();
        });

        reflectiveDeleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog(databaseHelper, reflectiveJournalEntity);
            }
        });

        calenderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int y = calendar.get(Calendar.YEAR);
                int m = calendar.get(Calendar.MONTH);
                int d = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(ReflectiveJournal.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        selectJournalDate.setText(i2 + ", " + getMonthName(String.valueOf(i1 + 1)) + " " + i);

                        Calendar calendarInside = Calendar.getInstance();
                        calendarInside.set(Calendar.YEAR, i); // Set the year
                        calendarInside.set(Calendar.MONTH, i1); // Set the month (Note: Months are zero-based, so November is 10)
                        calendarInside.set(Calendar.DAY_OF_MONTH, i2); // Set the day

                        selectedDate = calendarInside.getTime();

                    }
                }, y, m, d);
                datePickerDialog.show();
            }
        });

        saveJournalButton.setOnClickListener(view -> {
            saveJournalDetails();
            finish();
            Toast.makeText(ReflectiveJournal.this, "Journal saved successfully", Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveJournalDetails();

    }

    private void saveJournalDetails() {
        String title = journalTitle.getText().toString();
        String content = journalContent.getText().toString();

        if (title.isEmpty()) {
            Toast.makeText(ReflectiveJournal.this, "Journal Title can't be Empty", Toast.LENGTH_LONG).show();
            return;
        } else if (content.isEmpty()) {
            Toast.makeText(ReflectiveJournal.this, "Journal Content can't be Empty", Toast.LENGTH_LONG).show();
            return;
        }

        journal.setJournalCreatedOn(selectedDate == null ? new Date() : selectedDate);
        journal.setJournalCategory(ApplicationConstants.REFLECTIVE_JOURNAL);

        int contentLength = (content.length() > 100) ? 100 : content.length();
        journal.setJournalStartText(content.substring(0, contentLength));
        journal.setTitle(title);

        long journalId;
        if (journal.getJournalId() == 0) {
            journalId = databaseHelper.journalDao().addJournal(journal);
        } else {
            databaseHelper.journalDao().updateJournal(journal);
            journalId = journal.getJournalId();
        }

        ReflectiveJournalEntity reflectiveJournal = new ReflectiveJournalEntity();

        reflectiveJournal.setJournalCreatedOn(selectedDate == null ? new Date() : selectedDate);
        reflectiveJournal.setJournalCategory(ApplicationConstants.REFLECTIVE_JOURNAL);
        reflectiveJournal.setJournalStartText(content.substring(0, contentLength));
        reflectiveJournal.setTitle(title);
        reflectiveJournal.setJournalContent(Html.toHtml(new SpannableStringBuilder((Spanned) journalContent.getText())));
        reflectiveJournal.setJournalId(journalId);

        if (journal.getJournalId() == 0) {
            databaseHelper.reflectiveJournalContentDao().insert(reflectiveJournal);
            
            // Apply any temporary tags to the newly saved journal
            if (!tempTags.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (String tag : tempTags) {
                    if (sb.length() > 0) {
                        sb.append(",");
                    }
                    sb.append(tag);
                }
                TagUtils.addTagsToJournal(this, journalId, sb.toString());
                tempTags.clear();
            }
        } else {
            databaseHelper.reflectiveJournalContentDao().updateReflectiveJournalEntity(reflectiveJournal);
        }

        JournalUtils.updateStreak(ReflectiveJournal.this);
        HomeFragment.notifyHomeRecyclerViewChanges();

        Toast.makeText(ReflectiveJournal.this, "Journal Saved Successfully", Toast.LENGTH_LONG).show();
    }

    // Temporary storage for tags before journal is saved
    private Set<String> tempTags = new HashSet<>();
    
    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Discard Changes");
        builder.setMessage("Are you sure you want to discard changes?");
        builder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "Discard," so finish the activity and discard changes
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "Cancel," so do nothing and close the dialog
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

        /**
     * Show the tags management dialog
     * 
     * @param journalId The journal ID to manage tags for
     */
    private void showTagsDialog(long journalId) {
        TagDialogHelper tagDialogHelper = new TagDialogHelper(this, journalId);
        tagDialogHelper.showTagDialog();
    }
    
    /**
     * Show a dialog to collect tags before the journal is saved
     */
    private void showTemporaryTagsDialog() {
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.tags_dialog);
        
        // Initialize views
        EditText tagInput = dialog.findViewById(R.id.tag_input);
        FlexboxLayout tagsContainer = dialog.findViewById(R.id.tags_container);
        FlexboxLayout suggestedTagsContainer = dialog.findViewById(R.id.suggested_tags_container);
        Button addTagButton = dialog.findViewById(R.id.add_tag_button);
        Button doneButton = dialog.findViewById(R.id.done_button);
        
        // Set up button click listeners
        addTagButton.setOnClickListener(v -> {
            String tags = tagInput.getText().toString().trim();
            if (!tags.isEmpty()) {
                // Add to temporary storage
                String[] tagArray = tags.split(",");
                Collections.addAll(tempTags, tagArray);
                tagInput.setText("");
                refreshTempTags(tagsContainer, suggestedTagsContainer);
                // Also add to preferences for future suggestions
                TagUtils.saveTagToPreferences(this, tags);
            }
        });
        
        doneButton.setOnClickListener(v -> dialog.dismiss());
        
        // Show current temp tags and suggested tags
        refreshTempTags(tagsContainer, suggestedTagsContainer);
        
        dialog.show();
    }
    
    /**
     * Refresh the temporary tags display
     */
    private void refreshTempTags(FlexboxLayout tagsContainer, FlexboxLayout suggestedTagsContainer) {
        tagsContainer.removeAllViews();
        
        for (String tag : tempTags) {
            addTempTagChip(tagsContainer, tag);
        }
        
        // Update suggested tags
        suggestedTagsContainer.removeAllViews();
        Set<String> allTags = TagUtils.getAllTags(this);
        
        for (String tag : allTags) {
            if (!tempTags.contains(tag)) {
                // Add as suggestion
                View tagView = getLayoutInflater().inflate(R.layout.tag_chip_item, suggestedTagsContainer, false);
                TextView tagText = tagView.findViewById(R.id.tag_text);
                tagText.setText(tag);
                tagView.findViewById(R.id.tag_remove_button).setVisibility(View.GONE);
                
                // For suggested tags, clicking adds it
                tagView.setOnClickListener(v -> {
                    tempTags.add(tag);
                    refreshTempTags(tagsContainer, suggestedTagsContainer);
                });
                
                suggestedTagsContainer.addView(tagView);
            }
        }
    }
    
    /**
     * Add a tag chip to the temporary container
     */
    private void addTempTagChip(FlexboxLayout container, String tag) {
        View tagView = getLayoutInflater().inflate(R.layout.tag_chip_item, container, false);
        
        TextView tagText = tagView.findViewById(R.id.tag_text);
        ImageView removeButton = tagView.findViewById(R.id.tag_remove_button);
        
        tagText.setText(tag);
        removeButton.setVisibility(View.VISIBLE);
        removeButton.setOnClickListener(v -> {
            tempTags.remove(tag);
            container.removeView(tagView);
        });
        
        container.addView(tagView);
    }
    
    private void showDeleteConfirmationDialog(DatabaseHelper dbHelper, ReflectiveJournalEntity journalEntity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Journal");
        builder.setMessage("Are you sure you want to delete journal ?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "Discard," so finish the activity and discard changes
                databaseHelper.reflectiveJournalContentDao().deleteJournal(reflectiveJournalEntity);
                databaseHelper.journalDao().deleteJournal(journal);
                HomeFragment.notifyHomeRecyclerViewChanges();
                JournalListFragment.notifyJournalListFragment();

                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "Cancel," so do nothing and close the dialog
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}