package com.diary.superjournalapp.screens;

import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.adapters.BackupHistoryAdapter;
import com.diary.superjournalapp.base.ThemedActivity;
import com.diary.superjournalapp.database.DatabaseHelper;
import com.diary.superjournalapp.entity.BackupHistory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackupHistoryActivity extends ThemedActivity {
    
    private RecyclerView recyclerView;
    private BackupHistoryAdapter adapter;
    private ExecutorService executor;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_history);
        
        initializeViews();
        setupRecyclerView();
        loadBackupHistory();
    }
    
    private void initializeViews() {
        recyclerView = findViewById(R.id.backup_history_recycler_view);
        executor = Executors.newSingleThreadExecutor();
        
        // Set up back button
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
    }
    
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BackupHistoryAdapter(this);
        recyclerView.setAdapter(adapter);
    }
    
    private void loadBackupHistory() {
        executor.execute(() -> {
            try {
                List<BackupHistory> backupHistory = DatabaseHelper.getDb(this)
                        .backupHistoryDao()
                        .getAllBackupHistory();
                
                runOnUiThread(() -> {
                    if (backupHistory.isEmpty()) {
                        Toast.makeText(this, "No backup history found", Toast.LENGTH_SHORT).show();
                    } else {
                        adapter.setBackupHistory(backupHistory);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Failed to load backup history: " + e.getMessage(), 
                                 Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}
