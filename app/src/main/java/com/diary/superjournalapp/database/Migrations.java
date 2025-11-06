package com.diary.superjournalapp.database;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Database migrations for the journal app
 */
public class Migrations {

    /**
     * Migration from version 12 to 14
     * - Adds is_bookmarked column to journals table
     * - Adds Tag and JournalTag tables for the new tagging system
     * (Version 13 was internal only and never deployed to users)
     */
    public static final Migration MIGRATION_12_14 = new Migration(12, 14) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Add is_bookmarked column to journals table
            database.execSQL(
                "ALTER TABLE journals ADD COLUMN is_bookmarked INTEGER NOT NULL DEFAULT 0"
            );
            
            // Create tags table
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS `tags` (" +
                "`tagId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`name` TEXT NOT NULL, " +
                "`color` TEXT, " +
                "`usageCount` INTEGER NOT NULL, " +
                "`isFavorite` INTEGER NOT NULL)"
            );
            
            // Create unique index on tag name
            database.execSQL(
                "CREATE UNIQUE INDEX IF NOT EXISTS `index_tags_name` ON `tags` (`name`)"
            );
            
            // Create journal_tags junction table
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS `journal_tags` (" +
                "`journalId` INTEGER NOT NULL, " +
                "`tagId` INTEGER NOT NULL, " +
                "PRIMARY KEY(`journalId`, `tagId`), " +
                "FOREIGN KEY(`journalId`) REFERENCES `Journal`(`journalId`) ON DELETE CASCADE, " +
                "FOREIGN KEY(`tagId`) REFERENCES `tags`(`tagId`) ON DELETE CASCADE)"
            );
            
            // Create indices for foreign keys
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_journal_tags_journalId` ON `journal_tags` (`journalId`)"
            );
            
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_journal_tags_tagId` ON `journal_tags` (`tagId`)"
            );
        }
    };
}
