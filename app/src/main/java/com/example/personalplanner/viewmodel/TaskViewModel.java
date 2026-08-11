package com.example.personalplanner.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.personalplanner.database.entity.Task;
import com.example.personalplanner.database.repository.TaskRepository;

import java.util.List;

import androidx.lifecycle.LiveData;

public class TaskViewModel extends AndroidViewModel {

    private final TaskRepository repository;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);
    }

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

    public LiveData<List<Task>> getAllTasks(){
        return repository.getAllTasks();
    }

    public LiveData<List<Task>> getPendingTasks(){
        return repository.getPendingTasks();
    }

    public LiveData<List<Task>> getCompletedTasks(){
        return repository.getCompletedTasks();
    }

    public LiveData<List<Task>> searchTasks(String keyword){
        return repository.searchTasks(keyword);
    }

    public List<Task> getTasksByCategory(int categoryId) {
        return repository.getTasksByCategory(categoryId);
    }

    public Task getTaskById(int id) {
        return repository.getTaskById(id);
    }

    public void updateTaskStatus(int taskId, boolean completed) {
        repository.updateTaskStatus(taskId, completed);
    }
}