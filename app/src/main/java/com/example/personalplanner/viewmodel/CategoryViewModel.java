package com.example.personalplanner.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.personalplanner.database.entity.Category;
import com.example.personalplanner.database.repository.CategoryRepository;

import java.util.List;

import androidx.lifecycle.LiveData;

public class CategoryViewModel extends AndroidViewModel {

    private final CategoryRepository repository;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        repository = new CategoryRepository(application);
    }

    public void insert(Category category) {
        repository.insert(category);
    }

    public void update(Category category) {
        repository.update(category);
    }

    public void delete(Category category) {
        repository.delete(category);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public LiveData<List<Category>> getAllCategories(){
        return repository.getAllCategories();
    }

    public Category getCategoryById(int id) {
        return repository.getCategoryById(id);
    }
}