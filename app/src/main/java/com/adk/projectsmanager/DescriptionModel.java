package com.adk.projectsmanager;

/**
 * Created by DeniE46 on 3/1/2017.
 */

public class DescriptionModel {

    String projectTitle, projectDescription;

    public DescriptionModel(String projectTitle, String projectDescription) {
        this.projectTitle = projectTitle;
        this.projectDescription = projectDescription;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }
}
