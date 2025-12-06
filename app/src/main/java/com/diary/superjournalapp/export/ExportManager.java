package com.diary.superjournalapp.export;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.diary.superjournalapp.database.DatabaseHelper;
import com.diary.superjournalapp.entity.Journal;
import com.diary.superjournalapp.entity.JournalCategories.BulletJournalEntity;
import com.diary.superjournalapp.entity.JournalCategories.DreamJournalEntity;
import com.diary.superjournalapp.entity.JournalCategories.GratitudeJournalEntity;
import com.diary.superjournalapp.entity.JournalCategories.ReflectiveJournalEntity;
import com.diary.superjournalapp.entity.Tag;
import com.diary.superjournalapp.utils.PremiumFeatureManager;
import com.diary.superjournalapp.utils.TagManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Main class for managing journal export operations
 */
public class ExportManager {
    
    private static final String TAG = "ExportManager";
    private static final String EXPORT_FOLDER = "DiaryVerse Exports";
    
    private Context context;
    private DatabaseHelper databaseHelper;
    private TagManager tagManager;
    private PremiumFeatureManager premiumFeatureManager;
    
    public ExportManager(Context context) {
        this.context = context.getApplicationContext();
        this.databaseHelper = DatabaseHelper.getDb(context);
        this.tagManager = new TagManager(context);
        this.premiumFeatureManager = PremiumFeatureManager.getInstance(context);
    }
    
    /**
     * Export a single journal
     * 
     * @param journal The journal to export
     * @param options Export options
     * @param listener Progress listener (optional)
     * @return Export result
     */
    public ExportResult exportJournal(Journal journal, ExportOptions options, 
                                     ExportProgressListener listener) {
        try {
            // Validate premium access
            if (!premiumFeatureManager.canExportJournals()) {
                return ExportResult.failure("Premium subscription required for export");
            }
            
            // Update progress
            if (listener != null) {
                listener.onProgress(10, "Loading journal content...");
            }
            
            // Load full journal content based on category
            Object journalContent = loadJournalContent(journal);
            if (journalContent == null) {
                return ExportResult.failure("Failed to load journal content");
            }
            
            // Load tags if requested
            List<Tag> tags = null;
            if (options.isIncludeTags()) {
                if (listener != null) {
                    listener.onProgress(30, "Loading tags...");
                }
                tags = tagManager.getTagsForJournal(journal);
            }
            
            // Generate export data
            byte[] exportData;
            String fileName;
            
            if (listener != null) {
                listener.onProgress(50, "Generating export...");
            }
            
            if (options.getFormat() == ExportOptions.ExportFormat.PDF) {
                PdfExporter pdfExporter = new PdfExporter(context);
                exportData = pdfExporter.exportToPdf(journal, journalContent, tags, options);
                fileName = generateFileName(journal, "pdf", options);
            } else {
                TxtExporter txtExporter = new TxtExporter(context);
                exportData = txtExporter.exportToTxt(journal, journalContent, tags, options);
                fileName = generateFileName(journal, "txt", options);
            }
            
            if (exportData == null || exportData.length == 0) {
                return ExportResult.failure("Failed to generate export file");
            }
            
            // Save to Downloads
            if (listener != null) {
                listener.onProgress(80, "Saving file...");
            }
            
            ExportResult result = saveToDownloads(exportData, fileName, 
                                                 options.getFormat() == ExportOptions.ExportFormat.PDF ? 
                                                 "application/pdf" : "text/plain");
            
            if (result.isSuccess() && listener != null) {
                listener.onProgress(100, "Export complete!");
                listener.onSuccess(result);
            }
            
            return result;
            
        } catch (Exception e) {
            Log.e(TAG, "Export failed", e);
            String error = "Export failed: " + e.getMessage();
            if (listener != null) {
                listener.onError(error);
            }
            return ExportResult.failure(error);
        }
    }
    
    /**
     * Export multiple journals
     * 
     * @param journals List of journals to export
     * @param options Export options
     * @param listener Progress listener (optional)
     * @return Export result
     */
    public ExportResult exportJournals(List<Journal> journals, ExportOptions options,
                                      ExportProgressListener listener) {
        try {
            // Validate premium access
            if (!premiumFeatureManager.canExportJournals()) {
                return ExportResult.failure("Premium subscription required for export");
            }
            
            if (journals == null || journals.isEmpty()) {
                return ExportResult.failure("No journals to export");
            }
            
            int total = journals.size();
            int successful = 0;
            StringBuilder errors = new StringBuilder();
            
            for (int i = 0; i < journals.size(); i++) {
                Journal journal = journals.get(i);
                
                if (listener != null) {
                    int progress = (int) ((i / (float) total) * 100);
                    listener.onProgress(progress, 
                        String.format("Exporting journal %d of %d...", i + 1, total));
                }
                
                ExportResult result = exportJournal(journal, options, null);
                
                if (result.isSuccess()) {
                    successful++;
                } else {
                    errors.append(journal.getTitle())
                          .append(": ")
                          .append(result.getErrorMessage())
                          .append("\n");
                }
            }
            
            if (listener != null) {
                listener.onProgress(100, "Batch export complete!");
            }
            
            if (successful == total) {
                String message = String.format("Successfully exported %d journals", successful);
                if (listener != null) {
                    listener.onSuccess(ExportResult.success(null, null, 0));
                }
                return new ExportResult(true, message);
            } else if (successful > 0) {
                String message = String.format("Exported %d of %d journals. Errors:\n%s", 
                                              successful, total, errors.toString());
                return new ExportResult(false, message);
            } else {
                return ExportResult.failure("All exports failed:\n" + errors.toString());
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Batch export failed", e);
            String error = "Batch export failed: " + e.getMessage();
            if (listener != null) {
                listener.onError(error);
            }
            return ExportResult.failure(error);
        }
    }
    
    /**
     * Load full journal content based on journal category
     * 
     * @param journal The journal
     * @return Content object (ReflectiveJournalEntity, GratitudeJournalEntity, etc.)
     */
    private Object loadJournalContent(Journal journal) {
        long journalId = journal.getJournalId();
        String category = journal.getJournalCategory();
        
        try {
            switch (category) {
                case "My Diary":
                    return databaseHelper.reflectiveJournalContentDao()
                                        .getReflectiveJournalById(journalId);
                    
                case "Gratitude Journal":
                    return databaseHelper.gratitudeJournalContentDao()
                                        .getGratitudeJournalById(journalId);
                    
                case "Dream Journal":
                    return databaseHelper.dreamJournalContentDao()
                                        .getDreamJournalById(journalId);
                    
                case "Bullet Journal":
                    return databaseHelper.bulletJournalContentDao()
                                        .getBulletJournalById(journalId);
                    
                default:
                    Log.e(TAG, "Unknown journal category: " + category);
                    return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to load journal content", e);
            return null;
        }
    }
    
    /**
     * Generate filename for export
     * 
     * @param journal The journal
     * @param extension File extension (pdf or txt)
     * @param options Export options (may contain custom filename)
     * @return Generated filename
     */
    private String generateFileName(Journal journal, String extension, ExportOptions options) {
        // Use custom filename if provided
        if (options.getCustomFileName() != null && !options.getCustomFileName().isEmpty()) {
            String custom = options.getCustomFileName();
            // Sanitize filename
            custom = custom.replaceAll("[^a-zA-Z0-9._-]", "_");
            return custom + "." + extension;
        }
        
        // Generate default filename: DiaryVerse_Title_Date.ext
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = dateFormat.format(journal.getJournalCreatedOn());
        
        // Sanitize title for filename
        String title = journal.getTitle();
        if (title == null || title.isEmpty()) {
            title = "Journal";
        }
        title = title.replaceAll("[^a-zA-Z0-9._-]", "_");
        
        // Limit title length
        if (title.length() > 30) {
            title = title.substring(0, 30);
        }
        
        return String.format("DiaryVerse_%s_%s.%s", title, date, extension);
    }
    
    /**
     * Save export data to Downloads folder
     * 
     * @param data File data bytes
     * @param fileName Filename
     * @param mimeType MIME type
     * @return Export result with file URI
     */
    private ExportResult saveToDownloads(byte[] data, String fileName, String mimeType) {
        try {
            Uri fileUri;
            String filePath;
            
            // Use MediaStore for Android 10+ (Scoped Storage)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver resolver = context.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, 
                                 Environment.DIRECTORY_DOWNLOADS + "/" + EXPORT_FOLDER);
                
                fileUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                
                if (fileUri == null) {
                    return ExportResult.failure("Failed to create file in Downloads");
                }
                
                try (OutputStream outputStream = resolver.openOutputStream(fileUri)) {
                    if (outputStream == null) {
                        return ExportResult.failure("Failed to open output stream");
                    }
                    outputStream.write(data);
                    outputStream.flush();
                }
                
                filePath = Environment.DIRECTORY_DOWNLOADS + "/" + EXPORT_FOLDER + "/" + fileName;
                
            } else {
                // Legacy storage for Android 9 and below
                File downloadsDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
                File exportDir = new File(downloadsDir, EXPORT_FOLDER);
                
                if (!exportDir.exists()) {
                    exportDir.mkdirs();
                }
                
                File file = new File(exportDir, fileName);
                
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(data);
                    fos.flush();
                }
                
                fileUri = Uri.fromFile(file);
                filePath = file.getAbsolutePath();
            }
            
            return ExportResult.success(fileUri, filePath, data.length);
            
        } catch (IOException e) {
            Log.e(TAG, "Failed to save file", e);
            return ExportResult.failure("Failed to save file: " + e.getMessage());
        }
    }
}
