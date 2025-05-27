package com.example.todotomorrow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> taskList;
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
        void onTaskDelete(Task task);
        void onTaskImportantToggle(Task task);
    }

    public TaskAdapter(List<Task> taskList, OnTaskClickListener listener) {
        this.taskList = taskList;
        this.listener = listener;
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
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void updateTasks(List<Task> tasks) {
        taskList = tasks;
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBoxTask;
        TextView textViewTask, textViewCategory;
        ImageButton buttonDelete;
        ImageView imageViewImportant;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxTask = itemView.findViewById(R.id.checkBoxTask);
            textViewTask = itemView.findViewById(R.id.textViewTask);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            imageViewImportant = itemView.findViewById(R.id.imageViewImportant);
        }

        public void bind(Task task, OnTaskClickListener listener) {
            textViewTask.setText(task.getDescription());
            textViewCategory.setText(task.getCategory());
            checkBoxTask.setChecked(task.isCompleted());
            imageViewImportant.setVisibility(task.isImportant() ? View.VISIBLE : View.GONE);

            itemView.setOnClickListener(v -> listener.onTaskClick(task));
            buttonDelete.setOnClickListener(v -> listener.onTaskDelete(task));
            checkBoxTask.setOnCheckedChangeListener((buttonView, isChecked) -> {
                task.setCompleted(isChecked);
            });
            imageViewImportant.setOnClickListener(v -> {
                task.setImportant(!task.isImportant());
                listener.onTaskImportantToggle(task);
            });
        }
    }
}