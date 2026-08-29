package com.example.personalplanner.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.personalplanner.database.dao.CategoryDao;
import com.example.personalplanner.database.dao.ScheduleDao;
import com.example.personalplanner.database.dao.TaskDao;
import com.example.personalplanner.database.entity.Category;
import com.example.personalplanner.database.entity.Schedule;
import com.example.personalplanner.database.entity.Task;
import com.example.personalplanner.database.room.AppDatabase;
import com.example.personalplanner.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.List;

public class SchedulerTestActivity extends AppCompatActivity {

    private static final String TAG = "SCHEDULER_TEST";

    private Scheduler scheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "SchedulerTestActivity CREATED");

        new Thread(() -> {

            try {

                AppDatabase db =
                        AppDatabase.getInstance(
                                getApplicationContext()
                        );

                TaskDao taskDao = db.taskDao();
                CategoryDao categoryDao = db.categoryDao();
                ScheduleDao scheduleDao = db.scheduleDao();

                // =====================================================
                // 1. پاک کردن داده‌های تست قبلی
                // =====================================================

                scheduleDao.deleteAll();
                taskDao.deleteAll();
                categoryDao.deleteAll();

                Log.d(TAG, "OLD TEST DATA CLEARED");

                // =====================================================
                // 2. ایجاد Category تستی
                // =====================================================

                Category category = new Category(
                        "Test Category",
                        "#2196F3",
                        "ic_study"
                );

                long categoryId =
                        categoryDao.insert(category);

                Log.d(
                        TAG,
                        "CATEGORY INSERTED: id="
                                + categoryId
                );

                // =====================================================
                // 3. ایجاد Taskهای تستی
                // =====================================================

                long now =
                        System.currentTimeMillis();

                Task task1 = new Task(
                        "Study Machine Learning",
                        "AI Study",
                        5,
                        now + 24 * 60 * 60 * 1000,
                        120,
                        false,
                        (int) categoryId,
                        now,
                        now,
                        0,
                        false,
                        "NONE"
                );

                Task task2 = new Task(
                        "Exercise",
                        "Workout",
                        3,
                        now + 48 * 60 * 60 * 1000,
                        60,
                        false,
                        (int) categoryId,
                        now,
                        now,
                        0,
                        false,
                        "NONE"
                );

                Task task3 = new Task(
                        "Read Book",
                        "Reading",
                        2,
                        now + 72 * 60 * 60 * 1000,
                        60,
                        false,
                        (int) categoryId,
                        now,
                        now,
                        0,
                        false,
                        "NONE"
                );

                // =====================================================
                // 4. ذخیره Taskها در Room
                // =====================================================

                long id1 =
                        taskDao.insert(task1);

                long id2 =
                        taskDao.insert(task2);

                long id3 =
                        taskDao.insert(task3);

                task1.setId((int) id1);
                task2.setId((int) id2);
                task3.setId((int) id3);

                Log.d(
                        TAG,
                        "TASK INSERTED: id1=" + id1
                );

                Log.d(
                        TAG,
                        "TASK INSERTED: id2=" + id2
                );

                Log.d(
                        TAG,
                        "TASK INSERTED: id3=" + id3
                );

                // =====================================================
                // 5. ساخت لیست Taskها
                // =====================================================

                List<Task> tasks =
                        new ArrayList<>();

                tasks.add(task1);
                tasks.add(task2);
                tasks.add(task3);

                // =====================================================
                // 6. اجرای Scheduler
                // =====================================================

                scheduler =
                        new Scheduler(
                                getApplicationContext()
                        );

                String date =
                        "2026-08-28";

                List<Schedule> schedules =
                        scheduler.generateSchedule(
                                tasks,
                                date
                        );

                // =====================================================
                // 7. نمایش نتیجه Scheduler
                // =====================================================

                Log.d(
                        TAG,
                        "========== SCHEDULING RESULT =========="
                );

                Log.d(
                        TAG,
                        "Generated schedules: "
                                + schedules.size()
                );

                for (Schedule schedule :
                        schedules) {

                    Log.d(
                            TAG,
                            "SCHEDULE -> TaskId="
                                    + schedule.getTaskId()
                                    + " | Date="
                                    + schedule.getDate()
                                    + " | "
                                    + schedule.getStartTime()
                                    + " - "
                                    + schedule.getEndTime()
                    );
                }

                // =====================================================
                // 8. ذخیره Scheduleها
                // =====================================================

                scheduler.saveSchedule(schedules);

                Log.d(
                        TAG,
                        "SCHEDULE SAVED SUCCESSFULLY"
                );

                // =====================================================
                // 9. بررسی اینکه Schedule واقعاً در DB ذخیره شده
                // =====================================================

                List<Schedule> savedSchedules =
                        scheduleDao.getSchedulesByDate(date);

                Log.d(
                        TAG,
                        "SAVED SCHEDULE COUNT = "
                                + savedSchedules.size()
                );

                for (Schedule schedule :
                        savedSchedules) {

                    Log.d(
                            TAG,
                            "DB SCHEDULE -> TaskId="
                                    + schedule.getTaskId()
                                    + " | "
                                    + schedule.getStartTime()
                                    + " - "
                                    + schedule.getEndTime()
                    );
                }

                Log.d(
                        TAG,
                        "======================================"
                );

                // =====================================================
                // 10. موفقیت تست
                // =====================================================

                runOnUiThread(() ->
                        Toast.makeText(
                                SchedulerTestActivity.this,
                                "SCHEDULER TEST SUCCESS",
                                Toast.LENGTH_LONG
                        ).show()
                );

            } catch (Exception e) {

                Log.e(
                        TAG,
                        "SCHEDULER TEST FAILED",
                        e
                );

                runOnUiThread(() ->
                        Toast.makeText(
                                SchedulerTestActivity.this,
                                "SCHEDULER TEST FAILED",
                                Toast.LENGTH_LONG
                        ).show()
                );
            }

        }).start();
    }

    @Override
    protected void onDestroy() {

        if (scheduler != null) {
            scheduler.close();
        }

        super.onDestroy();
    }
}