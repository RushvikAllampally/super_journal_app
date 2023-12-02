package com.diary.superjournalapp.recyclerviews;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.database.DatabaseHelper;
import com.diary.superjournalapp.dto.BulletEntryDetails;
import com.diary.superjournalapp.entity.Journal;
import com.diary.superjournalapp.entity.JournalCategories.BulletJournalEntity;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.List;

public class BulletRecyclerAdaptor extends RecyclerView.Adapter<BulletRecyclerAdaptor.ViewHolder> implements BulletRecyclerRowMoveCallback.RecyclerViewRowTouchHelperContract {

    Context context;

    List<BulletEntryDetails> tasksList;
    private OnDragStartListener dragStartListener;
    private OnCheckedChangeListener onCheckedChangeListener;

    public BulletRecyclerAdaptor(Context context, List<BulletEntryDetails> tasksList) {
        this.context = context;
        this.tasksList = tasksList;
    }

    @NonNull
    @Override
    public BulletRecyclerAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bullet_entry_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BulletRecyclerAdaptor.ViewHolder holder, int position) {
        BulletEntryDetails task = tasksList.get(position);
        holder.task.setText(task.getTaskName());
        holder.task.setChecked(task.getIsTaskDone());

        // Apply strikethrough effect if item is checked
        if (task.getIsTaskDone()) {
            holder.task.setPaintFlags(holder.task.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.task.setPaintFlags(holder.task.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        holder.taskEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(view.getContext());
                dialog.setContentView(R.layout.addtask_dialog);
                dialog.show();

                EditText taskText = dialog.findViewById(R.id.task_entry_edittext);
                TextView delete = dialog.findViewById(R.id.delete_task);
                TextView cancel = dialog.findViewById(R.id.cancel_task_textview);
                TextView add = dialog.findViewById(R.id.add_task_textview);

                taskText.setText(tasksList.get(holder.getAdapterPosition()).getTaskName());

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (taskText.getText().toString().isEmpty()) {
                            Toast.makeText(view.getContext(), "Task Can't Be Empty", Toast.LENGTH_LONG);
                        }
                        tasksList.get(holder.getAdapterPosition()).setTaskName(taskText.getText().toString().trim());
                        dialog.dismiss();
                        notifyItemChanged(holder.getAdapterPosition());
                    }
                });

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tasksList.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                        dialog.dismiss();
                    }
                });
            }
        });

        holder.taskDragBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    // Notify the listener when the 6-dot button is touched
                    if (dragStartListener != null) {
                        dragStartListener.onDragStarted(holder);
                    }
                }
                return false;
            }
        });

        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    compoundButton.setPaintFlags(holder.task.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    compoundButton.setPaintFlags(holder.task.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
                tasksList.get(holder.getAdapterPosition()).setIsTaskDone(b);
                System.out.println("tasksList : " + tasksList.toString());
//                bulletEntryDetails.setIsTaskDone(b);
//                tasksList.add(position, bulletEntryDetails);
            }
        });


    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    @Override
    public void onRowMoved(int from, int to) {
        if (from < to) {
            for (int i = from; i < to; i++) {
                Collections.swap(tasksList, i, i + 1);
            }
        } else {
            for (int i = from; i > to; i--) {
                Collections.swap(tasksList, i, i - 1);
            }
        }
        notifyItemMoved(from, to);
    }

    @Override
    public void onRowSelected(ViewHolder myViewHolder) {
//        myViewHolder.task.setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onRowClear(ViewHolder myViewHolder) {
//        myViewHolder.task.setBackgroundColor(Color.parseColor("#12dddd"));
    }

    public void saveBulletJournal(DatabaseHelper databaseHelper, BulletJournalEntity bulletJournalEntity, Journal journal, boolean isNewEntry) {

        String startText = tasksList.size() > 0 ? tasksList.get(0).getTaskName().substring(0, Math.min(tasksList.get(0).getTaskName().length(), 30)) : "";
        if (tasksList.size() >= 2) {
            startText = startText + "\n" + tasksList.get(1).getTaskName().substring(0, Math.min(tasksList.get(1).getTaskName().length(), 30));
        }
        if (tasksList.size() >= 3) {
            startText = startText + "\n" + tasksList.get(2).getTaskName().substring(0, Math.min(tasksList.get(2).getTaskName().length(), 30));
        }
        journal.setJournalStartText(startText);
        journal.setTitle("What are my today's tasks ?");


        long journalId;
        if (journal.getJournalId() == 0) {
            journalId = databaseHelper.journalDao().addJournal(journal);
        } else {
            databaseHelper.journalDao().updateJournal(journal);
            journalId = journal.getJournalId();
        }

        Gson gson = new Gson();
        String tasksJson = gson.toJson(tasksList);

        bulletJournalEntity.setTitle("What are my today's tasks ??");
        bulletJournalEntity.setJournalId(journalId);
        bulletJournalEntity.setTaskListJson(tasksJson);
        bulletJournalEntity.setJournalStartText(startText);

        if (bulletJournalEntity.isListPinned) {
            List<BulletJournalEntity> bulletJournalEntities = databaseHelper.bulletJournalContentDao().getPinnedBulletJournals(true);
            if (!bulletJournalEntities.isEmpty()) {
                for (BulletJournalEntity bulletJournal : bulletJournalEntities) {
                    if (bulletJournal.getJournalId() != bulletJournalEntity.getJournalId()) {
                        bulletJournal.setListPinned(false);
                        databaseHelper.bulletJournalContentDao().updateBulletJournalEntity(bulletJournal);
                    }
                }
            }
        }


        if (isNewEntry) {
            databaseHelper.bulletJournalContentDao().insert(bulletJournalEntity);
        } else {
            databaseHelper.bulletJournalContentDao().updateBulletJournalEntity(bulletJournalEntity);
        }

    }

    public void setDragStartListener(OnDragStartListener onDragStartListener) {
        this.dragStartListener = onDragStartListener;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public interface OnDragStartListener {
        void onDragStarted(RecyclerView.ViewHolder viewHolder);
    }

    public interface OnCheckedChangeListener {
        void onItemCheckedChanged(int position, boolean isChecked);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox task;
        Button taskDragBtn;
        Button taskEditBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            task = itemView.findViewById(R.id.bullet_checkbox);
            taskDragBtn = itemView.findViewById(R.id.task_drag_btn);
            taskEditBtn = itemView.findViewById(R.id.task_edit_button);
        }


    }


}
