package com.diary.superjournalapp.backup;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Metadata class for backup files containing information about the backup
 */
public class BackupMetadata {
    
    @SerializedName("app_version")
    private String appVersion;
    
    @SerializedName("database_version")
    private int databaseVersion;
    
    @SerializedName("backup_timestamp")
    private long timestamp;
    
    @SerializedName("device_model")
    private String deviceModel;
    
    @SerializedName("device_manufacturer")
    private String deviceManufacturer;
    
    @SerializedName("journal_count")
    private int journalCount;
    
    @SerializedName("file_size")
    private long fileSize;
    
    @SerializedName("is_encrypted")
    private boolean isEncrypted;
    
    @SerializedName("backup_type")
    private String backupType; // "manual" or "auto"
    
    @SerializedName("android_version")
    private int androidVersion;
    
    public BackupMetadata() {
        this.timestamp = System.currentTimeMillis();
        this.isEncrypted = true;
        this.backupType = "manual";
    }
    
    // Constructor for creating metadata
    public BackupMetadata(String appVersion, int databaseVersion, String deviceModel, 
                         String deviceManufacturer, int journalCount, long fileSize, 
                         boolean isEncrypted, String backupType, int androidVersion) {
        this();
        this.appVersion = appVersion;
        this.databaseVersion = databaseVersion;
        this.deviceModel = deviceModel;
        this.deviceManufacturer = deviceManufacturer;
        this.journalCount = journalCount;
        this.fileSize = fileSize;
        this.isEncrypted = isEncrypted;
        this.backupType = backupType;
        this.androidVersion = androidVersion;
    }
    
    // Convert to JSON string
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    
    // Create from JSON string
    public static BackupMetadata fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, BackupMetadata.class);
    }
    
    // Getters and Setters
    public String getAppVersion() { return appVersion; }
    public void setAppVersion(String appVersion) { this.appVersion = appVersion; }
    
    public int getDatabaseVersion() { return databaseVersion; }
    public void setDatabaseVersion(int databaseVersion) { this.databaseVersion = databaseVersion; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public String getDeviceModel() { return deviceModel; }
    public void setDeviceModel(String deviceModel) { this.deviceModel = deviceModel; }
    
    public String getDeviceManufacturer() { return deviceManufacturer; }
    public void setDeviceManufacturer(String deviceManufacturer) { this.deviceManufacturer = deviceManufacturer; }
    
    public int getJournalCount() { return journalCount; }
    public void setJournalCount(int journalCount) { this.journalCount = journalCount; }
    
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    
    public boolean isEncrypted() { return isEncrypted; }
    public void setEncrypted(boolean encrypted) { this.isEncrypted = encrypted; }
    
    public String getBackupType() { return backupType; }
    public void setBackupType(String backupType) { this.backupType = backupType; }
    
    public int getAndroidVersion() { return androidVersion; }
    public void setAndroidVersion(int androidVersion) { this.androidVersion = androidVersion; }
    
    // Helper method to get formatted file size
    public String getFormattedFileSize() {
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024));
        }
    }
    
    // Helper method to get formatted timestamp
    public String getFormattedTimestamp() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date(timestamp));
    }
}
