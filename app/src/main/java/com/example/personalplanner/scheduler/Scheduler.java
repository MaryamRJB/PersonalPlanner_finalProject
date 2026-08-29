package com.example.personalplanner.scheduler;

import android.content.Context;
import android.util.Log;

import com.example.personalplanner.ai.FeatureBuilder;
import com.example.personalplanner.ai.OnnxModel;
import com.example.personalplanner.database.entity.Schedule;
import com.example.personalplanner.database.entity.Task;
import com.example.personalplanner.database.repository.ScheduleRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Scheduler {

    private static final String TAG = "SCHEDULER";

    private final ScheduleRepository scheduleRepository;
    private final OnnxModel model;

    public Scheduler(Context context) throws Exception {

        scheduleRepository =
                new ScheduleRepository(context);

        model =
                new OnnxModel(context);
    }

    /**
     * برنامه‌ریزی Taskها برای یک روز
     */
    public List<Schedule> generateSchedule(
            List<Task> tasks,
            String date
    ) throws Exception {

        List<ScheduleCandidate> candidates =
                new ArrayList<>();

        Calendar calendar =
                Calendar.getInstance();

        Date targetDate =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.US
                ).parse(date);

        if (targetDate == null) {
            throw new Exception("Invalid date");
        }

        calendar.setTime(targetDate);

        // Scheduleهای قبلی همین روز
        List<Schedule> existingSchedules =
                scheduleRepository.getSchedulesByDate(date);

        int dayOfWeek =
                calendar.get(Calendar.DAY_OF_WEEK) - 1;

        boolean isWeekend =
                dayOfWeek == Calendar.SATURDAY - 1 ||
                        dayOfWeek == Calendar.SUNDAY - 1;

        /*
         * ساعات قابل برنامه‌ریزی
         *
         * 06:00 تا 23:00
         */
        int startHour = 6;
        int endHour = 23;

        for (Task task : tasks) {

            /*
             * اگر Task قبلاً کامل شده،
             * دوباره برنامه‌ریزی نشود.
             */
            if (task.isCompleted()) {
                continue;
            }

            int taskDuration =
                    task.getDuration();

            /*
             * Candidate Slotها
             */
            for (int hour = startHour;
                 hour <= endHour;
                 hour++) {

                int slotDuration =
                        taskDuration;

                /*
                 * بررسی اینکه Slot از محدوده روز خارج نشود.
                 */
                if (hour +
                        taskDuration / 60.0
                        > endHour) {

                    continue;
                }

                /*
                 * در این مرحله preferredStartHour
                 * را از ساعت 19 به عنوان مقدار پیش‌فرض
                 * استفاده می‌کنیم.
                 *
                 * بعداً می‌توانیم این مقدار را
                 * مستقیماً از UserPreference بگیریم.
                 */
                int preferredStartHour =
                        19;

                float[] features =
                        FeatureBuilder.buildFeatures(
                                task,
                                hour,
                                slotDuration,
                                preferredStartHour,
                                dayOfWeek,
                                isWeekend
                        );

                float score =
                        model.predict(features);

                candidates.add(
                        new ScheduleCandidate(
                                task,
                                date,
                                hour,
                                taskDuration,
                                score
                        )
                );
            }
        }

        /*
         * بهترین Candidateها اول قرار می‌گیرند.
         */
        Collections.sort(
                candidates,
                Comparator.comparingDouble(
                        ScheduleCandidate::getScore
                ).reversed()
        );

        List<Schedule> result =
                new ArrayList<>();

        /*
         * ساعت‌های اشغال‌شده
         */
        List<int[]> occupiedSlots =
                new ArrayList<>();

        /*
         * برای هر Task فقط یک Slot انتخاب می‌کنیم.
         */
        List<Integer> scheduledTaskIds =
                new ArrayList<>();

        for (ScheduleCandidate candidate :
                candidates) {

            int taskId =
                    candidate.getTask().getId();

            /*
             * Task قبلاً Schedule شده؟
             */
            if (scheduledTaskIds.contains(taskId)) {
                continue;
            }

            // اگر این Task قبلاً در Database برای این روز Schedule شده
            boolean alreadyScheduled = false;

            for (Schedule existing : existingSchedules) {
                if (existing.getTaskId() == taskId) {
                    alreadyScheduled = true;
                    break;
                }
            }

            if (alreadyScheduled) {
                continue;
            }

            int start =
                    candidate.getStartHour();

            int duration =
                    candidate.getDuration();

            int end =
                    start +
                            (int)
                                    Math.ceil(
                                            duration / 60.0
                                    );

            // بررسی تداخل با Scheduleهای قبلی Database
            if (hasConflictWithExistingSchedules(
                    start,
                    duration,
                    existingSchedules
            )) {
                continue;
            }

            /*
             * بررسی Conflict
             */
            boolean conflict = false;

            for (int[] occupied :
                    occupiedSlots) {

                int occupiedStart =
                        occupied[0];

                int occupiedEnd =
                        occupied[1];

                if (start < occupiedEnd &&
                        end > occupiedStart) {

                    conflict = true;
                    break;
                }
            }

            if (conflict) {
                continue;
            }

            /*
             * Schedule جدید
             */
            String startTime =
                    String.format(
                            Locale.US,
                            "%02d:00",
                            start
                    );

            String endTime =
                    String.format(
                            Locale.US,
                            "%02d:00",
                            end
                    );

            Schedule schedule =
                    new Schedule(
                            taskId,
                            date,
                            startTime,
                            endTime
                    );

            result.add(schedule);

            occupiedSlots.add(
                    new int[]{
                            start,
                            end
                    }
            );

            scheduledTaskIds.add(taskId);

            Log.d(
                    TAG,
                    "SELECTED: Task="
                            + candidate.getTask().getId()
            );
        }

        return result;
    }

    /**
     * ذخیره Scheduleهای تولیدشده در Room
     */
    public void saveSchedule(
            List<Schedule> schedules
    ) {

        for (Schedule schedule : schedules) {

            scheduleRepository.insertAndWait(
                    schedule
            );
        }

        Log.d(
                TAG,
                "SCHEDULE SAVED: "
                        + schedules.size()
                        + " item(s)"
        );
    }

    public void close() {

        model.close();
    }

    /*
     * Candidate داخلی Scheduler
     */
    private static class ScheduleCandidate {

        private final Task task;
        private final String date;
        private final int startHour;
        private final int duration;
        private final float score;

        ScheduleCandidate(
                Task task,
                String date,
                int startHour,
                int duration,
                float score
        ) {

            this.task = task;
            this.date = date;
            this.startHour = startHour;
            this.duration = duration;
            this.score = score;
        }

        public Task getTask() {
            return task;
        }

        public String getDate() {
            return date;
        }

        public int getStartHour() {
            return startHour;
        }

        public int getDuration() {
            return duration;
        }

        public float getScore() {
            return score;
        }

    }

    private boolean hasConflictWithExistingSchedules(
            int startHour,
            int duration,
            List<Schedule> schedules
    ) {

        double newStart = startHour;

        double newEnd =
                startHour +
                        duration / 60.0;

        for (Schedule schedule : schedules) {

            double existingStart =
                    parseTime(schedule.getStartTime());

            double existingEnd =
                    parseTime(schedule.getEndTime());

            if (newStart < existingEnd &&
                    newEnd > existingStart) {

                return true;
            }
        }

        return false;
    }

    private double parseTime(String time) {

        if (time == null || !time.contains(":")) {
            return 0;
        }

        String[] parts = time.split(":");

        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);

        return hours + minutes / 60.0;
    }

}