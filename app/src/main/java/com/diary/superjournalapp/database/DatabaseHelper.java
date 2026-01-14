package com.diary.superjournalapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.diary.superjournalapp.dao.BackupHistoryDao;
import com.diary.superjournalapp.dao.BulletJournalContentDao;
import com.diary.superjournalapp.dao.DreamJournalContentDao;
import com.diary.superjournalapp.dao.GratitudeJournalContentDao;
import com.diary.superjournalapp.dao.JournalDao;
import com.diary.superjournalapp.dao.MoodTrackerDao;
import com.diary.superjournalapp.dao.ReflectiveJournalContentDao;
import com.diary.superjournalapp.dao.TagDao;
import com.diary.superjournalapp.entity.BackupHistory;
import com.diary.superjournalapp.entity.Journal;
import com.diary.superjournalapp.entity.JournalCategories.BulletJournalEntity;
import com.diary.superjournalapp.entity.JournalCategories.DreamJournalEntity;
import com.diary.superjournalapp.entity.JournalCategories.GratitudeJournalEntity;
import com.diary.superjournalapp.entity.JournalCategories.ReflectiveJournalEntity;
import com.diary.superjournalapp.entity.JournalTag;
import com.diary.superjournalapp.entity.MoodTracker;
import com.diary.superjournalapp.entity.Tag;


@Database(entities = {Journal.class, GratitudeJournalEntity.class, BulletJournalEntity.class, ReflectiveJournalEntity.class, DreamJournalEntity.class, MoodTracker.class, Tag.class, JournalTag.class, BackupHistory.class}, exportSchema = false, version = 16)
@TypeConverters({Converters.class})
public abstract class DatabaseHelper extends RoomDatabase {

    private static final String DATABASE_NAME = "journal_app_db";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getDb(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, DatabaseHelper.class, DATABASE_NAME)
                    .addMigrations(Migrations.MIGRATION_12_14, Migrations.MIGRATION_14_15, Migrations.MIGRATION_15_16)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract JournalDao journalDao();

    public abstract GratitudeJournalContentDao gratitudeJournalContentDao();

    public abstract ReflectiveJournalContentDao reflectiveJournalContentDao();

    public abstract BulletJournalContentDao bulletJournalContentDao();

    public abstract DreamJournalContentDao dreamJournalContentDao();
    public abstract MoodTrackerDao moodTrackerDao();
    
    public abstract TagDao tagDao();
    
    public abstract BackupHistoryDao backupHistoryDao();
}
