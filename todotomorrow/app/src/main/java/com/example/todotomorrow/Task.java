package com.example.todotomorrow;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Task {
    private String id;
    private String description;
    private boolean isCompleted;
    private boolean isImportant;
    private String category;
    private Date dueDate;
    private long createdAt;

    public Task(String description, String category, Date dueDate) {
        this.id = UUID.randomUUID().toString();
        this.description = description;
        this.category = category;
        this.dueDate = dueDate;
        this.createdAt = System.currentTimeMillis();
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
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    public long getCreatedAt() { return createdAt; }

    public String getFormattedDate() {
        if (dueDate == null) return "Без срока";
        return new SimpleDateFormat("dd.MM.yyyy EEEE", new Locale("ru")).format(dueDate);
    }

    public String getDayOfWeek() {
        if (dueDate == null) return "";
        return new SimpleDateFormat("EEEE", new Locale("ru")).format(dueDate);
    }
}