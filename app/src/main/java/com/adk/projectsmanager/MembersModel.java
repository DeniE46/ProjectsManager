package com.adk.projectsmanager;


 class MembersModel {
     private String name, occupation;
     private int PicId;



     MembersModel(String name, String occupation, int PicId){
        this.name = name;
        this.occupation = occupation;
        this.PicId = PicId;

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
}
