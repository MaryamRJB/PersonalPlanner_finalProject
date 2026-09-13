package com.example.aiplanner;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.personalplanner.database.entity.Routine;
import com.example.personalplanner.database.entity.RoutineCompletion;
import com.example.personalplanner.database.relation.ScheduleWithTask;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskBentoAdapter {

    private final ViewGroup leftColumn;
    private final ViewGroup rightColumn;

    private final List<CardItem> items =
            new ArrayList<>();

    private final ExecutorService databaseExecutor =
            Executors.newSingleThreadExecutor();

    private com.example.personalplanner.database.dao.RoutineCompletionDao routineCompletionDao;

    private String currentDate;

    private final int[] defaultColors = {
            Color.parseColor("#F5DEB3"),
            Color.parseColor("#A7B4FA"),
            Color.parseColor("#90D4DE"),
            Color.parseColor("#FA9286"),
            Color.parseColor("#C9B6E4"),
            Color.parseColor("#A8D5BA"),
            Color.parseColor("#F3C4A3"),
            Color.parseColor("#B8C9E8")
    };

    public TaskBentoAdapter(
            ViewGroup leftColumn,
            ViewGroup rightColumn) {

        this.leftColumn = leftColumn;
        this.rightColumn = rightColumn;
    }

    public void setRoutineCompletionDao(
            com.example.personalplanner.database.dao.RoutineCompletionDao dao) {

        this.routineCompletionDao = dao;
    }

    public void setDate(String date) {
        this.currentDate = date;
    }

    public void setSchedules(
            List<ScheduleWithTask> schedules) {

        items.clear();

        if (schedules != null) {

            for (ScheduleWithTask schedule :
                    schedules) {

                if (schedule != null &&
                        schedule.task != null &&
                        schedule.schedule != null) {

                    items.add(
                            CardItem.forTask(schedule)
                    );
                }
            }
        }

        refreshCards();
    }

    public void addRoutines(
            List<Routine> routines) {

        if (routines == null) {
            return;
        }

        for (Routine routine : routines) {

            if (routine != null) {

                items.add(
                        CardItem.forRoutine(routine)
                );
            }
        }

        refreshCards();
    }

    private void refreshCards() {

        leftColumn.removeAllViews();
        rightColumn.removeAllViews();

        for (int i = 0;
             i < items.size();
             i++) {

            CardItem item =
                    items.get(i);

            int position = i + 1;

            boolean isRightColumn =
                    i % 2 == 0;

            View card;

            if (item.type ==
                    CardItem.TYPE_TASK) {

                card =
                        createTaskCard(
                                isRightColumn
                                        ? rightColumn
                                        : leftColumn,
                                item.schedule,
                                position
                        );

            } else {

                card =
                        createRoutineCard(
                                isRightColumn
                                        ? rightColumn
                                        : leftColumn,
                                item.routine,
                                position
                        );
            }

            if (isRightColumn) {
                rightColumn.addView(card);
            } else {
                leftColumn.addView(card);
            }
        }
    }

    private View createTaskCard(
            ViewGroup parent,
            ScheduleWithTask item,
            int position) {

        View view =
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.task_bento_item,
                                parent,
                                false
                        );

        TextView title =
                view.findViewById(
                        R.id.title
                );

        TextView startTime =
                view.findViewById(
                        R.id.startTime
                );

        CircularProgressIndicator progressBar =
                view.findViewById(
                        R.id.progressBar
                );

        TextView progressPercent =
                view.findViewById(
                        R.id.progressPercent
                );

        title.setText(
                item.task.getTitle()
        );

        String start =
                toPersianDigits(
                        item.schedule.getStartTime()
                );

        String end =
                toPersianDigits(
                        item.schedule.getEndTime()
                );

        startTime.setText(
                start + "  —  " + end
        );

        int progress =
                Math.max(
                        0,
                        Math.min(
                                100,
                                item.task.getProgress()
                        )
                );

        progressBar.setProgress(progress);

        progressPercent.setText(
                toPersianDigits(
                        String.valueOf(progress)
                )
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                getCategoryColor(
                        item,
                        position
                )
        );

        background.setCornerRadius(
                dp(view, 22)
        );

        view.setBackground(background);

        setCardHeight(
                view,
                position
        );

        view.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            v.getContext(),
                            TaskDetailActivity.class
                    );

            intent.putExtra(
                    TaskDetailActivity.EXTRA_TASK_ID,
                    item.task.getId()
            );

            v.getContext()
                    .startActivity(intent);
        });

        return view;
    }

    private View createRoutineCard(
            ViewGroup parent,
            Routine routine,
            int position) {

        View view =
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.task_bento_item,
                                parent,
                                false
                        );

        TextView title =
                view.findViewById(
                        R.id.title
                );

        TextView startTime =
                view.findViewById(
                        R.id.startTime
                );

        CircularProgressIndicator progressBar =
                view.findViewById(
                        R.id.progressBar
                );

        TextView progressPercent =
                view.findViewById(
                        R.id.progressPercent
                );

        title.setText(
                routine.getTitle()
        );

        String start =
                toPersianDigits(
                        routine.getStartTime()
                );

        String end =
                toPersianDigits(
                        routine.getEndTime()
                );

        startTime.setText(
                start + "  —  " + end
        );

        boolean completed =
                isRoutineCompleted(
                        routine
                );

        if (completed) {

            progressBar.setProgress(100);

            progressPercent.setText(
                    "✓"
            );

        } else {

            progressBar.setProgress(0);

            progressPercent.setText(
                    "۰"
            );
        }

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                defaultColors[
                        (position - 1)
                                % defaultColors.length
                        ]
        );

        background.setCornerRadius(
                dp(view, 22)
        );

        view.setBackground(background);

        setCardHeight(
                view,
                position
        );

        /*
         * کلیک روی Routine:
         * وضعیت انجام/انجام‌نشده تغییر می‌کند.
         */
        view.setOnClickListener(v -> {

            if (routineCompletionDao == null ||
                    currentDate == null) {
                return;
            }

            databaseExecutor.execute(() -> {

                RoutineCompletion completion =
                        routineCompletionDao
                                .getCompletion(
                                        routine.getId(),
                                        currentDate
                                );

                if (completion == null) {

                    completion =
                            new RoutineCompletion(
                                    routine.getId(),
                                    currentDate,
                                    true
                            );

                    routineCompletionDao
                            .insert(completion);

                } else {

                    completion.setCompleted(
                            !completion.isCompleted()
                    );

                    routineCompletionDao
                            .update(completion);
                }

                android.os.Handler handler =
                        new android.os.Handler(
                                android.os.Looper.getMainLooper()
                        );

                handler.post(
                        this::refreshCards
                );
            });
        });

        return view;
    }

    private boolean isRoutineCompleted(
            Routine routine) {

        if (routineCompletionDao == null ||
                currentDate == null) {

            return false;
        }

        try {

            RoutineCompletion completion =
                    routineCompletionDao
                            .getCompletion(
                                    routine.getId(),
                                    currentDate
                            );

            return completion != null &&
                    completion.isCompleted();

        } catch (Exception e) {

            return false;
        }
    }

    private void setCardHeight(
            View view,
            int position) {

        int heightDp =
                getCardHeight(position);

        ViewGroup.LayoutParams params =
                view.getLayoutParams();

        if (params != null) {

            params.height =
                    dp(view, heightDp);

            view.setLayoutParams(params);
        }
    }

    private int getCardHeight(
            int position) {

        int pattern =
                (position - 1) % 4;

        switch (pattern) {

            case 0:
                return 180;

            case 1:
                return 150;

            case 2:
                return 155;

            case 3:
                return 185;

            default:
                return 150;
        }
    }

    private int getCategoryColor(
            ScheduleWithTask item,
            int position) {

        if (item.category != null) {

            String color =
                    item.category.getColor();

            if (color != null &&
                    !color.trim().isEmpty()) {

                try {

                    return Color.parseColor(
                            color
                    );

                } catch (Exception ignored) {
                }
            }
        }

        return defaultColors[
                (position - 1)
                        % defaultColors.length
                ];
    }

    private String toPersianDigits(
            String text) {

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
                .replace("9", "۹")
                .replace("%", "٪");
    }

    private int dp(
            View view,
            int value) {

        return (int) (
                value *
                        view.getResources()
                                .getDisplayMetrics()
                                .density
        );
    }

    public void shutdown() {
        databaseExecutor.shutdown();
    }

    private static class CardItem {

        static final int TYPE_TASK = 1;
        static final int TYPE_ROUTINE = 2;

        int type;

        ScheduleWithTask schedule;
        Routine routine;

        static CardItem forTask(
                ScheduleWithTask schedule) {

            CardItem item =
                    new CardItem();

            item.type =
                    TYPE_TASK;

            item.schedule =
                    schedule;

            return item;
        }

        static CardItem forRoutine(
                Routine routine) {

            CardItem item =
                    new CardItem();

            item.type =
                    TYPE_ROUTINE;

            item.routine =
                    routine;

            return item;
        }
    }
}