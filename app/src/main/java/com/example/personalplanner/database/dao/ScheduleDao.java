package com.example.personalplanner.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.personalplanner.database.entity.Schedule;

import java.util.List;

@Dao
public interface ScheduleDao {

    @Insert
    long insert(Schedule schedule);

    @Update
    void update(Schedule schedule);

    @Delete
    void delete(Schedule schedule);

    // =========================
    // BASIC QUERIES
    // =========================

    @Query("SELECT * FROM schedules ORDER BY date ASC, startTime ASC")
    List<Schedule> getAllSchedules();

    @Query("SELECT * FROM schedules WHERE id = :id")
    Schedule getScheduleById(int id);

    @Query("SELECT * FROM schedules WHERE taskId = :taskId ORDER BY date ASC, startTime ASC")
    List<Schedule> getSchedulesByTask(int taskId);

    // =========================
    // BY DATE
    // =========================

    @Query("SELECT * FROM schedules WHERE date = :date ORDER BY startTime ASC")
    List<Schedule> getSchedulesByDate(String date);

    @Query("SELECT * FROM schedules WHERE date = :date ORDER BY startTime ASC")
    LiveData<List<Schedule>> getSchedulesByDateLiveData(String date);

    // =========================
    // DELETE
    // =========================

    @Query("DELETE FROM schedules")
    void deleteAll();

    @Query("DELETE FROM schedules WHERE date = :date")
    void deleteSchedulesByDate(String date);

    @Query("DELETE FROM schedules WHERE taskId = :taskId")
    void deleteSchedulesByTask(int taskId);

    // =========================
    // STATISTICS / TESTING
    // =========================

    @Query("SELECT COUNT(*) FROM schedules")
    int countAllSchedules();

    @Query("SELECT COUNT(*) FROM schedules WHERE date = :date")
    int countSchedulesByDate(String date);
}