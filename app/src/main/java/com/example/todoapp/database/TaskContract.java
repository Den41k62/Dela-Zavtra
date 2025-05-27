package com.example.todoapp.database;

public final class TaskContract {
    private TaskContract() {}

    public static class TaskEntry {
        public static final String TABLE_NAME = "tasks";
        public static final String _ID = "_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_DUE_DATE = "due_date";
        public static final String COLUMN_COMPLETED = "completed";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_PRIORITY = "priority";
        public static final String COLUMN_IMAGE_PATH = "image_path";
    }
}