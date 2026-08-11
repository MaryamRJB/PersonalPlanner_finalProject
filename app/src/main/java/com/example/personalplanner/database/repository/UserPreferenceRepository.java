package com.example.personalplanner.database.repository;

import android.content.Context;

import com.example.personalplanner.database.dao.UserPreferenceDao;
import com.example.personalplanner.database.entity.UserPreference;
import com.example.personalplanner.database.room.AppDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserPreferenceRepository {

    private final UserPreferenceDao preferenceDao;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    public UserPreferenceRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        preferenceDao = db.userPreferenceDao();
    }

    public void insert(UserPreference preference) {
        executor.execute(() -> preferenceDao.insert(preference));
    }

    public void update(UserPreference preference) {
        executor.execute(() -> preferenceDao.update(preference));
    }

    public void delete(UserPreference preference) {
        executor.execute(() -> preferenceDao.delete(preference));
    }

    public void deleteAll() {
        executor.execute(preferenceDao::deleteAll);
    }

    public List<UserPreference> getAllPreferences() {
        return preferenceDao.getAllPreferences();
    }

    public UserPreference getPreferenceById(int id) {
        return preferenceDao.getPreferenceById(id);
    }

    public UserPreference getPreferenceByUserId(int userId) {
        return preferenceDao.getPreferenceByUserId(userId);
    }
}