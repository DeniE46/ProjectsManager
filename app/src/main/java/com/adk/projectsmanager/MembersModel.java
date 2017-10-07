package com.adk.projectsmanager;


 class MembersModel {
     private String name, occupation, phone, facebookAccount;
     private int PicId;



     MembersModel(String name, String occupation, int PicId, String phone, String facebookAccount){
        this.name = name;
        this.occupation = occupation;
        this.PicId = PicId;
        this.phone = phone;
        this.facebookAccount = facebookAccount;
    }

     public MembersModel() {
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

     public String getPhone() {
         return phone;
     }

     public String getFacebookAccount() {
         return facebookAccount;
     }


 }
