package com.example.aiplanner;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalplanner.database.room.AppDatabase;
import com.example.personalplanner.database.dao.SubTaskDao;
import com.example.personalplanner.database.dao.TaskDao;
import com.example.personalplanner.database.entity.SubTask;
import com.example.personalplanner.database.entity.Task;
import com.google.gson.Gson;
import com.example.personalplanner.database.dao.ScheduleDao;
import com.example.personalplanner.database.entity.Schedule;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskDetailActivity
        extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "task_id";

    private TextView taskTitle;

    private ImageView btnBackArrow;
    private ImageView priorityCircle;

    private RecyclerView subTaskRecyclerView;

    private Button btnGenerateSubTasks;

    private TaskDao taskDao;
    private SubTaskDao subTaskDao;
    private ScheduleDao scheduleDao;
    private TextView startTime;

    private SubTaskAdapter subTaskAdapter;

    private int taskId;

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.task_detail
        );

        // -------------------------------------------------
        // Views
        // -------------------------------------------------

        startTime = findViewById(R.id.startTime);

        taskTitle =
                findViewById(
                        R.id.taskTitle
                );

        btnBackArrow =
                findViewById(
                        R.id.btnBackArrow
                );

        priorityCircle =
                findViewById(
                        R.id.priorityCircle
                );

        subTaskRecyclerView =
                findViewById(
                        R.id.subTask
                );

        btnGenerateSubTasks =
                findViewById(
                        R.id.btnGenerateSubTasks
                );

        // -------------------------------------------------
        // Task ID
        // -------------------------------------------------

        taskId =
                getIntent().getIntExtra(
                        EXTRA_TASK_ID,
                        -1
                );

        if (taskId == -1) {
            finish();
            return;
        }

        // -------------------------------------------------
        // Database
        // -------------------------------------------------

        AppDatabase database =
                AppDatabase.getInstance(this);

        taskDao =
                database.taskDao();

        subTaskDao =
                database.subTaskDao();

        scheduleDao = database.scheduleDao();

        // -------------------------------------------------
        // RecyclerView
        // -------------------------------------------------

        subTaskRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        subTaskAdapter =
                new SubTaskAdapter(
                        subTaskDao,
                        this::updateTaskProgress
                );

        subTaskRecyclerView.setAdapter(
                subTaskAdapter
        );

        // -------------------------------------------------
        // Load
        // -------------------------------------------------

        loadTask();

        observeSubTasks();

        // -------------------------------------------------
        // Generate SubTasks
        // -------------------------------------------------

        btnGenerateSubTasks.setOnClickListener(
                v -> generateSubTasks()
        );

        // -------------------------------------------------
        // Back
        // -------------------------------------------------

        btnBackArrow.setOnClickListener(
                v -> finish()
        );
    }

    // =====================================================
    // Load Task
    // =====================================================

    private void loadTask() {

        new Thread(() -> {

            Task task = taskDao.getTaskById(taskId);

            List<Schedule> schedules =
                    scheduleDao.getSchedulesByTask(taskId);

            runOnUiThread(() -> {

                if (task == null) {
                    finish();
                    return;
                }

                taskTitle.setText(task.getTitle());

                setPriorityColor(
                        task.getPriority()
                );

                if (schedules != null &&
                        !schedules.isEmpty()) {

                    Schedule schedule =
                            schedules.get(0);

                    String start =
                            toPersianDigits(
                                    schedule.getStartTime()
                            );

                    String end =
                            toPersianDigits(
                                    schedule.getEndTime()
                            );

                    startTime.setText(
                            start + "  تا  " + end
                    );

                } else {

                    startTime.setText(
                            "زمان تعیین نشده"
                    );
                }
            });

        }).start();
    }

    // =====================================================
    // Observe SubTasks
    // =====================================================

    private void observeSubTasks() {

        LiveData<List<SubTask>> liveData =
                subTaskDao.getSubTasksByTaskId(
                        taskId
                );

        liveData.observe(
                this,
                subTasks -> {

                    subTaskAdapter.setSubTasks(
                            subTasks
                    );

                    updateTaskProgress();
                }
        );
    }

    // =====================================================
    // Generate SubTasks
    // =====================================================

    private void generateSubTasks() {

        btnGenerateSubTasks.setEnabled(false);

        new Thread(() -> {

            try {

                Task task =
                        taskDao.getTaskById(
                                taskId
                        );

                if (task == null) {

                    runOnUiThread(() -> {

                        btnGenerateSubTasks
                                .setEnabled(true);

                        Toast.makeText(
                                this,
                                "کار پیدا نشد",
                                Toast.LENGTH_SHORT
                        ).show();
                    });

                    return;
                }

                SubTaskRequest request =
                        new SubTaskRequest(
                                task.getTitle(),
                                task.getDescription(),
                                task.getDuration(),
                                task.getPriority()
                        );

                Gson gson =
                        new Gson();

                String json =
                        gson.toJson(request);

                // -----------------------------------------
                // API
                // -----------------------------------------

                URL url = new URL(
                        "http://127.0.0.1:8000/tasks/generate-subtasks"
                );

                HttpURLConnection connection =
                        (HttpURLConnection)
                                url.openConnection();

                connection.setRequestMethod(
                        "POST"
                );

                connection.setRequestProperty(
                        "Content-Type",
                        "application/json"
                );

                connection.setRequestProperty(
                        "Accept",
                        "application/json"
                );

                connection.setDoOutput(true);

                // -----------------------------------------
                // Send
                // -----------------------------------------

                try (OutputStream outputStream =
                             connection.getOutputStream()) {

                    outputStream.write(
                            json.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );
                }

                int responseCode =
                        connection.getResponseCode();

                if (responseCode != 200) {
                    throw new Exception(
                            "API Error: " +
                                    responseCode
                    );
                }

                // -----------------------------------------
                // Read
                // -----------------------------------------

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        connection.getInputStream()
                                )
                        );

                StringBuilder response =
                        new StringBuilder();

                String line;

                while (
                        (line = reader.readLine())
                                != null
                ) {

                    response.append(line);
                }

                reader.close();

                connection.disconnect();

                // -----------------------------------------
                // Parse
                // -----------------------------------------

                SubTaskResponse result =
                        gson.fromJson(
                                response.toString(),
                                SubTaskResponse.class
                        );

                // -----------------------------------------
                // Save to Room
                // -----------------------------------------

                if (result != null &&
                        result.subtasks != null) {

                    // زیرکارهای قبلی این Task
                    // حذف می‌شوند تا دوباره‌سازی
                    // باعث duplicate نشود.

                    subTaskDao
                            .deleteSubTasksByTaskId(
                                    taskId
                            );

                    for (
                            SubTaskResponse.SubTaskItem item
                            : result.subtasks
                    ) {

                        if (item == null ||
                                item.title == null ||
                                item.title.trim().isEmpty()) {

                            continue;
                        }

                        SubTask subTask =
                                new SubTask(
                                        taskId,
                                        item.title,
                                        item.description != null
                                                ? item.description
                                                : "",
                                        0L,
                                        0L,
                                        false
                                );

                        subTaskDao.insert(
                                subTask
                        );
                    }
                }

                runOnUiThread(() -> {

                    btnGenerateSubTasks
                            .setEnabled(true);

                    Toast.makeText(
                            this,
                            "زیرکارها ایجاد شدند",
                            Toast.LENGTH_SHORT
                    ).show();
                });

            } catch (Exception e) {

                e.printStackTrace();

                runOnUiThread(() -> {

                    btnGenerateSubTasks
                            .setEnabled(true);

                    Toast.makeText(
                            this,
                            "خطا در ایجاد زیرکارها",
                            Toast.LENGTH_LONG
                    ).show();
                });
            }

        }).start();
    }

    // =====================================================
    // Update Progress
    // =====================================================

    private void updateTaskProgress() {

        new Thread(() -> {

            int total =
                    subTaskDao.getSubTaskCount(
                            taskId
                    );

            int completed =
                    subTaskDao.getCompletedSubTaskCount(
                            taskId
                    );

            int progress = 0;

            if (total > 0) {

                progress =
                        Math.round(
                                completed *
                                        100f /
                                        total
                        );
            }

            taskDao.updateProgress(
                    taskId,
                    progress
            );

        }).start();
    }

    // =====================================================
    // Priority Color
    // =====================================================

    private void setPriorityColor(
            int priority) {

        int color;

        switch (priority) {

            case 1:
                // بالا
                color =
                        Color.parseColor(
                                "#F44336"
                        );
                break;

            case 2:
                // متوسط
                color =
                        Color.parseColor(
                                "#FFC107"
                        );
                break;

            case 3:
            default:
                // پایین
                color =
                        Color.parseColor(
                                "#4CAF50"
                        );
                break;
        }

        priorityCircle.setColorFilter(
                color
        );
    }
    @Override
    protected void onDestroy() {
        if (subTaskAdapter != null) {
            subTaskAdapter.shutdown();
        }

        super.onDestroy();
    }
    private String toPersianDigits(String text) {

        if (text == null) {
            return "";
        }

        return text
                .replace("0", "۰")
                .replace("1", "۱")
                .replace("2", "۲")
                .replace("3", "۳")
                .replace("4", "۴")
                .replace("5", "۵")
                .replace("6", "۶")
                .replace("7", "۷")
                .replace("8", "۸")
                .replace("9", "۹");
    }
}