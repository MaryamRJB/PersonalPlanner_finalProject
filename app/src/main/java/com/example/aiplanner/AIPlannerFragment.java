package com.example.aiplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.personalplanner.database.dao.ScheduleDao;
import com.example.personalplanner.database.dao.TaskDao;
import com.example.personalplanner.database.entity.Schedule;
import com.example.personalplanner.database.entity.Task;
import com.example.personalplanner.database.room.AppDatabase;
import com.example.personalplanner.scheduler.Scheduler;

import java.util.List;

public class AIPlannerFragment extends Fragment {

    private ImageView aiLogo;
    private Animation aiAnimation;

    private TextView aiStateText;
    private Button btnGenerateSchedule;


    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_a_i_planner,
                container,
                false
        );
    }


    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        aiLogo =
                view.findViewById(
                        R.id.aiLogo
                );

        aiStateText =
                view.findViewById(
                        R.id.aiStateText
                );

        btnGenerateSchedule =
                view.findViewById(
                        R.id.btnGenerateSchedule
                );


        // =========================
        // Animation
        // =========================

        aiAnimation =
                AnimationUtils.loadAnimation(
                        requireContext(),
                        R.anim.rotate
                );

        aiLogo.startAnimation(
                aiAnimation
        );


        // =========================
        // Generate Schedule
        // =========================

        btnGenerateSchedule.setOnClickListener(
                v -> generateSchedule()
        );
    }


    private void generateSchedule() {

        btnGenerateSchedule.setEnabled(false);

        aiStateText.setText(
                "در حال تحلیل کارهای شما..."
        );


        new Thread(() -> {

            Scheduler scheduler = null;

            try {

                // =========================================
                // Database
                // =========================================

                AppDatabase db =
                        AppDatabase.getInstance(
                                requireContext()
                        );

                TaskDao taskDao =
                        db.taskDao();


                // =========================================
                // دریافت Taskهای انجام نشده
                // =========================================

                List<Task> tasks =
                        taskDao.getPendingTasksSync();


                if (
                        tasks == null
                                ||
                                tasks.isEmpty()
                ) {

                    requireActivity()
                            .runOnUiThread(() -> {

                                aiStateText.setText(
                                        "کاری برای برنامه‌ریزی وجود ندارد."
                                );

                                btnGenerateSchedule
                                        .setEnabled(true);
                            });

                    return;
                }


                // =========================================
                // Scheduler
                // =========================================

                AppDatabase database =
                        AppDatabase.getInstance(
                                requireContext()
                        );

                ScheduleDao scheduleDao =
                        database.scheduleDao();

                scheduler =
                        new Scheduler(scheduleDao);


                // =========================================
                // اجرای AI
                //
                // Scheduler خودش:
                //
                // امروز
                //    ↓
                // deadline
                //
                // را بررسی می‌کند.
                // =========================================

                List<Schedule> schedules =
                        scheduler
                                .generateScheduleForTasks(
                                        tasks
                                );


                // =========================================
                // ذخیره برنامه‌ها
                // =========================================

                scheduler.saveSchedule(
                        schedules
                );


                // =========================================
                // نتیجه
                // =========================================

                int scheduleCount =
                        schedules != null
                                ? schedules.size()
                                : 0;


                requireActivity()
                        .runOnUiThread(() -> {

                            if (scheduleCount > 0) {

                                aiStateText.setText(
                                        "برنامه شما آماده شد ✓\n"
                                                + toPersianDigits(
                                                String.valueOf(
                                                        scheduleCount
                                                )
                                        )
                                                + " بخش برنامه‌ریزی شد."
                                );

                            } else {

                                aiStateText.setText(
                                        "زمان مناسبی برای برنامه‌ریزی پیدا نشد."
                                );
                            }


                            btnGenerateSchedule
                                    .setEnabled(true);
                        });


            } catch (Exception e) {

                e.printStackTrace();


                requireActivity()
                        .runOnUiThread(() -> {

                            aiStateText.setText(
                                    "خطا در ساخت برنامه"
                            );

                            btnGenerateSchedule
                                    .setEnabled(true);
                        });


            } finally {

                if (scheduler != null) {

                    scheduler.close();
                }
            }

        }).start();
    }


    // =========================================
    // فارسی کردن اعداد
    // =========================================

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
                .replace("9", "۹");
    }


    @Override
    public void onDestroyView() {

        if (aiLogo != null) {

            aiLogo.clearAnimation();
        }

        super.onDestroyView();
    }
}