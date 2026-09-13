package com.example.aiplanner;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.personalplanner.database.entity.Routine;
import com.example.personalplanner.database.room.AppDatabase;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AddRoutineActivity extends AppCompatActivity {

    private EditText titleInput;
    private TimePicker startTimePicker;
    private TimePicker endTimePicker;
    private Button btnConfirm;
    private ImageView btnBackArrow;

    private AppDatabase database;

    private final ExecutorService databaseExecutor =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_routine);

        titleInput = findViewById(R.id.TitleInput);
        startTimePicker = findViewById(R.id.startTimePicker);
        endTimePicker = findViewById(R.id.endTimePicker);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnBackArrow = findViewById(R.id.btnBackArrow);

        database =
                AppDatabase.getInstance(this);

        startTimePicker.setIs24HourView(true);
        endTimePicker.setIs24HourView(true);

        btnBackArrow.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> saveRoutine());
    }

    private void saveRoutine() {

        String title =
                titleInput.getText()
                        .toString()
                        .trim();

        if (title.isEmpty()) {
            titleInput.setError("عنوان روتین را وارد کنید");
            return;
        }

        int startHour =
                startTimePicker.getHour();

        int startMinute =
                startTimePicker.getMinute();

        int endHour =
                endTimePicker.getHour();

        int endMinute =
                endTimePicker.getMinute();

        int startTotalMinutes =
                startHour * 60 + startMinute;

        int endTotalMinutes =
                endHour * 60 + endMinute;

        if (endTotalMinutes <= startTotalMinutes) {
            Toast.makeText(
                    this,
                    "ساعت پایان باید بعد از ساعت شروع باشد",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String startTime =
                String.format(
                        "%02d:%02d",
                        startHour,
                        startMinute
                );

        String endTime =
                String.format(
                        "%02d:%02d",
                        endHour,
                        endMinute
                );

        Calendar startDateCalendar =
                Calendar.getInstance();

        startDateCalendar.set(
                Calendar.HOUR_OF_DAY,
                0
        );
        startDateCalendar.set(
                Calendar.MINUTE,
                0
        );
        startDateCalendar.set(
                Calendar.SECOND,
                0
        );
        startDateCalendar.set(
                Calendar.MILLISECOND,
                0
        );

        long startDate =
                startDateCalendar.getTimeInMillis();

        Calendar endDateCalendar =
                (Calendar) startDateCalendar.clone();

        endDateCalendar.add(
                Calendar.DAY_OF_YEAR,
                29
        );

        long endDate =
                endDateCalendar.getTimeInMillis();

        Routine routine =
                new Routine(
                        title,
                        startTime,
                        endTime,
                        startDate,
                        endDate
                );

        btnConfirm.setEnabled(false);

        databaseExecutor.execute(() -> {

            database.routineDao()
                    .insert(routine);

            runOnUiThread(() -> {

                Toast.makeText(
                        this,
                        "روتین با موفقیت ثبت شد",
                        Toast.LENGTH_SHORT
                ).show();

                finish();
            });
        });
    }

    @Override
    protected void onDestroy() {
        databaseExecutor.shutdown();
        super.onDestroy();
    }
}