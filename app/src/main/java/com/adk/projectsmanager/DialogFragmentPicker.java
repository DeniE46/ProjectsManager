package com.adk.projectsmanager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public  class DialogFragmentPicker extends DialogFragment implements DatePickerDialog.OnDateSetListener{

StringBuilder stringBuilder;


    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState){

        final Calendar today = Calendar.getInstance();
        int year = today.get(Calendar.YEAR);
        int month = today.get(Calendar.MONTH);
        int day = today.get(Calendar.DAY_OF_MONTH);
    return new DatePickerDialog(getActivity(), this, year, month, day);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        monthOfYear = monthOfYear+1;
        stringBuilder = new StringBuilder().append(dayOfMonth).append("/")
                .append(monthOfYear).append("/").append(year);
        String showTaskDeadline = stringBuilder.toString();
        Button setTaskDeadline = (Button) getActivity().findViewById(R.id.create_task_deadline);
        setTaskDeadline.setText(showTaskDeadline);


    }


}
