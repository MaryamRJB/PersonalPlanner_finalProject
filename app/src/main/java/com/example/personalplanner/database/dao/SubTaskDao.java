package com.example.personalplanner.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.personalplanner.database.entity.SubTask;

import java.util.List;

@Dao
public interface SubTaskDao {

    @Insert
    long insert(SubTask subTask);

    @Update
    void update(SubTask subTask);

    @Delete
    void delete(SubTask subTask);

    // Get one SubTask
    @Query("SELECT * FROM sub_tasks WHERE id = :id")
    SubTask getSubTaskById(int id);

    // Get all SubTasks of a Task
    @Query("SELECT * FROM sub_tasks WHERE taskId = :taskId ORDER BY startTime ASC")
    LiveData<List<SubTask>> getSubTasksByTaskId(int taskId);

    // Synchronous version for calculations
    @Query("SELECT * FROM sub_tasks WHERE taskId = :taskId ORDER BY startTime ASC")
    List<SubTask> getSubTasksByTaskIdSync(int taskId);

    // Get completed SubTasks
    @Query("SELECT * FROM sub_tasks WHERE taskId = :taskId AND completed = 1")
    List<SubTask> getCompletedSubTasks(int taskId);

    // Count all SubTasks
    @Query("SELECT COUNT(*) FROM sub_tasks WHERE taskId = :taskId")
    int getSubTaskCount(int taskId);

    // Count completed SubTasks
    @Query("SELECT COUNT(*) FROM sub_tasks WHERE taskId = :taskId AND completed = 1")
    int getCompletedSubTaskCount(int taskId);

    // Update completion status
    @Query("UPDATE sub_tasks SET completed = :completed WHERE id = :subTaskId")
    void updateSubTaskStatus(int subTaskId, boolean completed);

    // Delete all SubTasks of a Task
    @Query("DELETE FROM sub_tasks WHERE taskId = :taskId")
    void deleteSubTasksByTaskId(int taskId);

    // Delete all SubTasks
    @Query("DELETE FROM sub_tasks")
    void deleteAll();

    // Testing
    @Query("SELECT * FROM sub_tasks")
    List<SubTask> testGetAllSubTasks();

    @Query("SELECT * FROM sub_tasks WHERE taskId = :taskId")
    List<SubTask> testGetSubTasksByTaskId(int taskId);
}