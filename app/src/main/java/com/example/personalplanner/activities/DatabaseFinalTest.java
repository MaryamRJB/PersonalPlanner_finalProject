//package com.example.personalplanner.activities;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.personalplanner.database.entity.Category;
//import com.example.personalplanner.database.entity.Task;
//import com.example.personalplanner.database.models.TaskWithCategory;
//import com.example.personalplanner.database.room.AppDatabase;
//
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//public class DatabaseTestActivity extends AppCompatActivity {
//
//    private static final String TAG = "DATABASE_TEST";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//
//        executor.execute(() -> {
//
//            try {
//
//                AppDatabase db =
//                        AppDatabase.getInstance(getApplicationContext());
//
//                Log.d(TAG, "========== DATABASE TEST START ==========");
//
//
//                // ==========================================
//                // 1. INSERT CATEGORY
//                // ==========================================
//
//                Category category = new Category(
//                        "Test Category",
//                        "#2196F3",
//                        "book"
//                );
//
//                long categoryId =
//                        db.categoryDao().insert(category);
//
//                Log.d(TAG,
//                        "1. INSERT CATEGORY: SUCCESS - ID = "
//                                + categoryId);
//
//
//                // ==========================================
//                // 2. INSERT TASK
//                // ==========================================
//
//                long now = System.currentTimeMillis();
//
//                Task task = new Task(
//                        "Test Task",
//                        "Database comprehensive test",
//                        5,
//                        now + 86400000,
//                        60,
//                        false,
//                        (int) categoryId,
//                        now,
//                        now,
//                        0,
//                        false,
//                        "NONE"
//                );
//
//                long taskId =
//                        db.taskDao().insert(task);
//
//                Log.d(TAG,
//                        "2. INSERT TASK: SUCCESS - ID = "
//                                + taskId);
//
//
//                // ==========================================
//                // 3. READ
//                // ==========================================
//
//                Task readTask =
//                        db.taskDao().testGetTaskById(
//                                (int) taskId
//                        );
//
//                if (readTask != null) {
//
//                    Log.d(TAG,
//                            "3. READ: SUCCESS - "
//                                    + readTask.getTitle());
//
//                } else {
//
//                    throw new Exception(
//                            "READ FAILED"
//                    );
//                }
//
//
//                // ==========================================
//                // 4. UPDATE
//                // ==========================================
//
//                readTask.setTitle(
//                        "Updated Test Task"
//                );
//
//                readTask.setPriority(3);
//
//                db.taskDao().update(readTask);
//
//                Task updatedTask =
//                        db.taskDao().testGetTaskById(
//                                (int) taskId
//                        );
//
//                if (updatedTask != null &&
//                        updatedTask.getTitle()
//                                .equals("Updated Test Task") &&
//                        updatedTask.getPriority() == 3) {
//
//                    Log.d(TAG,
//                            "4. UPDATE: SUCCESS");
//
//                } else {
//
//                    throw new Exception(
//                            "UPDATE FAILED"
//                    );
//                }
//
//
//                // ==========================================
//                // 5. SEARCH
//                // ==========================================
//
//                List<Task> searchResults =
//                        db.taskDao().testSearchTasks(
//                                "Updated Test"
//                        );
//
//                if (!searchResults.isEmpty()) {
//
//                    Log.d(TAG,
//                            "5. SEARCH: SUCCESS - Found "
//                                    + searchResults.size()
//                                    + " task(s)");
//
//                } else {
//
//                    throw new Exception(
//                            "SEARCH FAILED"
//                    );
//                }
//
//
//                // ==========================================
//                // 6. FILTER BY PRIORITY
//                // ==========================================
//
//                List<Task> filteredTasks =
//                        db.taskDao().testFilterByPriority(3);
//
//                if (!filteredTasks.isEmpty()) {
//
//                    Log.d(TAG,
//                            "6. FILTER: SUCCESS - Found "
//                                    + filteredTasks.size()
//                                    + " task(s)");
//
//                } else {
//
//                    throw new Exception(
//                            "FILTER FAILED"
//                    );
//                }
//
//
//                // ==========================================
//                // 7. RELATION Task -> Category
//                // ==========================================
//
//                List<TaskWithCategory> relations =
//                        db.taskDao().getTasksWithCategory();
//
//                boolean relationFound = false;
//
//                for (TaskWithCategory item : relations) {
//
//                    if (item.task.getId() == (int) taskId &&
//                            item.category != null &&
//                            item.category.getId() == (int) categoryId) {
//
//                        relationFound = true;
//                        break;
//                    }
//                }
//
//                if (relationFound) {
//
//                    Log.d(TAG,
//                            "7. RELATION: SUCCESS");
//
//                } else {
//
//                    throw new Exception(
//                            "RELATION FAILED"
//                    );
//                }
//
//
//                // ==========================================
//                // 8. DELETE
//                // ==========================================
//
//                db.taskDao().delete(updatedTask);
//
//                Task deletedTask =
//                        db.taskDao().testGetTaskById(
//                                (int) taskId
//                        );
//
//                if (deletedTask == null) {
//
//                    Log.d(TAG,
//                            "8. DELETE: SUCCESS");
//
//                } else {
//
//                    throw new Exception(
//                            "DELETE FAILED"
//                    );
//                }
//
//
//                // ==========================================
//                // FINAL RESULT
//                // ==========================================
//
//                Log.d(TAG,
//                        "================================");
//
//                Log.d(TAG,
//                        "ALL DATABASE TESTS PASSED");
//
//                Log.d(TAG,
//                        "================================");
//
//                runOnUiThread(() ->
//                        Toast.makeText(
//                                DatabaseTestActivity.this,
//                                "ALL DATABASE TESTS PASSED",
//                                Toast.LENGTH_LONG
//                        ).show()
//                );
//
//            } catch (Exception e) {
//
//                Log.e(TAG,
//                        "DATABASE TEST FAILED",
//                        e);
//
//                runOnUiThread(() ->
//                        Toast.makeText(
//                                DatabaseTestActivity.this,
//                                "DATABASE TEST FAILED",
//                                Toast.LENGTH_LONG
//                        ).show()
//                );
//            }
//        });
//    }
//}
