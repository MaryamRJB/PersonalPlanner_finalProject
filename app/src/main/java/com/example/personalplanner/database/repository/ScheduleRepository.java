package com.example.personalplanner.database.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.personalplanner.database.dao.ScheduleDao;
import com.example.personalplanner.database.entity.Schedule;
import com.example.personalplanner.database.room.AppDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScheduleRepository {

    private final ScheduleDao scheduleDao;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    public ScheduleRepository(Context context) {

        AppDatabase db =
                AppDatabase.getInstance(context);

        scheduleDao =
                db.scheduleDao();
    }

    // =========================
    // INSERT
    // =========================

    public void insert(Schedule schedule) {

        executor.execute(() ->
                scheduleDao.insert(schedule)
        );
    }

    /*
     * فقط برای استفاده از Thread پس‌زمینه.
     * SchedulerTestActivity از این نوع استفاده می‌کند.
     */
    public void insertAndWait(Schedule schedule) {

        scheduleDao.insert(schedule);
    }

    // =========================
    // UPDATE / DELETE
    // =========================

    public void update(Schedule schedule) {

        executor.execute(() ->
                scheduleDao.update(schedule)
        );
    }

    public void delete(Schedule schedule) {

        executor.execute(() ->
                scheduleDao.delete(schedule)
        );
    }

    public void deleteAll() {

        executor.execute(
                scheduleDao::deleteAll
        );
    }

    public void deleteSchedulesByDate(String date) {

        executor.execute(() ->
                scheduleDao.deleteSchedulesByDate(date)
        );
    }

    public void deleteSchedulesByTask(int taskId) {

        executor.execute(() ->
                scheduleDao.deleteSchedulesByTask(taskId)
        );
    }

    // =========================
    // GET
    // =========================

    public List<Schedule> getAllSchedules() {

        return scheduleDao.getAllSchedules();
    }

    public List<Schedule> getSchedulesByDate(String date) {

        return scheduleDao.getSchedulesByDate(date);
    }

    public LiveData<List<Schedule>>
    getSchedulesByDateLiveData(String date) {

        return scheduleDao
                .getSchedulesByDateLiveData(date);
    }

    public List<Schedule> getSchedulesByTask(int taskId) {

        return scheduleDao.getSchedulesByTask(taskId);
    }

    public Schedule getScheduleById(int id) {

        return scheduleDao.getScheduleById(id);
    }

    // =========================
    // STATISTICS
    // =========================

    public int countAllSchedules() {

        return scheduleDao.countAllSchedules();
    }

    public int countSchedulesByDate(String date) {

        return scheduleDao.countSchedulesByDate(date);
    }
}