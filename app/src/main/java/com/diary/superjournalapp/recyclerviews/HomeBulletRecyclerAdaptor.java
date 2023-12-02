package com.diary.superjournalapp.recyclerviews;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.dto.BulletEntryDetails;

import java.util.List;

public class HomeBulletRecyclerAdaptor extends RecyclerView.Adapter<HomeBulletRecyclerAdaptor.ViewHolder> {

    private List<BulletEntryDetails> tasksList;
    private Context context;

    public HomeBulletRecyclerAdaptor(List<BulletEntryDetails> tasksList, Context context) {
        this.tasksList = tasksList;
        this.context = context;
    }

    @NonNull
    @Override
    public HomeBulletRecyclerAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeBulletRecyclerAdaptor.ViewHolder holder, int position) {
        BulletEntryDetails bulletEntryDetails = tasksList.get(position);
        holder.todoListTitle.setText("• " + bulletEntryDetails.getTaskName());
        if (bulletEntryDetails.getIsTaskDone()) {
            holder.todoListTitle.setPaintFlags(holder.todoListTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.todoListTitle.setPaintFlags(holder.todoListTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView todoListTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            todoListTitle = itemView.findViewById(R.id.taskText);
        }

    }
}
