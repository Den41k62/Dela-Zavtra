package com.example.todoapp.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.todoapp.data.TaskRepository;
import com.example.todoapp.utils.FileUtils;

import java.util.List;

public class BackupWorker extends Worker {
    private static final String TAG = "BackupWorker";

    public BackupWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            TaskRepository repository = App.getInstance().getTaskRepository();
            List<Task> tasks = repository.getAllTasksSync();

            boolean success = FileUtils.saveBackup(getApplicationContext(), tasks);
            if (success) {
                Log.i(TAG, "Backup completed successfully");
                return Result.success();
            } else {
                Log.e(TAG, "Backup failed");
                return Result.failure();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during backup", e);
            return Result.failure();
        }
    }
}