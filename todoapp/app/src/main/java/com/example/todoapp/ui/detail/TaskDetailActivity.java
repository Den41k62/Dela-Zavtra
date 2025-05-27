package com.example.todoapp.ui.detail;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.todoapp.R;
import com.example.todoapp.data.model.Task;
import com.example.todoapp.databinding.ActivityTaskDetailBinding;
import com.example.todoapp.ui.addedit.AddEditTaskActivity;

public class TaskDetailActivity extends AppCompatActivity {
    public static final String EXTRA_TASK_ID = "extra_task_id";

    private ActivityTaskDetailBinding binding;
    private TaskDetailViewModel viewModel;
    private String taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        taskId = getIntent().getStringExtra(EXTRA_TASK_ID);
        if (taskId == null) {
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(TaskDetailViewModel.class);
        viewModel.loadTask(taskId);

        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getTask().observe(this, task -> {
            if (task != null) {
                binding.titleTextView.setText(task.title);
                binding.descriptionTextView.setText(task.description);
                binding.priorityTextView.setText(task.priority.name());
                // Set other fields...
            }
        });

        viewModel.getDeleteResult().observe(this, result -> {
            if (result != null) {
                Toast.makeText(this,
                        result ? R.string.task_deleted : R.string.task_delete_error,
                        Toast.LENGTH_SHORT).show();
                if (result) finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            editTask();
            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            deleteTask();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void editTask() {
        startActivity(AddEditTaskActivity.newIntent(this, taskId));
    }

    private void deleteTask() {
        viewModel.deleteTask(taskId);
    }
}