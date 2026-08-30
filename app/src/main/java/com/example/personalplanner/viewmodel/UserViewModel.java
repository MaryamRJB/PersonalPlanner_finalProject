package com.example.personalplanner.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.personalplanner.database.entity.User;
import com.example.personalplanner.database.repository.UserRepository;

import java.util.List;

public class UserViewModel extends AndroidViewModel {

    private final UserRepository repository;

    public UserViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
    }

    public void insert(User user) {
        repository.insert(user);
    }

    public void update(User user) {
        repository.update(user);
    }

    public void delete(User user) {
        repository.delete(user);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public List<User> getAllUsers() {
        return repository.getAllUsers();
    }

    public User getUserById(int id) {
        return repository.getUserById(id);
    }

    public User getUserByPhone(String phone) {
        return repository.getUserByPhone(phone);
    }
}