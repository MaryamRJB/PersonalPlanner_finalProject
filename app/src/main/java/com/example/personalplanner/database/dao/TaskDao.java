package com.example.personalplanner.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.personalplanner.database.entity.Task;

import java.util.List;

import androidx.room.Transaction;
import com.example.personalplanner.database.models.TaskWithCategory;

import androidx.lifecycle.LiveData;

@Dao
public interface TaskDao {

    @Insert
    long insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM tasks ORDER BY deadline ASC")
    LiveData<List<Task>> getAllTasks();

    @Query("SELECT * FROM tasks WHERE id = :id")
    Task getTaskById(int id);

    @Query("SELECT * FROM tasks WHERE completed = 0")
    LiveData<List<Task>> getPendingTasks();

    @Query("SELECT * FROM tasks WHERE completed = 1")
    LiveData<List<Task>> getCompletedTasks();

    @Query("SELECT * FROM tasks WHERE categoryId = :categoryId")
    List<Task> getTasksByCategory(int categoryId);

    @Query("UPDATE tasks SET completed = :completed WHERE id = :taskId")
    void updateTaskStatus(int taskId, boolean completed);

    @Query("DELETE FROM tasks")
    void deleteAll();

    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :keyword || '%'")
    LiveData<List<Task>> searchTasks(String keyword);

    @Query("SELECT * FROM tasks ORDER BY priority DESC")
    LiveData<List<Task>> getTasksByPriority();

    @Query("SELECT * FROM tasks WHERE deadline BETWEEN :start AND :end")
    List<Task> getTasksBetweenDates(long start, long end);

    @Query("SELECT COUNT(*) FROM tasks")
    int countAllTasks();

    @Query("SELECT COUNT(*) FROM tasks WHERE completed = 1")
    int countCompletedTasks();

    @Query("SELECT COUNT(*) FROM tasks WHERE completed = 0")
    int countPendingTasks();

    @Query("DELETE FROM tasks WHERE completed = 1")
    void deleteCompletedTasks();

    @Query("SELECT * FROM tasks WHERE reminderTime > 0")
    List<Task> getReminderTasks();

    @Query("SELECT * FROM tasks WHERE repeated = 1")
    List<Task> getRepeatedTasks();

    @Query("SELECT * FROM tasks WHERE deadline < :currentTime AND completed = 0")
    List<Task> getOverdueTasks(long currentTime);

    @Transaction
    @Query("SELECT * FROM tasks")
    List<TaskWithCategory> getTasksWithCategory();

    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :keyword || '%'")
    List<Task> testSearchTasks(String keyword);

    @Query("SELECT * FROM tasks WHERE priority = :priority")
    List<Task> testFilterByPriority(int priority);

    @Query("SELECT * FROM tasks WHERE id = :id")
    Task testGetTaskById(int id);

    @Query("SELECT * FROM tasks")
    List<Task> testGetAllTasks();

}