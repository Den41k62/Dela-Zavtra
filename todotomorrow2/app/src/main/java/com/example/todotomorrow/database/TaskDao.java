package com.example.todotomorrow.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY priority DESC, createdAt")
    LiveData<List<Task>> getAllTasks();

    @Query("SELECT * FROM tasks WHERE dueDate >= :start AND dueDate < :end ORDER BY priority DESC, createdAt")
    LiveData<List<Task>> getTasksByDateRange(long start, long end);

    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("DELETE FROM tasks WHERE isCompleted = 1")
    void deleteCompleted();

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    LiveData<Task> getTaskById(int taskId);
}