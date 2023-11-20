package com.example.superjournalapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.superjournalapp.dao.BulletJournalContentDao;
import com.example.superjournalapp.dao.DreamJournalContentDao;
import com.example.superjournalapp.dao.GratitudeJournalContentDao;
import com.example.superjournalapp.dao.JournalDao;
import com.example.superjournalapp.dao.MoodTrackerDao;
import com.example.superjournalapp.dao.ReflectiveJournalContentDao;
import com.example.superjournalapp.entity.Journal;
import com.example.superjournalapp.entity.JournalCategories.BulletJournalEntity;
import com.example.superjournalapp.entity.JournalCategories.DreamJournalEntity;
import com.example.superjournalapp.entity.JournalCategories.GratitudeJournalEntity;
import com.example.superjournalapp.entity.JournalCategories.ReflectiveJournalEntity;
import com.example.superjournalapp.entity.MoodTracker;


@Database(entities = {Journal.class, GratitudeJournalEntity.class, BulletJournalEntity.class, ReflectiveJournalEntity.class, DreamJournalEntity.class, MoodTracker.class}, exportSchema = false, version = 12)
@TypeConverters({Converters.class})
public abstract class DatabaseHelper extends RoomDatabase {

    private static final String DATABASE_NAME = "journal_app_db";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getDb(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, DatabaseHelper.class, DATABASE_NAME).fallbackToDestructiveMigration().allowMainThreadQueries().build();
        }
        return instance;
    }

    public abstract JournalDao journalDao();

    public abstract GratitudeJournalContentDao gratitudeJournalContentDao();

    public abstract ReflectiveJournalContentDao reflectiveJournalContentDao();

    public abstract BulletJournalContentDao bulletJournalContentDao();

    public abstract DreamJournalContentDao dreamJournalContentDao();

    public abstract MoodTrackerDao moodTrackerDao();


}

