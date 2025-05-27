package com.example.todotomorrow.database;

import android.content.Context;
import androidx.room.Room;

public class DatabaseClient {
    private Context context;
    private static DatabaseClient instance;
    private TaskDatabase taskDatabase;

    private DatabaseClient(Context context) {
        this.context = context;
        taskDatabase = Room.databaseBuilder(context, TaskDatabase.class, "TaskDB").build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseClient(context);
        }
        return instance;
    }

    public TaskDatabase getTaskDatabase() {
        return taskDatabase;
    }
}