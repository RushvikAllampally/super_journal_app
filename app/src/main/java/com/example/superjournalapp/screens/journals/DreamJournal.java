package com.example.superjournalapp.screens.journals;

import static com.example.superjournalapp.utils.JournalUtils.getMonthName;

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

import com.example.superjournalapp.R;
import com.example.superjournalapp.constants.ApplicationConstants;
import com.example.superjournalapp.database.DatabaseHelper;
import com.example.superjournalapp.entity.Journal;
import com.example.superjournalapp.entity.JournalCategories.DreamJournalEntity;
import com.example.superjournalapp.screens.fragments.HomeFragment;
import com.example.superjournalapp.screens.fragments.JournalListFragment;
import com.example.superjournalapp.utils.JournalUtils;
import com.example.superjournalapp.utils.TextEditorUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.vanniktech.emoji.EmojiPopup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DreamJournal extends AppCompatActivity {

    private BottomSheetDialog bottomSheetDialog;
    private ImageView closeJournalButton;
    private Button saveJournalButton;
    private ImageView calenderImage;
    private TextView dateState;
    private ImageButton deleteIcon;
    private ImageButton colorPalette;
    private ImageButton textStylesBtn;
    private ImageButton emojiesBtn;
    private TextView selectJournalDate;
    private EditText journalTitle = null;
    private EditText journalContent;
    private Date selectedDate = null;
    private Journal journal;
    private DatabaseHelper databaseHelper;
    private DreamJournalEntity dreamJournalEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dream_journal);

        final Calendar calendar = Calendar.getInstance();
        journal = new Journal();

        databaseHelper = DatabaseHelper.getDb(this);

        closeJournalButton = findViewById(R.id.close_journal_dream);
        saveJournalButton = findViewById(R.id.save_journal_dream);
        calenderImage = findViewById(R.id.journal_calender_icon_dream);
        dateState = findViewById(R.id.journal_date_state_dream);
        selectJournalDate = findViewById(R.id.selected_journal_date_dream);
        journalTitle = findViewById(R.id.journal_title_dream);
        journalContent = findViewById(R.id.journal_content_dream);
        deleteIcon = findViewById(R.id.dream_delete_icon);
        colorPalette = findViewById(R.id.dream_color_palette);
        textStylesBtn = findViewById(R.id.dream_text_style_icon);
        emojiesBtn = findViewById(R.id.dream_emoji_icon);

        EmojiPopup popup = EmojiPopup.Builder.fromRootView(findViewById(R.id.dream_journal_root)).build(journalContent);
        emojiesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.toggle();
            }
        });

        colorPalette.setOnClickListener(view -> {
            TextEditorUtils.colorPaletteOnClickListener(bottomSheetDialog, journalContent, DreamJournal.this);
        });

        textStylesBtn.setOnClickListener(view -> {
            TextEditorUtils.textStylesOnClickListener(bottomSheetDialog, journalContent, DreamJournal.this);

        });

//        EmojiPop


        // Retrieve the data from the Intent
        Intent intent = getIntent();
        if (intent != null) {
            String journalId = intent.getStringExtra(ApplicationConstants.JOURNAL_ID_INTENT); // -1 is a default value in case the key is not found
            System.out.println("journalId : " + journalId);
            if (!(journalId == null || journalId.isEmpty())) {
                // Now you have the journalId, and you can use it in your Activity
                dreamJournalEntity = databaseHelper.dreamJournalContentDao().getDreamJournalById(Long.parseLong(journalId));
                journal = databaseHelper.journalDao().getMainJournalById(Long.parseLong(journalId));

                journal.setJournalId(Long.parseLong(journalId));

                journalTitle.setText(dreamJournalEntity.getTitle());

                /// Convert the HTML-formatted string back to a Spannable
                Spanned spanned = Html.fromHtml(dreamJournalEntity.getJournalContent(), Html.FROM_HTML_MODE_LEGACY, null, null);

                journalContent.setText(spanned);

                // Define the desired date format
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd, MMMM yyyy", Locale.ENGLISH);

                // Convert the date to a string with the specified format
                String formattedDate = dateFormat.format(dreamJournalEntity.getJournalCreatedOn());

                selectJournalDate.setText(formattedDate);
                selectedDate = dreamJournalEntity.getJournalCreatedOn();

            } else {
                selectJournalDate.setText(calendar.get(Calendar.DAY_OF_MONTH) + ", " + getMonthName(String.valueOf(calendar.get(Calendar.MONTH) + 1)) + " " + calendar.get(Calendar.YEAR));
            }
        }

        closeJournalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationDialog();
            }
        });

        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog(databaseHelper, dreamJournalEntity);
            }
        });

        calenderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int y = calendar.get(Calendar.YEAR);
                int m = calendar.get(Calendar.MONTH);
                int d = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(DreamJournal.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        System.out.println(getMonthName(String.valueOf(i1)));
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

        saveJournalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveJournalDetails();
                finish();
            }
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
            Toast.makeText(DreamJournal.this, "Journal Title can't be Empty", Toast.LENGTH_LONG).show();
            return;
        } else if (content.isEmpty()) {
            Toast.makeText(DreamJournal.this, "Journal Content can't be Empty", Toast.LENGTH_LONG).show();
            return;
        }

        journal.setJournalCreatedOn(selectedDate == null ? new Date() : selectedDate);
        journal.setJournalCategory(ApplicationConstants.DREAM_JOURNAL);

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

        DreamJournalEntity dreamJournal = new DreamJournalEntity();

        dreamJournal.setJournalCreatedOn(selectedDate == null ? new Date() : selectedDate);
        dreamJournal.setJournalCategory(ApplicationConstants.DREAM_JOURNAL);
        dreamJournal.setJournalStartText(content.substring(0, contentLength));
        dreamJournal.setTitle(title);
        dreamJournal.setJournalContent(Html.toHtml(new SpannableStringBuilder((Spanned) journalContent.getText())));
        dreamJournal.setJournalId(journalId);

        if (journal.getJournalId() == 0) {
            databaseHelper.dreamJournalContentDao().insert(dreamJournal);
        } else {
            databaseHelper.dreamJournalContentDao().updateDreamJournalEntity(dreamJournal);

        }

        JournalUtils.updateStreak(DreamJournal.this);
        HomeFragment.notifyHomeRecyclerViewChanges();

        Toast.makeText(DreamJournal.this, "Journal Saved Successfully", Toast.LENGTH_LONG).show();
    }

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

    private void showDeleteConfirmationDialog(DatabaseHelper databaseHelper, DreamJournalEntity dreamJournalEntity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Journal");
        builder.setMessage("Are you sure you want to delete journal ?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "Discard," so finish the activity and discard changes
                databaseHelper.dreamJournalContentDao().deleteJournal(dreamJournalEntity);
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