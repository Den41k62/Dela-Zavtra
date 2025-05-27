package com.example.todoapp.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.todoapp.data.TaskRepository;
import com.example.todoapp.data.model.Task;

import java.util.List;

public class MainViewModel extends ViewModel {
    private final TaskRepository repository;
    private final LiveData<List<Task>> tasks;

    public MainViewModel(TaskRepository repository) {
        this.repository = repository;
        this.tasks = repository.getAllTasks();
    }

    public LiveData<List<Task>> getTasks() {
        return tasks;
    }

    public void loadTasks() {
        // Data is automatically loaded via LiveData
    }

    public void updateTaskCompletion(String taskId, boolean isCompleted) {
        repository.updateCompletedStatus(taskId, isCompleted);
    }

    public void deleteTask(Task task) {
        repository.delete(task);
    }
}