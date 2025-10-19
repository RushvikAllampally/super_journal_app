package com.diary.superjournalapp.screens.journals;

import static com.diary.superjournalapp.utils.JournalUtils.getMonthName;

import android.app.DatePickerDialog;
import android.app.Dialog;
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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.constants.ApplicationConstants;
import com.diary.superjournalapp.database.DatabaseHelper;
import com.diary.superjournalapp.dto.BulletEntryDetails;
import com.diary.superjournalapp.entity.Journal;
import com.diary.superjournalapp.entity.JournalCategories.BulletJournalEntity;
import com.diary.superjournalapp.recyclerviews.BulletRecyclerAdaptor;
import com.diary.superjournalapp.recyclerviews.BulletRecyclerRowMoveCallback;
import com.diary.superjournalapp.screens.fragments.HomeFragment;
import com.diary.superjournalapp.screens.fragments.JournalListFragment;
import com.diary.superjournalapp.utils.JournalUtils;
import com.diary.superjournalapp.utils.TagManager;
import com.diary.superjournalapp.dialogs.TagDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BulletJournal extends AppCompatActivity {

    private ImageView closeJournalButton;

    private Button saveJournalButton;
    private ImageView calenderImage;
    private TextView dateState;
    private TextView selectJournalDate;
    private FloatingActionButton addTaskBtn;
    private EditText journalTitle = null;
    private RecyclerView tasksRecyclerView;
    private BulletRecyclerAdaptor bulletRecyclerAdaptor;
    private BulletJournalEntity bulletJournalEntity;
    private ImageView pinImage;
    private boolean isPinned;
    private Journal journal;
    private ImageButton deleteIcon;
    private Date selectedDate = null;
    private DatabaseHelper databaseHelper;
    private List<BulletEntryDetails> tasksList = new ArrayList<>();
    private TagManager tagManager;
    private ImageButton manageTagsButton;
    private ArrayList<String> temporaryTags = new ArrayList<>();  // For unsaved journals

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bullet_journal);

        final Calendar calendar = Calendar.getInstance();
        journal = new Journal();

        databaseHelper = DatabaseHelper.getDb(this);
        tagManager = new TagManager(this);

        closeJournalButton = findViewById(R.id.close_journal_bullet);
        saveJournalButton = findViewById(R.id.save_journal_bullet);
        calenderImage = findViewById(R.id.journal_calender_icon_bullet);
        dateState = findViewById(R.id.journal_date_state_bullet);
        selectJournalDate = findViewById(R.id.selected_journal_date_bullet);
        tasksRecyclerView = findViewById(R.id.bullet_tasks_recyclerview);
        addTaskBtn = findViewById(R.id.addBulletTask);
        pinImage = findViewById(R.id.journal_pin_icon);
        deleteIcon = findViewById(R.id.bullet_delete_icon);
        manageTagsButton = findViewById(R.id.manage_tags_button);
        
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
                bulletJournalEntity = databaseHelper.bulletJournalContentDao().getBulletJournalById(Long.parseLong(journalId));
                journal = databaseHelper.journalDao().getMainJournalById(Long.parseLong(journalId));

                journal.setJournalId(Long.parseLong(journalId));

//                journalTitle.setText("title");

                Gson gson = new Gson();
                Type listType = new TypeToken<List<BulletEntryDetails>>() {
                }.getType();
                tasksList = gson.fromJson(bulletJournalEntity.taskListJson, listType);

                isPinned = bulletJournalEntity.isListPinned();

                if (isPinned) {
                    pinImage.setImageResource(R.drawable.unpin_icon);
                }

//                journalContent.setText(bulletJournalEntity.getJournalContent());

                // Define the desired date format
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd, MMMM yyyy", Locale.ENGLISH);

                // Convert the date to a string with the specified format
                String formattedDate = dateFormat.format(bulletJournalEntity.getJournalCreatedOn());

                selectJournalDate.setText(formattedDate);
                selectedDate = bulletJournalEntity.getJournalCreatedOn();

            } else {
                selectJournalDate.setText(calendar.get(Calendar.DAY_OF_MONTH) + ", " + getMonthName(String.valueOf(calendar.get(Calendar.MONTH) + 1)) + " " + calendar.get(Calendar.YEAR));
            }
        }


        //recycler view

        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(BulletJournal.this));
        bulletRecyclerAdaptor = new BulletRecyclerAdaptor(BulletJournal.this, tasksList);

        ItemTouchHelper.Callback callback = new BulletRecyclerRowMoveCallback(bulletRecyclerAdaptor);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);

        bulletRecyclerAdaptor.setDragStartListener(new BulletRecyclerAdaptor.OnDragStartListener() {
            @Override
            public void onDragStarted(RecyclerView.ViewHolder viewHolder) {
                // Start dragging when the 6-dot button is touched
                touchHelper.startDrag(viewHolder);
            }
        });

        touchHelper.attachToRecyclerView(tasksRecyclerView);

        tasksRecyclerView.setAdapter(bulletRecyclerAdaptor);

        closeJournalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmationDialog();
            }
        });

        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog(databaseHelper, bulletJournalEntity);
            }
        });

        calenderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int y = calendar.get(Calendar.YEAR);
                int m = calendar.get(Calendar.MONTH);
                int d = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(BulletJournal.this, new DatePickerDialog.OnDateSetListener() {
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

        pinImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toggle pin status
                isPinned = !isPinned;
                
                // Update UI immediately
                updatePinIconDisplay();
                
                // Only proceed if we have a valid journal
                if (bulletJournalEntity != null && journal != null && journal.getJournalId() != 0) {
                    // If pinning, we should handle potential multiple pinned journals
                    if (isPinned) {
                        handlePinning();
                    } else {
                        handleUnpinning();
                    }
                    
                    // Force UI update in home fragment
                    refreshHomeFragment();
                    
                    // Provide user feedback
                    Toast.makeText(BulletJournal.this, 
                        isPinned ? "Journal Pinned to Home Screen" : "Journal Unpinned from Home Screen", 
                        Toast.LENGTH_SHORT).show();
                }
            }
        });

        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(BulletJournal.this);
                dialog.setContentView(R.layout.addtask_dialog);
                dialog.show();

                EditText taskText = dialog.findViewById(R.id.task_entry_edittext);
                TextView delete = dialog.findViewById(R.id.delete_task);
                TextView cancel = dialog.findViewById(R.id.cancel_task_textview);
                TextView add = dialog.findViewById(R.id.add_task_textview);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (taskText.getText().toString().trim().isEmpty()) {
                            Toast.makeText(BulletJournal.this, "Task Can't Be Empty", Toast.LENGTH_LONG);
                        }
                        BulletEntryDetails bulletEntryDetails = new BulletEntryDetails();
                        bulletEntryDetails.setTaskName(taskText.getText().toString().trim());
                        tasksList.add(bulletEntryDetails);
                        dialog.dismiss();

                        bulletRecyclerAdaptor.notifyItemInserted(tasksList.size() - 1);
                    }
                });

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
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
    
    /**
     * Update the pin icon display based on the current pin state
     */
    private void updatePinIconDisplay() {
        if (isPinned) {
            pinImage.setImageResource(R.drawable.unpin_icon);
        } else {
            pinImage.setImageResource(R.drawable.pin_icon);
        }
    }
    
    /**
     * Handle pinning this journal, accounting for other potentially pinned journals
     */
    private void handlePinning() {
        // Update this journal's pinned status
        bulletJournalEntity.setListPinned(true);
        bulletJournalEntity.setDontShow(false); // Make sure it's visible
        
        // Save changes to database
        databaseHelper.bulletJournalContentDao().updateBulletJournalEntity(bulletJournalEntity);
    }
    
    /**
     * Handle unpinning this journal
     */
    private void handleUnpinning() {
        // Update this journal's pinned status
        bulletJournalEntity.setListPinned(false);
        bulletJournalEntity.setDontShow(true); // Hide from home screen
        
        // Save changes to database
        databaseHelper.bulletJournalContentDao().updateBulletJournalEntity(bulletJournalEntity);
    }
    
    /**
     * Refresh the home fragment to reflect changes in pin status
     */
    private void refreshHomeFragment() {
        // Force UI updates on the home screen
        HomeFragment.notifyHomeRecyclerViewChanges();
        HomeFragment.notifyHomeBulletChanges();
    }

    private void saveJournalDetails() {
        // Update journal basic details
        journal.setJournalCreatedOn(selectedDate == null ? new Date() : selectedDate);
        journal.setJournalCategory(ApplicationConstants.BULLET_JOURNAL);
        
        boolean isNewJournal = journal.getJournalId() == 0;
        
        // Handle existing vs new bullet journal entity
        if (isNewJournal || this.bulletJournalEntity == null) {
            // Create new entity for new journals
            this.bulletJournalEntity = new BulletJournalEntity();
            this.bulletJournalEntity.setJournalCreatedOn(selectedDate == null ? new Date() : selectedDate);
            this.bulletJournalEntity.setJournalCategory(ApplicationConstants.BULLET_JOURNAL);
            this.bulletJournalEntity.setListPinned(isPinned);
            
            // If pinned, make sure it's visible
            if (isPinned) {
                this.bulletJournalEntity.setDontShow(false);
            }
        } else {
            // Update existing entity
            this.bulletJournalEntity.setJournalCreatedOn(selectedDate == null ? new Date() : selectedDate);
            // Note: we don't update the pin status here as that's handled by the pin button click
        }

        // Save to database
        bulletRecyclerAdaptor.saveBulletJournal(databaseHelper, this.bulletJournalEntity, journal, isNewJournal);

        // Update UI
        JournalUtils.updateStreak(BulletJournal.this);
        HomeFragment.notifyHomeRecyclerViewChanges();
        HomeFragment.notifyHomeBulletChanges();
        
        // Save temporary tags if journal was just created
        // Note: journal.getJournalId() is updated by saveBulletJournal, so we check the temporaryTags list
        if (!temporaryTags.isEmpty() && journal.getJournalId() > 0) {
            for (String tagName : temporaryTags) {
                tagManager.addTagToJournal(journal.getJournalId(), tagName);
            }
            temporaryTags.clear();  // Clear after saving
        }

        Toast.makeText(BulletJournal.this, "Journal Saved Successfully", Toast.LENGTH_LONG).show();
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
    
    private void showDeleteConfirmationDialog(DatabaseHelper databaseHelper, BulletJournalEntity bulletJournalEntity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Journal");
        builder.setMessage("Are you sure you want to delete journal ?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "Discard," so finish the activity and discard changes
                databaseHelper.bulletJournalContentDao().deleteJournal(bulletJournalEntity);
                databaseHelper.journalDao().deleteJournal(journal);
                HomeFragment.notifyHomeRecyclerViewChanges();
                HomeFragment.notifyHomeBulletChanges();
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