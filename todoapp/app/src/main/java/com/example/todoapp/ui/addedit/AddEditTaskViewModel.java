package com.example.todoapp.ui.addedit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.todoapp.data.TaskRepository;
import com.example.todoapp.data.model.Task;

public class AddEditTaskViewModel extends ViewModel {
    private final TaskRepository repository;
    private final MutableLiveData<Task> task = new MutableLiveData<>();
    private final MutableLiveData<Boolean> saveResult = new MutableLiveData<>();

    public AddEditTaskViewModel(TaskRepository repository) {
        this.repository = repository;
    }

    public LiveData<Task> getTask() {
        return task;
    }

    public LiveData<Boolean> getSaveResult() {
        return saveResult;
    }

    public void loadTask(String taskId) {
        new Thread(() -> {
            Task loadedTask = repository.getTaskById(taskId);
            task.postValue(loadedTask);
        }).start();
    }

    public void saveTask(Task task) {
        new Thread(() -> {
            try {
                repository.insert(task);
                saveResult.postValue(true);
            } catch (Exception e) {
                saveResult.postValue(false);
            }
        }).start();
    }

    public void updateTask(Task task) {
        new Thread(() -> {
            try {
                repository.update(task);
                saveResult.postValue(true);
            } catch (Exception e) {
                saveResult.postValue(false);
            }
        }).start();
    }
}