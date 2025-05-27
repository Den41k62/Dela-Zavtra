package com.example.todotomorrow.dialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.example.todotomorrow.R;
import com.example.todotomorrow.database.Task;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTaskDialog extends DialogFragment {
    private Calendar selectedDate;
    private Button dateButton;
    private boolean isEditMode = false;
    private int taskId;

    public interface AddTaskDialogListener {
        void onTaskAdded(Task task);
        void onTaskUpdated(Task task);
    }

    private AddTaskDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (AddTaskDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement AddTaskDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_task, null);

        final EditText titleEditText = view.findViewById(R.id.edit_text_title);
        final EditText descriptionEditText = view.findViewById(R.id.edit_text_description);
        final Spinner prioritySpinner = view.findViewById(R.id.spinner_priority);
        dateButton = view.findViewById(R.id.button_date);
        selectedDate = Calendar.getInstance();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.priority_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapter);

        if (getArguments() != null) {
            isEditMode = true;
            taskId = getArguments().getInt("taskId");
            titleEditText.setText(getArguments().getString("title"));
            descriptionEditText.setText(getArguments().getString("description"));
            prioritySpinner.setSelection(getArguments().getInt("priority"));

            long dueDate = getArguments().getLong("dueDate");
            if (dueDate > 0) {
                selectedDate.setTimeInMillis(dueDate);
                updateDateButtonText();
            }
        }

        dateButton.setOnClickListener(v -> showDatePickerDialog());

        builder.setView(view)
                .setTitle(isEditMode ? R.string.edit_task : R.string.add_new_task)
                .setPositiveButton(isEditMode ? R.string.save : R.string.add, (dialog, which) -> {
                    String title = titleEditText.getText().toString();
                    String description = descriptionEditText.getText().toString();
                    int priority = prioritySpinner.getSelectedItemPosition();
                    Long dueDate = selectedDate != null ? selectedDate.getTimeInMillis() : null;

                    if (title.isEmpty()) {
                        titleEditText.setError("Введите название задачи");
                        return;
                    }

                    Task task = new Task(
                            isEditMode ? taskId : 0,
                            title,
                            description,
                            false,
                            System.currentTimeMillis(),
                            dueDate,
                            priority
                    );

                    if (isEditMode) {
                        listener.onTaskUpdated(task);
                    } else {
                        listener.onTaskAdded(task);
                    }
                })
                .setNegativeButton(R.string.cancel, null);

        return builder.create();
    }

    private void showDatePickerDialog() {
        Calendar calendar = selectedDate != null ? selectedDate : Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, yearSelected, monthOfYear, dayOfMonth) -> {
                    selectedDate.set(yearSelected, monthOfYear, dayOfMonth);
                    updateDateButtonText();
                },
                year, month, day);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void updateDateButtonText() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        dateButton.setText(sdf.format(selectedDate.getTime()));
    }
}