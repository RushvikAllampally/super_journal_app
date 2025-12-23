package com.diary.superjournalapp.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Entity for tracking backup history in the local database
 */
@Entity(tableName = "backup_history")
public class BackupHistory {
    
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    @ColumnInfo(name = "backup_date")
    private long backupDate;
    
    @ColumnInfo(name = "backup_type")
    private String backupType; // "manual" or "auto"
    
    @ColumnInfo(name = "drive_file_id")
    private String driveFileId;
    
    @ColumnInfo(name = "file_name")
    private String fileName;
    
    @ColumnInfo(name = "file_size")
    private long fileSize;
    
    @ColumnInfo(name = "journal_count")
    private int journalCount;
    
    @ColumnInfo(name = "backup_status")
    private String backupStatus; // "success", "failed", "in_progress"
    
    @ColumnInfo(name = "error_message")
    private String errorMessage; // null if successful
    
    @ColumnInfo(name = "is_encrypted")
    private boolean isEncrypted;
    
    @ColumnInfo(name = "app_version")
    private String appVersion;
    
    @ColumnInfo(name = "database_version")
    private int databaseVersion;
    
    @ColumnInfo(name = "device_info")
    private String deviceInfo;
    
    public BackupHistory() {}
    
    @Ignore
    public BackupHistory(long backupDate, String backupType, String driveFileId, 
                        String fileName, long fileSize, int journalCount, 
                        String backupStatus, String errorMessage, boolean isEncrypted,
                        String appVersion, int databaseVersion, String deviceInfo) {
        this.backupDate = backupDate;
        this.backupType = backupType;
        this.driveFileId = driveFileId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.journalCount = journalCount;
        this.backupStatus = backupStatus;
        this.errorMessage = errorMessage;
        this.isEncrypted = isEncrypted;
        this.appVersion = appVersion;
        this.databaseVersion = databaseVersion;
        this.deviceInfo = deviceInfo;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public long getBackupDate() { return backupDate; }
    public void setBackupDate(long backupDate) { this.backupDate = backupDate; }
    
    public String getBackupType() { return backupType; }
    public void setBackupType(String backupType) { this.backupType = backupType; }
    
    public String getDriveFileId() { return driveFileId; }
    public void setDriveFileId(String driveFileId) { this.driveFileId = driveFileId; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    
    public int getJournalCount() { return journalCount; }
    public void setJournalCount(int journalCount) { this.journalCount = journalCount; }
    
    public String getBackupStatus() { return backupStatus; }
    public void setBackupStatus(String backupStatus) { this.backupStatus = backupStatus; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public boolean isEncrypted() { return isEncrypted; }
    public void setEncrypted(boolean encrypted) { isEncrypted = encrypted; }
    
    public String getAppVersion() { return appVersion; }
    public void setAppVersion(String appVersion) { this.appVersion = appVersion; }
    
    public int getDatabaseVersion() { return databaseVersion; }
    public void setDatabaseVersion(int databaseVersion) { this.databaseVersion = databaseVersion; }
    
    public String getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }
    
    // Helper methods
    public String getFormattedDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date(backupDate));
    }
    
    public String getFormattedFileSize() {
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format(java.util.Locale.getDefault(), "%.1f KB", fileSize / 1024.0);
        } else {
            return String.format(java.util.Locale.getDefault(), "%.1f MB", fileSize / (1024.0 * 1024));
        }
    }
    
    public boolean isSuccessful() {
        return "success".equals(backupStatus);
    }
    
    public boolean isFailed() {
        return "failed".equals(backupStatus);
    }
    
    public boolean isInProgress() {
        return "in_progress".equals(backupStatus);
    }
}
