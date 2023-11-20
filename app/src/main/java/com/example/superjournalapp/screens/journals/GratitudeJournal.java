package com.example.superjournalapp.screens.journals;

import static com.example.superjournalapp.utils.JournalUtils.getMonthName;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.superjournalapp.entity.JournalCategories.GratitudeJournalEntity;
import com.example.superjournalapp.screens.fragments.HomeFragment;
import com.example.superjournalapp.screens.fragments.JournalListFragment;
import com.example.superjournalapp.utils.JournalUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GratitudeJournal extends AppCompatActivity {

    private ImageView closeJournalButton;

    private Button saveJournalButton;
    private ImageView calenderImage;
    private TextView dateState;
    private TextView selectJournalDate;
    private TextView journalTitle = null;
    private EditText journalContent;
    private ImageButton deleteIcon;
    private Journal journal;
    private GratitudeJournalEntity gratitudeJournal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gratitude_journal);

        final Calendar calendar = Calendar.getInstance();
        journal = new Journal();

        DatabaseHelper databaseHelper = DatabaseHelper.getDb(this);

        closeJournalButton = findViewById(R.id.close_journal);
        saveJournalButton = findViewById(R.id.save_journal);
        calenderImage = findViewById(R.id.journal_calender_icon);
        dateState = findViewById(R.id.journal_date_state);
        selectJournalDate = findViewById(R.id.selected_journal_date);
        journalTitle = findViewById(R.id.journal_title);
        journalContent = findViewById(R.id.journal_content);
        deleteIcon = findViewById(R.id.gratitude_delete_icon);

        // Retrieve the data from the Intent
        Intent intent = getIntent();
        if (intent != null) {
            String journalId = intent.getStringExtra(ApplicationConstants.JOURNAL_ID_INTENT); // -1 is a default value in case the key is not found
            System.out.println("journalId : " + journalId);
            if (!(journalId == null || journalId.isEmpty())) {
                // Now you have the journalId, and you can use it in your Activity
                gratitudeJournal = databaseHelper.gratitudeJournalContentDao().getGratitudeJournalById(Long.parseLong(journalId));
                journal = databaseHelper.journalDao().getMainJournalById(Long.parseLong(journalId));

                journal.setJournalId(Long.parseLong(journalId));

                journalTitle.setText(gratitudeJournal.getTitle());
                journalContent.setText(gratitudeJournal.getJournalContent());

                // Define the desired date format
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd, MMMM yyyy", Locale.ENGLISH);

                // Convert the date to a string with the specified format
                String formattedDate = dateFormat.format(gratitudeJournal.getJournalCreatedOn());

                selectJournalDate.setText(formattedDate);

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
                showDeleteConfirmationDialog(databaseHelper, gratitudeJournal);
            }
        });

        calenderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int y = calendar.get(Calendar.YEAR);
                int m = calendar.get(Calendar.MONTH);
                int d = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(GratitudeJournal.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        System.out.println(getMonthName(String.valueOf(i1)));
                        selectJournalDate.setText(i2 + ", " + getMonthName(String.valueOf(i1 + 1)) + " " + i);
                    }
                }, y, m, d);
                datePickerDialog.show();
            }
        });

        saveJournalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = journalTitle.getText().toString();
                String content = journalContent.getText().toString();

                if (title.isEmpty()) {
                    Toast.makeText(GratitudeJournal.this, "Journal Title can't be Empty", Toast.LENGTH_LONG).show();
                    return;
                } else if (content.isEmpty()) {
                    Toast.makeText(GratitudeJournal.this, "Journal Content can't be Empty", Toast.LENGTH_LONG).show();
                    return;
                }

                journal.setJournalCreatedOn(new Date());
                journal.setJournalCategory(ApplicationConstants.GRATITUDE_JOURNAL);

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

                GratitudeJournalEntity gratitudeJournalEntity = new GratitudeJournalEntity();

                gratitudeJournalEntity.setJournalCreatedOn(new Date());
                gratitudeJournalEntity.setJournalCategory(ApplicationConstants.GRATITUDE_JOURNAL);
                gratitudeJournalEntity.setJournalStartText(content.substring(0, contentLength));
                gratitudeJournalEntity.setTitle(title);
                gratitudeJournalEntity.setJournalContent(content);
                gratitudeJournalEntity.setJournalId(journalId);

                if (journal.getJournalId() == 0) {
                    databaseHelper.gratitudeJournalContentDao().insert(gratitudeJournalEntity);
                } else {
                    databaseHelper.gratitudeJournalContentDao().updateGratitudeJournalEntity(gratitudeJournalEntity);

                }
                JournalUtils.updateStreak(view.getContext());
                Toast.makeText(GratitudeJournal.this, "Journal Saved Successfully", Toast.LENGTH_LONG).show();

                HomeFragment.notifyHomeRecyclerViewChanges();

                finish();
            }
        });

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

    private void showDeleteConfirmationDialog(DatabaseHelper databaseHelper, GratitudeJournalEntity gratitudeJournalEntity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Journal");
        builder.setMessage("Are you sure you want to delete journal ?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "Discard," so finish the activity and discard changes
                databaseHelper.gratitudeJournalContentDao().deleteJournal(gratitudeJournalEntity);
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