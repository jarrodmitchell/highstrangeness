package com.example.highstrangeness.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.highstrangeness.R;

import java.util.Calendar;
import java.util.Objects;

public class DatePickerFragment extends DialogFragment {
    
    public static final String TAG = "DatePickerFragment";
    
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        final DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, dayOfMonth);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button buttonPositive = datePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                Button buttonNegative = datePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                if (getActivity() != null) {
                    buttonPositive.setTextColor(ContextCompat.getColor(getActivity(), R.color.pink));
                    buttonNegative.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
                }
            }
        });
        return datePickerDialog;
    }
}
