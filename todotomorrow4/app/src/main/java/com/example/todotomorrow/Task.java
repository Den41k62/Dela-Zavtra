package com.example.todotomorrow;

import java.util.Date;

public class Task {
    private String id;
    private String description;
    private boolean isCompleted;
    private boolean isImportant;
    private String category;
    private long createdAt;

    public Task(String description, String category) {
        this.id = java.util.UUID.randomUUID().toString();
        this.description = description;
        this.isCompleted = false;
        this.isImportant = false;
        this.category = category;
        this.createdAt = new Date().getTime();
    }

    // Геттеры и сеттеры
    public String getId() { return id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
    public boolean isImportant() { return isImportant; }
    public void setImportant(boolean important) { isImportant = important; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public long getCreatedAt() { return createdAt; }
}