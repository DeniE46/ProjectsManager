package com.adk.projectsmanager;

/**
 * Created by DeniE46 on 3/1/2017.
 */

public class TasksModel {
    private String taskName, taskOwner, taskDescription;
    private Boolean taskIsCompleted;
    private long timeRemaining;

    public TasksModel(String taskName, String taskOwner, long timeRemaining, String taskDescription, Boolean taskIsCompleted) {
        this.taskName = taskName;
        this.taskOwner = taskOwner;
        this.timeRemaining = timeRemaining;
        this.taskIsCompleted = taskIsCompleted;
        this.taskDescription = taskDescription;

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

    public Boolean getTaskIsCompleted() {
        return taskIsCompleted;
    }

    public long getTimeRemaining() {
        return timeRemaining;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setTaskOwner(String taskOwner) {
        this.taskOwner = taskOwner;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public void setTaskIsCompleted(Boolean taskIsCompleted) {
        this.taskIsCompleted = taskIsCompleted;
    }

    public void setTimeRemaining(long timeRemaining) {
        this.timeRemaining = timeRemaining;
    }
}
