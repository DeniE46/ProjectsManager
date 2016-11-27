package com.adk.projectsmanager;


 class TeamMembers {
     private String name, occupation;
     private String taskName, taskMember, taskDescription;
     private String flag;
     private int PicId;
     private long taskDaysLeft;


     TeamMembers(String name, String occupation, int PicId){
        this.name = name;
        this.occupation = occupation;
        this.PicId = PicId;

    }
    //teamMembers overloaded for tasks
     TeamMembers(String taskName, String taskMember, long taskDaysLeft, String taskDescription, String flag){
        this.taskName = taskName;
        this.taskMember = taskMember;
        this.taskDaysLeft = taskDaysLeft;
        this.taskDescription = taskDescription;
        this.flag = flag;

    }




    //getters and setters for team members
    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

     String getOccupation(){
        return occupation;
    }

     int getPicId(){
        return PicId;
    }


    //getters and setters for tasks
     String getTaskName() {
        return taskName;
    }

     String getTaskMember() {
        return taskMember;
    }

     long getTaskDaysLeft() {
        return taskDaysLeft;
    }

     String getTaskDescription() {
        return taskDescription;
    }

     String getFlag() {
        return flag;
    }


}
