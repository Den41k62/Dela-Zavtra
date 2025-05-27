package com.example.todotomorrow.dialog;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.example.todotomorrow.R;
import java.util.Calendar;

public class DatePickerDialog extends DialogFragment
        implements android.app.DatePickerDialog.OnDateSetListener {

    public interface DatePickerListener {
        void onDateSelected(long dateInMillis);
    }

    private DatePickerListener listener;

    @Override
    public void onAttach(@NonNull android.content.Context context) {
        super.onAttach(context);
        try {
            listener = (DatePickerListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement DatePickerListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new android.app.DatePickerDialog(
                requireContext(),
                R.style.DatePickerTheme,
                this,
                year, month, day);
    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        listener.onDateSelected(calendar.getTimeInMillis());
    }
}