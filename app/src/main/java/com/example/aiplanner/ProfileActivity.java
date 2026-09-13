package com.example.aiplanner;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.personalplanner.database.dao.UserDao;
import com.example.personalplanner.database.entity.User;
import com.example.personalplanner.database.room.AppDatabase;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_USER_ID = "userId";

    private EditText etName;
    private EditText etPhone;
    private EditText etWakeUpTime;
    private EditText etSleepTime;

    private UserDao userDao;

    private final ExecutorService executorService =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile_detail);

        // ==========================================
        // Views
        // ==========================================

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etWakeUpTime = findViewById(R.id.etWakeUpTime);
        etSleepTime = findViewById(R.id.etSleepTime);

        ImageView btnBackArrow =
                findViewById(R.id.btnBackArrow);


        // ==========================================
        // Database
        // ==========================================

        AppDatabase database =
                AppDatabase.getInstance(
                        getApplicationContext()
                );

        userDao = database.userDao();


        // ==========================================
        // User ID
        // ==========================================

        SharedPreferences prefs =
                getSharedPreferences(
                        PREFS_NAME,
                        Context.MODE_PRIVATE
                );

        int userId =
                prefs.getInt(
                        KEY_USER_ID,
                        -1
                );


        // ==========================================
        // Load User
        // ==========================================

        if (userId != -1) {

            executorService.execute(() -> {

                User user =
                        userDao.getUserById(userId);

                if (user != null) {

                    runOnUiThread(() -> {

                        // شماره تلفن
                        etPhone.setText(
                                user.getPhone()
                        );


                        // نام
                        if (user.getName() != null &&
                                !user.getName().isEmpty()) {

                            etName.setText(
                                    user.getName()
                            );

                        } else {

                            etName.setText("");
                        }


                        // ساعت بیداری
                        if (user.getWakeUpTime() != null &&
                                !user.getWakeUpTime().isEmpty()) {

                            etWakeUpTime.setText(
                                    user.getWakeUpTime()
                            );
                        }


                        // ساعت خواب
                        if (user.getSleepTime() != null &&
                                !user.getSleepTime().isEmpty()) {

                            etSleepTime.setText(
                                    user.getSleepTime()
                            );
                        }
                    });
                }
            });
        }


        // ==========================================
        // Wake Up Time
        // ==========================================

        etWakeUpTime.setOnClickListener(v -> {

            showTimePicker(
                    etWakeUpTime
            );
        });


        // ==========================================
        // Sleep Time
        // ==========================================

        etSleepTime.setOnClickListener(v -> {

            showTimePicker(
                    etSleepTime
            );
        });


        // ==========================================
        // Back
        // ==========================================

        if (btnBackArrow != null) {

            btnBackArrow.setOnClickListener(v -> {

                String name =
                        etName.getText()
                                .toString()
                                .trim();

                String wakeUpTime =
                        etWakeUpTime.getText()
                                .toString()
                                .trim();

                String sleepTime =
                        etSleepTime.getText()
                                .toString()
                                .trim();


                if (userId != -1) {

                    executorService.execute(() -> {

                        User user =
                                userDao.getUserById(
                                        userId
                                );

                        if (user != null) {

                            user.setName(name);

                            user.setWakeUpTime(
                                    wakeUpTime
                            );

                            user.setSleepTime(
                                    sleepTime
                            );

                            userDao.update(user);
                        }


                        runOnUiThread(
                                this::finish
                        );
                    });

                } else {

                    finish();
                }
            });
        }
    }


    // =========================================================
    // Time Picker
    // =========================================================

    private void showTimePicker(
            EditText targetEditText) {

        Calendar calendar =
                Calendar.getInstance();


        int currentHour =
                calendar.get(Calendar.HOUR_OF_DAY);

        int currentMinute =
                calendar.get(Calendar.MINUTE);


        TimePickerDialog dialog =
                new TimePickerDialog(
                        this,
                        (view, hourOfDay, minute) -> {

                            String time =
                                    String.format(
                                            java.util.Locale.US,
                                            "%02d:%02d",
                                            hourOfDay,
                                            minute
                                    );

                            targetEditText.setText(
                                    time
                            );
                        },
                        currentHour,
                        currentMinute,
                        true
                );


        dialog.show();
    }


    // =========================================================
    // Destroy
    // =========================================================

    @Override
    protected void onDestroy() {

        super.onDestroy();

        executorService.shutdown();
    }
}