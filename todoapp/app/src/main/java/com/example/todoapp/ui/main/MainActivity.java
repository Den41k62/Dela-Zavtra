package com.example.todoapp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.data.model.Task;
import com.example.todoapp.databinding.ActivityMainBinding;
import com.example.todoapp.ui.addedit.AddEditTaskActivity;
import com.example.todoapp.ui.detail.TaskDetailActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TasksAdapter.OnTaskClickListener {
    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private TasksAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setupRecyclerView();
        setupObservers();
        setupListeners();

        viewModel.loadTasks();
    }

    private void setupRecyclerView() {
        adapter = new TasksAdapter(new ArrayList<>(), this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getTasks().observe(this, tasks -> {
            if (tasks != null) {
                adapter.updateTasks(tasks);
                binding.emptyState.setVisibility(tasks.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void setupListeners() {
        binding.fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            // Open settings
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTaskClick(Task task) {
        Intent intent = new Intent(this, TaskDetailActivity.class);
        intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, task.id);
        startActivity(intent);
    }

    @Override
    public void onTaskChecked(Task task, boolean isChecked) {
        viewModel.updateTaskCompletion(task.id, isChecked);
    }
}