package com.example.personalplanner.ai;

import com.example.personalplanner.database.entity.Task;

public class FeatureBuilder {

    private FeatureBuilder() {
    }

    public static float[] buildFeatures(
            Task task,
            int slotStartHour,
            int slotDuration,
            int preferredStartHour,
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

        float preferredTimeMatch =
                calculatePreferredTimeMatch(
                        slotStartHour,
                        preferredStartHour
                );

        float deadlinePressure =
                calculateDeadlinePressure(
                        deadlineHours
                );

        float durationEfficiency =
                calculateDurationEfficiency(
                        taskDuration,
                        slotDuration
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

                taskPriority,

                taskDuration,

                deadlineHours,

                slotStartHour,

                slotDuration,

                preferredStartHour,

                preferredTimeMatch,

                deadlinePressure,

                durationEfficiency,

                dayOfWeek,

                isWeekend ? 1f : 0f,

                repeatedTask,

                reminderExists,

                category
        };
    }

    private static float calculateDeadlineHours(
            long deadline
    ) {

        long now =
                System.currentTimeMillis();

        long difference =
                deadline - now;

        return Math.max(
                0,
                difference / 3600000f
        );
    }

    private static float calculatePreferredTimeMatch(
            int slotStartHour,
            int preferredStartHour
    ) {

        float difference =
                Math.abs(
                        slotStartHour -
                                preferredStartHour
                );

        difference =
                Math.min(
                        difference,
                        24 - difference
                );

        return (float) Math.exp(
                -difference / 4.0
        );
    }

    private static float calculateDeadlinePressure(
            float deadlineHours
    ) {

        return Math.max(
                0,
                Math.min(
                        1,
                        1 -
                                deadlineHours /
                                        168f
                )
        );
    }

    private static float calculateDurationEfficiency(
            float taskDuration,
            float slotDuration
    ) {

        if (slotDuration <= 0) {
            return 0;
        }

        return Math.max(
                0,
                Math.min(
                        1,
                        taskDuration /
                                slotDuration
                )
        );
    }
}