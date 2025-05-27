// TaskDao.java
package com.example.todoapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.todoapp.data.model.Task;

import java.util.Date;
import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    LiveData<Task> getTaskById(String taskId);

    @Query("SELECT * FROM tasks ORDER BY " +
            "CASE WHEN dueDate IS NULL THEN 1 ELSE 0 END, dueDate ASC")
    LiveData<List<Task>> getAllTasks();

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY " +
            "CASE WHEN dueDate IS NULL THEN 1 ELSE 0 END, dueDate ASC")
    LiveData<List<Task>> getActiveTasks();

    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY createdAt DESC")
    LiveData<List<Task>> getCompletedTasks();

    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :taskId")
    void updateCompletedStatus(String taskId, boolean isCompleted);
}