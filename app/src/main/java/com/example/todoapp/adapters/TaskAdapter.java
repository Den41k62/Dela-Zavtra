package com.example.todoapp.adapters;

import android.content.Context;
import android.util.Log;
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
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView descriptionTextView;
        private TextView dueTimeTextView;
        private TextView categoryTextView;
        private TextView priorityTextView;
        private CheckBox completedCheckBox;
        private ImageButton moveToNextDayButton;
        private ImageView cameraIcon;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.task_title);
            descriptionTextView = itemView.findViewById(R.id.task_description);
            dueTimeTextView = itemView.findViewById(R.id.task_due_time);
            categoryTextView = itemView.findViewById(R.id.task_category);
            priorityTextView = itemView.findViewById(R.id.task_priority);
            completedCheckBox = itemView.findViewById(R.id.task_completed);
            moveToNextDayButton = itemView.findViewById(R.id.move_to_next_day);
            cameraIcon = itemView.findViewById(R.id.camera_icon);

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
            descriptionTextView.setText(task.getDescription());

            if (task.getDueDate() != null) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                dueTimeTextView.setText(timeFormat.format(task.getDueDate()));
            }

            categoryTextView.setText(task.getCategory());

            String priorityText;
            switch (task.getPriority()) {
                case 1: priorityText = "Низкий"; break;
                case 2: priorityText = "Средний"; break;
                case 3: priorityText = "Высокий"; break;
                default: priorityText = "Средний"; break;
            }
            priorityTextView.setText(priorityText);

            // Исправленный вызов метода getPriorityColor
            itemView.setBackgroundColor(TaskAdapter.this.getPriorityColor(task.getPriority()));

            completedCheckBox.setChecked(task.isCompleted());

            if (task.hasPhoto()) {
                cameraIcon.setVisibility(View.VISIBLE);
                Log.d("TaskAdapter", "Showing camera icon for task: " + task.getTitle());
            } else {
                cameraIcon.setVisibility(View.GONE);
            }
        }
    }
}
