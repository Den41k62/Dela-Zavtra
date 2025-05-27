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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements TaskAdapter.OnTaskClickListener, DatePickerFragment.OnDateSelectedListener {

    private List<Task> tasks = new ArrayList<>();
    private TaskAdapter adapter;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "TodoPrefs";
    private static final String TASKS_KEY = "tasks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        loadTasks();

        RecyclerView recyclerView = findViewById(R.id.recyclerViewTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(tasks, this, this);
        recyclerView.setAdapter(adapter);

        setupCategorySpinner();
        setupAddTaskButton();
        setupSortButtons();
    }

    private void setupCategorySpinner() {
        Spinner categorySpinner = findViewById(R.id.spinnerCategory);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                filterTasksByCategory(parent.getItemAtPosition(pos).toString());
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupAddTaskButton() {
        Button buttonAdd = findViewById(R.id.buttonAdd);
        EditText editTextTask = findViewById(R.id.editTextTask);
        Spinner spinnerNewCategory = findViewById(R.id.spinnerNewCategory);

        buttonAdd.setOnClickListener(v -> {
            String description = editTextTask.getText().toString().trim();
            if (!description.isEmpty()) {
                String category = spinnerNewCategory.getSelectedItem().toString();
                Task newTask = new Task(description, category, null); // Дата будет установлена позже
                tasks.add(newTask);
                saveTasks();
                adapter.updateTasks(tasks);
                editTextTask.setText("");
            }
        });
    }

    private void setupSortButtons() {
        Button buttonSortDate = findViewById(R.id.buttonSortDate);
        Button buttonSortPriority = findViewById(R.id.buttonSortPriority);

        buttonSortDate.setOnClickListener(v -> sortTasksByDate());
        buttonSortPriority.setOnClickListener(v -> sortTasksByPriority());
    }

    private void sortTasksByDate() {
        Collections.sort(tasks, (t1, t2) -> {
            if (t1.getDueDate() == null && t2.getDueDate() == null) return 0;
            if (t1.getDueDate() == null) return 1;
            if (t2.getDueDate() == null) return -1;
            return t1.getDueDate().compareTo(t2.getDueDate());
        });
        adapter.updateTasks(tasks);
    }

    private void sortTasksByPriority() {
        Collections.sort(tasks, (t1, t2) -> {
            if (t1.isImportant() == t2.isImportant()) {
                return Long.compare(t2.getCreatedAt(), t1.getCreatedAt());
            }
            return t1.isImportant() ? -1 : 1;
        });
        adapter.updateTasks(tasks);
    }

    private void filterTasksByCategory(String category) {
        if (category.equals("Все")) {
            adapter.updateTasks(tasks);
        } else {
            List<Task> filtered = new ArrayList<>();
            for (Task task : tasks) {
                if (task.getCategory().equals(category)) {
                    filtered.add(task);
                }
            }
            adapter.updateTasks(filtered);
        }
    }

    @Override
    public void onTaskClick(Task task) {
        TaskDialog dialog = new TaskDialog(this, task, updatedTask -> {
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getId().equals(updatedTask.getId())) {
                    tasks.set(i, updatedTask);
                    break;
                }
            }
            saveTasks();
            adapter.updateTasks(tasks);
        });
        dialog.show();
    }

    @Override
    public void onTaskDelete(Task task) {
        tasks.remove(task);
        saveTasks();
        adapter.updateTasks(tasks);
    }

    @Override
    public void onTaskImportantToggle(Task task) {
        task.setImportant(!task.isImportant());
        saveTasks();
        adapter.updateTasks(tasks);
    }

    @Override
    public void onDateSelected(Date date) {
        // Реализация для фильтрации по дате
    }

    private void saveTasks() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TASKS_KEY, new Gson().toJson(tasks));
        editor.apply();
    }

    private void loadTasks() {
        String json = sharedPreferences.getString(TASKS_KEY, null);
        Type type = new TypeToken<ArrayList<Task>>(){}.getType();
        List<Task> loadedTasks = new Gson().fromJson(json, type);
        if (loadedTasks != null) {
            tasks.clear();
            tasks.addAll(loadedTasks);
        }
    }
}