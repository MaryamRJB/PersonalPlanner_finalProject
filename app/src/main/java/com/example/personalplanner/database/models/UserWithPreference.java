package com.example.personalplanner.database.models;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.personalplanner.database.entity.User;
import com.example.personalplanner.database.entity.UserPreference;

public class UserWithPreference {

    @Embedded
    public User user;

    @Relation(
            parentColumn = "id",
            entityColumn = "userId"
    )
    public UserPreference preference;
}