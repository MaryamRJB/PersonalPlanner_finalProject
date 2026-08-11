package com.example.personalplanner.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.personalplanner.database.entity.UserPreference;

import java.util.List;

@Dao
public interface UserPreferenceDao {

    @Insert
    long insert(UserPreference preference);

    @Update
    void update(UserPreference preference);

    @Delete
    void delete(UserPreference preference);

    @Query("SELECT * FROM user_preferences")
    List<UserPreference> getAllPreferences();

    @Query("SELECT * FROM user_preferences WHERE id = :id")
    UserPreference getPreferenceById(int id);

    @Query("SELECT * FROM user_preferences WHERE userId = :userId LIMIT 1")
    UserPreference getPreferenceByUserId(int userId);

    @Query("DELETE FROM user_preferences")
    void deleteAll();
}