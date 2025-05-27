package com.example.todoapp.utils;

import android.content.Context;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.todoapp.workers.BackupWorker;
import com.example.todoapp.workers.ReminderWorker;

import java.util.concurrent.TimeUnit;

public class WorkManagerUtils {
    private static final String BACKUP_WORK_TAG = "backup_work";
    private static final String REMINDER_WORK_TAG = "reminder_work";

    public static void scheduleBackup(Context context) {
        PeriodicWorkRequest backupRequest =
                new PeriodicWorkRequest.Builder(BackupWorker.class, 24, TimeUnit.HOURS)
                        .addTag(BACKUP_WORK_TAG)
                        .build();

        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                        BACKUP_WORK_TAG,
                        ExistingPeriodicWorkPolicy.REPLACE,
                        backupRequest);
    }

    public static void scheduleReminderCheck(Context context) {
        PeriodicWorkRequest reminderRequest =
                new PeriodicWorkRequest.Builder(ReminderWorker.class, 15, TimeUnit.MINUTES)
                        .addTag(REMINDER_WORK_TAG)
                        .build();

        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                        REMINDER_WORK_TAG,
                        ExistingPeriodicWorkPolicy.KEEP,
                        reminderRequest);
    }
}