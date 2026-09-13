package com.example.aiplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalplanner.database.dao.RoutineCompletionDao;
import com.example.personalplanner.database.entity.Routine;
import com.example.personalplanner.database.entity.RoutineCompletion;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RoutineAdapter
        extends RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder> {

    private final List<Routine> routines = new ArrayList<>();

    private RoutineCompletionDao completionDao;

    private String selectedDate;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();


    // ==========================================
    // Constructor
    // ==========================================

    public RoutineAdapter(
            RoutineCompletionDao completionDao) {

        this.completionDao = completionDao;
    }


    // ==========================================
    // Set selected date
    // ==========================================

    public void setSelectedDate(String date) {

        this.selectedDate = date;

        notifyDataSetChanged();
    }


    // ==========================================
    // Set routines
    // ==========================================

    public void setRoutines(List<Routine> newRoutines) {

        routines.clear();

        if (newRoutines != null) {
            routines.addAll(newRoutines);
        }

        notifyDataSetChanged();
    }


    // ==========================================
    // Create ViewHolder
    // ==========================================

    @NonNull
    @Override
    public RoutineViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.routine_item,
                                parent,
                                false
                        );

        return new RoutineViewHolder(view);
    }


    // ==========================================
    // Bind
    // ==========================================

    @Override
    public void onBindViewHolder(
            @NonNull RoutineViewHolder holder,
            int position) {

        Routine routine =
                routines.get(position);

        holder.title.setText(
                routine.getTitle()
        );

        holder.time.setText(
                toPersianDigits(
                        routine.getStartTime()
                                + " - "
                                + routine.getEndTime()
                )
        );


        // جلوگیری از اجرای listener قبلی
        holder.checkBox.setOnCheckedChangeListener(null);

        holder.checkBox.setChecked(false);


        if (selectedDate == null ||
                completionDao == null) {

            return;
        }


        // ==========================================
        // خواندن وضعیت Checkbox
        // ==========================================

        executor.execute(() -> {

            RoutineCompletion completion =
                    completionDao.getCompletion(
                            routine.getId(),
                            selectedDate
                    );

            boolean completed =
                    completion != null &&
                            completion.isCompleted();


            holder.itemView.post(() -> {

                holder.checkBox.setOnCheckedChangeListener(null);

                holder.checkBox.setChecked(completed);

                holder.checkBox.setOnCheckedChangeListener(
                        (buttonView, isChecked) -> {

                            saveCompletion(
                                    routine.getId(),
                                    selectedDate,
                                    isChecked
                            );
                        }
                );
            });
        });
    }


    // ==========================================
    // Save completion
    // ==========================================

    private void saveCompletion(
            int routineId,
            String date,
            boolean completed) {

        executor.execute(() -> {

            RoutineCompletion existing =
                    completionDao.getCompletion(
                            routineId,
                            date
                    );

            if (existing == null) {

                RoutineCompletion completion =
                        new RoutineCompletion(
                                routineId,
                                date,
                                completed
                        );

                completionDao.insert(completion);

            } else {

                completionDao.updateCompletion(
                        routineId,
                        date,
                        completed
                );
            }
        });
    }


    // ==========================================
    // Count
    // ==========================================

    @Override
    public int getItemCount() {

        return routines.size();
    }


    // ==========================================
    // Persian digits
    // ==========================================

    private String toPersianDigits(String text) {

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


    // ==========================================
    // ViewHolder
    // ==========================================

    static class RoutineViewHolder
            extends RecyclerView.ViewHolder {

        TextView title;
        TextView time;
        CheckBox checkBox;


        public RoutineViewHolder(
                @NonNull View itemView) {

            super(itemView);

            title =
                    itemView.findViewById(
                            R.id.title
                    );

            time =
                    itemView.findViewById(
                            R.id.time
                    );

            checkBox =
                    itemView.findViewById(
                            R.id.checkBox
                    );
        }
    }


    // ==========================================
    // Shutdown
    // ==========================================

    public void shutdown() {

        executor.shutdown();
    }
}