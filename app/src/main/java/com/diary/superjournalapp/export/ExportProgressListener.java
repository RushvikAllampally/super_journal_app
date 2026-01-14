package com.diary.superjournalapp.export;

/**
 * Callback interface for export progress updates
 */
public interface ExportProgressListener {
    
    /**
     * Called when export progress changes
     * 
     * @param progress Current progress (0-100)
     * @param message Status message
     */
    void onProgress(int progress, String message);
    
    /**
     * Called when export completes successfully
     * 
     * @param result Export result with file details
     */
    void onSuccess(ExportResult result);
    
    /**
     * Called when export fails
     * 
     * @param error Error message
     */
    void onError(String error);
}
