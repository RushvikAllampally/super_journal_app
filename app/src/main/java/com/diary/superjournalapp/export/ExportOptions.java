package com.diary.superjournalapp.export;

/**
 * Configuration options for journal export
 * NOTE: Image support not included as app doesn't support images in text editor
 */
public class ExportOptions {
    
    private ExportFormat format;
    private boolean includeTags;
    private boolean includeMetadata;
    private String customFileName;
    
    public ExportOptions() {
        // Default values
        this.format = ExportFormat.PDF;
        this.includeTags = true;
        this.includeMetadata = true;
        this.customFileName = null;
    }
    
    public enum ExportFormat {
        PDF,
        TXT
    }
    
    // Getters and Setters
    
    public ExportFormat getFormat() {
        return format;
    }
    
    public void setFormat(ExportFormat format) {
        this.format = format;
    }
    
    public boolean isIncludeTags() {
        return includeTags;
    }
    
    public void setIncludeTags(boolean includeTags) {
        this.includeTags = includeTags;
    }
    
    public boolean isIncludeMetadata() {
        return includeMetadata;
    }
    
    public void setIncludeMetadata(boolean includeMetadata) {
        this.includeMetadata = includeMetadata;
    }
    
    public String getCustomFileName() {
        return customFileName;
    }
    
    public void setCustomFileName(String customFileName) {
        this.customFileName = customFileName;
    }
}
