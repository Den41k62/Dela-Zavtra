package com.example.todotomorrow.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todotomorrow.R;
import com.example.todotomorrow.database.Task;
import java.text.SimpleDateFormat;
import java.util.Date; // Добавлен этот импорт
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> tasks;
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
        void onTaskLongClick(Task task);
        void onTaskCheckedChange(Task task, boolean isChecked);
    }

    public TaskAdapter(OnTaskClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task, listener);
    }

    @Override
    public int getItemCount() {
        return tasks != null ? tasks.size() : 0;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public Task getTaskAt(int position) {
        return tasks.get(position);
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView descriptionTextView;
        private TextView dueDateTextView;
        private CheckBox completedCheckBox;
        private View priorityIndicator;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.task_title);
            descriptionTextView = itemView.findViewById(R.id.task_description);
            dueDateTextView = itemView.findViewById(R.id.task_due_date);
            completedCheckBox = itemView.findViewById(R.id.task_completed);
            priorityIndicator = itemView.findViewById(R.id.priority_indicator);
        }

        public void bind(final Task task, final OnTaskClickListener listener) {
            titleTextView.setText(task.getTitle());
            descriptionTextView.setText(task.getDescription());
            completedCheckBox.setChecked(task.isCompleted());

            if (task.getDueDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                dueDateTextView.setText(sdf.format(new Date(task.getDueDate())));
                dueDateTextView.setVisibility(View.VISIBLE);
            } else {
                dueDateTextView.setVisibility(View.GONE);
            }

            // Set priority color
            int priorityColor;
            switch (task.getPriority()) {
                case 2:
                    priorityColor = itemView.getContext().getResources().getColor(R.color.high_priority);
                    break;
                case 1:
                    priorityColor = itemView.getContext().getResources().getColor(R.color.medium_priority);
                    break;
                default:
                    priorityColor = itemView.getContext().getResources().getColor(R.color.low_priority);
            }
            priorityIndicator.setBackgroundColor(priorityColor);

            itemView.setOnClickListener(v -> listener.onTaskClick(task));
            itemView.setOnLongClickListener(v -> {
                listener.onTaskLongClick(task);
                return true;
            });

            completedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                task.setCompleted(isChecked);
                listener.onTaskCheckedChange(task, isChecked);
            });
        }
    }
}