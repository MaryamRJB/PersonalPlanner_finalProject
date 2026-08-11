package com.example.personalplanner.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.personalplanner.database.entity.UserPreference;
import com.example.personalplanner.database.repository.UserPreferenceRepository;

import java.util.List;

public class UserPreferenceViewModel extends AndroidViewModel {

    private final UserPreferenceRepository repository;

    public UserPreferenceViewModel(@NonNull Application application) {
        super(application);
        repository = new UserPreferenceRepository(application);
    }

    public void insert(UserPreference preference){
        repository.insert(preference);
    }

    public void update(UserPreference preference){
        repository.update(preference);
    }

    public void delete(UserPreference preference){
        repository.delete(preference);
    }

    public void deleteAll(){
        repository.deleteAll();
    }

    public List<UserPreference> getAllPreferences(){
        return repository.getAllPreferences();
    }

    public UserPreference getPreferenceById(int id){
        return repository.getPreferenceById(id);
    }

    public UserPreference getPreferenceByUserId(int userId){
        return repository.getPreferenceByUserId(userId);
    }
}