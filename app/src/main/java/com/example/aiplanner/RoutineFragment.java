package com.example.aiplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apachat.primecalendar.core.PrimeCalendar;
import com.apachat.primecalendar.core.persian.PersianCalendar;
import com.example.personalplanner.database.entity.Routine;
import com.example.personalplanner.database.room.AppDatabase;

import java.util.List;

import adapter.CalendarAdapter;
import model.DataGenerator;
import model.DayModel;

public class RoutineFragment extends Fragment {

    private RecyclerView calendar;
    private CalendarAdapter calendarAdapter;

    private List<DayModel> daysCurrMonth;

    private RecyclerView recyclerViewRoutines;
    private RoutineAdapter routineAdapter;

    private AppDatabase database;

    private LiveData<List<Routine>> currentRoutineLiveData;

    public RoutineFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_routine,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(
                view,
                savedInstanceState
        );

        database =
                AppDatabase.getInstance(
                        requireContext()
                );

        recyclerViewRoutines =
                view.findViewById(
                        R.id.recyclerViewRoutines
                );

        recyclerViewRoutines.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );

        routineAdapter =
                new RoutineAdapter(
                        database.routineCompletionDao()
                );

        recyclerViewRoutines.setAdapter(
                routineAdapter
        );

        /*
         * دکمه ایجاد روتین
         */
        View btnAddRoutine =
                view.findViewById(
                        R.id.btnNewRoutine
                );

        if (btnAddRoutine != null) {

            btnAddRoutine.setOnClickListener(v -> {

                Intent intent =
                        new Intent(
                                requireContext(),
                                AddRoutineActivity.class
                        );

                startActivity(intent);
            });
        }

        /*
         * Calendar
         */
        calendar =
                view.findViewById(
                        R.id.calendar
                );

        LinearLayoutManager calendarLayoutManager =
                new LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                );

        calendar.setLayoutManager(
                calendarLayoutManager
        );

        DataGenerator dataGenerator =
                new DataGenerator();

        daysCurrMonth =
                dataGenerator.generateDaysForCurrMonth();

        int todayPosition = -1;

        for (int i = 0;
             i < daysCurrMonth.size();
             i++) {

            DayModel day =
                    daysCurrMonth.get(i);

            if (day.isToday()) {

                todayPosition = i;

                break;
            }
        }

        calendarAdapter =
                new CalendarAdapter(
                        daysCurrMonth
                );

        calendar.setAdapter(
                calendarAdapter
        );

        /*
         * انتخاب روز
         */
        calendarAdapter.setOnClickListener(
                new CalendarAdapter.OnDayClickListener() {

                    @Override
                    public void onClick(
                            int position,
                            DayModel day) {

                        if (day.isSelected()) {
                            return;
                        }

                        for (DayModel d :
                                daysCurrMonth) {

                            if (d.isSelected()) {

                                d.setSelected(false);

                                break;
                            }
                        }

                        day.setSelected(true);

                        calendarAdapter.notifyDataSetChanged();

                        loadRoutinesForDate(day);
                    }
                }
        );

        /*
         * رفتن روی امروز
         */
        if (todayPosition != -1) {

            calendar.scrollToPosition(
                    todayPosition
            );

            DayModel today =
                    daysCurrMonth.get(
                            todayPosition
                    );

            loadRoutinesForDate(today);
        }
    }

    private void loadRoutinesForDate(DayModel day) {

        long date =
                convertPersianToMillis(day);

        String completionDate =
                new java.text.SimpleDateFormat(
                        "yyyy-MM-dd",
                        java.util.Locale.US
                ).format(
                        new java.util.Date(date)
                );

        routineAdapter.setSelectedDate(
                completionDate
        );

        if (currentRoutineLiveData != null) {

            currentRoutineLiveData.removeObservers(
                    getViewLifecycleOwner()
            );
        }

        currentRoutineLiveData =
                database.routineDao()
                        .getRoutinesForDate(date);

        currentRoutineLiveData.observe(
                getViewLifecycleOwner(),
                routines -> {

                    routineAdapter.setRoutines(
                            routines
                    );
                }
        );
    }

    private long convertPersianToMillis(
            DayModel day) {

        PrimeCalendar persianCalendar =
                new PersianCalendar();

        persianCalendar.set(
                day.getYear(),
                day.getMonth(),
                day.getDayOfMonth(),
                0,
                0,
                0
        );

        return persianCalendar.getTimeInMillis();
    }

    @Override
    public void onDestroyView() {

        if (currentRoutineLiveData != null) {

            currentRoutineLiveData.removeObservers(
                    getViewLifecycleOwner()
            );
        }

        super.onDestroyView();
    }
}