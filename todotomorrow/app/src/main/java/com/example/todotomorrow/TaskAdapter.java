package com.example.todotomorrow;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Date;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private final List<Task> taskList;
    private final OnTaskClickListener listener;
    private final Context context;

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
        void onTaskDelete(Task task);
        void onTaskImportantToggle(Task task);
    }

    public TaskAdapter(List<Task> taskList, OnTaskClickListener listener, Context context) {
        this.taskList = taskList;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task, listener);

        // Подсветка просроченных задач
        if (task.getDueDate() != null && task.getDueDate().before(new Date())) {
            holder.itemView.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.past_due));
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() { return taskList.size(); }

    public void updateTasks(List<Task> tasks) {
        taskList.clear();
        taskList.addAll(tasks);
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        final CheckBox checkBoxTask;
        final TextView textViewTask, textViewCategory, textViewDate;
        final ImageButton buttonDelete;
        final ImageView imageViewImportant;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxTask = itemView.findViewById(R.id.checkBoxTask);
            textViewTask = itemView.findViewById(R.id.textViewTask);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            imageViewImportant = itemView.findViewById(R.id.imageViewImportant);
        }

        public void bind(Task task, OnTaskClickListener listener) {
            textViewTask.setText(task.getDescription());
            textViewCategory.setText(task.getCategory());
            textViewDate.setText(task.getFormattedDate());
            checkBoxTask.setChecked(task.isCompleted());
            imageViewImportant.setVisibility(task.isImportant() ? View.VISIBLE : View.GONE);

            itemView.setOnClickListener(v -> listener.onTaskClick(task));
            buttonDelete.setOnClickListener(v -> listener.onTaskDelete(task));
            checkBoxTask.setOnCheckedChangeListener((buttonView, isChecked) ->
                    task.setCompleted(isChecked));
            imageViewImportant.setOnClickListener(v -> {
                task.setImportant(!task.isImportant());
                listener.onTaskImportantToggle(task);
            });
        }
    }
}