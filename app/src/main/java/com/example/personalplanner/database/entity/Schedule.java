package com.example.personalplanner.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "schedules",
        foreignKeys = @ForeignKey(
                entity = Task.class,
                parentColumns = "id",
                childColumns = "taskId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("taskId")}
)
public class Schedule {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int taskId;
    private String date;
    private String startTime;
    private String endTime;

    public Schedule(int taskId,
                    String date,
                    String startTime,
                    String endTime) {
        this.taskId = taskId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}