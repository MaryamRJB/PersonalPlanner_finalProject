package com.example.personalplanner.utils;

import android.content.Context;

import com.example.personalplanner.database.entity.Category;
import com.example.personalplanner.database.repository.CategoryRepository;

public class DatabaseInitializer {

    public static void initialize(Context context){

        CategoryRepository repository = new CategoryRepository(context);

        repository.insert(new Category(
                "Study",
                "#2196F3",
                "ic_study"
        ));

        repository.insert(new Category(
                "Work",
                "#4CAF50",
                "ic_work"
        ));

        repository.insert(new Category(
                "Exercise",
                "#FF9800",
                "ic_exercise"
        ));

        repository.insert(new Category(
                "Personal",
                "#9C27B0",
                "ic_person"
        ));

        repository.insert(new Category(
                "Shopping",
                "#F44336",
                "ic_shopping"
        ));
    }
}