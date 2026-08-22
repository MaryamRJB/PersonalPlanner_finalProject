package com.example.personalplanner.database.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.personalplanner.database.dao.SubTaskDao;
import com.example.personalplanner.database.dao.TaskDao;
import com.example.personalplanner.database.entity.SubTask;
import com.example.personalplanner.database.entity.Task;
import com.example.personalplanner.database.room.AppDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskRepository {

    private final TaskDao taskDao;
    private final SubTaskDao subTaskDao;

    private final ExecutorService executorService =
            Executors.newSingleThreadExecutor();

    public TaskRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);

        taskDao = db.taskDao();
        subTaskDao = db.subTaskDao();
    }

    // =========================
    // TASK
    // =========================

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

    public LiveData<List<Task>> getAllTasks() {
        return taskDao.getAllTasks();
    }

    public LiveData<List<Task>> getPendingTasks() {
        return taskDao.getPendingTasks();
    }

    public LiveData<List<Task>> getCompletedTasks() {
        return taskDao.getCompletedTasks();
    }

    public LiveData<List<Task>> searchTasks(String keyword) {
        return taskDao.searchTasks(keyword);
    }

    public List<Task> getTasksByCategory(int categoryId) {
        return taskDao.getTasksByCategory(categoryId);
    }

    public Task getTaskById(int id) {
        return taskDao.getTaskById(id);
    }

    // =========================
    // TASKS BY DATE
    // =========================

    public LiveData<List<Task>> getTasksByDate(String date) {
        return taskDao.getTasksByDate(date);
    }

    // =========================
    // TASK STATUS
    // =========================

    public void updateTaskStatus(int taskId, boolean completed) {
        executorService.execute(() ->
                taskDao.updateTaskStatus(taskId, completed));
    }

    // =========================
    // TASK PROGRESS
    // =========================

    public void updateTaskProgress(int taskId) {
        executorService.execute(() -> {

            int totalSubTasks =
                    subTaskDao.getSubTaskCount(taskId);

            int completedSubTasks =
                    subTaskDao.getCompletedSubTaskCount(taskId);

            int progress = 0;

            if (totalSubTasks > 0) {
                progress = (completedSubTasks * 100) / totalSubTasks;
            }

            taskDao.updateProgress(taskId, progress);
        });
    }

    // =========================
    // SUBTASK
    // =========================

    public void insertSubTask(SubTask subTask) {

        executorService.execute(() -> {

            subTaskDao.insert(subTask);

            updateProgressInternal(subTask.getTaskId());
        });
    }

    public void updateSubTask(SubTask subTask) {

        executorService.execute(() -> {

            subTaskDao.update(subTask);

            updateProgressInternal(subTask.getTaskId());
        });
    }

    public void deleteSubTask(SubTask subTask) {

        executorService.execute(() -> {

            int taskId = subTask.getTaskId();

            subTaskDao.delete(subTask);

            updateProgressInternal(taskId);
        });
    }

    public LiveData<List<SubTask>> getSubTasksByTaskId(int taskId) {
        return subTaskDao.getSubTasksByTaskId(taskId);
    }

    public SubTask getSubTaskById(int id) {
        return subTaskDao.getSubTaskById(id);
    }

    public void updateSubTaskStatus(
            int subTaskId,
            int taskId,
            boolean completed) {

        executorService.execute(() -> {

            subTaskDao.updateSubTaskStatus(
                    subTaskId,
                    completed
            );

            updateProgressInternal(taskId);
        });
    }

    public void deleteSubTasksByTaskId(int taskId) {

        executorService.execute(() -> {

            subTaskDao.deleteSubTasksByTaskId(taskId);

            taskDao.updateProgress(taskId, 0);
        });
    }

    // =========================
    // INTERNAL PROGRESS
    // =========================

    private void updateProgressInternal(int taskId) {

        int totalSubTasks =
                subTaskDao.getSubTaskCount(taskId);

        int completedSubTasks =
                subTaskDao.getCompletedSubTaskCount(taskId);

        int progress = 0;

        if (totalSubTasks > 0) {
            progress =
                    (completedSubTasks * 100) / totalSubTasks;
        }

        taskDao.updateProgress(taskId, progress);
    }
}