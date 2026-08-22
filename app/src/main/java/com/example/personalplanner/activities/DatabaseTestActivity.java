package com.example.personalplanner.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.personalplanner.database.entity.Category;
import com.example.personalplanner.database.entity.Schedule;
import com.example.personalplanner.database.entity.SubTask;
import com.example.personalplanner.database.entity.Task;
import com.example.personalplanner.database.models.TaskWithCategory;
import com.example.personalplanner.database.room.AppDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseTestActivity extends AppCompatActivity {

    private static final String TAG = "DATABASE_TEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ExecutorService executor =
                Executors.newSingleThreadExecutor();

        executor.execute(() -> {

            try {

                AppDatabase db =
                        AppDatabase.getInstance(
                                getApplicationContext()
                        );

                Log.d(TAG,
                        "========== FINAL DATABASE TEST START ==========");


                // ==========================================
                // 1. INSERT CATEGORY
                // ==========================================

                Category category = new Category(
                        "Test Category",
                        "#2196F3",
                        "book"
                );

                long categoryId =
                        db.categoryDao().insert(category);

                Log.d(TAG,
                        "1. INSERT CATEGORY: SUCCESS - ID = "
                                + categoryId);


                // ==========================================
                // 2. INSERT TASK
                // ==========================================

                long now = System.currentTimeMillis();

                Task task = new Task(
                        "Final Database Test",
                        "Complete database test",
                        5,
                        now + 86400000,
                        120,
                        false,
                        (int) categoryId,
                        now,
                        now,
                        0,
                        false,
                        "NONE"
                );

                long taskId =
                        db.taskDao().insert(task);

                Log.d(TAG,
                        "2. INSERT TASK: SUCCESS - ID = "
                                + taskId);


                // ==========================================
                // 3. READ TASK
                // ==========================================

                Task readTask =
                        db.taskDao().testGetTaskById(
                                (int) taskId
                        );

                if (readTask != null) {

                    Log.d(TAG,
                            "3. READ TASK: SUCCESS - "
                                    + readTask.getTitle());

                } else {

                    throw new Exception(
                            "READ TASK FAILED"
                    );
                }


                // ==========================================
                // 4. INSERT SUBTASKS
                // ==========================================

                SubTask subTask1 = new SubTask(
                        (int) taskId,
                        "Frontend",
                        "Develop frontend",
                        10,
                        11,
                        false
                );

                SubTask subTask2 = new SubTask(
                        (int) taskId,
                        "Backend",
                        "Develop backend",
                        11,
                        12,
                        false
                );

                long subTaskId1 =
                        db.subTaskDao().insert(subTask1);

                long subTaskId2 =
                        db.subTaskDao().insert(subTask2);

                Log.d(TAG,
                        "4. INSERT SUBTASKS: SUCCESS - IDs = "
                                + subTaskId1
                                + ", "
                                + subTaskId2);


                // ==========================================
                // 5. READ SUBTASKS
                // ==========================================

                List<SubTask> subTasks =
                        db.subTaskDao()
                                .testGetSubTasksByTaskId(
                                        (int) taskId
                                );

                if (subTasks.size() == 2) {

                    Log.d(TAG,
                            "5. READ SUBTASKS: SUCCESS - Found "
                                    + subTasks.size());

                } else {

                    throw new Exception(
                            "SUBTASK READ FAILED"
                    );
                }


                // ==========================================
                // 6. COMPLETE FIRST SUBTASK
                // ==========================================

                db.subTaskDao().updateSubTaskStatus(
                        (int) subTaskId1,
                        true
                );

                Log.d(TAG,
                        "6. UPDATE SUBTASK STATUS: SUCCESS");


                // ==========================================
                // 7. CALCULATE PROGRESS
                // ==========================================

                int totalSubTasks =
                        db.subTaskDao()
                                .getSubTaskCount(
                                        (int) taskId
                                );

                int completedSubTasks =
                        db.subTaskDao()
                                .getCompletedSubTaskCount(
                                        (int) taskId
                                );

                int progress = 0;

                if (totalSubTasks > 0) {

                    progress =
                            (completedSubTasks * 100)
                                    / totalSubTasks;
                }

                db.taskDao().updateProgress(
                        (int) taskId,
                        progress
                );

                Task progressTask =
                        db.taskDao().testGetTaskById(
                                (int) taskId
                        );

                if (progressTask != null &&
                        progressTask.getProgress() == 50) {

                    Log.d(TAG,
                            "7. PROGRESS: SUCCESS - "
                                    + progressTask.getProgress()
                                    + "%");

                } else {

                    throw new Exception(
                            "PROGRESS FAILED"
                    );
                }


                // ==========================================
                // 8. INSERT SCHEDULE
                // ==========================================

                String testDate = "2026-08-25";

                Schedule schedule = new Schedule(
                        (int) taskId,
                        testDate,
                        "10:00",
                        "12:00"
                );

                long scheduleId =
                        db.scheduleDao().insert(schedule);

                Log.d(TAG,
                        "8. INSERT SCHEDULE: SUCCESS - ID = "
                                + scheduleId);


                // ==========================================
                // 9. QUERY SCHEDULE BY DATE
                // ==========================================

                List<Schedule> schedules =
                        db.scheduleDao()
                                .getSchedulesByDate(
                                        testDate
                                );

                boolean scheduleFound = false;

                for (Schedule item : schedules) {

                    if (item.getTaskId() == (int) taskId) {

                        scheduleFound = true;
                        break;
                    }
                }

                if (scheduleFound) {

                    Log.d(TAG,
                            "9. DATE QUERY: SUCCESS - "
                                    + testDate);

                } else {

                    throw new Exception(
                            "DATE QUERY FAILED"
                    );
                }


                // ==========================================
                // 10. TASK -> CATEGORY RELATION
                // ==========================================

                List<TaskWithCategory> relations =
                        db.taskDao().getTasksWithCategory();

                boolean relationFound = false;

                for (TaskWithCategory item : relations) {

                    if (item.task.getId() == (int) taskId &&
                            item.category != null &&
                            item.category.getId()
                                    == (int) categoryId) {

                        relationFound = true;
                        break;
                    }
                }

                if (relationFound) {

                    Log.d(TAG,
                            "11. TASK-CATEGORY RELATION: SUCCESS");

                } else {

                    throw new Exception(
                            "TASK-CATEGORY RELATION FAILED"
                    );
                }


                // ==========================================
                // 11. DELETE
                // ==========================================

                db.taskDao().delete(
                        progressTask
                );

                Task deletedTask =
                        db.taskDao().testGetTaskById(
                                (int) taskId
                        );

                if (deletedTask == null) {

                    Log.d(TAG,
                            "12. DELETE: SUCCESS");

                } else {

                    throw new Exception(
                            "DELETE FAILED"
                    );
                }


                // ==========================================
                // FINAL RESULT
                // ==========================================

                Log.d(TAG,
                        "========================================");

                Log.d(TAG,
                        "ALL FINAL DATABASE TESTS PASSED");

                Log.d(TAG,
                        "========================================");

                runOnUiThread(() ->
                        Toast.makeText(
                                DatabaseTestActivity.this,
                                "ALL FINAL DATABASE TESTS PASSED",
                                Toast.LENGTH_LONG
                        ).show()
                );

            } catch (Exception e) {

                Log.e(TAG,
                        "FINAL DATABASE TEST FAILED",
                        e);

                runOnUiThread(() ->
                        Toast.makeText(
                                DatabaseTestActivity.this,
                                "FINAL DATABASE TEST FAILED",
                                Toast.LENGTH_LONG
                        ).show()
                );
            }
        });
    }
}