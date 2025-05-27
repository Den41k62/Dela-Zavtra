package com.example.todoapp;

import android.app.Application;

import com.example.todoapp.data.TaskDatabase;
import com.example.todoapp.data.TaskRepository;
import com.example.todoapp.utils.NotificationHelper;

public class App extends Application {
    private static App instance;
    private TaskRepository taskRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Initialize database and repository
        TaskDatabase database = TaskDatabase.getDatabase(this);
        taskRepository = new TaskRepository(database.taskDao());

        // Create notification channel
        NotificationHelper.createNotificationChannel(this);
    }

    public static App getInstance() {
        return instance;
    }

    public TaskRepository getTaskRepository() {
        return taskRepository;
    }
}