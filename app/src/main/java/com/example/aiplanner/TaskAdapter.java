package com.example.aiplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalplanner.database.relation.ScheduleWithTask;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter
        extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<ScheduleWithTask> schedules =
            new ArrayList<>();


    public void setSchedules(
            List<ScheduleWithTask> schedules) {

        if (schedules == null) {

            this.schedules =
                    new ArrayList<>();

        } else {

            this.schedules =
                    schedules;
        }

        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(
                                R.layout.task_item,
                                parent,
                                false
                        );

        return new TaskViewHolder(
                view
        );
    }


    @Override
    public void onBindViewHolder(
            @NonNull TaskViewHolder holder,
            int position) {

        ScheduleWithTask item =
                schedules.get(position);

        if (item == null ||
                item.task == null ||
                item.schedule == null) {

            return;
        }


        // ==========================================
        // Title
        // ==========================================

        holder.title.setText(
                item.task.getTitle()
        );


        // ==========================================
        // Time Range
        // ==========================================

        String start =
                toPersianDigits(
                        item.schedule.getStartTime()
                );

        String end =
                toPersianDigits(
                        item.schedule.getEndTime()
                );

        holder.startTime.setText(
                start + "  —  " + end
        );


        // ==========================================
        // Progress
        // ==========================================

        int progress =
                item.task.getProgress();

        progress =
                Math.max(
                        0,
                        Math.min(
                                100,
                                progress
                        )
                );

        holder.progressPercent.setText(
                toPersianDigits(
                        progress + "%"
                )
        );

        holder.progressBar.setProgress(
                progress
        );
    }


    @Override
    public int getItemCount() {

        return schedules.size();
    }


    // ==============================================
    // Persian Digits
    // ==============================================

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


    // ==============================================
    // ViewHolder
    // ==============================================

    static class TaskViewHolder
            extends RecyclerView.ViewHolder {

        TextView title;
        TextView startTime;
        TextView progressPercent;

        ProgressBar progressBar;


        public TaskViewHolder(
                @NonNull View itemView) {

            super(itemView);

            title =
                    itemView.findViewById(
                            R.id.title
                    );

            startTime =
                    itemView.findViewById(
                            R.id.startTime
                    );

            progressPercent =
                    itemView.findViewById(
                            R.id.progressPercent
                    );

            progressBar =
                    itemView.findViewById(
                            R.id.progressBar
                    );
        }
    }
}