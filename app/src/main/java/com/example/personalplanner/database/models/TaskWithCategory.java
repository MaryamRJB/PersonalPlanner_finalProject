package com.example.personalplanner.database.models;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.personalplanner.database.entity.Category;
import com.example.personalplanner.database.entity.Task;

public class TaskWithCategory {

    @Embedded
    public Task task;

    @Relation(
            parentColumn = "categoryId",
            entityColumn = "id"
    )
    public Category category;
}