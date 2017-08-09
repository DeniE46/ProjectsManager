package com.adk.projectsmanager;


import android.os.Parcel;
import android.os.Parcelable;

public class TasksModel implements Parcelable {
    private String taskName, taskOwner, taskDescription, taskDeadlineDate, taskDifficulty, taskPriority, taskPeopleWorking;
    String taskStatus;
    private long timeRemaining;








    public TasksModel(String taskName, String taskOwner, long timeRemaining, String taskDescription, String taskStatus, String taskDeadlineDate, String taskPriority, String taskPeopleWorking, String taskDifficulty) {
        this.taskName = taskName;
        this.taskOwner = taskOwner;
        this.timeRemaining = timeRemaining;
        this.taskStatus = taskStatus;
        this.taskDescription = taskDescription;
        this.taskDeadlineDate = taskDeadlineDate;
        this.taskPriority = taskPriority;
        this.taskPeopleWorking = taskPeopleWorking;
        this.taskDifficulty = taskDifficulty;


    }


    public TasksModel() {

    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskOwner() {
        return taskOwner;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public long getTimeRemaining() {
        return timeRemaining;
    }

    public String getTaskDeadlineDate() {
        return taskDeadlineDate;
    }

    public String getTaskDifficulty() {
        return taskDifficulty;
    }

    public String getTaskPriority() {
        return taskPriority;
    }

    public String getTaskPeopleWorking() {
        return taskPeopleWorking;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    //adding toString() to provide what to display for spinners binding lists that implement this model
    public String toString(){
        return taskName;
    }
}
