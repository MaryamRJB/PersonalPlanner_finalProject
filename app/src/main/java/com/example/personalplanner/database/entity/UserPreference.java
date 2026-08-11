package com.example.personalplanner.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "user_preferences",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("userId")}
)
public class UserPreference {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int userId;

    private int studyPriority;
    private int workPriority;
    private int exercisePriority;
    private int personalPriority;

    public UserPreference(int userId,
                          int studyPriority,
                          int workPriority,
                          int exercisePriority,
                          int personalPriority) {

        this.userId = userId;
        this.studyPriority = studyPriority;
        this.workPriority = workPriority;
        this.exercisePriority = exercisePriority;
        this.personalPriority = personalPriority;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getStudyPriority() {
        return studyPriority;
    }

    public void setStudyPriority(int studyPriority) {
        this.studyPriority = studyPriority;
    }

    public int getWorkPriority() {
        return workPriority;
    }

    public void setWorkPriority(int workPriority) {
        this.workPriority = workPriority;
    }

    public int getExercisePriority() {
        return exercisePriority;
    }

    public void setExercisePriority(int exercisePriority) {
        this.exercisePriority = exercisePriority;
    }

    public int getPersonalPriority() {
        return personalPriority;
    }

    public void setPersonalPriority(int personalPriority) {
        this.personalPriority = personalPriority;
    }
}