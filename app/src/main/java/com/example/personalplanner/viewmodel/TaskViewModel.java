package com.example.personalplanner.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.personalplanner.database.entity.SubTask;
import com.example.personalplanner.database.entity.Task;
import com.example.personalplanner.database.repository.TaskRepository;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private final TaskRepository repository;

    public TaskViewModel(@NonNull Application application) {
        super(application);

        repository = new TaskRepository(application);
    }

    // =========================
    // TASK
    // =========================

    public void insert(Task task) {
        repository.insert(task);
    }

    public void update(Task task) {
        repository.update(task);
    }

    public void delete(Task task) {
        repository.delete(task);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public LiveData<List<Task>> getAllTasks() {
        return repository.getAllTasks();
    }

    public LiveData<List<Task>> getPendingTasks() {
        return repository.getPendingTasks();
    }

    public LiveData<List<Task>> getCompletedTasks() {
        return repository.getCompletedTasks();
    }

    public LiveData<List<Task>> searchTasks(String keyword) {
        return repository.searchTasks(keyword);
    }

    public List<Task> getTasksByCategory(int categoryId) {
        return repository.getTasksByCategory(categoryId);
    }

    public Task getTaskById(int id) {
        return repository.getTaskById(id);
    }

    // =========================
    // TASKS BY DATE
    // =========================

    public LiveData<List<Task>> getTasksByDate(String date) {
        return repository.getTasksByDate(date);
    }

    // =========================
    // TASK STATUS
    // =========================

    public void updateTaskStatus(
            int taskId,
            boolean completed) {

        repository.updateTaskStatus(
                taskId,
                completed
        );
    }

    // =========================
    // TASK PROGRESS
    // =========================

    public void updateTaskProgress(int taskId) {
        repository.updateTaskProgress(taskId);
    }

    // =========================
    // SUBTASK
    // =========================

    public void insertSubTask(SubTask subTask) {
        repository.insertSubTask(subTask);
    }

    public void updateSubTask(SubTask subTask) {
        repository.updateSubTask(subTask);
    }

    public void deleteSubTask(SubTask subTask) {
        repository.deleteSubTask(subTask);
    }

    public LiveData<List<SubTask>> getSubTasksByTaskId(int taskId) {
        return repository.getSubTasksByTaskId(taskId);
    }

    public SubTask getSubTaskById(int id) {
        return repository.getSubTaskById(id);
    }

    public void updateSubTaskStatus(
            int subTaskId,
            int taskId,
            boolean completed) {

        repository.updateSubTaskStatus(
                subTaskId,
                taskId,
                completed
        );
    }

    public void deleteSubTasksByTaskId(int taskId) {
        repository.deleteSubTasksByTaskId(taskId);
    }
}