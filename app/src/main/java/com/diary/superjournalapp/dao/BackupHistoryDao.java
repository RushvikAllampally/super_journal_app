package com.diary.superjournalapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.diary.superjournalapp.entity.BackupHistory;

import java.util.List;

/**
 * DAO for BackupHistory entity operations
 */
@Dao
public interface BackupHistoryDao {
    
    /**
     * Insert a new backup history record
     */
    @Insert
    long insertBackupHistory(BackupHistory backupHistory);
    
    /**
     * Update an existing backup history record
     */
    @Update
    void updateBackupHistory(BackupHistory backupHistory);
    
    /**
     * Delete a backup history record
     */
    @Delete
    void deleteBackupHistory(BackupHistory backupHistory);
    
    /**
     * Get all backup history records ordered by date (newest first)
     */
    @Query("SELECT * FROM backup_history ORDER BY backup_date DESC")
    List<BackupHistory> getAllBackupHistory();
    
    /**
     * Get backup history by ID
     */
    @Query("SELECT * FROM backup_history WHERE id = :id")
    BackupHistory getBackupHistoryById(int id);
    
    /**
     * Get backup history by Drive file ID
     */
    @Query("SELECT * FROM backup_history WHERE drive_file_id = :fileId")
    BackupHistory getBackupHistoryByFileId(String fileId);
    
    /**
     * Get the most recent successful backup
     */
    @Query("SELECT * FROM backup_history WHERE backup_status = 'success' ORDER BY backup_date DESC LIMIT 1")
    BackupHistory getLastSuccessfulBackup();
    
    /**
     * Get all successful backups
     */
    @Query("SELECT * FROM backup_history WHERE backup_status = 'success' ORDER BY backup_date DESC")
    List<BackupHistory> getSuccessfulBackups();
    
    /**
     * Get all failed backups
     */
    @Query("SELECT * FROM backup_history WHERE backup_status = 'failed' ORDER BY backup_date DESC")
    List<BackupHistory> getFailedBackups();
    
    /**
     * Get backups by type (manual/auto)
     */
    @Query("SELECT * FROM backup_history WHERE backup_type = :type ORDER BY backup_date DESC")
    List<BackupHistory> getBackupsByType(String type);
    
    /**
     * Get total number of successful backups
     */
    @Query("SELECT COUNT(*) FROM backup_history WHERE backup_status = 'success'")
    int getSuccessfulBackupCount();
    
    /**
     * Get total number of failed backups
     */
    @Query("SELECT COUNT(*) FROM backup_history WHERE backup_status = 'failed'")
    int getFailedBackupCount();
    
    /**
     * Get backup history within date range
     */
    @Query("SELECT * FROM backup_history WHERE backup_date BETWEEN :startDate AND :endDate ORDER BY backup_date DESC")
    List<BackupHistory> getBackupHistoryInDateRange(long startDate, long endDate);
    
    /**
     * Get backup history for the last N days
     */
    @Query("SELECT * FROM backup_history WHERE backup_date > :sinceDate ORDER BY backup_date DESC")
    List<BackupHistory> getBackupHistorySince(long sinceDate);
    
    /**
     * Delete backup history older than specified date
     */
    @Query("DELETE FROM backup_history WHERE backup_date < :beforeDate")
    void deleteBackupHistoryBefore(long beforeDate);
    
    /**
     * Delete all failed backup records
     */
    @Query("DELETE FROM backup_history WHERE backup_status = 'failed'")
    void deleteFailedBackups();
    
    /**
     * Get backup statistics
     */
    @Query("SELECT backup_status, COUNT(*) as count FROM backup_history GROUP BY backup_status")
    List<BackupStatistics> getBackupStatistics();
    
    /**
     * Update backup status by file ID
     */
    @Query("UPDATE backup_history SET backup_status = :status, error_message = :errorMessage WHERE drive_file_id = :fileId")
    void updateBackupStatus(String fileId, String status, String errorMessage);
    
    /**
     * Get the most recent backup (regardless of status)
     */
    @Query("SELECT * FROM backup_history ORDER BY backup_date DESC LIMIT 1")
    BackupHistory getMostRecentBackup();
    
    /**
     * Check if a backup with specific file ID exists
     */
    @Query("SELECT COUNT(*) FROM backup_history WHERE drive_file_id = :fileId")
    int backupExistsByFileId(String fileId);
    
    /**
     * Get the size of all successful backups combined
     */
    @Query("SELECT SUM(file_size) FROM backup_history WHERE backup_status = 'success'")
    Long getTotalBackupSize();
    
    /**
     * Delete backup history by file ID
     */
    @Query("DELETE FROM backup_history WHERE drive_file_id = :fileId")
    void deleteBackupHistoryByFileId(String fileId);
    
    /**
     * Get backups by status
     */
    @Query("SELECT * FROM backup_history WHERE backup_status = :status ORDER BY backup_date DESC")
    List<BackupHistory> getBackupsByStatus(String status);
    
    /**
     * Update backup record
     */
    @Update
    void update(BackupHistory backupHistory);
    
    /**
     * Data class for backup statistics
     */
    class BackupStatistics {
        public String backup_status;
        public int count;
        
        public BackupStatistics(String backup_status, int count) {
            this.backup_status = backup_status;
            this.count = count;
        }
        
        public String getBackupStatus() { return backup_status; }
        public int getCount() { return count; }
    }
}
