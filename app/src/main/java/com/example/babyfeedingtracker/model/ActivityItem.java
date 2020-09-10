package com.example.babyfeedingtracker.model;

public class ActivityItem {
    private int id;
    private String activityType;

    public ActivityItem(String activityType) {
        this.activityType = activityType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }
}
