package com.example.aiplanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apachat.primecalendar.core.persian.PersianCalendar;
import com.example.personalplanner.database.dao.ScheduleDao;
import com.example.personalplanner.database.relation.ScheduleWithTask;
import com.example.personalplanner.database.room.AppDatabase;
import com.google.android.material.imageview.ShapeableImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import adapter.CalendarAdapter;
import model.DataGenerator;
import model.DayModel;

public class HomeFragment extends Fragment {

    // ==========================================
    // Calendar
    // ==========================================

    private RecyclerView calendar;
    private CalendarAdapter calendarAdapter;
    private List<DayModel> daysCurrMonth;


    // ==========================================
    // Schedule / Task Bento
    // ==========================================

    private ViewGroup leftTaskColumn;
    private ViewGroup rightTaskColumn;

    private TaskBentoAdapter taskBentoAdapter;

    private ScheduleDao scheduleDao;


    // ==========================================
    // LiveData
    // ==========================================

    private LiveData<List<ScheduleWithTask>> currentScheduleLiveData;

    private final Observer<List<ScheduleWithTask>> scheduleObserver =
            schedules -> {

                if (taskBentoAdapter == null) {
                    return;
                }

                taskBentoAdapter.setSchedules(schedules);
            };


    public HomeFragment() {
        // Required empty public constructor
    }


    // ==========================================
    // onCreateView
    // ==========================================

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_home,
                container,
                false
        );
    }


    // ==========================================
    // onViewCreated
    // ==========================================

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(
                view,
                savedInstanceState
        );


        // ==========================================
        // Database
        // ==========================================

        AppDatabase database =
                AppDatabase.getInstance(
                        requireContext()
                );

        scheduleDao =
                database.scheduleDao();


        // ==========================================
        // Bento Task Layout
        // ==========================================

        leftTaskColumn =
                view.findViewById(
                        R.id.leftTaskColumn
                );

        rightTaskColumn =
                view.findViewById(
                        R.id.rightTaskColumn
                );


        taskBentoAdapter =
                new TaskBentoAdapter(
                        leftTaskColumn,
                        rightTaskColumn
                );


        // ==========================================
        // Calendar
        // ==========================================

        calendar =
                view.findViewById(
                        R.id.calendar
                );


        LinearLayoutManager layoutManager =
                new LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                );

        calendar.setLayoutManager(
                layoutManager
        );


        DataGenerator dataGenerator =
                new DataGenerator();


        daysCurrMonth =
                dataGenerator.generateDaysForCurrMonth();


        // ==========================================
        // پیدا کردن امروز
        // ==========================================

        int todayPosition = -1;


        for (
                int i = 0;
                i < daysCurrMonth.size();
                i++
        ) {

            DayModel day =
                    daysCurrMonth.get(i);


            if (day.isToday()) {

                todayPosition = i;

                break;
            }
        }


        // ==========================================
        // Calendar Adapter
        // ==========================================

        calendarAdapter =
                new CalendarAdapter(
                        daysCurrMonth
                );


        calendar.setAdapter(
                calendarAdapter
        );


        // ==========================================
        // نمایش امروز
        // ==========================================

        if (todayPosition != -1) {

            calendar.scrollToPosition(
                    todayPosition
            );


            DayModel today =
                    daysCurrMonth.get(
                            todayPosition
                    );


            loadSchedulesForDate(
                    today
            );
        }


        // ==========================================
        // Calendar Click
        // ==========================================

        calendarAdapter.setOnClickListener(
                new CalendarAdapter.OnDayClickListener() {

                    @Override
                    public void onClick(
                            int position,
                            DayModel day) {

                        if (day.isSelected()) {
                            return;
                        }


                        // ------------------------------
                        // حذف انتخاب قبلی
                        // ------------------------------

                        for (
                                DayModel d :
                                daysCurrMonth
                        ) {

                            if (d.isSelected()) {

                                d.setSelected(false);

                                break;
                            }
                        }


                        // ------------------------------
                        // انتخاب روز جدید
                        // ------------------------------

                        day.setSelected(true);


                        calendarAdapter.notifyDataSetChanged();


                        // ------------------------------
                        // بارگذاری Taskهای روز
                        // ------------------------------

                        loadSchedulesForDate(
                                day
                        );
                    }
                }
        );


        // ==========================================
        // Profile
        // ==========================================

        setupProfile(
                view
        );


        // ==========================================
        // Add Task
        // ==========================================

        setupAddTaskButton(
                view
        );
    }


    // =========================================================
    // Load schedules for selected Persian day
    // =========================================================

    private void loadSchedulesForDate(
            DayModel day) {

        String gregorianDate =
                convertPersianDayToGregorian(
                        day
                );


        taskBentoAdapter.setDate(
                gregorianDate
        );


        // ------------------------------
        // حذف Observer قبلی
        // ------------------------------

        if (currentScheduleLiveData != null) {

            currentScheduleLiveData.removeObservers(
                    getViewLifecycleOwner()
            );
        }


        // ------------------------------
        // دریافت Scheduleهای روز
        // ------------------------------

        currentScheduleLiveData =
                scheduleDao
                        .getSchedulesWithTasksByDate(
                                gregorianDate
                        );


        // ------------------------------
        // مشاهده Scheduleها
        // ------------------------------

        currentScheduleLiveData.observe(
                getViewLifecycleOwner(),
                schedules -> {

                    if (taskBentoAdapter == null) {
                        return;
                    }


                    // فقط Taskها
                    taskBentoAdapter.setSchedules(
                            schedules
                    );
                }
        );
    }


    // =========================================================
    // Persian Date -> Gregorian yyyy-MM-dd
    // =========================================================

    private String convertPersianDayToGregorian(
            DayModel day) {

        PersianCalendar persianCalendar =
                new PersianCalendar();


        persianCalendar.set(
                day.getYear(),
                day.getMonth(),
                day.getDayOfMonth()
        );


        Date gregorianDate =
                new Date(
                        persianCalendar.getTimeInMillis()
                );


        SimpleDateFormat formatter =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.US
                );


        return formatter.format(
                gregorianDate
        );
    }


    // =========================================================
    // Profile
    // =========================================================

    private void setupProfile(
            View view) {

        View profileHeader =
                view.findViewById(
                        R.id.profile_header
                );


        if (profileHeader == null) {
            return;
        }


        ShapeableImageView btnProfile =
                profileHeader.findViewById(
                        R.id.imgProfile
                );


        if (btnProfile == null) {
            return;
        }


        btnProfile.setOnClickListener(
                v -> {

                    SharedPreferences prefs =
                            requireActivity()
                                    .getSharedPreferences(
                                            "MyPrefs",
                                            Context.MODE_PRIVATE
                                    );


                    boolean isLoggedIn =
                            prefs.getBoolean(
                                    "isLoggedIn",
                                    false
                            );


                    Intent intent;


                    if (isLoggedIn) {

                        intent =
                                new Intent(
                                        requireContext(),
                                        ProfileActivity.class
                                );

                    } else {

                        intent =
                                new Intent(
                                        requireContext(),
                                        SignInActivity.class
                                );
                    }


                    startActivity(
                            intent
                    );
                }
        );
    }


    // =========================================================
    // Add Task
    // =========================================================

    private void setupAddTaskButton(
            View view) {

        View taskNavView =
                view.findViewById(
                        R.id.task_nav
                );


        if (taskNavView == null) {
            return;
        }


        ImageButton btnNewNote =
                taskNavView.findViewById(
                        R.id.btnNewNote
                );


        if (btnNewNote == null) {
            return;
        }


        btnNewNote.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    requireContext(),
                                    AddTaskActivity.class
                            );


                    startActivity(
                            intent
                    );
                }
        );
    }


    // =========================================================
    // Destroy
    // =========================================================

    @Override
    public void onDestroyView() {

        if (currentScheduleLiveData != null) {

            currentScheduleLiveData.removeObserver(
                    scheduleObserver
            );

            currentScheduleLiveData = null;
        }


        super.onDestroyView();
    }
}