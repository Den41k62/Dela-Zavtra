package com.example.todoapp.ui.addedit;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.todoapp.R;
import com.example.todoapp.data.model.Task;
import com.example.todoapp.databinding.ActivityAddEditTaskBinding;

import java.util.Calendar;

public class AddEditTaskActivity extends AppCompatActivity {
    public static final String EXTRA_TASK_ID = "extra_task_id";

    private ActivityAddEditTaskBinding binding;
    private AddEditTaskViewModel viewModel;
    private String taskId;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewModel = new ViewModelProvider(this).get(AddEditTaskViewModel.class);

        setupPrioritySpinner();
        setupDatePicker();
        setupTimePicker();

        if (getIntent().hasExtra(EXTRA_TASK_ID)) {
            taskId = getIntent().getStringExtra(EXTRA_TASK_ID);
            isEditMode = true;
            viewModel.loadTask(taskId);
        }

        observeViewModel();
    }

    private void setupPrioritySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priority_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.prioritySpinner.setAdapter(adapter);
    }

    private void setupDatePicker() {
        binding.dueDateEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);
                binding.dueDateEditText.setText(
                        String.format("%02d/%02d/%d", dayOfMonth, month + 1, year));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void setupTimePicker() {
        binding.reminderTimeEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                binding.reminderTimeEditText.setText(
                        String.format("%02d:%02d", hourOfDay, minute));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });
    }

    private void observeViewModel() {
        viewModel.getTask().observe(this, task -> {
            if (task != null) {
                binding.titleEditText.setText(task.title);
                binding.descriptionEditText.setText(task.description);
                binding.prioritySpinner.setSelection(task.priority.ordinal());
                // Set other fields...
            }
        });

        viewModel.getSaveResult().observe(this, result -> {
            if (result != null) {
                Toast.makeText(this,
                        result ? R.string.task_saved : R.string.task_save_error,
                        Toast.LENGTH_SHORT).show();
                if (result) finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_edit_task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            saveTask();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveTask() {
        String title = binding.titleEditText.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            binding.titleEditText.setError(getString(R.string.error_title_required));
            return;
        }

        Task task = new Task(title);
        task.description = binding.descriptionEditText.getText().toString();
        task.priority = Task.Priority.values()[binding.prioritySpinner.getSelectedItemPosition()];
        // Set other fields...

        if (isEditMode) {
            task.id = taskId;
            viewModel.updateTask(task);
        } else {
            viewModel.saveTask(task);
        }
    }
}