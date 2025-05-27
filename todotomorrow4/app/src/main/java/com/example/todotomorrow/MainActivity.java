package com.example.todotomorrow;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {
    private List<Task> tasks = new ArrayList<>();
    private TaskAdapter adapter;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "TodoPrefs";
    private static final String TASKS_KEY = "tasks";
    private String selectedCategory = "Все";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        loadTasks();

        RecyclerView recyclerView = findViewById(R.id.recyclerViewTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(tasks, this);
        recyclerView.setAdapter(adapter);

        Spinner categorySpinner = findViewById(R.id.spinnerCategory);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.categories_array,
                android.R.layout.simple_spinner_item
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = parent.getItemAtPosition(position).toString();
                filterTasks();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        Button buttonAdd = findViewById(R.id.buttonAdd);
        EditText editTextTask = findViewById(R.id.editTextTask);
        Spinner spinnerNewCategory = findViewById(R.id.spinnerNewCategory);

        buttonAdd.setOnClickListener(v -> {
            String taskDescription = editTextTask.getText().toString().trim();
            String category = spinnerNewCategory.getSelectedItem().toString();
            if (!taskDescription.isEmpty()) {
                Task newTask = new Task(taskDescription, category);
                tasks.add(newTask);
                saveTasks();
                filterTasks();
                editTextTask.setText("");
            }
        });

        Button buttonSort = findViewById(R.id.buttonSort);
        buttonSort.setOnClickListener(v -> {
            Collections.sort(tasks, (t1, t2) -> {
                if (t1.isImportant() == t2.isImportant()) {
                    return Long.compare(t2.getCreatedAt(), t1.getCreatedAt());
                }
                return t1.isImportant() ? -1 : 1;
            });
            adapter.updateTasks(tasks);
        });
    }

    private void filterTasks() {
        List<Task> filteredTasks = new ArrayList<>();
        if (selectedCategory.equals("Все")) {
            filteredTasks.addAll(tasks);
        } else {
            for (Task task : tasks) {
                if (task.getCategory().equals(selectedCategory)) {
                    filteredTasks.add(task);
                }
            }
        }
        adapter.updateTasks(filteredTasks);
    }

    private void saveTasks() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(tasks);
        editor.putString(TASKS_KEY, json);
        editor.apply();
    }

    private void loadTasks() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(TASKS_KEY, null);
        Type type = new TypeToken<ArrayList<Task>>() {}.getType();
        tasks = gson.fromJson(json, type);
        if (tasks == null) {
            tasks = new ArrayList<>();
        }
    }

    @Override
    public void onTaskClick(Task task) {
        // Реализация редактирования задачи через диалог
        TaskDialog dialog = new TaskDialog(this, task, (updatedTask) -> {
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getId().equals(updatedTask.getId())) {
                    tasks.set(i, updatedTask);
                    break;
                }
            }
            saveTasks();
            filterTasks();
        });
        dialog.show();
    }

    @Override
    public void onTaskDelete(Task task) {
        tasks.remove(task);
        saveTasks();
        filterTasks();
    }

    @Override
    public void onTaskImportantToggle(Task task) {
        task.setImportant(!task.isImportant());
        saveTasks();
        filterTasks();
    }
}