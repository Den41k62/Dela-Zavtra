package com.example.todotomorrow;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class TaskDialog extends Dialog {
    private Task task;
    private OnTaskUpdatedListener listener;

    public interface OnTaskUpdatedListener {
        void onTaskUpdated(Task updatedTask);
    }

    public TaskDialog(Context context, Task task, OnTaskUpdatedListener listener) {
        super(context);
        this.task = task;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_task);

        EditText editText = findViewById(R.id.editTextDialog);
        Spinner spinner = findViewById(R.id.spinnerDialog);
        CheckBox checkBoxImportant = findViewById(R.id.checkBoxImportant);
        Button buttonSave = findViewById(R.id.buttonSave);

        editText.setText(task.getDescription());
        checkBoxImportant.setChecked(task.isImportant());

        // Установка текущей категории в спиннере
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.categories_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if (!task.getCategory().isEmpty()) {
            int spinnerPosition = adapter.getPosition(task.getCategory());
            spinner.setSelection(spinnerPosition);
        }

        buttonSave.setOnClickListener(v -> {
            task.setDescription(editText.getText().toString());
            task.setCategory(spinner.getSelectedItem().toString());
            task.setImportant(checkBoxImportant.isChecked());
            listener.onTaskUpdated(task);
            dismiss();
        });
    }
}