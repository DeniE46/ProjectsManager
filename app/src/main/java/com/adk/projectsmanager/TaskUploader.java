package com.adk.projectsmanager;

import android.os.AsyncTask;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DeniE46 on 2/1/2017.
 */

public class TaskUploader extends AsyncTask<String, String, Integer> {


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(String... params) {
        String taskName = params[0];
        String taskOwner = params[1];
        String taskDeadline = params[2];
        String taskDescription = params[3];
        String taskFlag = "false";

        Map<String, Object> taskData = new HashMap<>();
        taskData.put("task name", taskName);
        taskData.put("task owner", taskOwner);
        taskData.put("task status", taskDeadline);
        taskData.put("task description", taskDescription);
        taskData.put("flag", taskFlag);
        new Firebase(TaskURL.tasksURL)
                .push()
                .child("Tasks")
                .setValue(taskData);

        return null;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        //Toast.makeText(TaskUploader.this, "Done", Toast.LENGTH_SHORT).show();
        super.onPostExecute(integer);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }
}