package com.example.todoapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {
    private RecyclerView tasksRecyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private DatabaseHelper databaseHelper;
    private Button addTaskButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        addTaskButton = findViewById(R.id.addTaskButton);

        databaseHelper = new DatabaseHelper(this);
        taskList = new ArrayList<>();
        taskList.addAll(databaseHelper.getAllTasks());

        taskAdapter = new TaskAdapter(taskList, this);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksRecyclerView.setAdapter(taskAdapter);

        addTaskButton.setOnClickListener(v -> showAddTaskDialog());
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_task, null);

        EditText titleEditText = view.findViewById(R.id.titleEditText);
        EditText descriptionEditText = view.findViewById(R.id.descriptionEditText);
        Button saveButton = view.findViewById(R.id.saveButton);

        builder.setView(view);
        AlertDialog dialog = builder.create();

        saveButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();

            if (!title.isEmpty()) {
                Task task = new Task(0, title, description, false);
                databaseHelper.addTask(task);
                taskList.clear();
                taskList.addAll(databaseHelper.getAllTasks());
                taskAdapter.notifyDataSetChanged();
                dialog.dismiss();
            } else {
                titleEditText.setError("Введите название задачи");
            }
        });

        dialog.show();
    }

    @Override
    public void onTaskClick(int position) {
        Task task = taskList.get(position);
        showEditTaskDialog(task, position);
    }

    @Override
    public void onTaskLongClick(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Удаление задачи")
                .setMessage("Вы уверены, что хотите удалить эту задачу?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    Task task = taskList.get(position);
                    databaseHelper.deleteTask(task.getId());
                    taskList.remove(position);
                    taskAdapter.notifyItemRemoved(position);
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    @Override
    public void onCheckboxClick(int position, boolean isChecked) {
        Task task = taskList.get(position);
        task.setCompleted(isChecked);
        databaseHelper.updateTask(task);
        taskAdapter.notifyItemChanged(position);
    }

    private void showEditTaskDialog(Task task, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_task, null);

        EditText titleEditText = view.findViewById(R.id.titleEditText);
        EditText descriptionEditText = view.findViewById(R.id.descriptionEditText);
        Button saveButton = view.findViewById(R.id.saveButton);

        titleEditText.setText(task.getTitle());
        descriptionEditText.setText(task.getDescription());
        saveButton.setText("Обновить");

        builder.setView(view);
        AlertDialog dialog = builder.create();

        saveButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();

            if (!title.isEmpty()) {
                task.setTitle(title);
                task.setDescription(description);
                databaseHelper.updateTask(task);
                taskAdapter.notifyItemChanged(position);
                dialog.dismiss();
            } else {
                titleEditText.setError("Введите название задачи");
            }
        });

        dialog.show();
    }
}