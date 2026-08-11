package com.example.personalplanner.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.personalplanner.database.entity.User;

import java.util.List;

import androidx.room.Transaction;
import com.example.personalplanner.database.models.UserWithPreference;

@Dao
public interface UserDao {

    @Insert
    long insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    @Query("SELECT * FROM users WHERE id = :id")
    User getUserById(int id);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User getUserByEmail(String email);

    @Query("DELETE FROM users")
    void deleteAll();

    @Transaction
    @Query("SELECT * FROM users WHERE id=:id")
    UserWithPreference getUserWithPreference(int id);
}