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
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.graphics.Color;
import android.app.Dialog;

import java.util.List;
import java.util.Random;

import com.diary.superjournalapp.utils.PromptUtils;

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
import com.diary.superjournalapp.utils.JournalUtils;
import com.diary.superjournalapp.utils.TagManager;
import com.diary.superjournalapp.utils.TextEditorUtils;
import com.diary.superjournalapp.dialogs.TagDialogFragment;

import java.util.ArrayList;
import java.util.List;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.vanniktech.emoji.EmojiPopup;
import jp.wasabeef.richeditor.RichEditor;

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
    private RichEditor journalContent;
    private TextView placeholderTextView;
    private TextView wordCountTextView;
    private Journal journal;
    private ReflectiveJournalEntity reflectiveJournalEntity;
    private ImageButton reflectiveDeleteIcon;
    private ImageButton promptIcon;
    private DatabaseHelper databaseHelper;
    private ImageView calenderImage;
    
    // Formatting buttons
    private ImageButton undoButton, redoButton;
    private Button boldButton, italicButton, underlineButton;
    private Button heading1Button, heading2Button;
    private ImageButton bulletButton;
    private Button numbersButton;
    private Button textColorButton, fontSizeButton;
    private Date selectedDate;
    private TagManager tagManager;
    private ArrayList<String> temporaryTags = new ArrayList<>();  // For unsaved journals
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reflective_journal);
        
        journal = new Journal();
        databaseHelper = DatabaseHelper.getDb(this);
        tagManager = new TagManager(this);

        // Initialize UI components
        closeJournalButton = findViewById(R.id.close_journal_reflective);
        journalCalenderIconReflective = findViewById(R.id.journal_calender_icon_reflective);
        selectJournalDate = findViewById(R.id.selected_journal_date_reflective);
        selectedJournalDate = findViewById(R.id.selected_journal_date_reflective);
        journalTitle = findViewById(R.id.journal_title_reflective);
        journalContent = findViewById(R.id.journal_content_reflective);
        placeholderTextView = findViewById(R.id.editor_placeholder);
        wordCountTextView = findViewById(R.id.word_count);
        reflectiveDeleteIcon = findViewById(R.id.reflective_delete_icon);
        promptIcon = findViewById(R.id.reflective_prompt_icon);
        manageTagsButton = findViewById(R.id.manage_tags_button);
        calenderImage = findViewById(R.id.journal_calender_icon_reflective);
        saveJournalButton = findViewById(R.id.save_journal_reflective);
        
        // Initialize formatting buttons
        undoButton = findViewById(R.id.action_undo);
        redoButton = findViewById(R.id.action_redo);
        boldButton = findViewById(R.id.action_bold);
        italicButton = findViewById(R.id.action_italic);
        underlineButton = findViewById(R.id.action_underline);
        heading1Button = findViewById(R.id.action_heading1);
        heading2Button = findViewById(R.id.action_heading2);
        bulletButton = findViewById(R.id.action_bullet);
        numbersButton = findViewById(R.id.action_numbers);
        textColorButton = findViewById(R.id.action_text_color);
        fontSizeButton = findViewById(R.id.action_font_size);
        
        // Setup Rich Editor
        setupRichEditor();
        setupFormattingButtons();
        
        // Set up prompt icon click listener
        promptIcon.setVisibility(View.VISIBLE);
        promptIcon.setOnClickListener(v -> {
            // Only show prompts if they're enabled in settings
            if (PromptUtils.arePromptsEnabled(this)) {
                showRichEditorPrompt();
            } else {
                Toast.makeText(this, "Prompts are disabled in settings", Toast.LENGTH_SHORT).show();
            }
        });
        
        // Set up tag management
        manageTagsButton.setOnClickListener(v -> {
            showTagsDialog();
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

                // Load HTML content directly into rich editor
                journalContent.setHtml(reflectiveJournalEntity.getJournalContent());
                
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
        String content = Html.fromHtml(journalContent.getHtml(), Html.FROM_HTML_MODE_LEGACY).toString();

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
        reflectiveJournal.setJournalContent(journalContent.getHtml());
        reflectiveJournal.setJournalId(journalId);

        if (journal.getJournalId() == 0) {
            databaseHelper.reflectiveJournalContentDao().insert(reflectiveJournal);
            
            // No need for temporary tag handling - the new system manages this directly
        } else {
            databaseHelper.reflectiveJournalContentDao().updateReflectiveJournalEntity(reflectiveJournal);
        }

        JournalUtils.updateStreak(ReflectiveJournal.this);
        HomeFragment.notifyHomeRecyclerViewChanges();
        
        // Save temporary tags if journal was just created
        if (!temporaryTags.isEmpty()) {
            for (String tagName : temporaryTags) {
                tagManager.addTagToJournal(journalId, tagName);
            }
            temporaryTags.clear();  // Clear after saving
        }

        Toast.makeText(ReflectiveJournal.this, "Journal Saved Successfully", Toast.LENGTH_LONG).show();
    }

    // No need for temporary tags with the new system
    
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

    private void showRichEditorPrompt() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.prompt_dialog_layout);
        
        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        TextView promptText = dialog.findViewById(R.id.prompt_text);
        Button newPromptButton = dialog.findViewById(R.id.new_prompt_button);
        Button usePromptButton = dialog.findViewById(R.id.use_prompt_button);
        
        // Set dialog title
        dialogTitle.setText(ApplicationConstants.REFLECTIVE_JOURNAL + " Prompt");
        
        // Add subtitle/explanation
        TextView dialogSubtitle = dialog.findViewById(R.id.dialog_subtitle);
        if (dialogSubtitle != null) {
            dialogSubtitle.setText("Select a writing prompt to inspire your journal entry");
            dialogSubtitle.setVisibility(View.VISIBLE);
        }
        
        // Get prompts for this journal type
        List<String> prompts = ApplicationConstants.REFLECTIVE_PROMPTS;
        
        // Show a random prompt
        if (prompts != null && !prompts.isEmpty()) {
            Random random = new Random();
            int index = random.nextInt(prompts.size());
            String randomPrompt = prompts.get(index);
            promptText.setText(randomPrompt);
        }
        
        // Get a new prompt when clicking "New Prompt" button
        newPromptButton.setOnClickListener(view -> {
            if (prompts != null && !prompts.isEmpty()) {
                Random random = new Random();
                int index = random.nextInt(prompts.size());
                String randomPrompt = prompts.get(index);
                promptText.setText(randomPrompt);
            }
        });
        
        // Use the prompt
        usePromptButton.setOnClickListener(view -> {
            String prompt = promptText.getText().toString();
            
            // Set the title
            journalTitle.setText(prompt);
            
            // Set focus to the content editor
            journalContent.focusEditor();
            
            dialog.dismiss();
            Toast.makeText(this, "Prompt applied as title", Toast.LENGTH_SHORT).show();
        });
        
        dialog.show();
    }
    
    /**
     * Show the tags management dialog
     */
    private void showTagsDialog() {
        if (journal != null && journal.getJournalId() > 0) {
            // Saved journal - use database mode
            TagDialogFragment dialogFragment = TagDialogFragment.newInstance(journal.getJournalId());
            dialogFragment.show(getSupportFragmentManager(), "tag_dialog");
        } else {
            // Unsaved journal - use temporary mode
            TagDialogFragment dialogFragment = TagDialogFragment.newInstanceTemporary(
                temporaryTags,
                updatedTags -> {
                    temporaryTags = updatedTags;
                }
            );
            dialogFragment.show(getSupportFragmentManager(), "tag_dialog");
        }
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
    
    private void setupRichEditor() {
        journalContent.setEditorFontSize(16);
        journalContent.setPadding(16, 16, 16, 16);
        journalContent.setBackgroundColor(Color.TRANSPARENT);
        journalContent.setPlaceholder("Reflect on your day...");
        
        journalContent.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                updateWordCount(text);
                if (android.text.TextUtils.isEmpty(text) || text.equals("<br>")) {
                    placeholderTextView.setVisibility(View.VISIBLE);
                } else {
                    placeholderTextView.setVisibility(View.GONE);
                }
            }
        });
        
        journalContent.focusEditor();
    }
    
    private void setupFormattingButtons() {
        undoButton.setOnClickListener(v -> journalContent.undo());
        redoButton.setOnClickListener(v -> journalContent.redo());
        boldButton.setOnClickListener(v -> journalContent.setBold());
        italicButton.setOnClickListener(v -> journalContent.setItalic());
        underlineButton.setOnClickListener(v -> journalContent.setUnderline());
        heading1Button.setOnClickListener(v -> journalContent.setHeading(1));
        heading2Button.setOnClickListener(v -> journalContent.setHeading(2));
        bulletButton.setOnClickListener(v -> journalContent.setBullets());
        numbersButton.setOnClickListener(v -> journalContent.setNumbers());
        textColorButton.setOnClickListener(v -> showColorPicker());
        fontSizeButton.setOnClickListener(v -> showFontSizePicker());
    }
    
    private void updateWordCount(String htmlContent) {
        String plainText = Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY).toString();
        String[] words = plainText.trim().split("\\s+");
        int wordCount = plainText.trim().isEmpty() ? 0 : words.length;
        wordCountTextView.setText(wordCount + (wordCount == 1 ? " word" : " words"));
    }
    
    private void showColorPicker() {
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.dialog_color_picker);
        dialog.show();
        
        GridLayout colorGrid = dialog.findViewById(R.id.color_grid);
        final int[] colors = {
            Color.BLACK, Color.DKGRAY, Color.GRAY,
            Color.RED, Color.rgb(255, 100, 100), Color.rgb(255, 150, 150),
            Color.GREEN, Color.rgb(144, 238, 144), Color.rgb(152, 251, 152),
            Color.BLUE, Color.rgb(135, 206, 250), Color.rgb(173, 216, 230),
            Color.rgb(138, 43, 226), Color.MAGENTA, Color.rgb(221, 160, 221),
            Color.rgb(165, 42, 42), Color.rgb(210, 105, 30), Color.rgb(244, 164, 96)
        };
        
        colorGrid.setColumnCount(3);
        colorGrid.setRowCount((colors.length + 2) / 3);
        
        for (int color : colors) {
            Button colorButton = new Button(this);
            colorButton.setBackgroundColor(color);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 100;
            params.height = 100;
            params.setMargins(8, 8, 8, 8);
            colorButton.setLayoutParams(params);
            colorButton.setOnClickListener(v -> {
                journalContent.setTextColor(color);
                textColorButton.setTextColor(color);
                dialog.dismiss();
            });
            colorGrid.addView(colorButton);
        }
        
        Button cancelButton = dialog.findViewById(R.id.btn_cancel_color);
        cancelButton.setOnClickListener(v -> dialog.dismiss());
    }
    
    private void showFontSizePicker() {
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.dialog_font_size_picker);
        dialog.show();
        
        LinearLayout sizeContainer = dialog.findViewById(R.id.font_size_container);
        final int[] fontSizes = {12, 14, 16, 18, 20, 24, 28, 32};
        final String[] sizeLabels = {"Tiny", "Small", "Normal", "Medium", "Large", "XL", "XXL", "Huge"};
        
        for (int i = 0; i < fontSizes.length; i++) {
            final int size = fontSizes[i];
            final String label = sizeLabels[i];
            Button sizeButton = new Button(this);
            sizeButton.setText(label + " (" + size + "px)");
            sizeButton.setTextSize(Math.min(size, 20));
            sizeButton.setAllCaps(false);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 8, 0, 8);
            sizeButton.setLayoutParams(params);
            sizeButton.setOnClickListener(v -> {
                journalContent.setFontSize(size);
                dialog.dismiss();
            });
            sizeContainer.addView(sizeButton);
        }
        
        Button cancelButton = dialog.findViewById(R.id.btn_cancel_font_size);
        cancelButton.setOnClickListener(v -> dialog.dismiss());
    }

}