package com.example.personalplanner.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.personalplanner.database.entity.Schedule;
import com.example.personalplanner.database.repository.ScheduleRepository;

import java.util.List;

public class ScheduleViewModel extends AndroidViewModel {

    private final ScheduleRepository repository;

    public ScheduleViewModel(
            @NonNull Application application) {

        super(application);

        repository =
                new ScheduleRepository(application);
    }

    // =========================
    // INSERT
    // =========================

    public void insert(Schedule schedule) {

        repository.insert(schedule);
    }

    // =========================
    // UPDATE / DELETE
    // =========================

    public void update(Schedule schedule) {

        repository.update(schedule);
    }

    public void delete(Schedule schedule) {

        repository.delete(schedule);
    }

    public void deleteAll() {

        repository.deleteAll();
    }

    public void deleteSchedulesByDate(String date) {

        repository.deleteSchedulesByDate(date);
    }

    public void deleteSchedulesByTask(int taskId) {

        repository.deleteSchedulesByTask(taskId);
    }

    // =========================
    // GET
    // =========================

    public List<Schedule> getAllSchedules() {

        return repository.getAllSchedules();
    }

    public List<Schedule> getSchedulesByDate(String date) {

        return repository.getSchedulesByDate(date);
    }

    public LiveData<List<Schedule>>
    getSchedulesByDateLiveData(String date) {

        return repository
                .getSchedulesByDateLiveData(date);
    }

    public List<Schedule> getSchedulesByTask(int taskId) {

        return repository.getSchedulesByTask(taskId);
    }

    public Schedule getScheduleById(int id) {

        return repository.getScheduleById(id);
    }

    // =========================
    // STATISTICS
    // =========================

    public int countAllSchedules() {

        return repository.countAllSchedules();
    }

    public int countSchedulesByDate(String date) {

        return repository.countSchedulesByDate(date);
    }
}