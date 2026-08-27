package com.example.personalplanner.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.personalplanner.database.entity.Task;
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

                scheduler = new Scheduler(
                        getApplicationContext()
                );

                long now = System.currentTimeMillis();

                Task task1 = new Task(
                        "Study Machine Learning",
                        "AI Study",
                        5,
                        now + 24 * 60 * 60 * 1000,
                        120,
                        false,
                        1,
                        now,
                        now,
                        0,
                        false,
                        "NONE"
                );

                task1.setId(1);

                Task task2 = new Task(
                        "Exercise",
                        "Workout",
                        3,
                        now + 48 * 60 * 60 * 1000,
                        60,
                        false,
                        1,
                        now,
                        now,
                        0,
                        false,
                        "NONE"
                );

                task2.setId(2);

                Task task3 = new Task(
                        "Read Book",
                        "Reading",
                        2,
                        now + 72 * 60 * 60 * 1000,
                        60,
                        false,
                        1,
                        now,
                        now,
                        0,
                        false,
                        "NONE"
                );

                task3.setId(3);

                List<Task> tasks = new ArrayList<>();

                tasks.add(task1);
                tasks.add(task2);
                tasks.add(task3);

                String date = "2026-08-26";

                List<?> schedules =
                        scheduler.generateSchedule(
                                tasks,
                                date
                        );

                Log.d(
                        TAG,
                        "========== SCHEDULING RESULT =========="
                );

                Log.d(
                        TAG,
                        "Generated schedules: "
                                + schedules.size()
                );

                scheduler.saveSchedule(
                        (List) schedules
                );

                Log.d(
                        TAG,
                        "SCHEDULE SAVED SUCCESSFULLY"
                );

                Log.d(
                        TAG,
                        "======================================"
                );

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