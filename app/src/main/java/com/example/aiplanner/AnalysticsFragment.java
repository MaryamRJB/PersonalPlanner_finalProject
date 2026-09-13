package com.example.aiplanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.personalplanner.database.dao.RoutineCompletionDao;
import com.example.personalplanner.database.dao.RoutineDao;
import com.example.personalplanner.database.dao.ScheduleDao;
import com.example.personalplanner.database.dao.UserDao;
import com.example.personalplanner.database.entity.RoutineCompletion;
import com.example.personalplanner.database.entity.User;
import com.example.personalplanner.database.models.ScheduleAnalyticsItem;
import com.example.personalplanner.database.room.AppDatabase;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnalysticsFragment extends Fragment {

    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_USER_ID = "userId";

    // =========================================================
    // UI
    // =========================================================

    private TextView wakeUp;
    private TextView sleep;

    private TextView percentageWorkMonth;
    private TextView percentageWorkDay;
    private TextView percentageRoutine;

    private TextView power;
    private TextView rest;

    private BarChart barChart;


    // =========================================================
    // DATABASE
    // =========================================================

    private AppDatabase database;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private UserDao userDao;
    private ScheduleDao scheduleDao;

    private RoutineDao routineDao;
    private RoutineCompletionDao routineCompletionDao;


    // =========================================================
    // EXECUTOR
    // =========================================================

    private final ExecutorService executorService =
            Executors.newSingleThreadExecutor();


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AnalysticsFragment() {
        // Required empty public constructor
    }


    // =========================================================
    // ON CREATE VIEW
    // =========================================================

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_analystics,
                container,
                false
        );
    }


    // =========================================================
    // ON VIEW CREATED
    // =========================================================

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(
                view,
                savedInstanceState
        );


        // =====================================================
        // FIND VIEWS
        // =====================================================

        wakeUp =
                view.findViewById(
                        R.id.WakeUp
                );

        sleep =
                view.findViewById(
                        R.id.Sleep
                );

        percentageWorkMonth =
                view.findViewById(
                        R.id.percentageWorkMonth
                );

        percentageWorkDay =
                view.findViewById(
                        R.id.percentageWorkDay
                );

        percentageRoutine =
                view.findViewById(
                        R.id.percentageRoutine
                );

        power =
                view.findViewById(
                        R.id.Power
                );

        rest =
                view.findViewById(
                        R.id.Rest
                );

        barChart =
                view.findViewById(
                        R.id.barChart
                );


        // =====================================================
        // DATABASE
        // =====================================================

        database =
                AppDatabase.getInstance(
                        requireContext()
                );

        userDao =
                database.userDao();

        scheduleDao =
                database.scheduleDao();

        routineDao =
                database.routineDao();

        routineCompletionDao =
                database.routineCompletionDao();


        // =====================================================
        // LOAD ANALYTICS
        // =====================================================

        loadUserSleepAndWakeTime();

        loadTaskStatistics();

        loadEnergyStatistics();

        loadRoutineStatistics();

        loadMonthlyChart();
    }


    // =========================================================
    // USER SLEEP / WAKE TIME
    // =========================================================

    private void loadUserSleepAndWakeTime() {

        SharedPreferences prefs =
                requireActivity()
                        .getSharedPreferences(
                                PREFS_NAME,
                                Context.MODE_PRIVATE
                        );


        int userId =
                prefs.getInt(
                        KEY_USER_ID,
                        -1
                );


        if (userId == -1) {

            wakeUp.setText("--:--");

            sleep.setText("--:--");

            return;
        }


        executorService.execute(() -> {

            User user =
                    userDao.getUserById(
                            userId
                    );


            if (user == null) {
                return;
            }


            String wakeUpTime =
                    user.getWakeUpTime();

            String sleepTime =
                    user.getSleepTime();


            if (!isAdded()) {
                return;
            }


            requireActivity().runOnUiThread(() -> {

                if (wakeUpTime != null &&
                        !wakeUpTime.isEmpty()) {

                    wakeUp.setText(
                            convertToPersianDigits(
                                    wakeUpTime
                            )
                    );

                } else {

                    wakeUp.setText("--:--");
                }


                if (sleepTime != null &&
                        !sleepTime.isEmpty()) {

                    sleep.setText(
                            convertToPersianDigits(
                                    sleepTime
                            )
                    );

                } else {

                    sleep.setText("--:--");
                }
            });
        });
    }


    // =========================================================
    // TASK STATISTICS
    // =========================================================

    private void loadTaskStatistics() {

        executorService.execute(() -> {

            SimpleDateFormat dateFormat =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.US
                    );


            // =================================================
            // TODAY
            // =================================================

            Calendar todayCalendar =
                    Calendar.getInstance();


            String today =
                    dateFormat.format(
                            todayCalendar.getTime()
                    );


            int todayTotal =
                    scheduleDao
                            .countScheduledTasksBetweenDates(
                                    today,
                                    today
                            );


            int todayCompleted =
                    scheduleDao
                            .countCompletedTasksBetweenDates(
                                    today,
                                    today
                            );


            int todayPercentage =
                    calculatePercentage(
                            todayCompleted,
                            todayTotal
                    );


            // =================================================
            // CURRENT MONTH
            // =================================================

            Calendar monthCalendar =
                    Calendar.getInstance();


            monthCalendar.set(
                    Calendar.DAY_OF_MONTH,
                    1
            );


            String monthStart =
                    dateFormat.format(
                            monthCalendar.getTime()
                    );


            monthCalendar.set(
                    Calendar.DAY_OF_MONTH,
                    monthCalendar.getActualMaximum(
                            Calendar.DAY_OF_MONTH
                    )
            );


            String monthEnd =
                    dateFormat.format(
                            monthCalendar.getTime()
                    );


            int monthTotal =
                    scheduleDao
                            .countScheduledTasksBetweenDates(
                                    monthStart,
                                    monthEnd
                            );


            int monthCompleted =
                    scheduleDao
                            .countCompletedTasksBetweenDates(
                                    monthStart,
                                    monthEnd
                            );


            int monthPercentage =
                    calculatePercentage(
                            monthCompleted,
                            monthTotal
                    );


            // =================================================
            // UPDATE UI
            // =================================================

            if (!isAdded()) {
                return;
            }


            requireActivity().runOnUiThread(() -> {

                percentageWorkDay.setText(
                        convertToPersianDigits(
                                String.valueOf(
                                        todayPercentage
                                )
                        ) + "%"
                );


                percentageWorkMonth.setText(
                        convertToPersianDigits(
                                String.valueOf(
                                        monthPercentage
                                )
                        ) + "%"
                );
            });
        });
    }


    // =========================================================
    // ENERGY STATISTICS
    // =========================================================

    private void loadEnergyStatistics() {

        executorService.execute(() -> {

            Calendar endCalendar =
                    Calendar.getInstance();


            Calendar startCalendar =
                    Calendar.getInstance();


            startCalendar.add(
                    Calendar.DAY_OF_MONTH,
                    -29
            );


            SimpleDateFormat dateFormat =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.US
                    );


            String startDate =
                    dateFormat.format(
                            startCalendar.getTime()
                    );


            String endDate =
                    dateFormat.format(
                            endCalendar.getTime()
                    );


            List<ScheduleAnalyticsItem> schedules =
                    scheduleDao.getSchedulesForAnalytics(
                            startDate,
                            endDate
                    );

            System.out.println(
                    "ANALYTICS SCHEDULE COUNT = " +
                            schedules.size()
            );


            int[] totalTasks =
                    new int[5];


            int[] completedTasks =
                    new int[5];


            for (
                    ScheduleAnalyticsItem item :
                    schedules
            ) {

                int hour =
                        getHourFromTime(
                                item.getStartTime()
                        );


                int range =
                        getTimeRange(
                                hour
                        );


                if (range == -1) {
                    continue;
                }


                totalTasks[range]++;


                if (item.isCompleted()) {
                    completedTasks[range]++;
                }
            }


            int bestRange = -1;

            int worstRange = -1;


            float bestPercentage = -1;

            float worstPercentage = 101;


            for (int i = 0; i < 5; i++) {

                if (totalTasks[i] == 0) {
                    continue;
                }


                float percentage =
                        ((float) completedTasks[i]
                                / totalTasks[i])
                                * 100;


                if (percentage > bestPercentage) {

                    bestPercentage =
                            percentage;

                    bestRange =
                            i;
                }


                if (percentage < worstPercentage) {

                    worstPercentage =
                            percentage;

                    worstRange =
                            i;
                }
            }


            String powerText =
                    getRangeName(
                            bestRange
                    );


            String restText =
                    getRangeName(
                            worstRange
                    );


            if (!isAdded()) {
                return;
            }


            requireActivity().runOnUiThread(() -> {

                power.setText(
                        powerText
                );

                rest.setText(
                        restText
                );
            });
        });
    }


    // =========================================================
    // ROUTINE STATISTICS
    // =========================================================

    private void loadRoutineStatistics() {

        executorService.execute(() -> {

            Calendar calendar =
                    Calendar.getInstance();


            SimpleDateFormat dateFormat =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.US
                    );


            int totalExpected = 0;

            int totalCompleted = 0;


            for (int i = 29; i >= 0; i--) {

                Calendar day =
                        (Calendar) calendar.clone();


                day.add(
                        Calendar.DAY_OF_MONTH,
                        -i
                );


                day.set(
                        Calendar.HOUR_OF_DAY,
                        0
                );

                day.set(
                        Calendar.MINUTE,
                        0
                );

                day.set(
                        Calendar.SECOND,
                        0
                );

                day.set(
                        Calendar.MILLISECOND,
                        0
                );


                long dayMillis =
                        day.getTimeInMillis();


                String date =
                        dateFormat.format(
                                day.getTime()
                        );


                int activeRoutines =
                        routineDao
                                .countActiveRoutinesForDate(
                                        dayMillis
                                );


                totalExpected +=
                        activeRoutines;


                List<RoutineCompletion> completions =
                        routineCompletionDao
                                .getCompletedCompletionsForDateSync(
                                        date
                                );


                totalCompleted +=
                        completions.size();
            }


            int percentage;


            if (totalExpected == 0) {

                percentage = 0;

            } else {

                percentage =
                        Math.round(
                                ((float) totalCompleted
                                        / totalExpected)
                                        * 100
                        );
            }


            if (!isAdded()) {
                return;
            }


            requireActivity().runOnUiThread(() -> {

                percentageRoutine.setText(
                        convertToPersianDigits(
                                String.valueOf(
                                        percentage
                                )
                        ) + "%"
                );
            });
        });
    }


    // =========================================================
    // MONTHLY WEEKLY CHART
    // =========================================================

    private void loadMonthlyChart() {

        executorService.execute(() -> {

            Calendar startCalendar = Calendar.getInstance();

            startCalendar.set(Calendar.DAY_OF_MONTH, 1);
            startCalendar.set(Calendar.HOUR_OF_DAY, 0);
            startCalendar.set(Calendar.MINUTE, 0);
            startCalendar.set(Calendar.SECOND, 0);
            startCalendar.set(Calendar.MILLISECOND, 0);


            Calendar endCalendar = (Calendar) startCalendar.clone();

            endCalendar.set(
                    Calendar.DAY_OF_MONTH,
                    endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            );

            endCalendar.set(Calendar.HOUR_OF_DAY, 23);
            endCalendar.set(Calendar.MINUTE, 59);
            endCalendar.set(Calendar.SECOND, 59);
            endCalendar.set(Calendar.MILLISECOND, 999);


            SimpleDateFormat formatter =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.US
                    );


            String startDate =
                    formatter.format(startCalendar.getTime());

            String endDate =
                    formatter.format(endCalendar.getTime());


            List<ScheduleAnalyticsItem> schedules =
                    scheduleDao.getSchedulesForAnalytics(
                            startDate,
                            endDate
                    );


            int[] totalTasks = new int[4];

            int[] completedTasks = new int[4];


            for (ScheduleAnalyticsItem item : schedules) {

                try {

                    Date taskDate =
                            formatter.parse(item.getDate());

                    if (taskDate == null) {
                        continue;
                    }


                    Calendar taskCalendar =
                            Calendar.getInstance();

                    taskCalendar.setTime(taskDate);


                    int dayOfMonth =
                            taskCalendar.get(Calendar.DAY_OF_MONTH);


                    int week;


                    if (dayOfMonth <= 7) {

                        week = 0;

                    } else if (dayOfMonth <= 14) {

                        week = 1;

                    } else if (dayOfMonth <= 21) {

                        week = 2;

                    } else {

                        week = 3;
                    }


                    totalTasks[week]++;


                    if (item.isCompleted()) {
                        completedTasks[week]++;
                    }


                } catch (Exception e) {

                    Log.e(
                            "MONTHLY_CHART",
                            "Error processing task",
                            e
                    );
                }
            }


            for (int i = 0; i < 4; i++) {

                Log.d(
                        "MONTHLY_CHART",
                        "Week " + (i + 1) +
                                " -> total: " + totalTasks[i] +
                                " completed: " + completedTasks[i]
                );
            }


            requireActivity().runOnUiThread(() -> {

                setupBarChart(totalTasks);
            });
        });
    }
    // =========================================================
    // SETUP BAR CHART
    // =========================================================

    private void setupBarChart(int[] weeklyTasks) {

        if (barChart == null) {
            return;
        }


        List<BarEntry> entries =
                new ArrayList<>();


        for (int i = 0; i < 4; i++) {

            entries.add(
                    new BarEntry(
                            i,
                            weeklyTasks[i]
                    )
            );
        }


        BarDataSet dataSet =
                new BarDataSet(
                        entries,
                        "تعداد کارها"
                );


        dataSet.setDrawValues(true);

        dataSet.setValueTextSize(11f);


        BarData barData =
                new BarData(dataSet);


        barData.setBarWidth(0.55f);


        barChart.setData(barData);


        // =====================================================
        // GENERAL
        // =====================================================

        barChart.getDescription()
                .setEnabled(false);

        barChart.getLegend()
                .setEnabled(false);

        barChart.setFitBars(true);

        barChart.setDrawGridBackground(false);

        barChart.setExtraBottomOffset(8f);


        // =====================================================
        // X AXIS
        // =====================================================

        XAxis xAxis =
                barChart.getXAxis();


        xAxis.setPosition(
                XAxis.XAxisPosition.BOTTOM
        );

        xAxis.setDrawGridLines(false);

        xAxis.setGranularity(1f);

        xAxis.setLabelCount(4);


        xAxis.setValueFormatter(
                new IndexAxisValueFormatter(
                        new String[]{
                                "هفته ۱",
                                "هفته ۲",
                                "هفته ۳",
                                "هفته ۴"
                        }
                )
        );


        // =====================================================
        // LEFT Y AXIS
        // =====================================================

        YAxis leftAxis =
                barChart.getAxisLeft();


        leftAxis.setAxisMinimum(0f);


        int maxTasks = 0;

        for (int taskCount : weeklyTasks) {

            if (taskCount > maxTasks) {
                maxTasks = taskCount;
            }
        }


        // حداقل ارتفاع محور
        // تا وقتی تعداد کارها کم است نمودار خوانا بماند
        leftAxis.setAxisMaximum(
                Math.max(5, maxTasks + 1)
        );


        leftAxis.setGranularity(1f);

        leftAxis.setLabelCount(6, true);

        leftAxis.setDrawGridLines(true);


        // =====================================================
        // RIGHT Y AXIS
        // =====================================================

        barChart.getAxisRight()
                .setEnabled(false);


        // =====================================================
        // ANIMATION
        // =====================================================

        barChart.animateY(700);

        barChart.invalidate();
    }


    // =========================================================
    // CALCULATE PERCENTAGE
    // =========================================================

    private int calculatePercentage(
            int completed,
            int total) {

        if (total == 0) {
            return 0;
        }


        return Math.round(
                ((float) completed / total) * 100
        );
    }


    // =========================================================
    // GET HOUR FROM TIME
    // =========================================================

    private int getHourFromTime(
            String time) {

        if (time == null ||
                time.isEmpty()) {

            return -1;
        }


        try {

            SimpleDateFormat format =
                    new SimpleDateFormat(
                            "HH:mm",
                            Locale.US
                    );


            Date date =
                    format.parse(
                            time
                    );


            if (date == null) {
                return -1;
            }


            Calendar calendar =
                    Calendar.getInstance();


            calendar.setTime(
                    date
            );


            return calendar.get(
                    Calendar.HOUR_OF_DAY
            );


        } catch (ParseException e) {

            return -1;
        }
    }


    // =========================================================
    // GET TIME RANGE
    // =========================================================

    private int getTimeRange(
            int hour) {

        if (hour >= 0 &&
                hour < 7) {

            return 0;
        }


        if (hour >= 7 &&
                hour < 12) {

            return 1;
        }


        if (hour >= 12 &&
                hour < 17) {

            return 2;
        }


        if (hour >= 17 &&
                hour < 22) {

            return 3;
        }


        if (hour >= 22 &&
                hour < 24) {

            return 4;
        }


        return -1;
    }


    // =========================================================
    // GET RANGE NAME
    // =========================================================

    private String getRangeName(
            int range) {

        switch (range) {

            case 0:
                return "نیمه‌شب تا صبح";

            case 1:
                return "صبح";

            case 2:
                return "ظهر تا عصر";

            case 3:
                return "عصر تا شب";

            case 4:
                return "شب";

            default:
                return "--";
        }
    }


    // =========================================================
    // PERSIAN DIGITS
    // =========================================================

    private String convertToPersianDigits(
            String value) {

        return value
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


    // =========================================================
    // DESTROY
    // =========================================================

    @Override
    public void onDestroy() {

        executorService.shutdown();

        super.onDestroy();
    }
}