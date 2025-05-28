package com.example.todoapp.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;  // Добавьте эту строку
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;

import com.example.todoapp.R;
import com.example.todoapp.adapters.TaskAdapter;
import com.example.todoapp.database.DatabaseHelper;
import com.example.todoapp.database.TaskContract;
import com.example.todoapp.models.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private List<Task> taskList;
    private DatabaseHelper dbHelper;
    private Calendar currentDate;
    private TextView dateTextView;
    private Button prevDayButton, nextDayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация базы данных
        dbHelper = new DatabaseHelper(this);
        currentDate = Calendar.getInstance();

        // Настройка RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Инициализация элементов интерфейса
        dateTextView = findViewById(R.id.date_text_view);
        ImageButton prevDayButton = findViewById(R.id.prev_day_button);
        ImageButton nextDayButton = findViewById(R.id.next_day_button);

        // Настройка списка задач
        taskList = new ArrayList<>();
        adapter = new TaskAdapter(taskList, this, this);
        recyclerView.setAdapter(adapter);

        // Обработчики кнопок навигации по дням
        prevDayButton.setOnClickListener(v -> {
            currentDate.add(Calendar.DATE, -1);
            updateDateAndTasks();
        });

        nextDayButton.setOnClickListener(v -> {
            currentDate.add(Calendar.DATE, 1);
            updateDateAndTasks();
        });

        // Кнопка добавления новой задачи
        findViewById(R.id.fab).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            intent.putExtra("due_date", currentDate.getTimeInMillis());
            startActivity(intent);
        });

        // Первоначальная загрузка задач
        updateDateAndTasks();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDateAndTasks();
    }

    private void updateDateAndTasks() {
        // Обновление отображаемой даты
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale("ru"));
        dateTextView.setText(sdf.format(currentDate.getTime()));

        // Загрузка задач для выбранной даты
        loadTasksForDate(currentDate.getTime());
    }

    private void loadTasksForDate(Date date) {
        taskList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Установка временных границ для выбранного дня
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long startOfDay = cal.getTimeInMillis();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        long endOfDay = cal.getTimeInMillis();

        // Запрос задач для выбранного дня
        String selection = TaskContract.TaskEntry.COLUMN_DUE_DATE + " BETWEEN ? AND ?";
        String[] selectionArgs = {String.valueOf(startOfDay), String.valueOf(endOfDay)};

        Cursor cursor = db.query(
                TaskContract.TaskEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                TaskContract.TaskEntry.COLUMN_PRIORITY + " DESC, " +
                        TaskContract.TaskEntry.COLUMN_DUE_DATE + " ASC"
        );

        // Обработка результатов запроса
        while (cursor.moveToNext()) {
            Task task = new Task();
            task.setId(cursor.getInt(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry._ID)));
            task.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_TITLE)));
            task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_DESCRIPTION)));

            long dueDateMillis = cursor.getLong(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_DUE_DATE));
            task.setDueDate(new Date(dueDateMillis));

            task.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_COMPLETED)) == 1);
            task.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_CATEGORY)));
            task.setPriority(cursor.getInt(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_PRIORITY)));

            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_IMAGE_PATH));
            task.setImagePath(imagePath);

            taskList.add(task);

            Log.d("MainActivity", "Task loaded: " + task.getTitle() +
                    ", has photo: " + task.hasPhoto());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onTaskClick(int position) {
        // Редактирование задачи
        Task task = taskList.get(position);
        Intent intent = new Intent(this, AddTaskActivity.class);
        intent.putExtra("task_id", task.getId());
        startActivity(intent);
    }

    @Override
    public void onTaskLongClick(int position) {
        // Удаление задачи
        Task task = taskList.get(position);
        new android.app.AlertDialog.Builder(this)
                .setTitle("Удаление задачи")
                .setMessage("Вы уверены, что хотите удалить эту задачу?")
                .setPositiveButton("Удалить", (dialog, which) -> deleteTask(task.getId()))
                .setNegativeButton("Отмена", null)
                .show();
    }

    @Override
    public void onTaskCheckedChange(int position, boolean isChecked) {
        // Изменение статуса выполнения
        Task task = taskList.get(position);
        updateTaskCompletion(task.getId(), isChecked);
    }

    @Override
    public void onMoveToNextDayClick(int position) {
        // Перенос задачи на следующий день
        Task task = taskList.get(position);
        moveTaskToNextDay(task.getId());
        Toast.makeText(this, "Задача перенесена на завтра", Toast.LENGTH_SHORT).show();
    }

    private void moveTaskToNextDay(int taskId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate.getTime());
        cal.add(Calendar.DATE, 1);
        values.put(TaskContract.TaskEntry.COLUMN_DUE_DATE, cal.getTimeInMillis());

        String selection = TaskContract.TaskEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(taskId)};

        db.update(
                TaskContract.TaskEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

        loadTasksForDate(currentDate.getTime());
    }

    private void deleteTask(int taskId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = TaskContract.TaskEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(taskId)};

        db.delete(TaskContract.TaskEntry.TABLE_NAME, selection, selectionArgs);
        loadTasksForDate(currentDate.getTime());
    }

    private void updateTaskCompletion(int taskId, boolean isCompleted) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_COMPLETED, isCompleted ? 1 : 0);

        String selection = TaskContract.TaskEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(taskId)};

        db.update(
                TaskContract.TaskEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Toast.makeText(this, "Настройки", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
