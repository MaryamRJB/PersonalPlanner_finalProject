package com.example.aiplanner;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.personalplanner.database.dao.CategoryDao;
import com.example.personalplanner.database.entity.Category;
import com.example.personalplanner.database.entity.Task;
import com.example.personalplanner.database.room.AppDatabase;
import com.example.personalplanner.viewmodel.TaskViewModel;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddTaskActivity extends AppCompatActivity {

    private EditText etTitle;
    private EditText etDescription;

    private RadioGroup radioGroupPriority;
    private RadioButton rbHigh, rbMedium, rbLow;

    private TextInputEditText etDeadline;
    private MaterialAutoCompleteTextView etCategory;
    private MaterialAutoCompleteTextView etDuration;

    private Button btnConfirm;

    private TaskViewModel taskViewModel;

    // تاریخ واقعی انتخاب شده
    private long selectedDeadline = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task_activity);

        // =====================================================
        // پیدا کردن View ها
        // =====================================================

        ImageView btnBackArrow = findViewById(R.id.btnBackArrow);

        etTitle = findViewById(R.id.TitleInput);
        etDescription = findViewById(R.id.DiscriptionInput);

        radioGroupPriority = findViewById(R.id.radioGroupPriority);

        rbHigh = findViewById(R.id.rbHigh);
        rbMedium = findViewById(R.id.rbMedium);
        rbLow = findViewById(R.id.rbLow);

        etDeadline = findViewById(R.id.etDeadline);
        etCategory = findViewById(R.id.etCategory);
        etDuration = findViewById(R.id.etDuration);

        btnConfirm = findViewById(R.id.btnConfirm);

        taskViewModel = new ViewModelProvider(this)
                .get(TaskViewModel.class);


        // =====================================================
        // CATEGORY
        // =====================================================

        List<String> categories = new ArrayList<>();

        categories.add("خانه");
        categories.add("سلامت");
        categories.add("درس");
        categories.add("کار");
        categories.add("سایر");

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                R.layout.dropdown_item,
                R.id.dropdownItemText,
                categories
        );

        etCategory.setAdapter(categoryAdapter);

        etCategory.setDropDownVerticalOffset(8);
        etCategory.setDropDownWidth(200);

        etCategory.setDropDownBackgroundDrawable(
                getDrawable(R.drawable.dropdown_background)
        );

        etCategory.setOnClickListener(v ->
                etCategory.showDropDown()
        );


        // فقط نام دسته‌بندی را داخل TextView قرار می‌دهیم.
        // دیگر ID را دستی تعیین نمی‌کنیم.
        etCategory.setOnItemClickListener((parent, view, position, id) -> {

            String selectedCategory =
                    parent.getItemAtPosition(position).toString();

            etCategory.setText(selectedCategory, false);
        });


        // =====================================================
        // DURATION
        // =====================================================

        List<String> durations = new ArrayList<>();

        durations.add("1 ساعت");
        durations.add("2 ساعت");
        durations.add("3 ساعت");
        durations.add("4 ساعت");
        durations.add("5 ساعت");
        durations.add("6 ساعت");
        durations.add("1 روز");
        durations.add("2 روز");
        durations.add("1 هفته");
        durations.add("2 هفته");
        durations.add("1 ماه");

        ArrayAdapter<String> durationAdapter = new ArrayAdapter<>(
                this,
                R.layout.dropdown_item,
                R.id.dropdownItemText,
                durations
        );

        etDuration.setAdapter(durationAdapter);

        etDuration.setDropDownVerticalOffset(8);
        etDuration.setDropDownWidth(200);

        etDuration.setDropDownBackgroundDrawable(
                getDrawable(R.drawable.dropdown_background)
        );

        etDuration.setOnClickListener(v ->
                etDuration.showDropDown()
        );


        // =====================================================
        // DEADLINE
        // =====================================================

        etDeadline.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog =
                    new DatePickerDialog(
                            AddTaskActivity.this,

                            (view, selectedYear, selectedMonth, selectedDay) -> {

                                Calendar selectedCalendar =
                                        Calendar.getInstance();

                                selectedCalendar.set(
                                        selectedYear,
                                        selectedMonth,
                                        selectedDay,
                                        23,
                                        59,
                                        59
                                );

                                selectedDeadline =
                                        selectedCalendar.getTimeInMillis();

                                etDeadline.setText(
                                        selectedDay
                                                + "/"
                                                + (selectedMonth + 1)
                                                + "/"
                                                + selectedYear
                                );
                            },

                            year,
                            month,
                            day
                    );

            datePickerDialog.show();
        });


        // =====================================================
        // BACK
        // =====================================================

        if (btnBackArrow != null) {

            btnBackArrow.setOnClickListener(v ->
                    finish()
            );
        }


        // =====================================================
        // CONFIRM
        // =====================================================

        btnConfirm.setOnClickListener(v -> {

            // -------------------------------------------------
            // عنوان
            // -------------------------------------------------

            String title =
                    etTitle.getText()
                            .toString()
                            .trim();

            if (title.isEmpty()) {

                etTitle.setError("عنوان کار را وارد کنید");
                etTitle.requestFocus();

                return;
            }


            // -------------------------------------------------
            // توضیحات
            // -------------------------------------------------

            String description =
                    etDescription.getText()
                            .toString()
                            .trim();


            // -------------------------------------------------
            // اولویت
            // -------------------------------------------------

            int priority = 1;

            int selectedId =
                    radioGroupPriority.getCheckedRadioButtonId();

            if (selectedId == R.id.rbHigh) {

                priority = 2;

            } else if (selectedId == R.id.rbLow) {

                priority = 0;
            }

            final int finalPriority = priority;


            // -------------------------------------------------
            // Deadline
            // -------------------------------------------------

            if (selectedDeadline == 0) {

                Toast.makeText(
                        this,
                        "لطفاً تاریخ را انتخاب کنید",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }


            // -------------------------------------------------
            // Duration
            // -------------------------------------------------

            int duration = getDurationInHours(
                    etDuration.getText().toString()
            );
            int durationMinutes = duration * 60;

            if (duration <= 0) {

                Toast.makeText(
                        this,
                        "لطفاً مدت زمان را انتخاب کنید",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }


            // -------------------------------------------------
            // Category
            // -------------------------------------------------

            String categoryName =
                    etCategory.getText()
                            .toString()
                            .trim();

            if (categoryName.isEmpty()) {

                Toast.makeText(
                        this,
                        "لطفاً دسته‌بندی را انتخاب کنید",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }


            // =================================================
            // ذخیره در دیتابیس
            // =================================================

            btnConfirm.setEnabled(false);

            new Thread(() -> {

                try {

                    AppDatabase db =
                            AppDatabase.getInstance(
                                    getApplicationContext()
                            );

                    CategoryDao categoryDao =
                            db.categoryDao();

                    // پیدا کردن Category واقعی از روی نام
                    Category category =
                            categoryDao.getCategoryByName(categoryName);


                    // اگر Category در دیتابیس وجود نداشت
                    if (category == null) {

                        runOnUiThread(() -> {

                            btnConfirm.setEnabled(true);

                            Toast.makeText(
                                    AddTaskActivity.this,
                                    "دسته‌بندی پیدا نشد: " + categoryName,
                                    Toast.LENGTH_SHORT
                            ).show();
                        });

                        return;
                    }


                    // ID واقعی Category
                    int categoryId =
                            category.getId();


                    // =================================================
                    // ساخت Task
                    // =================================================

                    long currentTime =
                            System.currentTimeMillis();

                    Task task = new Task(

                            title,

                            description,

                            finalPriority,

                            selectedDeadline,

                            durationMinutes,

                            false,

                            categoryId,

                            currentTime,

                            currentTime,

                            selectedDeadline,

                            false,

                            ""
                    );


                    // ذخیره Task
                    taskViewModel.insert(task);


                    runOnUiThread(() -> {

                        Toast.makeText(
                                AddTaskActivity.this,
                                "کار با موفقیت ثبت شد!",
                                Toast.LENGTH_SHORT
                        ).show();

                        finish();
                    });


                } catch (Exception e) {

                    e.printStackTrace();

                    runOnUiThread(() -> {

                        btnConfirm.setEnabled(true);

                        Toast.makeText(
                                AddTaskActivity.this,
                                "خطا در ثبت کار: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    });
                }

            }).start();
        });
    }


    // =====================================================
    // تبدیل Duration به ساعت
    // =====================================================

    private int getDurationInHours(String durationText) {

        if (durationText == null ||
                durationText.trim().isEmpty()) {

            return 0;
        }

        try {

            if (durationText.contains("ساعت")) {

                return Integer.parseInt(
                        durationText
                                .replace(" ساعت", "")
                                .trim()
                );
            }

            if (durationText.contains("روز")) {

                return Integer.parseInt(
                        durationText
                                .replace(" روز", "")
                                .trim()
                ) * 24;
            }

            if (durationText.contains("هفته")) {

                return Integer.parseInt(
                        durationText
                                .replace(" هفته", "")
                                .trim()
                ) * 24 * 7;
            }

            if (durationText.contains("ماه")) {

                return Integer.parseInt(
                        durationText
                                .replace(" ماه", "")
                                .trim()
                ) * 24 * 30;
            }

        } catch (NumberFormatException e) {

            return 0;
        }

        return 0;
    }
}