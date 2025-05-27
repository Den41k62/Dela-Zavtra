package com.example.todoapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.models.Task;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> taskList;
    private Context context;
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskClick(int position);
        void onTaskLongClick(int position);
        void onTaskCheckedChange(int position, boolean isChecked);
        void onMoveToNextDayClick(int position);
    }

    public TaskAdapter(List<Task> taskList, Context context, OnTaskClickListener listener) {
        this.taskList = taskList;
        this.context = context;
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
        holder.bind(task); // Передаем задачу в метод bind ViewHolder'а
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    // Метод для получения цвета приоритета
    private int getPriorityColor(int priority) {
        switch (priority) {
            case 1: // Низкий
                return ContextCompat.getColor(context, R.color.priority_low_transparent);
            case 2: // Средний
                return ContextCompat.getColor(context, R.color.priority_medium_transparent);
            case 3: // Высокий
                return ContextCompat.getColor(context, R.color.priority_high_transparent);
            default:
                return ContextCompat.getColor(context, R.color.priority_medium);
        }
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView dueTimeTextView;
        private TextView categoryTextView;
        private TextView priorityTextView;
        private CheckBox completedCheckBox;
        private View moveToNextDayButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.task_title);
            dueTimeTextView = itemView.findViewById(R.id.task_due_time);
            categoryTextView = itemView.findViewById(R.id.task_category);
            priorityTextView = itemView.findViewById(R.id.task_priority);
            completedCheckBox = itemView.findViewById(R.id.task_completed);
            moveToNextDayButton = itemView.findViewById(R.id.move_to_next_day);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onTaskClick(position);
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onTaskLongClick(position);
                    return true;
                }
                return false;
            });

            completedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onTaskCheckedChange(position, isChecked);
                }
            });

            moveToNextDayButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onMoveToNextDayClick(position);
                }
            });
        }

        public void bind(Task task) {
            titleTextView.setText(task.getTitle());

            // Форматирование времени, если dueDate не null
            if (task.getDueDate() != null) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                dueTimeTextView.setText(timeFormat.format(task.getDueDate()));
            }

            categoryTextView.setText(task.getCategory());

            // Установка текста и цвета приоритета
            String priorityText;
            int priorityColor;
            switch (task.getPriority()) {
                case 1: // Низкий
                    priorityText = "Низкий";
                    break;
                case 2: // Средний
                    priorityText = "Средний";
                    break;
                case 3: // Высокий
                    priorityText = "Высокий";
                    break;
                default:
                    priorityText = "Средний";
                    break;
            }
            priorityTextView.setText(priorityText);

            // Установка цвета фона элемента
            itemView.setBackgroundColor(getPriorityColor(task.getPriority()));

            completedCheckBox.setChecked(task.isCompleted());
        }
    }
}