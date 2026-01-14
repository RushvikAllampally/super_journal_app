package com.diary.superjournalapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.backup.GoogleDriveManager;

import java.util.ArrayList;
import java.util.List;

public class RestoreBackupAdapter extends RecyclerView.Adapter<RestoreBackupAdapter.RestoreBackupViewHolder> {
    
    private Context context;
    private List<GoogleDriveManager.BackupInfo> backups = new ArrayList<>();
    private OnBackupSelectedListener listener;
    
    public interface OnBackupSelectedListener {
        void onBackupSelected(GoogleDriveManager.BackupInfo backup);
    }
    
    public RestoreBackupAdapter(Context context, OnBackupSelectedListener listener) {
        this.context = context;
        this.listener = listener;
    }
    
    public void setBackups(List<GoogleDriveManager.BackupInfo> backups) {
        this.backups = backups;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public RestoreBackupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_restore_backup, parent, false);
        return new RestoreBackupViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull RestoreBackupViewHolder holder, int position) {
        GoogleDriveManager.BackupInfo backup = backups.get(position);
        holder.bind(backup, listener);
    }
    
    @Override
    public int getItemCount() {
        return backups.size();
    }
    
    static class RestoreBackupViewHolder extends RecyclerView.ViewHolder {
        
        private TextView dateText;
        private TextView sizeText;
        private TextView journalCountText;
        private TextView deviceInfoText;
        private Button restoreButton;
        
        public RestoreBackupViewHolder(@NonNull View itemView) {
            super(itemView);
            
            dateText = itemView.findViewById(R.id.backup_date_text);
            sizeText = itemView.findViewById(R.id.backup_size_text);
            journalCountText = itemView.findViewById(R.id.journal_count_text);
            deviceInfoText = itemView.findViewById(R.id.device_info_text);
            restoreButton = itemView.findViewById(R.id.restore_button);
        }
        
        public void bind(GoogleDriveManager.BackupInfo backup, OnBackupSelectedListener listener) {
            dateText.setText(backup.getFormattedCreatedTime());
            sizeText.setText(backup.getFormattedFileSize());
            
            if (backup.getMetadata() != null) {
                journalCountText.setText(backup.getMetadata().getJournalCount() + " journals");
                String deviceInfo = backup.getMetadata().getDeviceModel() + " (" + 
                                  backup.getMetadata().getDeviceManufacturer() + ")";
                deviceInfoText.setText(deviceInfo);
            } else {
                journalCountText.setText("Unknown journal count");
                deviceInfoText.setText("Unknown device");
            }
            
            restoreButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBackupSelected(backup);
                }
            });
        }
    }
}
