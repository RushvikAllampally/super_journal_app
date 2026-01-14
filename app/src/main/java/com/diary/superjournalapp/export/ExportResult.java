package com.diary.superjournalapp.export;

import android.net.Uri;

/**
 * Result of an export operation
 */
public class ExportResult {
    
    private boolean success;
    private String message;
    private Uri fileUri;
    private String filePath;
    private long fileSize;
    private String errorMessage;
    
    public ExportResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public static ExportResult success(Uri fileUri, String filePath, long fileSize) {
        ExportResult result = new ExportResult(true, "Export successful");
        result.fileUri = fileUri;
        result.filePath = filePath;
        result.fileSize = fileSize;
        return result;
    }
    
    public static ExportResult failure(String errorMessage) {
        ExportResult result = new ExportResult(false, "Export failed");
        result.errorMessage = errorMessage;
        return result;
    }
    
    // Getters
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public Uri getFileUri() {
        return fileUri;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
}
