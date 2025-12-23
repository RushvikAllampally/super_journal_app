package com.diary.superjournalapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.entity.BackupHistory;
import com.diary.superjournalapp.screens.RestoreActivity;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class BackupHistoryAdapter extends RecyclerView.Adapter<BackupHistoryAdapter.BackupHistoryViewHolder> {
    
    private Context context;
    private List<BackupHistory> backupHistory = new ArrayList<>();
    
    public BackupHistoryAdapter(Context context) {
        this.context = context;
    }
    
    public void setBackupHistory(List<BackupHistory> backupHistory) {
        this.backupHistory = backupHistory;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public BackupHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_backup_history, parent, false);
        return new BackupHistoryViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull BackupHistoryViewHolder holder, int position) {
        BackupHistory backup = backupHistory.get(position);
        holder.bind(backup);
    }
    
    @Override
    public int getItemCount() {
        return backupHistory.size();
    }
    
    class BackupHistoryViewHolder extends RecyclerView.ViewHolder {
        
        private TextView dateText;
        private TextView statusText;
        private TextView sizeText;
        private TextView journalCountText;
        private TextView typeText;
        private ImageView statusIcon;
        private MaterialButton actionButton;
        
        public BackupHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            
            dateText = itemView.findViewById(R.id.backup_date_text);
            statusText = itemView.findViewById(R.id.backup_status_text);
            sizeText = itemView.findViewById(R.id.backup_size_text);
            journalCountText = itemView.findViewById(R.id.journal_count_text);
            typeText = itemView.findViewById(R.id.backup_type_text);
            statusIcon = itemView.findViewById(R.id.backup_status_icon);
            actionButton = itemView.findViewById(R.id.backup_action_btn);
        }
        
        public void bind(BackupHistory backup) {
            dateText.setText(backup.getFormattedDate());
            statusText.setText(backup.getBackupStatus() != null ? backup.getBackupStatus() : "Unknown");
            sizeText.setText(backup.getFormattedFileSize() != null ? backup.getFormattedFileSize() : "Unknown size");
            journalCountText.setText(backup.getJournalCount() + " journals");
            String backupType = backup.getBackupType();
            typeText.setText(backupType != null ? backupType.toUpperCase() : "MANUAL");
            
            // Set status icon and color based on backup status
            String status = backup.getBackupStatus();
            if (status != null && backup.isSuccessful()) {
                statusIcon.setImageResource(R.drawable.ic_check_circle);
                statusIcon.setColorFilter(ContextCompat.getColor(context, R.color.success_green));
                statusText.setTextColor(ContextCompat.getColor(context, R.color.success_green));
            } else if (status != null && backup.isFailed()) {
                statusIcon.setImageResource(R.drawable.ic_error);
                statusIcon.setColorFilter(ContextCompat.getColor(context, R.color.error_red));
                statusText.setTextColor(ContextCompat.getColor(context, R.color.error_red));
            } else if (status != null && backup.isInProgress()) {
                statusIcon.setImageResource(R.drawable.ic_sync);
                statusIcon.setColorFilter(ContextCompat.getColor(context, R.color.warning_orange));
                statusText.setTextColor(ContextCompat.getColor(context, R.color.warning_orange));
            } else {
                // Default status for unknown/null status
                statusIcon.setImageResource(R.drawable.ic_backup);
                statusIcon.setColorFilter(ContextCompat.getColor(context, android.R.color.darker_gray));
                statusText.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray));
            }
            
            // Set up restore button
            actionButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, RestoreActivity.class);
                context.startActivity(intent);
            });
        }
    }
}
