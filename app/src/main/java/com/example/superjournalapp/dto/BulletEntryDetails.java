package com.example.superjournalapp.dto;

public class BulletEntryDetails {

    private String taskName;
    private boolean isTaskDone;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public boolean getIsTaskDone() {
        return isTaskDone;
    }

    public void setIsTaskDone(boolean isTaskDone) {
        this.isTaskDone = isTaskDone;
    }

    @Override
    public String toString() {
        return "BulletEntryDetails{" +
                "taskName='" + taskName + '\'' +
                ", isTaskDone=" + isTaskDone +
                '}';
    }
}
