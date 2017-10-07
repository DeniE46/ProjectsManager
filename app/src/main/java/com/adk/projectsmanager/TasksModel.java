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

    protected TasksModel(Parcel in) {
        taskName = in.readString();
        taskOwner = in.readString();
        taskDescription = in.readString();
        taskDeadlineDate = in.readString();
        taskDifficulty = in.readString();
        taskPriority = in.readString();
        taskPeopleWorking = in.readString();
        taskStatus = in.readString();
        timeRemaining = in.readLong();
    }

    public static final Creator<TasksModel> CREATOR = new Creator<TasksModel>() {
        @Override
        public TasksModel createFromParcel(Parcel in) {
            return new TasksModel(in);
        }

        @Override
        public TasksModel[] newArray(int size) {
            return new TasksModel[size];
        }
    };

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
        dest.writeString(taskName);
        dest.writeString(taskOwner);
        dest.writeString(taskDescription);
        dest.writeString(taskDeadlineDate);
        dest.writeString(taskDifficulty);
        dest.writeString(taskPriority);
        dest.writeString(taskPeopleWorking);
        dest.writeString(taskStatus);
        dest.writeLong(timeRemaining);
    }

    //adding toString() to provide what to display for spinners binding lists that implement this model
    public String toString(){
        return taskName;
    }
}
