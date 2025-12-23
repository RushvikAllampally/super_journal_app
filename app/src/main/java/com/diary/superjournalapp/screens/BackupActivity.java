package com.diary.superjournalapp.screens;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.diary.superjournalapp.R;
import com.diary.superjournalapp.backup.BackupMetadata;
import com.diary.superjournalapp.backup.GoogleDriveManager;
import com.diary.superjournalapp.base.ThemedActivity;
import com.diary.superjournalapp.database.DatabaseHelper;
import com.diary.superjournalapp.entity.BackupHistory;
import com.diary.superjournalapp.utils.PremiumFeatureManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackupActivity extends ThemedActivity implements GoogleDriveManager.BackupProgressListener {
    
    private GoogleDriveManager driveManager;
    private Button backupNowBtn;
    private Button restoreBtn;
    private Button viewHistoryBtn;
    private Button signOutBtn;
    private Button signInButton;
    private TextView accountStatusText;
    private TextView lastBackupText;
    private ProgressDialog progressDialog;
    private ExecutorService executor;
    
    private ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    driveManager.initializeDriveService(account);
                    updateUI();
                    Toast.makeText(this, "Signed in successfully!", Toast.LENGTH_SHORT).show();
                } catch (ApiException e) {
                    Toast.makeText(this, "Sign-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
    );
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);
        
        initializeViews();
        setupDriveManager();
        setupClickListeners();
        updateUI();
    }
    
    private void initializeViews() {
        backupNowBtn = findViewById(R.id.backup_now_btn);
        restoreBtn = findViewById(R.id.restore_btn);
        viewHistoryBtn = findViewById(R.id.view_history_btn);
        signOutBtn = findViewById(R.id.sign_out_btn);
        signInButton = findViewById(R.id.sign_in_button);
        accountStatusText = findViewById(R.id.account_status_text);
        lastBackupText = findViewById(R.id.last_backup_text);
        executor = Executors.newSingleThreadExecutor();
        
        // Set up back button
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
    }
    
    private void setupDriveManager() {
        driveManager = new GoogleDriveManager(this);
        driveManager.setProgressListener(this);
    }
    
    private void setupClickListeners() {
        backupNowBtn.setOnClickListener(v -> {
            if (!PremiumFeatureManager.getInstance(this).canUseBackupFeature()) {
                showPremiumUpgradeDialog();
                return;
            }
            
            if (driveManager.isSignedIn()) {
                startBackup();
            } else {
                signInToGoogle();
            }
        });
        
        restoreBtn.setOnClickListener(v -> {
            if (!PremiumFeatureManager.getInstance(this).canUseBackupFeature()) {
                showPremiumUpgradeDialog();
                return;
            }
            
            if (driveManager.isSignedIn()) {
                startRestoreFlow();
            } else {
                signInToGoogle();
            }
        });
        
        viewHistoryBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, BackupHistoryActivity.class);
            startActivity(intent);
        });
        
        signInButton.setOnClickListener(v -> {
            if (!driveManager.isSignedIn()) {
                signInToGoogle();
            }
        });
        
        signOutBtn.setOnClickListener(v -> {
            driveManager.signOut(() -> {
                updateUI();
                Toast.makeText(BackupActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
            });
        });
    }
    
    private void updateUI() {
        if (driveManager.isSignedIn()) {
            String email = driveManager.getSignedInUserEmail();
            accountStatusText.setText("Signed in as: " + email);
            backupNowBtn.setEnabled(true);
            restoreBtn.setEnabled(true);
            signOutBtn.setEnabled(true);
            signInButton.setVisibility(View.GONE);
            updateLastBackupStatus();
        } else {
            accountStatusText.setText("Not signed in to Google Drive");
            backupNowBtn.setEnabled(false);
            restoreBtn.setEnabled(false);
            signOutBtn.setEnabled(false);
            signInButton.setVisibility(View.VISIBLE);
            lastBackupText.setText("Sign in to view backup status");
        }
    }
    
    private void updateLastBackupStatus() {
        executor.execute(() -> {
            try {
                BackupHistory lastBackup = DatabaseHelper.getDb(this)
                        .backupHistoryDao()
                        .getLastSuccessfulBackup();
                
                runOnUiThread(() -> {
                    if (lastBackup != null) {
                        lastBackupText.setText("Last backup: " + lastBackup.getFormattedDate());
                    } else {
                        lastBackupText.setText("No backups found");
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> lastBackupText.setText("Error loading backup status"));
            }
        });
    }
    
    private void signInToGoogle() {
        Intent signInIntent = driveManager.getSignInIntent();
        signInLauncher.launch(signInIntent);
    }
    
    
    private void startBackup() {
        showProgressDialog("Creating backup...");
        
        executor.execute(() -> {
            try {
                // Get database file path
                File dbFile = getDatabasePath("journal_app_db");
                if (!dbFile.exists()) {
                    runOnUiThread(() -> {
                        hideProgressDialog();
                        Toast.makeText(this, "Database file not found", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }
                
                // Count journals for metadata
                int journalCount = DatabaseHelper.getDb(this).journalDao().getTotalJournalsCount();
                
                // Create metadata
                BackupMetadata metadata = new BackupMetadata(
                        getPackageManager().getPackageInfo(getPackageName(), 0).versionName,
                        16, // Database version
                        Build.MODEL,
                        Build.MANUFACTURER,
                        journalCount,
                        0, // Will be updated after encryption
                        true,
                        "manual",
                        Build.VERSION.SDK_INT
                );
                
                // Save backup record as in progress
                BackupHistory backupRecord = new BackupHistory();
                backupRecord.setBackupDate(System.currentTimeMillis());
                backupRecord.setJournalCount(journalCount);
                backupRecord.setBackupStatus("in_progress");
                backupRecord.setEncrypted(true);
                backupRecord.setAppVersion(metadata.getAppVersion());
                backupRecord.setDatabaseVersion(metadata.getDatabaseVersion());
                backupRecord.setDeviceInfo(Build.MODEL + " (" + Build.MANUFACTURER + ")");
                
                // Save backup record
                long recordId = DatabaseHelper.getDb(this).backupHistoryDao().insertBackupHistory(backupRecord);
                
                // Start backup process
                driveManager.createBackup(dbFile, metadata);
                
            } catch (Exception e) {
                runOnUiThread(() -> {
                    hideProgressDialog();
                    Toast.makeText(this, "Backup failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void startRestoreFlow() {
        showProgressDialog("Loading backups...");
        
        driveManager.listBackups(new GoogleDriveManager.BackupListCallback() {
            @Override
            public void onSuccess(List<GoogleDriveManager.BackupInfo> backups) {
                hideProgressDialog();
                if (backups.isEmpty()) {
                    Toast.makeText(BackupActivity.this, "No backups found", Toast.LENGTH_SHORT).show();
                } else {
                    // Start restore activity with backup list
                    Intent intent = new Intent(BackupActivity.this, RestoreActivity.class);
                    startActivity(intent);
                }
            }
            
            @Override
            public void onError(String error) {
                hideProgressDialog();
                Toast.makeText(BackupActivity.this, "Failed to load backups: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void showPremiumUpgradeDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.premium_upgrade_dialog);
        dialog.setCancelable(true);
        
        TextView messageText = dialog.findViewById(R.id.premium_message_text);
        Button upgradeBtn = dialog.findViewById(R.id.upgrade_btn);
        Button cancelBtn = dialog.findViewById(R.id.cancel_btn);
        
        messageText.setText("Backup & Restore is a Premium feature.\n\n" +
                "✓ Automatic daily backups\n" +
                "✓ Restore from any backup\n" +
                "✓ Encrypted & secure\n" +
                "✓ No ads, ever!\n\n" +
                "Upgrade now to protect your memories!");
        
        upgradeBtn.setOnClickListener(v -> {
            // Launch premium upgrade flow
            PremiumFeatureManager.getInstance(this).showPremiumUpgradeDialog(this);
            dialog.dismiss();
        });
        
        cancelBtn.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
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
    
    // GoogleDriveManager.BackupProgressListener implementation
    @Override
    public void onProgressUpdate(String message, int percentage) {
        runOnUiThread(() -> {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.setMessage(message + " (" + percentage + "%)");
            }
        });
    }
    
    @Override
    public void onSuccess(String message) {
        runOnUiThread(() -> {
            hideProgressDialog();
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            updateLastBackupStatus();
            updateBackupHistoryToCompleted();
        });
    }
    
    @Override
    public void onError(String error) {
        runOnUiThread(() -> {
            hideProgressDialog();
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            updateBackupHistoryToFailed(error);
        });
    }
    
    private void updateBackupHistoryToCompleted() {
        executor.execute(() -> {
            try {
                // Get the most recent in_progress backup and mark it as completed
                List<BackupHistory> inProgressBackups = DatabaseHelper.getDb(this)
                    .backupHistoryDao().getBackupsByStatus("in_progress");
                
                if (!inProgressBackups.isEmpty()) {
                    BackupHistory latestBackup = inProgressBackups.get(0);
                    latestBackup.setBackupStatus("success");
                    DatabaseHelper.getDb(this).backupHistoryDao().update(latestBackup);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    private void updateBackupHistoryToFailed(String errorMessage) {
        executor.execute(() -> {
            try {
                // Get the most recent in_progress backup and mark it as failed
                List<BackupHistory> inProgressBackups = DatabaseHelper.getDb(this)
                    .backupHistoryDao().getBackupsByStatus("in_progress");
                
                if (!inProgressBackups.isEmpty()) {
                    BackupHistory latestBackup = inProgressBackups.get(0);
                    latestBackup.setBackupStatus("failed");
                    latestBackup.setErrorMessage(errorMessage);
                    DatabaseHelper.getDb(this).backupHistoryDao().update(latestBackup);
                }
            } catch (Exception e) {
                e.printStackTrace();
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
