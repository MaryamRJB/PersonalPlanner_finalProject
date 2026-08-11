package com.example.personalplanner.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "tasks",
        foreignKeys = @ForeignKey(
                entity = Category.class,
                parentColumns = "id",
                childColumns = "categoryId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("categoryId")}
)
public class Task {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String description;

    private int priority;

    private long deadline;

    private int duration;

    private boolean completed;

    private int categoryId;

    private long createdAt;

    private long updatedAt;

    private long reminderTime;

    private boolean repeated;

    private String repeatType;

    public Task(String title,
                String description,
                int priority,
                long deadline,
                int duration,
                boolean completed,
                int categoryId,
                long createdAt,
                long updatedAt,
                long reminderTime,
                boolean repeated,
                String repeatType) {

        this.title = title;
        this.description = description;
        this.priority = priority;
        this.deadline = deadline;
        this.duration = duration;
        this.completed = completed;
        this.categoryId = categoryId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.reminderTime = reminderTime;
        this.repeated = repeated;
        this.repeatType = repeatType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(long reminderTime) {
        this.reminderTime = reminderTime;
    }

    public boolean isRepeated() {
        return repeated;
    }

    public void setRepeated(boolean repeated) {
        this.repeated = repeated;
    }

    public String getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
    }
}