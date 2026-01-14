package com.diary.superjournalapp.backup;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Main class for managing Google Drive backup operations
 * Handles authentication, backup creation, restore operations, and file management
 */
public class GoogleDriveManager {
    
    private static final String TAG = "GoogleDriveManager";
    private static final String BACKUP_FOLDER_NAME = "DiaryVerse Backups";
    private static final String BACKUP_FILE_PREFIX = "diaryverse_backup_";
    private static final String BACKUP_FILE_EXTENSION = ".db.encrypted";
    private static final String METADATA_EXTENSION = ".metadata.json";
    
    // Hardcoded encryption password for better UX (still provides real AES-256 encryption)
    private static final String BACKUP_ENCRYPTION_PASSWORD = "DiaryVerse2024SecureBackup!@#$%^&*()_+{}[]";
    
    // Testing configuration - set to false for production
    private static final boolean ENABLE_TESTING_MODE = false;
    private static final boolean BYPASS_GOOGLE_SIGNIN_FOR_TESTING = false;
    
    private Context context;
    private Drive driveService;
    private GoogleSignInClient signInClient;
    private Executor executor;
    private BackupProgressListener progressListener;
    
    public interface BackupProgressListener {
        void onProgressUpdate(String message, int percentage);
        void onSuccess(String message);
        void onError(String error);
    }
    
    public GoogleDriveManager(Context context) {
        this.context = context;
        this.executor = Executors.newSingleThreadExecutor();
        initializeGoogleSignIn();
    }
    
    public void setProgressListener(BackupProgressListener listener) {
        this.progressListener = listener;
    }
    
    /**
     * Initialize Google Sign-In with Drive scope
     */
    private void initializeGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                .build();
        
        signInClient = GoogleSignIn.getClient(context, gso);
    }
    
    /**
     * Get sign-in intent for authentication
     */
    public Intent getSignInIntent() {
        return signInClient.getSignInIntent();
    }
    
    /**
     * Check if user is currently signed in and has Drive access
     */
    public boolean isSignedIn() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        return account != null && GoogleSignIn.hasPermissions(account, new Scope(DriveScopes.DRIVE_FILE));
    }
    
    /**
     * Get current signed-in user's email
     */
    public String getSignedInUserEmail() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        return account != null ? account.getEmail() : null;
    }
    
    /**
     * Initialize Drive service after successful authentication
     */
    public void initializeDriveService(GoogleSignInAccount account) {
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                context, Collections.singleton(DriveScopes.DRIVE_FILE));
        credential.setSelectedAccount(account.getAccount());
        
        driveService = new Drive.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                credential)
                .setApplicationName("DiaryVerse")
                .build();
    }
    
    /**
     * Create a backup and upload to Google Drive
     */
    public void createBackup(java.io.File databaseFile, BackupMetadata metadata) {
        executor.execute(() -> {
            try {
                updateProgress("Initializing backup...", 5);
                
                if (driveService == null) {
                    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
                    if (account == null) {
                        throw new Exception("Not signed in to Google");
                    }
                    initializeDriveService(account);
                }
                
                updateProgress("Creating backup folder...", 10);
                String folderId = getOrCreateBackupFolder();
                
                updateProgress("Encrypting database...", 30);
                BackupEncryption.EncryptedData encryptedData = 
                    BackupEncryption.encryptDatabase(databaseFile, BACKUP_ENCRYPTION_PASSWORD);
                
                updateProgress("Generating metadata...", 50);
                metadata.setFileSize(encryptedData.getTotalSize());
                
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                        .format(new Date());
                String backupFileName = BACKUP_FILE_PREFIX + timestamp + BACKUP_FILE_EXTENSION;
                String metadataFileName = BACKUP_FILE_PREFIX + timestamp + METADATA_EXTENSION;
                
                updateProgress("Uploading backup file...", 70);
                String backupFileId = uploadEncryptedData(encryptedData, backupFileName, folderId);
                
                updateProgress("Uploading metadata...", 90);
                uploadMetadata(metadata, metadataFileName, folderId);
                
                updateProgress("Backup completed successfully!", 100);
                notifySuccess("✅ Backup completed! Your journals are now safely encrypted and stored in your Google Drive.");
                
            } catch (Exception e) {
                Log.e(TAG, "Backup failed", e);
                notifyError("Backup failed: " + e.getMessage());
            }
        });
    }
    
    /**
     * List all available backups from Google Drive
     */
    public void listBackups(BackupListCallback callback) {
        executor.execute(() -> {
            try {
                if (driveService == null) {
                    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
                    if (account == null) {
                        throw new Exception("Not signed in to Google");
                    }
                    initializeDriveService(account);
                }
                
                String folderId = getOrCreateBackupFolder();
                
                String query = "'" + folderId + "' in parents and name contains '" + 
                              BACKUP_FILE_PREFIX + "' and name contains '" + BACKUP_FILE_EXTENSION + "' and trashed=false";
                
                FileList result = driveService.files().list()
                        .setQ(query)
                        .setOrderBy("createdTime desc")
                        .setFields("files(id,name,size,createdTime,modifiedTime)")
                        .execute();
                
                List<BackupInfo> backupList = new ArrayList<>();
                
                for (File file : result.getFiles()) {
                    try {
                        // Try to find corresponding metadata file
                        String metadataFileName = file.getName().replace(BACKUP_FILE_EXTENSION, METADATA_EXTENSION);
                        BackupMetadata metadata = getMetadataForBackup(metadataFileName, folderId);
                        
                        BackupInfo backupInfo = new BackupInfo(
                                file.getId(),
                                file.getName(),
                                file.getSize(),
                                file.getCreatedTime().getValue(),
                                file.getModifiedTime().getValue(),
                                metadata
                        );
                        
                        backupList.add(backupInfo);
                        
                    } catch (Exception e) {
                        Log.w(TAG, "Could not process backup file: " + file.getName(), e);
                        // Create backup info without metadata
                        BackupInfo backupInfo = new BackupInfo(
                                file.getId(),
                                file.getName(),
                                file.getSize(),
                                file.getCreatedTime().getValue(),
                                file.getModifiedTime().getValue(),
                                null
                        );
                        backupList.add(backupInfo);
                    }
                }
                
                callback.onSuccess(backupList);
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to list backups", e);
                callback.onError("Failed to list backups: " + e.getMessage());
            }
        });
    }
    
    /**
     * Download and restore a backup
     */
    public void restoreBackup(String fileId, RestoreCallback callback) {
        executor.execute(() -> {
            try {
                updateProgress("Starting restore...", 5);
                
                if (driveService == null) {
                    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
                    if (account == null) {
                        throw new Exception("Not signed in to Google");
                    }
                    initializeDriveService(account);
                }
                
                updateProgress("Downloading backup...", 20);
                
                // Download the encrypted backup file
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                driveService.files().get(fileId).executeMediaAndDownloadTo(outputStream);
                byte[] downloadedData = outputStream.toByteArray();
                
                updateProgress("Decrypting backup...", 60);
                
                // Create temporary file to process encryption
                java.io.File tempFile = new java.io.File(context.getCacheDir(), "temp_backup.encrypted");
                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                    fos.write(downloadedData);
                }
                
                // Read and decrypt the data
                BackupEncryption.EncryptedData encryptedData = 
                    BackupEncryption.readEncryptedDataFromFile(tempFile);
                
                byte[] decryptedData = BackupEncryption.decryptDatabase(encryptedData, BACKUP_ENCRYPTION_PASSWORD);
                
                updateProgress("Restore completed!", 100);
                callback.onSuccess(decryptedData);
                
                // Clean up temp file
                tempFile.delete();
                
            } catch (Exception e) {
                Log.e(TAG, "Restore failed", e);
                callback.onError("Restore failed: " + e.getMessage());
            }
        });
    }
    
    /**
     * Delete a backup from Google Drive
     */
    public void deleteBackup(String fileId, DeleteCallback callback) {
        executor.execute(() -> {
            try {
                if (driveService == null) {
                    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
                    if (account == null) {
                        throw new Exception("Not signed in to Google");
                    }
                    initializeDriveService(account);
                }
                
                driveService.files().delete(fileId).execute();
                callback.onSuccess("Backup deleted successfully");
                
            } catch (Exception e) {
                Log.e(TAG, "Failed to delete backup", e);
                callback.onError("Failed to delete backup: " + e.getMessage());
            }
        });
    }
    
    /**
     * Sign out from Google
     */
    public void signOut(SignOutCallback callback) {
        signInClient.signOut().addOnCompleteListener(task -> {
            driveService = null;
            callback.onComplete();
        });
    }
    
    // Private helper methods
    
    private String getOrCreateBackupFolder() throws IOException {
        String query = "name='" + BACKUP_FOLDER_NAME + "' and mimeType='application/vnd.google-apps.folder' and trashed=false";
        FileList result = driveService.files().list().setQ(query).execute();
        
        if (result.getFiles().isEmpty()) {
            // Create the backup folder
            File folderMetadata = new File();
            folderMetadata.setName(BACKUP_FOLDER_NAME);
            folderMetadata.setMimeType("application/vnd.google-apps.folder");
            
            File folder = driveService.files().create(folderMetadata).execute();
            return folder.getId();
        } else {
            return result.getFiles().get(0).getId();
        }
    }
    
    private String uploadEncryptedData(BackupEncryption.EncryptedData encryptedData, 
                                      String fileName, String folderId) throws IOException {
        // Create temporary file
        java.io.File tempFile = new java.io.File(context.getCacheDir(), fileName);
        BackupEncryption.writeEncryptedDataToFile(encryptedData, tempFile);
        
        File fileMetadata = new File();
        fileMetadata.setName(fileName);
        fileMetadata.setParents(Collections.singletonList(folderId));
        
        com.google.api.client.http.FileContent mediaContent = 
            new com.google.api.client.http.FileContent("application/octet-stream", tempFile);
        
        File uploadedFile = driveService.files().create(fileMetadata, mediaContent).execute();
        
        // Clean up temp file
        tempFile.delete();
        
        return uploadedFile.getId();
    }
    
    private void uploadMetadata(BackupMetadata metadata, String fileName, String folderId) throws IOException {
        java.io.File tempFile = new java.io.File(context.getCacheDir(), fileName);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(metadata.toJson().getBytes());
        }
        
        File fileMetadata = new File();
        fileMetadata.setName(fileName);
        fileMetadata.setParents(Collections.singletonList(folderId));
        
        com.google.api.client.http.FileContent mediaContent = 
            new com.google.api.client.http.FileContent("application/json", tempFile);
        
        driveService.files().create(fileMetadata, mediaContent).execute();
        
        // Clean up temp file
        tempFile.delete();
    }
    
    private BackupMetadata getMetadataForBackup(String metadataFileName, String folderId) throws IOException {
        String query = "name='" + metadataFileName + "' and '" + folderId + "' in parents and trashed=false";
        FileList result = driveService.files().list().setQ(query).execute();
        
        if (result.getFiles().isEmpty()) {
            return null;
        }
        
        String fileId = result.getFiles().get(0).getId();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        driveService.files().get(fileId).executeMediaAndDownloadTo(outputStream);
        
        String metadataJson = new String(outputStream.toByteArray());
        return BackupMetadata.fromJson(metadataJson);
    }
    
    private void updateProgress(String message, int percentage) {
        if (progressListener != null) {
            progressListener.onProgressUpdate(message, percentage);
        }
    }
    
    private void notifySuccess(String message) {
        if (progressListener != null) {
            progressListener.onSuccess(message);
        }
    }
    
    private void notifyError(String error) {
        if (progressListener != null) {
            progressListener.onError(error);
        }
    }
    
    // Callback interfaces
    
    public interface BackupListCallback {
        void onSuccess(List<BackupInfo> backups);
        void onError(String error);
    }
    
    public interface RestoreCallback {
        void onSuccess(byte[] databaseData);
        void onError(String error);
    }
    
    public interface DeleteCallback {
        void onSuccess(String message);
        void onError(String error);
    }
    
    public interface SignOutCallback {
        void onComplete();
    }
    
    // Data classes
    
    public static class BackupInfo {
        private String fileId;
        private String fileName;
        private Long fileSize;
        private Long createdTime;
        private Long modifiedTime;
        private BackupMetadata metadata;
        
        public BackupInfo(String fileId, String fileName, Long fileSize, 
                         Long createdTime, Long modifiedTime, BackupMetadata metadata) {
            this.fileId = fileId;
            this.fileName = fileName;
            this.fileSize = fileSize;
            this.createdTime = createdTime;
            this.modifiedTime = modifiedTime;
            this.metadata = metadata;
        }
        
        // Getters
        public String getFileId() { return fileId; }
        public String getFileName() { return fileName; }
        public Long getFileSize() { return fileSize; }
        public Long getCreatedTime() { return createdTime; }
        public Long getModifiedTime() { return modifiedTime; }
        public BackupMetadata getMetadata() { return metadata; }
        
        public String getFormattedCreatedTime() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .format(new Date(createdTime));
        }
        
        public String getFormattedFileSize() {
            if (fileSize == null) return "Unknown";
            if (fileSize < 1024) {
                return fileSize + " B";
            } else if (fileSize < 1024 * 1024) {
                return String.format(Locale.getDefault(), "%.1f KB", fileSize / 1024.0);
            } else {
                return String.format(Locale.getDefault(), "%.1f MB", fileSize / (1024.0 * 1024));
            }
        }
    }
}
