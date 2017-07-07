package com.adk.projectsmanager;



public class FilterModel {
    boolean filterWIP = false;
    boolean filterCompleted = false;

    public FilterModel(boolean filterWIP, boolean filterCompleted) {
        this.filterWIP = filterWIP;
        this.filterCompleted = filterCompleted;
    }

    public FilterModel() {
    }

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
}
