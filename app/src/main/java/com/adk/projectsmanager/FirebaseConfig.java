package com.adk.projectsmanager;


class FirebaseConfig {
     static final String FIREBASE_URL = "https://projectsmanager.firebaseio.com/";
    String descriptionURL, membersURL, tasksURL;
    String mUserID;

    public FirebaseConfig() {
    }

    public void setMembersURL(String membersURL) {
        this.membersURL = membersURL;
    }

    public void setDescriptionURL(String descriptionURL) {
        this.descriptionURL = descriptionURL;
    }

    public void setTasksURL(String tasksURL) {
        this.tasksURL = tasksURL;
    }

    public String getDescriptionURL() {
        return descriptionURL;
    }

    public String getMembersURL() {
        return membersURL;
    }

    public String getTasksURL() {
        return tasksURL;
    }
}
