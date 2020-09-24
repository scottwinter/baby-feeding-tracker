package com.example.babyfeedingtracker.model;

import com.example.babyfeedingtracker.ActivityType;

public class ActivityItem {
    private int id;
    private String activityType;
    private Long dateTime;

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

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }
}
