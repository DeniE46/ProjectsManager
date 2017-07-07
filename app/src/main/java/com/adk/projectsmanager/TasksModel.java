package com.adk.projectsmanager;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class TasksModel implements Parcelable {
    private String taskName, taskOwner, taskDescription, taskDeadlineDate, taskDifficulty, taskPriority, taskPeopleWorking;
    String taskStatus;
    private long timeRemaining;

    boolean filterWIP = false;
    boolean filterCompleted = false;

    public boolean isFilterWIP() {
        return filterWIP;
    }

    public void setFilterWIP(boolean filterWIP) {
        this.filterWIP = filterWIP;
    }

    public boolean isFilterCompleted() {
        return filterCompleted;
    }

    public void setFilterCompleted(boolean filterCompleted) {
        this.filterCompleted = filterCompleted;
    }

    public TasksModel(boolean filterWIP, boolean filterCompleted) {

        this.filterWIP = filterWIP;
        this.filterCompleted = filterCompleted;
    }



    public TasksModel(String taskName, String taskOwner, long timeRemaining, String taskDescription, String taskStatus, String taskDeadlineDate) {
        this.taskName = taskName;
        this.taskOwner = taskOwner;
        this.timeRemaining = timeRemaining;
        this.taskStatus = taskStatus;
        this.taskDescription = taskDescription;
        this.taskDeadlineDate = taskDeadlineDate;


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
}
