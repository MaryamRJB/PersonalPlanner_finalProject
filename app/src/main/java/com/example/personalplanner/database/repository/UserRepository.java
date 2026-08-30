package com.example.personalplanner.database.repository;

import android.content.Context;

import com.example.personalplanner.database.dao.UserDao;
import com.example.personalplanner.database.entity.User;
import com.example.personalplanner.database.room.AppDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {

    private final UserDao userDao;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    public UserRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        userDao = db.userDao();
    }

    public void insert(User user) {
        executor.execute(() -> userDao.insert(user));
    }

    public void update(User user) {
        executor.execute(() -> userDao.update(user));
    }

    public void delete(User user) {
        executor.execute(() -> userDao.delete(user));
    }

    public void deleteAll() {
        executor.execute(userDao::deleteAll);
    }

    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    public User getUserById(int id) {
        return userDao.getUserById(id);
    }

    public User getUserByPhone(String phone) {
        return userDao.getUserByPhone(phone);
    }
}