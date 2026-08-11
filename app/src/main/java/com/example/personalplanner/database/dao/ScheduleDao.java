package com.example.personalplanner.database.dao;

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

    @Query("SELECT * FROM schedules ORDER BY date,startTime")
    List<Schedule> getAllSchedules();

    @Query("SELECT * FROM schedules WHERE id = :id")
    Schedule getScheduleById(int id);

    @Query("SELECT * FROM schedules WHERE taskId = :taskId")
    List<Schedule> getSchedulesByTask(int taskId);

    @Query("SELECT * FROM schedules WHERE date = :date")
    List<Schedule> getSchedulesByDate(String date);

    @Query("DELETE FROM schedules")
    void deleteAll();
}