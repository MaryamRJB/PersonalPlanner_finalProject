package com.example.aiplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalplanner.database.dao.SubTaskDao;
import com.example.personalplanner.database.entity.SubTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SubTaskAdapter
        extends RecyclerView.Adapter<SubTaskAdapter.ViewHolder> {

    private final SubTaskDao subTaskDao;
    private final Runnable onProgressChanged;

    private final List<SubTask> subTasks =
            new ArrayList<>();

    private final ExecutorService databaseExecutor =
            Executors.newSingleThreadExecutor();


    public SubTaskAdapter(
            SubTaskDao subTaskDao,
            Runnable onProgressChanged) {

        this.subTaskDao = subTaskDao;
        this.onProgressChanged = onProgressChanged;
    }


    public void setSubTasks(List<SubTask> list) {

        subTasks.clear();

        if (list != null) {
            subTasks.addAll(list);
        }

        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.subtask_item,
                                parent,
                                false
                        );

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        SubTask subTask = subTasks.get(position);

        // عنوان زیرکار
        holder.title.setText(
                subTask.getTitle()
        );

        // توضیحات زیرکار
        holder.description.setText(
                subTask.getDescription()
        );

        // جلوگیری از اجرای Listener هنگام
        // تنظیم مقدار اولیه CheckBox
        holder.checkBox.setOnCheckedChangeListener(null);

        holder.checkBox.setChecked(
                subTask.isCompleted()
        );

        // Listener مربوط به CheckBox
        holder.checkBox.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    // تغییر وضعیت در مدل
                    subTask.setCompleted(isChecked);

                    // عملیات دیتابیس خارج از Main Thread
                    databaseExecutor.execute(() -> {

                        subTaskDao.updateSubTaskStatus(
                                subTask.getId(),
                                isChecked
                        );

                        // محاسبه مجدد پیشرفت Task
                        if (onProgressChanged != null) {
                            onProgressChanged.run();
                        }
                    });
                }
        );
    }


    @Override
    public int getItemCount() {
        return subTasks.size();
    }


    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView title;
        TextView description;
        CheckBox checkBox;


        ViewHolder(@NonNull View itemView) {
            super(itemView);

            title =
                    itemView.findViewById(
                            R.id.subTaskTitle
                    );

            description =
                    itemView.findViewById(
                            R.id.subTaskDescription
                    );

            checkBox =
                    itemView.findViewById(
                            R.id.subTaskCheckBox
                    );
        }
    }


    /**
     * بستن Executor هنگام خروج از صفحه
     */
    public void shutdown() {
        databaseExecutor.shutdown();
    }
}