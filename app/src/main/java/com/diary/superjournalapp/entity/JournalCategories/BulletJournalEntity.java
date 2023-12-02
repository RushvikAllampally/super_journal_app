package com.diary.superjournalapp.entity.JournalCategories;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.diary.superjournalapp.entity.Journal;

@Entity(tableName = "bullet_journal_content")
public class BulletJournalEntity extends Journal {

    @ColumnInfo(name = "task_list_json")
    public String taskListJson;

    @ColumnInfo(name = "is_list_pinned")
    public boolean isListPinned;

    @ColumnInfo(name = "is_dont_show")
    public boolean isDontShow;

    public boolean isDontShow() {
        return isDontShow;
    }

    public void setDontShow(boolean dontShow) {
        isDontShow = dontShow;
    }

    public boolean isListPinned() {
        return isListPinned;
    }

    public void setListPinned(boolean listPinned) {
        isListPinned = listPinned;
    }

    public String getTaskListJson() {
        return taskListJson;
    }

    public void setTaskListJson(String taskListJson) {
        this.taskListJson = taskListJson;
    }

}
