package com.example.todoapp.ui.detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.todoapp.data.TaskRepository;
import com.example.todoapp.data.model.Task;

public class TaskDetailViewModel extends ViewModel {
    private final TaskRepository repository;
    private final MutableLiveData<Task> task = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteResult = new MutableLiveData<>();

    public TaskDetailViewModel(TaskRepository repository) {
        this.repository = repository;
    }

    public LiveData<Task> getTask() {
        return task;
    }

    public LiveData<Boolean> getDeleteResult() {
        return deleteResult;
    }

    public void loadTask(String taskId) {
        new Thread(() -> {
            Task loadedTask = repository.getTaskById(taskId);
            task.postValue(loadedTask);
        }).start();
    }

    public void deleteTask(String taskId) {
        new Thread(() -> {
            try {
                Task taskToDelete = repository.getTaskById(taskId);
                if (taskToDelete != null) {
                    repository.delete(taskToDelete);
                    deleteResult.postValue(true);
                } else {
                    deleteResult.postValue(false);
                }
            } catch (Exception e) {
                deleteResult.postValue(false);
            }
        }).start();
    }
}