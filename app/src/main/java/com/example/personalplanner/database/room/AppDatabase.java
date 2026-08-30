package com.example.personalplanner.database.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.personalplanner.database.dao.CategoryDao;
import com.example.personalplanner.database.dao.ScheduleDao;
import com.example.personalplanner.database.dao.SubTaskDao;
import com.example.personalplanner.database.dao.TaskDao;
import com.example.personalplanner.database.dao.UserDao;
import com.example.personalplanner.database.dao.UserPreferenceDao;
import com.example.personalplanner.database.entity.Category;
import com.example.personalplanner.database.entity.Schedule;
import com.example.personalplanner.database.entity.SubTask;
import com.example.personalplanner.database.entity.Task;
import com.example.personalplanner.database.entity.User;
import com.example.personalplanner.database.entity.UserPreference;

import androidx.sqlite.db.SupportSQLiteDatabase;

@TypeConverters(Converters.class)
@Database(
        entities = {
                User.class,
                Category.class,
                Task.class,
                Schedule.class,
                UserPreference.class,
                SubTask.class
        },
        version = 3,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();

    public abstract CategoryDao categoryDao();

    public abstract TaskDao taskDao();

    public abstract ScheduleDao scheduleDao();

    public abstract UserPreferenceDao userPreferenceDao();

    public abstract SubTaskDao subTaskDao();

    private static final Callback roomCallback = new Callback() {

        @Override
        public void onCreate(SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };

    public static AppDatabase getInstance(Context context) {

        if (INSTANCE == null) {

            synchronized (AppDatabase.class) {

                if (INSTANCE == null) {

                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "personal_planner_database"
                            )
                            .addCallback(roomCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }

        return INSTANCE;
    }
}