package com.example.personalplanner.algorithm;

import android.content.Context;

import com.example.personalplanner.database.entity.Schedule;
import com.example.personalplanner.database.entity.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SchedulingEngine {

    private final ModelPredictor modelPredictor;

    public SchedulingEngine(Context context) throws Exception {
        modelPredictor = new ModelPredictor(context);
    }

    /**
     * برنامه‌ریزی چند Task برای یک تاریخ مشخص
     */
    public List<Schedule> createSchedule(
            List<Task> tasks,
            String date,
            int dayOfWeek,
            boolean isWeekend
    ) throws Exception {

        List<Schedule> result = new ArrayList<>();

        if (tasks == null || tasks.isEmpty()) {
            return result;
        }

        // Taskهای مهم‌تر و نزدیک‌تر به Deadline اول بررسی می‌شوند
        List<Task> sortedTasks =
                new ArrayList<>(tasks);

        Collections.sort(
                sortedTasks,
                new Comparator<Task>() {
                    @Override
                    public int compare(
                            Task t1,
                            Task t2
                    ) {

                        if (t1.getPriority()
                                != t2.getPriority()) {

                            return Integer.compare(
                                    t2.getPriority(),
                                    t1.getPriority()
                            );
                        }

                        return Long.compare(
                                t1.getDeadline(),
                                t2.getDeadline()
                        );
                    }
                }
        );

        // ساعت‌های قابل بررسی
        List<Integer> candidateHours =
                new ArrayList<>();

        for (int hour = 6; hour <= 22; hour++) {
            candidateHours.add(hour);
        }

        for (Task task : sortedTasks) {

            Candidate bestCandidate = null;

            for (int hour : candidateHours) {

                int duration =
                        task.getDuration();

                if (duration <= 0) {
                    continue;
                }

                // بررسی اینکه Slot قبلاً استفاده نشده باشد
                if (hasConflict(
                        hour,
                        duration,
                        result
                )) {
                    continue;
                }

                // بررسی Deadline
                if (!fitsDeadline(
                        task,
                        hour
                )) {
                    continue;
                }

                float[] features =
                        buildFeatures(
                                task,
                                hour,
                                duration,
                                dayOfWeek,
                                isWeekend
                        );

                float score =
                        modelPredictor.predict(
                                features
                        );

                if (bestCandidate == null ||
                        score >
                                bestCandidate.score) {

                    bestCandidate =
                            new Candidate(
                                    hour,
                                    duration,
                                    score
                            );
                }
            }

            if (bestCandidate != null) {

                Schedule schedule =
                        new Schedule(
                                task.getId(),
                                date,
                                formatTime(
                                        bestCandidate.startHour
                                ),
                                formatTime(
                                        bestCandidate.startHour
                                                + bestCandidate.duration
                                                / 60.0
                                )
                        );

                result.add(schedule);
            }
        }

        return result;
    }

    /**
     * ساخت 14 Feature دقیقاً مطابق مدل
     */
    private float[] buildFeatures(
            Task task,
            int slotStartHour,
            int slotDuration,
            int dayOfWeek,
            boolean isWeekend
    ) {

        float taskPriority =
                task.getPriority();

        float taskDuration =
                task.getDuration();

        float deadlineHours =
                calculateDeadlineHours(
                        task.getDeadline()
                );

        /*
         * در نسخه فعلی دیتابیس،
         * preferredStartHour مستقیماً داخل Task نیست.
         *
         * فعلاً مقدار پیش‌فرض 18 در نظر گرفته شده.
         * بعداً این مقدار را از UserPreference
         * یا تنظیمات کاربر می‌گیریم.
         */
        float preferredStartHour = 18f;

        float timeDifference =
                Math.abs(
                        slotStartHour -
                                preferredStartHour
                );

        timeDifference =
                Math.min(
                        timeDifference,
                        24 - timeDifference
                );

        float preferredTimeMatch =
                (float) Math.exp(
                        -timeDifference / 4.0
                );

        float deadlinePressure =
                Math.max(
                        0f,
                        Math.min(
                                1f,
                                1f -
                                        deadlineHours /
                                                168f
                        )
                );

        float durationEfficiency =
                Math.max(
                        0f,
                        Math.min(
                                1f,
                                taskDuration /
                                        slotDuration
                        )
                );

        float repeatedTask =
                task.isRepeated() ? 1f : 0f;

        float reminderExists =
                task.getReminderTime() > 0
                        ? 1f
                        : 0f;

        float category =
                task.getCategoryId();

        return new float[]{

                // 1
                taskPriority,

                // 2
                taskDuration,

                // 3
                deadlineHours,

                // 4
                slotStartHour,

                // 5
                slotDuration,

                // 6
                preferredStartHour,

                // 7
                preferredTimeMatch,

                // 8
                deadlinePressure,

                // 9
                durationEfficiency,

                // 10
                dayOfWeek,

                // 11
                isWeekend ? 1f : 0f,

                // 12
                repeatedTask,

                // 13
                reminderExists,

                // 14
                category
        };
    }

    /**
     * محاسبه زمان باقی‌مانده تا Deadline
     */
    private float calculateDeadlineHours(
            long deadline
    ) {

        long now =
                System.currentTimeMillis();

        long difference =
                deadline - now;

        return Math.max(
                0f,
                difference / 3600000f
        );
    }

    /**
     * بررسی Deadline
     */
    private boolean fitsDeadline(
            Task task,
            int startHour
    ) {

        long now =
                System.currentTimeMillis();

        long remaining =
                task.getDeadline() - now;

        double durationMillis =
                task.getDuration() *
                        60.0 *
                        1000.0;

        return remaining >= durationMillis;
    }

    /**
     * جلوگیری از تداخل Scheduleها
     */
    private boolean hasConflict(
            int startHour,
            int duration,
            List<Schedule> schedules
    ) {

        double newStart =
                startHour;

        double newEnd =
                startHour +
                        duration / 60.0;

        for (Schedule schedule : schedules) {

            double existingStart =
                    parseTime(
                            schedule.getStartTime()
                    );

            double existingEnd =
                    parseTime(
                            schedule.getEndTime()
                    );

            if (newStart < existingEnd &&
                    newEnd > existingStart) {

                return true;
            }
        }

        return false;
    }

    /**
     * تبدیل 19.5 به 19:30
     */
    private String formatTime(
            double hour
    ) {

        int hours =
                (int) hour;

        int minutes =
                (int) Math.round(
                        (hour - hours) * 60
                );

        if (minutes == 60) {
            hours++;
            minutes = 0;
        }

        return String.format(
                "%02d:%02d",
                hours,
                minutes
        );
    }

    /**
     * تبدیل 19:30 به 19.5
     */
    private double parseTime(
            String time
    ) {

        if (time == null ||
                !time.contains(":")) {

            return 0;
        }

        String[] parts =
                time.split(":");

        int hours =
                Integer.parseInt(parts[0]);

        int minutes =
                Integer.parseInt(parts[1]);

        return hours +
                minutes / 60.0;
    }

    /**
     * Candidate موقت برای انتخاب بهترین Slot
     */
    private static class Candidate {

        int startHour;
        int duration;
        float score;

        Candidate(
                int startHour,
                int duration,
                float score
        ) {

            this.startHour =
                    startHour;

            this.duration =
                    duration;

            this.score =
                    score;
        }
    }

    /**
     * آزاد کردن منابع مدل
     */
    public void close() throws Exception {
        modelPredictor.close();
    }
}