package com.example.todoapp.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.todoapp.R;
import com.example.todoapp.database.DatabaseHelper;
import com.example.todoapp.database.TaskContract;
import com.example.todoapp.models.Task;
import com.example.todoapp.utils.DatePickerFragment;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

public class AddTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "AddTaskActivity";
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText titleEditText;
    private EditText descriptionEditText;
    private Button dueDateButton;
    private Spinner categorySpinner;
    private Spinner prioritySpinner;
    private CheckBox completedCheckBox;
    private Button saveButton;
    private Button addPhotoButton;
    private ImageView imagePreview;

    private DatabaseHelper dbHelper;
    private Calendar dueDateCalendar;
    private int taskId = -1;
    private String currentImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        try {
            dbHelper = new DatabaseHelper(this);
            dueDateCalendar = Calendar.getInstance();

            initViews();
            setupSpinners();

            dueDateButton.setOnClickListener(v -> showDatePickerDialog());
            saveButton.setOnClickListener(v -> saveTask());
            addPhotoButton.setOnClickListener(v -> openImageChooser());

            if (getIntent().hasExtra("task_id")) {
                taskId = getIntent().getIntExtra("task_id", -1);
                loadTaskData(taskId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при создании активности", e);
            Toast.makeText(this, "Ошибка инициализации: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initViews() {
        titleEditText = findViewById(R.id.edit_text_title);
        descriptionEditText = findViewById(R.id.edit_text_description);
        dueDateButton = findViewById(R.id.button_due_date);
        categorySpinner = findViewById(R.id.spinner_category);
        prioritySpinner = findViewById(R.id.spinner_priority);
        completedCheckBox = findViewById(R.id.checkbox_completed);
        saveButton = findViewById(R.id.button_save);
        addPhotoButton = findViewById(R.id.btn_add_photo);
        imagePreview = findViewById(R.id.image_preview);
    }

    private void setupSpinners() {
        try {
            // Категории
            ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                    this,
                    R.array.categories_array,
                    android.R.layout.simple_spinner_item);
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(categoryAdapter);

            // Приоритеты
            ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource(
                    this,
                    R.array.priorities_array,
                    android.R.layout.simple_spinner_item);
            priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            prioritySpinner.setAdapter(priorityAdapter);
        } catch (Exception e) {
            Log.e(TAG, "Ошибка настройки спиннеров", e);
            Toast.makeText(this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDatePickerDialog() {
        try {
            DatePickerFragment newFragment = new DatePickerFragment();
            newFragment.setListener((year, month, day) -> {
                dueDateCalendar.set(Calendar.YEAR, year);
                dueDateCalendar.set(Calendar.MONTH, month);
                dueDateCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateDateButtonText();
            });
            newFragment.show(getSupportFragmentManager(), "datePicker");
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при показе DatePicker", e);
            Toast.makeText(this, "Ошибка выбора даты", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDateButtonText() {
        String dateText = dueDateCalendar.get(Calendar.DAY_OF_MONTH) + "." +
                (dueDateCalendar.get(Calendar.MONTH) + 1) + "." +
                dueDateCalendar.get(Calendar.YEAR);
        dueDateButton.setText(dateText);
    }

    private void openImageChooser() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при открытии галереи", e);
            Toast.makeText(this, "Не удалось открыть галерею", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                Uri imageUri = data.getData();
                currentImagePath = getPathFromUri(imageUri);

                if (currentImagePath != null) {
                    imagePreview.setVisibility(View.VISIBLE);
                    Glide.with(this)
                            .load(new File(currentImagePath))
                            .fitCenter()
                            .into(imagePreview);
                } else {
                    Toast.makeText(this, "Не удалось получить изображение", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Ошибка при выборе изображения", e);
                Toast.makeText(this, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getPathFromUri(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(contentUri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(column_index);
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при получении пути изображения", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    private void loadTaskData(int taskId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();

            String[] projection = {
                    TaskContract.TaskEntry._ID,
                    TaskContract.TaskEntry.COLUMN_TITLE,
                    TaskContract.TaskEntry.COLUMN_DESCRIPTION,
                    TaskContract.TaskEntry.COLUMN_DUE_DATE,
                    TaskContract.TaskEntry.COLUMN_COMPLETED,
                    TaskContract.TaskEntry.COLUMN_CATEGORY,
                    TaskContract.TaskEntry.COLUMN_PRIORITY,
                    TaskContract.TaskEntry.COLUMN_IMAGE_PATH
            };

            cursor = db.query(
                    TaskContract.TaskEntry.TABLE_NAME,
                    projection,
                    TaskContract.TaskEntry._ID + " = ?",
                    new String[]{String.valueOf(taskId)},
                    null, null, null);

            if (cursor.moveToFirst()) {
                titleEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_TITLE)));
                descriptionEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_DESCRIPTION)));

                long dueDateMillis = cursor.getLong(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_DUE_DATE));
                dueDateCalendar.setTimeInMillis(dueDateMillis);
                updateDateButtonText();

                completedCheckBox.setChecked(cursor.getInt(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_COMPLETED)) == 1);

                String category = cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_CATEGORY));
                setSpinnerSelection(categorySpinner, category);

                int priority = cursor.getInt(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_PRIORITY));
                setSpinnerSelection(prioritySpinner, String.valueOf(priority));

                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_IMAGE_PATH));
                if (imagePath != null && !imagePath.isEmpty()) {
                    currentImagePath = imagePath;
                    imagePreview.setVisibility(View.VISIBLE);
                    Glide.with(this)
                            .load(new File(imagePath))
                            .fitCenter()
                            .into(imagePreview);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при загрузке данных задачи", e);
            Toast.makeText(this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        try {
            ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).toString().equalsIgnoreCase(value)) {
                    spinner.setSelection(i);
                    break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при установке значения спиннера", e);
        }
    }

    private void saveTask() {
        SQLiteDatabase db = null;
        try {
            String title = titleEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(this, "Введите название задачи", Toast.LENGTH_SHORT).show();
                return;
            }

            Date dueDate = dueDateCalendar.getTime();
            String category = categorySpinner.getSelectedItem().toString();
            int priority = prioritySpinner.getSelectedItemPosition() + 1;
            boolean isCompleted = completedCheckBox.isChecked();

            Task task = new Task(title, description, dueDate, category, priority);
            task.setCompleted(isCompleted);
            task.setImagePath(currentImagePath);

            db = dbHelper.getWritableDatabase();
            android.content.ContentValues values = new android.content.ContentValues();
            values.put(TaskContract.TaskEntry.COLUMN_TITLE, task.getTitle());
            values.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, task.getDescription());
            values.put(TaskContract.TaskEntry.COLUMN_DUE_DATE, task.getDueDate().getTime());
            values.put(TaskContract.TaskEntry.COLUMN_COMPLETED, task.isCompleted() ? 1 : 0);
            values.put(TaskContract.TaskEntry.COLUMN_CATEGORY, task.getCategory());
            values.put(TaskContract.TaskEntry.COLUMN_PRIORITY, task.getPriority());
            values.put(TaskContract.TaskEntry.COLUMN_IMAGE_PATH, task.getImagePath());

            Log.d(TAG, "Попытка сохранения задачи: " + values.toString());

            if (taskId == -1) {
                long newRowId = db.insert(TaskContract.TaskEntry.TABLE_NAME, null, values);
                if (newRowId == -1) {
                    Log.e(TAG, "Ошибка при вставке в базу данных");
                    Toast.makeText(this, "Ошибка при сохранении задачи", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Задача успешно сохранена, ID: " + newRowId);
                    Toast.makeText(this, "Задача сохранена", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                int count = db.update(
                        TaskContract.TaskEntry.TABLE_NAME,
                        values,
                        TaskContract.TaskEntry._ID + " = ?",
                        new String[]{String.valueOf(taskId)});
                if (count == 0) {
                    Log.e(TAG, "Ошибка при обновлении задачи");
                    Toast.makeText(this, "Ошибка при обновлении задачи", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Задача успешно обновлена");
                    Toast.makeText(this, "Задача обновлена", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при сохранении задачи", e);
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        dueDateCalendar.set(Calendar.YEAR, year);
        dueDateCalendar.set(Calendar.MONTH, month);
        dueDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateDateButtonText();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}