package com.example.personalplanner.database.repository;

import android.content.Context;

import com.example.personalplanner.database.dao.ScheduleDao;
import com.example.personalplanner.database.entity.Schedule;
import com.example.personalplanner.database.room.AppDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import androidx.lifecycle.LiveData;

public class ScheduleRepository {

    private final ScheduleDao scheduleDao;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    public ScheduleRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        scheduleDao = db.scheduleDao();
    }

    public void insert(Schedule schedule) {
        executor.execute(() -> scheduleDao.insert(schedule));
    }

    // Insert synchronously - must be called from a background thread
    public void insertAndWait(Schedule schedule) {
        scheduleDao.insert(schedule);
    }

    public void update(Schedule schedule) {
        executor.execute(() -> scheduleDao.update(schedule));
    }

    public void delete(Schedule schedule) {
        executor.execute(() -> scheduleDao.delete(schedule));
    }

    public void deleteAll() {
        executor.execute(scheduleDao::deleteAll);
    }

    public List<Schedule> getAllSchedules() {
        return scheduleDao.getAllSchedules();
    }

    public List<Schedule> getSchedulesByDate(String date) {
        return scheduleDao.getSchedulesByDate(date);
    }

    public List<Schedule> getSchedulesByTask(int taskId) {
        return scheduleDao.getSchedulesByTask(taskId);
    }

    public Schedule getScheduleById(int id) {
        return scheduleDao.getScheduleById(id);
    }

    public LiveData<List<Schedule>> getSchedulesByDateLiveData(String date) {
        return scheduleDao.getSchedulesByDateLiveData(date);
    }
}