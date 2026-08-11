package com.example.personalplanner.database.repository;

import android.content.Context;

import com.example.personalplanner.database.dao.TaskDao;
import com.example.personalplanner.database.entity.Task;
import com.example.personalplanner.database.room.AppDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;

public class TaskRepository {

    private final TaskDao taskDao;

    private final ExecutorService executorService =
            Executors.newSingleThreadExecutor();

    public TaskRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        taskDao = db.taskDao();
    }

    public void insert(Task task) {
        executorService.execute(() -> taskDao.insert(task));
    }

    public void update(Task task) {
        executorService.execute(() -> taskDao.update(task));
    }

    public void delete(Task task) {
        executorService.execute(() -> taskDao.delete(task));
    }

    public void deleteAll() {
        executorService.execute(taskDao::deleteAll);
    }

    public LiveData<List<Task>> getAllTasks(){
        return taskDao.getAllTasks();
    }

    public LiveData<List<Task>> getPendingTasks(){
        return taskDao.getPendingTasks();
    }

    public LiveData<List<Task>> getCompletedTasks(){
        return taskDao.getCompletedTasks();
    }

    public LiveData<List<Task>> searchTasks(String keyword){
        return taskDao.searchTasks(keyword);
    }

    public List<Task> getTasksByCategory(int categoryId) {
        return taskDao.getTasksByCategory(categoryId);
    }

    public Task getTaskById(int id) {
        return taskDao.getTaskById(id);
    }

    public void updateTaskStatus(int taskId, boolean completed) {
        executorService.execute(() ->
                taskDao.updateTaskStatus(taskId, completed));
    }
}