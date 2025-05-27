package com.example.todotomorrow;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todotomorrow.adapter.TaskAdapter;
import com.example.todotomorrow.database.DatabaseClient;
import com.example.todotomorrow.database.Task;
import com.example.todotomorrow.database.TaskDatabase;
import com.example.todotomorrow.dialog.AddTaskDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        AddTaskDialog.AddTaskDialogListener,
        TaskAdapter.OnTaskClickListener {

    private TaskDatabase taskDatabase;
    private TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskDatabase = DatabaseClient.getInstance(getApplicationContext()).getTaskDatabase();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(this);
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                deleteTask(adapter.getTaskAt(viewHolder.getAdapterPosition()));
            }
        }).attachToRecyclerView(recyclerView);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> showAddTaskDialog());

        loadTasks();
    }

    private void loadTasks() {
        taskDatabase.taskDao().getAllTasks().observe(this, tasks -> adapter.setTasks(tasks));
    }

    private void showAddTaskDialog() {
        AddTaskDialog dialog = new AddTaskDialog();
        dialog.show(getSupportFragmentManager(), "AddTaskDialog");
    }

    private void showEditTaskDialog(Task task) {
        AddTaskDialog dialog = new AddTaskDialog();
        Bundle args = new Bundle();
        args.putInt("taskId", task.getId());
        args.putString("title", task.getTitle());
        args.putString("description", task.getDescription());
        args.putInt("priority", task.getPriority());
        args.putLong("dueDate", task.getDueDate() != null ? task.getDueDate() : 0);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "EditTaskDialog");
    }

    @Override
    public void onTaskAdded(Task task) {
        insertTask(task);
    }

    @Override
    public void onTaskUpdated(Task task) {
        updateTask(task);
    }

    private void insertTask(final Task task) {
        AsyncTask.execute(() -> taskDatabase.taskDao().insert(task));
    }

    private void updateTask(final Task task) {
        AsyncTask.execute(() -> taskDatabase.taskDao().update(task));
    }

    private void deleteTask(final Task task) {
        AsyncTask.execute(() -> {
            taskDatabase.taskDao().delete(task);
            runOnUiThread(() -> Toast.makeText(MainActivity.this,
                    "Задача удалена", Toast.LENGTH_SHORT).show());
        });
    }

    private void deleteCompletedTasks() {
        AsyncTask.execute(() -> {
            taskDatabase.taskDao().deleteCompleted();
            runOnUiThread(() -> Toast.makeText(MainActivity.this,
                    "Выполненные задачи удалены", Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public void onTaskClick(Task task) {
        Toast.makeText(this, "Нажмите и удерживайте для редактирования", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskLongClick(Task task) {
        showEditTaskDialog(task);
    }

    @Override
    public void onTaskCheckedChange(Task task, boolean isChecked) {
        task.setCompleted(isChecked);
        updateTask(task);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete_completed) {
            deleteCompletedTasks();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}