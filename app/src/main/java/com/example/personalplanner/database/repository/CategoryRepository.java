package com.example.personalplanner.database.repository;

import android.content.Context;

import com.example.personalplanner.database.dao.CategoryDao;
import com.example.personalplanner.database.entity.Category;
import com.example.personalplanner.database.room.AppDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;

public class CategoryRepository {

    private final CategoryDao categoryDao;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    public CategoryRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        categoryDao = db.categoryDao();
    }

    public void insert(Category category) {
        executor.execute(() -> categoryDao.insert(category));
    }

    public void update(Category category) {
        executor.execute(() -> categoryDao.update(category));
    }

    public void delete(Category category) {
        executor.execute(() -> categoryDao.delete(category));
    }

    public void deleteAll() {
        executor.execute(categoryDao::deleteAll);
    }

    public LiveData<List<Category>> getAllCategories(){
        return categoryDao.getAllCategories();
    }

    public Category getCategoryById(int id) {
        return categoryDao.getCategoryById(id);
    }
}