package com.diary.superjournalapp.screens;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.adapters.RestoreBackupAdapter;
import com.diary.superjournalapp.backup.GoogleDriveManager;
import com.diary.superjournalapp.base.ThemedActivity;
import com.diary.superjournalapp.database.DatabaseHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RestoreActivity extends ThemedActivity implements RestoreBackupAdapter.OnBackupSelectedListener {
    
    private RecyclerView recyclerView;
    private RestoreBackupAdapter adapter;
    private GoogleDriveManager driveManager;
    private ProgressDialog progressDialog;
    private ExecutorService executor;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore);
        
        initializeViews();
        setupDriveManager();
        setupRecyclerView();
        loadBackups();
    }
    
    private void initializeViews() {
        recyclerView = findViewById(R.id.restore_recycler_view);
        executor = Executors.newSingleThreadExecutor();
        
        // Set up back button
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
    }
    
    private void setupDriveManager() {
        driveManager = new GoogleDriveManager(this);
        
        // Initialize with current signed-in account
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            driveManager.initializeDriveService(account);
        }
    }
    
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RestoreBackupAdapter(this, this);
        recyclerView.setAdapter(adapter);
    }
    
    private void loadBackups() {
        showProgressDialog("Loading backups...");
        
        driveManager.listBackups(new GoogleDriveManager.BackupListCallback() {
            @Override
            public void onSuccess(List<GoogleDriveManager.BackupInfo> backups) {
                runOnUiThread(() -> {
                    hideProgressDialog();
                    if (backups.isEmpty()) {
                        Toast.makeText(RestoreActivity.this, "No backups found in Google Drive", Toast.LENGTH_SHORT).show();
                    } else {
                        adapter.setBackups(backups);
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    hideProgressDialog();
                    Toast.makeText(RestoreActivity.this, "Failed to load backups: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    @Override
    public void onBackupSelected(GoogleDriveManager.BackupInfo backup) {
        startRestore(backup);
    }
    
    private void startRestore(GoogleDriveManager.BackupInfo backup) {
        showProgressDialog("Restoring backup...");
        
        driveManager.restoreBackup(backup.getFileId(), new GoogleDriveManager.RestoreCallback() {
            @Override
            public void onSuccess(byte[] databaseData) {
                executor.execute(() -> {
                    try {
                        // Create a backup of current database first
                        File currentDb = getDatabasePath("journal_app_db");
                        File backupCurrentDb = new File(getFilesDir(), "journal_app_db.backup");
                        
                        if (currentDb.exists()) {
                            // Create backup of current database
                            java.nio.file.Files.copy(currentDb.toPath(), backupCurrentDb.toPath(), 
                                                   java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        }
                        
                        // Close current database connections
                        DatabaseHelper.getDb(RestoreActivity.this).close();
                        
                        // Write restored data to database file
                        try (FileOutputStream fos = new FileOutputStream(currentDb)) {
                            fos.write(databaseData);
                        }
                        
                        runOnUiThread(() -> {
                            hideProgressDialog();
                            Toast.makeText(RestoreActivity.this, "✅ Restore completed! Your journals have been safely decrypted and restored. Please restart the app to see your data.", Toast.LENGTH_LONG).show();
                            
                            // You might want to restart the app here
                            finishAffinity();
                        });
                        
                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            hideProgressDialog();
                            Toast.makeText(RestoreActivity.this, 
                                         "Failed to restore: " + e.getMessage(), 
                                         Toast.LENGTH_LONG).show();
                        });
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    hideProgressDialog();
                    Toast.makeText(RestoreActivity.this, "Restore failed: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    private void showProgressDialog(String message) {
        runOnUiThread(() -> {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(this);
                progressDialog.setCancelable(false);
            }
            progressDialog.setMessage(message);
            progressDialog.show();
        });
    }
    
    private void hideProgressDialog() {
        runOnUiThread(() -> {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
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
