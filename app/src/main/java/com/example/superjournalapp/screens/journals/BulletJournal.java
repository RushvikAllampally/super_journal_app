package com.example.superjournalapp.screens.journals;

import static com.example.superjournalapp.utils.JournalUtils.getMonthName;

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

import com.example.superjournalapp.R;
import com.example.superjournalapp.constants.ApplicationConstants;
import com.example.superjournalapp.database.DatabaseHelper;
import com.example.superjournalapp.dto.BulletEntryDetails;
import com.example.superjournalapp.entity.Journal;
import com.example.superjournalapp.entity.JournalCategories.BulletJournalEntity;
import com.example.superjournalapp.recyclerviews.BulletRecyclerAdaptor;
import com.example.superjournalapp.recyclerviews.BulletRecyclerRowMoveCallback;
import com.example.superjournalapp.screens.fragments.HomeFragment;
import com.example.superjournalapp.screens.fragments.JournalListFragment;
import com.example.superjournalapp.utils.JournalUtils;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bullet_journal);

        final Calendar calendar = Calendar.getInstance();
        journal = new Journal();

        databaseHelper = DatabaseHelper.getDb(this);

        closeJournalButton = findViewById(R.id.close_journal_bullet);
        saveJournalButton = findViewById(R.id.save_journal_bullet);
        calenderImage = findViewById(R.id.journal_calender_icon_bullet);
        dateState = findViewById(R.id.journal_date_state_bullet);
        selectJournalDate = findViewById(R.id.selected_journal_date_bullet);
        tasksRecyclerView = findViewById(R.id.bullet_tasks_recyclerview);
        addTaskBtn = findViewById(R.id.addBulletTask);
        pinImage = findViewById(R.id.journal_pin_icon);
        deleteIcon = findViewById(R.id.bullet_delete_icon);


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
                isPinned = !isPinned;
                if (isPinned) {
                    pinImage.setImageResource(R.drawable.unpin_icon);
                } else {
                    pinImage.setImageResource(R.drawable.pin_icon);
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

    private void saveJournalDetails() {
        journal.setJournalCreatedOn(selectedDate == null ? new Date() : selectedDate);
        journal.setJournalCategory(ApplicationConstants.BULLET_JOURNAL);

        BulletJournalEntity bulletJournalEntity = new BulletJournalEntity();

        bulletJournalEntity.setJournalCreatedOn(selectedDate == null ? new Date() : selectedDate);
        bulletJournalEntity.setJournalCategory(ApplicationConstants.BULLET_JOURNAL);
        bulletJournalEntity.setListPinned(isPinned);

        bulletRecyclerAdaptor.saveBulletJournal(databaseHelper, bulletJournalEntity, journal, journal.getJournalId() == 0);

        JournalUtils.updateStreak(BulletJournal.this);
        HomeFragment.notifyHomeRecyclerViewChanges();
        HomeFragment.notifyHomeBulletChanges();

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