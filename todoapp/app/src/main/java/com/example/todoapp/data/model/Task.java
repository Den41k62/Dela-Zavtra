package com.example.todoapp.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.todoapp.data.Converters;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity(tableName = "tasks")
@TypeConverters(Converters.class)
public class Task {
    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    @PrimaryKey
    public String id = UUID.randomUUID().toString();
    public String title;
    public String description;
    public Priority priority = Priority.MEDIUM;
    public Date dueDate;
    public Date reminderTime;
    public boolean isCompleted = false;
    public Date createdAt = new Date();
    public String category;
    public List<String> tags;
    public List<Subtask> subtasks;
    public String attachmentPath;

    public static class Subtask {
        public String id = UUID.randomUUID().toString();
        public String title;
        public boolean isCompleted = false;

        public Subtask(String title) {
            this.title = title;
        }
    }

    public Task(String title) {
        this.title = title;
    }
}