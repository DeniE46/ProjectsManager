package com.adk.projectsmanager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateTasks extends Activity {

    private DatePicker datePicker;
    private TextView setDeadline;
    private int year, month, day;
    EditText addTaskName, addTaskOwner, addTaskDescription;
    Button createTask;
    String string;
    StringBuilder stringBuilder;
    StringBuilder stringBuilderShow;
    String deadLine;


    //TextWatcher
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3)
        {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            checkFieldsForEmptyValues();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tasks);


        //registering activity_create_tasks.xml widgets
        addTaskName = (EditText)findViewById(R.id.add_task_name);
        addTaskOwner = (EditText)findViewById(R.id.add_task_owner);
        addTaskDescription = (EditText)findViewById(R.id.add_task_description);
        createTask = (Button)findViewById(R.id.create_task);
        setDeadline = (TextView)findViewById(R.id.create_task_set_deadline);
        TextView cancelNewTask = (TextView) findViewById(R.id.cancel_new_task);

        final Intent intent = new Intent(this, Tasks.class);


        String TaskName = addTaskName.getText().toString();
        String TaskOwner = addTaskOwner.getText().toString();
        String TaskDescription = addTaskDescription.getText().toString();


        cancelNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
                finish();
                overridePendingTransition( android.R.anim.slide_in_left, android.R.anim.slide_out_right );
            }
        });


        //set listeners for editTexts to check if empty
        addTaskName.addTextChangedListener(textWatcher);
        addTaskOwner.addTextChangedListener(textWatcher);
        addTaskDescription.addTextChangedListener(textWatcher);
        setDeadline.addTextChangedListener(textWatcher);

        // run once to disable if empty
        checkFieldsForEmptyValues();



        createTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload_task();
                startActivity(intent);
                finish();
                overridePendingTransition( android.R.anim.slide_in_left, android.R.anim.slide_out_right );
            }
        });




        //Gets current day/month/year
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        //showDate(year, month+1, day);


    }

    //calling datePicker
    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {

            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDate(arg1, arg2+1, arg3);

        }
    };

    public void showDate(int year, int month, int day) {
        stringBuilder = new StringBuilder().append(day).append(" ")
                .append(month).append(" ").append(year);
        stringBuilderShow = new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year);
        string = stringBuilder.toString();
        String string_show = stringBuilderShow.toString();
        deadLine = string_show;
        setDeadline.setText(string_show);

    }

    //./calling datePicker
    //add conditions for EditTexts and datePicker according to witch the save button will be activated/disabled
    private  void checkFieldsForEmptyValues(){
        String TaskName = addTaskName.getText().toString();
        String TaskOwner = addTaskOwner.getText().toString();
        String TaskDescription = addTaskDescription.getText().toString();
        String deadline = setDeadline.getText().toString();

        if(TaskName.equals("") && TaskOwner.equals(""))
        {
            createTask.setEnabled(false);
        }

        else if(TaskName.equals("")){
            createTask.setEnabled(false);
        }

        else if(TaskOwner.equals(""))
        {
            createTask.setEnabled(false);
        }
        else if(TaskDescription.equals(""))
        {
            createTask.setEnabled(false);
        }
        else if(deadline.equals(""))
        {
            createTask.setEnabled(false);
        }
        else
        {
            createTask.setEnabled(true);
        }
    }






    public final void upload_task(){

        String TaskName = addTaskName.getText().toString();
        String TaskOwner = addTaskOwner.getText().toString();
        String TaskDescription = addTaskDescription.getText().toString();
        String TaskFlag = "false";


        Map<String, Object> taskMap = new HashMap<>();
        taskMap.put("task name", TaskName );
        taskMap.put("task owner", TaskOwner);
        taskMap.put("task status", string);
        taskMap.put("task description", TaskDescription);
        taskMap.put("flag", TaskFlag);
        new Firebase(TaskURL.tasksURL)
                .push()
                .child("Tasks")
                .setValue(taskMap);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

    }

}
